package com.cherry.mongofle.dao;

import com.cherry.mongofle.po.DoctorPo;
import com.cherry.mongofle.po.PatientPo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorDao extends MongoRepository<DoctorPo, String> {
}
