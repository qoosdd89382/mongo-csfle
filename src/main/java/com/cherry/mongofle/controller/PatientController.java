package com.cherry.mongofle.controller;

import com.cherry.mongofle.dao.PatientDao;
import com.cherry.mongofle.po.PatientPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/p")
public class PatientController {

    @Autowired
    private PatientDao patientDao;

    @GetMapping("/add")
    public String add() {
        PatientPo po = new PatientPo();
        po.setName("abc");
        return patientDao.save(po)
                .getId();
    }

    @GetMapping("/get")
    public String get(@RequestParam("id") String id) {
        return patientDao.findById(id).get()
                .getName();
    }
}
