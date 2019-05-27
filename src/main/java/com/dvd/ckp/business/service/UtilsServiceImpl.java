/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import com.dvd.ckp.business.dao.UtilsDAO;
import com.dvd.ckp.domain.Param;
import java.math.BigInteger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dvd.ckp.domain.Object;

/**
 *
 * @author dmin
 */
@Service
public class UtilsServiceImpl implements UtilsService {

    @Autowired
    private UtilsDAO UtilsDAO;

    @Transactional(readOnly = true)
    @Override
    public List<Param> getParamByKey(String key) {
        return UtilsDAO.getParamByKey(key);
    }

    @Transactional(readOnly = true)
    @Override
    public BigInteger getId() {
        return UtilsDAO.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Object> getListObject(Long userId) {
        return UtilsDAO.getListObject(userId);
    }

}
