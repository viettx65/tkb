/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import com.dvd.ckp.domain.Object;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daond
 */
@Repository
public class ObjectDAOImpl implements ObjectDAO {

    @Autowired
    SessionFactory sessionFactory;

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Object> getAllObject() {
        Query query = getCurrentSession().getNamedQuery("Object.getAllObject");
        List<Object> lstUsers = query.list();
        if (lstUsers != null && !lstUsers.isEmpty()) {
            return lstUsers;
        }
        return null;
    }

    @Override
    public void insertOrUpdateObject(Object object) {
        getCurrentSession().saveOrUpdate(object);
    }

}
