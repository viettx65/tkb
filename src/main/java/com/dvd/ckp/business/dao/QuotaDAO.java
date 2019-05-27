/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import java.util.List;

import com.dvd.ckp.domain.Quota;

/**
 *
 * @author daond
 */
public interface QuotaDAO {

    List<Quota> onSearch(Quota quota);

    void save(Quota quota);
    
    void importData(List<Quota> listData);
}
