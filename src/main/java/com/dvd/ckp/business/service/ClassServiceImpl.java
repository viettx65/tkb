/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import com.dvd.ckp.business.dao.ClassDAO;
import com.dvd.ckp.domain.SClass;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author viettx
 */
@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassDAO classDAO;

    @Transactional(readOnly = true)
    @Override
    public List<SClass> getAllClass() {
        return classDAO.getAllClass();
    }

    @Transactional
    @Override
    public void save(SClass sclass) {
        classDAO.save(sclass);
    }

    @Transactional
    @Override
    public void delete(SClass sclass) {
        classDAO.delete(sclass);
    }

}
