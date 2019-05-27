/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import com.dvd.ckp.domain.SClass;

import java.util.List;

/**
 *
 * @author viettx
 */
public interface ClassService {

    List<SClass> getAllClass();

    void save(SClass sclass);
    
    void delete(SClass sclass);
}
