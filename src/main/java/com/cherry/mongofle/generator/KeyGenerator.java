package com.cherry.mongofle.generator;

import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

import static com.cherry.mongofle.config.MongoConnectionConfig.*;

public class KeyGenerator {

    @Value("${mongo-csfle.autoenc-keyalt}")
    public String globalKeyAltName;

    private MongoClient keyVaultClient = MongoClients.create(connectionString);

//    public static void main(String[] args) throws Exception {
//        KeyGenerator keyGenerator = new KeyGenerator();
//        keyGenerator.genMasterKey();
//        keyGenerator.genVault();
//        keyGenerator.genDataKey();
//    }

    private Map<String, Map<String, Object>> getKmsProviders() throws Exception {
        String path = "master-key.txt";

        byte[] localMasterKeyRead = new byte[96];

        try (FileInputStream fis = new FileInputStream(path)) {
            if (fis.read(localMasterKeyRead) < 96)
                throw new Exception("Expected to read 96 bytes from file");
        }
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put("key", localMasterKeyRead);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<String, Map<String, Object>>();
        kmsProviders.put("local", keyMap);
        return kmsProviders;
    }

    public static ClientEncryptionSettings getClientEncryptionSettings(Map<String, Map<String, Object>> kmsProviders) {
        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build())
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviders)
                .build();
        return clientEncryptionSettings;
    }


    private void genDataKey() throws Exception {
        Map<String, Map<String, Object>> kmsProviders = this.getKmsProviders();
        ClientEncryptionSettings clientEncryptionSettings = getClientEncryptionSettings(kmsProviders);

        MongoClient regularClient = MongoClients.create(connectionString);

        ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);

        List<String> keyAltNames = new ArrayList<String>();
        keyAltNames.add(globalKeyAltName);

        BsonBinary dataKeyId = clientEncryption.createDataKey(kmsProvider, new DataKeyOptions()
                .keyAltNames(keyAltNames));
        String base64DataKeyId = Base64.getEncoder().encodeToString(dataKeyId.getData());
        System.out.println("DataKeyId [base64]: " + base64DataKeyId);
        clientEncryption.close();
    }

    private void genVault() {

        // Drop the Key Vault Collection in case you created this collection
        // in a previous run of this application.
        keyVaultClient.getDatabase(keyVaultDb).getCollection(keyVaultColl).drop();
        // Drop the database storing your encrypted fields as all
        // the DEKs encrypting those fields were deleted in the preceding line.
//        keyVaultClient.getDatabase("medicalRecords").getCollection("patients").drop();

        MongoCollection keyVaultCollection = keyVaultClient.getDatabase(keyVaultDb).getCollection(keyVaultColl);
        IndexOptions indexOpts = new IndexOptions()
                .partialFilterExpression(
                        new BsonDocument("keyAltNames", new BsonDocument("$exists", new BsonBoolean(true)) )
                ).unique(true);
        keyVaultCollection.createIndex(new BsonDocument("keyAltNames", new BsonInt32(1)), indexOpts);
        keyVaultClient.close();
    }

    private void genMasterKey() {
        byte[] localMasterKeyWrite = new byte[96];
        new SecureRandom().nextBytes(localMasterKeyWrite);
        try (FileOutputStream stream = new FileOutputStream("master-key.txt")) {
            stream.write(localMasterKeyWrite);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
