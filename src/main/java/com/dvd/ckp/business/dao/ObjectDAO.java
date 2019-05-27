/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import java.util.List;
import com.dvd.ckp.domain.Object;

/**
 *
 * @author daond
 */
public interface ObjectDAO {

    List<com.dvd.ckp.domain.Object> getAllObject();

    void insertOrUpdateObject(Object object);
}
