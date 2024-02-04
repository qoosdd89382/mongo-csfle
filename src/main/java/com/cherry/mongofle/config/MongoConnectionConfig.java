package com.cherry.mongofle.config;

import com.cherry.mongofle.po.DoctorPo;
import com.cherry.mongofle.po.PatientPo;
import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoJsonSchemaCreator;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;

import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.cherry.mongofle.generator.KeyGenerator.*;

@Configuration
public class MongoConnectionConfig {
    @Value("${mongo-csfle.autoenc-keyalt}")
    public String globalKeyAltName;
    public static String connectionString = "mongodb+srv://******************";
    public static String keyVaultDb = "test_db";
    public static String keyVaultColl = "__keyVault";
    public static String keyVaultNamespace = keyVaultDb + "." + keyVaultColl;
    public static String kmsProvider = "local";

    public static final String GLOBAL_ALGO = "AEAD_AES_256_CBC_HMAC_SHA_512-Random";

    /**
     * 避免循環依賴造成的問題, 改用 clientEncryption 取得 keyId
     *
     * @param kmsProviders
     * @return
     */
    @Bean
    public String globalDataKeyId(Map<String, Map<String, Object>> kmsProviders){
        ClientEncryptionSettings clientEncryptionSettings = getClientEncryptionSettings(kmsProviders);
        ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
        BsonDocument dataKey = clientEncryption.getKeyByAltName(globalKeyAltName);
        String base64DataKeyId = Base64.getEncoder()
                .encodeToString(dataKey.get("_id").asBinary().getData());
        System.out.println("DataKeyId [base64]: " + base64DataKeyId);
        clientEncryption.close();
        return base64DataKeyId;
    }

    @Bean
    MongoClientSettingsBuilderCustomizer customizer(MappingContext mappingContext,
                                                    Map<String, Map<String, Object>> kmsProviders) {
        return (builder) -> {
            MongoJsonSchemaCreator schemaCreator = MongoJsonSchemaCreator.create(mappingContext);
            MongoJsonSchema patientSchema = schemaCreator
                    .filter(MongoJsonSchemaCreator.encryptedOnly())
                    .createSchemaFor(PatientPo.class);
            MongoJsonSchema doctorScheme = schemaCreator
                    .filter(MongoJsonSchemaCreator.encryptedOnly())
                    .createSchemaFor(DoctorPo.class);

            AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                    .keyVaultNamespace(keyVaultNamespace)
                    .kmsProviders(kmsProviders)
                    .extraOptions(Map.of(
                            // mac local
                            "cryptSharedLibPath", "mongo_crypt_shared_v1-macos-arm64-enterprise-7.0.5/lib/mongo_crypt_v1.dylib",
                            // container
//                            "cryptSharedLibPath", "/app/mongo_crypt/lib/mongo_crypt_v1.so",
                            "cryptSharedLibRequired", true))
                    .schemaMap(Map.of(
                            keyVaultDb + ".patient",
                            patientSchema.schemaDocument().toBsonDocument(),
                            keyVaultDb + ".doctor",
                            doctorScheme.schemaDocument().toBsonDocument()
                    ))
                    .build();

            builder.autoEncryptionSettings(autoEncryptionSettings);
        };
    }

    @Bean
    public Map<String, Map<String, Object>> kmsProviders() throws Exception {
        byte[] localMasterKeyRead = new byte[96];

        try (InputStream fis = getClass().getResourceAsStream("/master-key.txt");) {
            if (fis != null) {
                fis.read(localMasterKeyRead);
            }
        }
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put("key", localMasterKeyRead);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<String, Map<String, Object>>();
        kmsProviders.put("local", keyMap);
        return kmsProviders;
    }


}
