/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import com.dvd.ckp.business.dao.ParamDAO;
import com.dvd.ckp.domain.Param;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author daond
 */
@Service
public class ParamServiceImpl implements ParamService {

    @Autowired
    private ParamDAO paramDAO;

    @Transactional(readOnly = true)
    @Override
    public List<Param> getAllParam() {
        return paramDAO.getAllParam();
    }

    @Transactional
    @Override
    public void insertOrUpdateParam(Param param) {
        paramDAO.insertOrUpdateParam(param);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Param> getDistinctParamKey() {
        return paramDAO.getDistinctParamKey();
    }

}
