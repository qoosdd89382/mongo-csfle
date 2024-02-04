//package com.cherry.mongofle.controller;
//
//import com.cherry.mongofle.dao.KeyVaultDao;
//import org.bson.types.Binary;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/kv")
//public class KeyVaultController {
//
//    @Autowired
//    private KeyVaultDao keyVaultDao;
//
//    @GetMapping("/get")
//    public Binary get(@RequestParam("alt") String keyAltName) {
//        Binary id = keyVaultDao.findKeyAltNamesContains(keyAltName).get()
//                .getId();
//        System.out.println(keyVaultDao.findById(id).get().getKeyAltNames());
//        return id;
//    }
//}
