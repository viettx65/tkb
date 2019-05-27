/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import com.dvd.ckp.domain.Param;
import java.math.BigInteger;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.dvd.ckp.domain.Object;
import org.hibernate.transform.Transformers;

/**
 *
 * @author dmin
 */
@Repository
public class UtilsDAOImpl implements UtilsDAO {

    @Autowired
    SessionFactory sessionFactory;

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Param> getParamByKey(String key) {
        Query query = getCurrentSession().getNamedQuery("Param.fillParamByKey");
        query.setParameter("paramKey", key);
        return (List<Param>) query.list();
    }

    @Override
    public BigInteger getId() {
        Query query = getCurrentSession().createSQLQuery("SELECT LAST_INSERT_ID()");
        return (BigInteger) query.list().get(0);
    }

    @Override
    public List<Object> getListObject(Long userId) {
        StringBuilder vstrSql = new StringBuilder("SELECT DISTINCT o.objectId as objectId, o.objectCode as objectCode, o.objectName as objectName, o.objectType as objectType, o.path as path, o.parentId as parentId, o.icon as icon");
        vstrSql.append(" FROM Object o, User u, RoleObject ro, UserRole ur, Role r");
        vstrSql.append(" WHERE o.objectId=ro.objectId");
        vstrSql.append(" AND ro.roleId=r.roleId");
        vstrSql.append(" AND r.roleId=ur.roleId");
        vstrSql.append(" AND ur.userId=u.userId");
        vstrSql.append(" AND o.status=1 AND u.status=1 AND ro.status=1 AND ur.status=1 AND r.status=1");
        vstrSql.append(" AND u.userId= :userId");
        Query query = getCurrentSession().createQuery(vstrSql.toString())
                .setResultTransformer(Transformers.aliasToBean(Object.class));
        query.setParameter("userId", userId);
        return (List<Object>) query.list();
    }

}
