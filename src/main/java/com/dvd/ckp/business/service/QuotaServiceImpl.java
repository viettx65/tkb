/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvd.ckp.business.dao.QuotaDAO;
import com.dvd.ckp.domain.Quota;

/**
 *
 * @author viettx
 * @version 1.0
 * @since 01/06/2018
 */
@Service
public class QuotaServiceImpl implements QuotaService {

    @Autowired
    private QuotaDAO quotaDAO;


    @Transactional(readOnly = true)
	@Override
	public List<Quota> onSearch(Quota quota) {
		// TODO Auto-generated method stub
		return quotaDAO.onSearch(quota);
	}
    @Transactional
	@Override
	public void save(Quota quota) {
		// TODO Auto-generated method stub
    	quotaDAO.save(quota);	
		
	}
    @Transactional
	@Override
	public void importData(List<Quota> listData) {
		// TODO Auto-generated method stub
    	quotaDAO.importData(listData);
	}

}
