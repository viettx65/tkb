/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import com.dvd.ckp.domain.Object;
import com.dvd.ckp.domain.SClass;
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
public class ClassDAOImpl implements ClassDAO {

    @Autowired
    SessionFactory sessionFactory;

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<SClass> getAllClass() {
        Query query = getCurrentSession().getNamedQuery("SClass.getAll");
        List<SClass> lstUsers = query.list();
        return lstUsers;
    }

    @Override
    public void save(SClass sclass) {
        getCurrentSession().saveOrUpdate(sclass);
    }

    @Override
    public void delete(SClass sclass) {
        getCurrentSession().delete(sclass);
    }

}
