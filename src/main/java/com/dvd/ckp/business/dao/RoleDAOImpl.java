/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import com.dvd.ckp.domain.Role;
import com.dvd.ckp.domain.Object;
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
public class RoleDAOImpl implements RoleDAO {

    @Autowired
    SessionFactory sessionFactory;

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Role> getAllRole() {
        Query query = getCurrentSession().getNamedQuery("Role.getAllRole");
        List<Role> lstRoles = query.list();
        if (lstRoles != null && !lstRoles.isEmpty()) {
            return lstRoles;
        }
        return null;
    }

    @Override
    public void insertOrUpdateRole(Role role) {
        getCurrentSession().saveOrUpdate(role);
    }

    @Override
    public List<Object> getObjectsByRole(String roleId) {
        StringBuilder vstrSql = new StringBuilder("SELECT o.object_id as objectId, o.object_code as objectCode, o.object_name as objectName, o.object_type as objectType, o.path as path, o.parent_id as parentId, o.icon as icon");
        vstrSql.append(" FROM objects o");
        vstrSql.append(" WHERE object_id in (select object_id from role_object ro where ro.role_id = :roleId and ro.status = 1)");
        Query query = getCurrentSession()
                .createSQLQuery(vstrSql.toString())
                .addScalar("objectId", StandardBasicTypes.LONG)
                .addScalar("objectCode", StandardBasicTypes.STRING)
                .addScalar("objectName", StandardBasicTypes.STRING)
                .addScalar("objectType", StandardBasicTypes.LONG)
                .addScalar("path", StandardBasicTypes.STRING)
                .addScalar("parentId", StandardBasicTypes.LONG)
                .addScalar("icon", StandardBasicTypes.STRING)
                .setResultTransformer(
                        Transformers.aliasToBean(Object.class));
        query.setParameter("roleId", roleId);
        return (List<Object>) query.list();
    }

    @Override
    public void deleteRoleObject(Long roleId, Long objectId) {
        Session session = getCurrentSession();
        try {
            StringBuilder builder = new StringBuilder("update role_object set ");
            builder.append(" status = 0");
            builder.append(" where ");
            builder.append(" role_id = :roleId and ");
            builder.append(" object_id = :objectId ");
            Query query = session.createSQLQuery(builder.toString());
            query.setParameter("roleId", roleId);
            query.setParameter("objectId", objectId);
            query.executeUpdate();
        } catch (Exception e) {
            session.beginTransaction().rollback();
        }
    }

}
