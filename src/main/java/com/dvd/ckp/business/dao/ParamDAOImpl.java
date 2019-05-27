/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import com.dvd.ckp.domain.Param;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daond
 */
@Repository
public class ParamDAOImpl implements ParamDAO {

    @Autowired
    SessionFactory sessionFactory;

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Param> getAllParam() {
        Query query = getCurrentSession().getNamedQuery("Param.fillParamActive");
        List<Param> lstParams = query.list();
        if (lstParams != null && !lstParams.isEmpty()) {
            return lstParams;
        }
        return null;
    }

    @Override
    public void insertOrUpdateParam(Param param) {
        getCurrentSession().saveOrUpdate(param);
    }

    @Override
    public List<Param> getDistinctParamKey() {
        String vstrSql = "select distinct param_key as paramKey from params where status = 1";
        Query query = getCurrentSession()
                .createSQLQuery(vstrSql)
                .addScalar("paramKey", StandardBasicTypes.STRING)
                .setResultTransformer(
                        Transformers.aliasToBean(Param.class));
        return (List<Param>) query.list();
    }

}
