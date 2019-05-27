/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import java.util.List;
import com.dvd.ckp.domain.SClass;

/**
 *
 * @author daond
 */
public interface ClassDAO {

    List<SClass> getAllClass();

    void save(SClass sclass);
    
    void delete(SClass sclass);
}
