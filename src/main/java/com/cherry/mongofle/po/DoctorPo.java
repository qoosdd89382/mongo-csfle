package com.cherry.mongofle.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;

import static com.cherry.mongofle.config.MongoConnectionConfig.GLOBAL_ALGO;

@Document("doctor")
@Encrypted(
        keyId = "#{@globalDataKeyId}",
        algorithm = GLOBAL_ALGO)
public class DoctorPo {

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
