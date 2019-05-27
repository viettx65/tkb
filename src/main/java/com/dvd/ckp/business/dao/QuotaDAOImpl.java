/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dvd.ckp.domain.Quota;
import com.dvd.ckp.utils.StringUtils;

import bsh.StringUtil;

/**
 *
 * @author daond
 */
@Repository
public class QuotaDAOImpl implements QuotaDAO {

    @Autowired
    SessionFactory sessionFactory;

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Quota> onSearch(Quota quota) {
        StringBuilder builder = new StringBuilder("SELECT o FROM Quota o WHERE o.status=1 ");
        if (StringUtils.isValidString(quota.getStaffCode())) {
            builder.append(" and staffID = :staffID");
        }
        if (StringUtils.isValidString(quota.getYear())) {
            builder.append(" and year = :year");
        }
        builder.append(" ORDER BY createDate");
        Query query = getCurrentSession().createQuery(builder.toString());
        if (StringUtils.isValidString(quota.getStaffCode())) {
            query.setParameter("staffID", quota.getStaffCode());
        }
        if (StringUtils.isValidString(quota.getYear())) {
            query.setParameter("year", quota.getYear());
        }
        List<Quota> lstQuota = query.list();
        if (lstQuota != null && !lstQuota.isEmpty()) {
            return lstQuota;
        }
        return null;
    }

    @Override
    public void save(Quota quota) {
        getCurrentSession().saveOrUpdate(quota);

    }

    @Override
    public void importData(List<Quota> listData) {
        // TODO Auto-generated method stub
        Session session = getCurrentSession();
        if (listData != null && !listData.isEmpty()) {
            for (Quota quota : listData) {
                session.saveOrUpdate(quota);
            }
        }

    }

}
