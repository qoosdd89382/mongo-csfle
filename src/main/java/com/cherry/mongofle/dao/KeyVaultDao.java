//package com.cherry.mongofle.dao;
//
//import com.cherry.mongofle.po.KeyVaultPo;
//import org.bson.types.Binary;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface KeyVaultDao extends MongoRepository<KeyVaultPo, Binary> {
//
//    @Query("{ 'keyAltNames': { $in: [?0] } }")
//    Optional<KeyVaultPo> findKeyAltNamesContains(String keyAltName);
//
//}
