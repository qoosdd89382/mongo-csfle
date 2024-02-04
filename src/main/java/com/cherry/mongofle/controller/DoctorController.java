package com.cherry.mongofle.controller;

import com.cherry.mongofle.dao.DoctorDao;
import com.cherry.mongofle.dao.PatientDao;
import com.cherry.mongofle.po.DoctorPo;
import com.cherry.mongofle.po.PatientPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/d")
public class DoctorController {

    @Autowired
    private DoctorDao doctorDao;

    @GetMapping("/add")
    public String add(@RequestParam("name") String name) {
        DoctorPo po = new DoctorPo();
        po.setName(name);
        return doctorDao.save(po)
                .getId();
    }

    @GetMapping("/get")
    public String get(@RequestParam("id") String id) {
        return doctorDao.findById(id).get()
                .getName();
    }
}
