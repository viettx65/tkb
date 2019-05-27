/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import com.dvd.ckp.business.dao.ObjectDAO;
import com.dvd.ckp.domain.Object;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author daond
 */
@Service
public class ObjectServiceImpl implements ObjectService {

    @Autowired
    private ObjectDAO ObjectDAO;

    @Transactional(readOnly = true)
    @Override
    public List<Object> getAllObject() {
        return ObjectDAO.getAllObject();
    }

    @Transactional
    @Override
    public void insertOrUpdateObject(Object object) {
        ObjectDAO.insertOrUpdateObject(object);
    }

}
