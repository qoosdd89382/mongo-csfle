package com.cherry.mongofle.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;

import static com.cherry.mongofle.config.MongoConnectionConfig.GLOBAL_ALGO;

@Document("patient")
@Encrypted(
//        keyId = "8a2e3199-c378-4ecf-908a-06599da5c28c",
//        keyId = "ii4xmcN4Ts+QigZZnaXCjA==",
        keyId = "#{@globalDataKeyId}",
        algorithm = GLOBAL_ALGO)
public class PatientPo {

    @Id
    private String id;

    @Encrypted
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
