/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import com.dvd.ckp.domain.Role;
import com.dvd.ckp.domain.User;
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
 * @author dmin
 */
@Repository
public class UserDAOImpl implements UserDAO {

    @Autowired
    SessionFactory sessionFactory;

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getUserByName(String pstrUserName) {

        Query query = getCurrentSession().getNamedQuery("User.getUserByName");
        query.setParameter("userName", pstrUserName);
        List lstUsers = query.list();
        if (lstUsers != null && !lstUsers.isEmpty()) {
            return (User) lstUsers.get(0);
        }
        return null;
    }

    @Override
    public List<User> getAllUser() {
        Query query = getCurrentSession().getNamedQuery("User.getAllUser");
        List<User> lstUsers = query.list();
        if (lstUsers != null && !lstUsers.isEmpty()) {
            return lstUsers;
        }
        return null;
    }

    @Override
    public void insertOrUpdateUser(User user) {
        getCurrentSession().saveOrUpdate(user);
    }

    /*
    * type 1: Tim kiem Role duoc phan quyen cho User
    * type 2: Tim kiem Role khong duoc phan quyen cho User
     */
    @Override
    public List<Role> getRoleByUser(String userId, int type) {
        StringBuilder vstrSql = new StringBuilder("select r.role_id as roleId, r.role_code as roleCode, r.role_name as roleName,r.description as description");
        vstrSql.append(" from roles r");
        if (type == 1) {
            vstrSql.append(" where r.role_id in (select ur.role_id from user_role ur where ur.user_id = :userId and ur.status = 1)");
        } else {
            vstrSql.append(" where r.role_id not in (select ur.role_id from user_role ur where ur.user_id = :userId and ur.status = 1)");
        }
        vstrSql.append(" and r.status <> 0");
        Query query = getCurrentSession()
                .createSQLQuery(vstrSql.toString())
                .addScalar("roleId", StandardBasicTypes.LONG)
                .addScalar("roleCode", StandardBasicTypes.STRING)
                .addScalar("roleName", StandardBasicTypes.STRING)
                .addScalar("description", StandardBasicTypes.STRING)
                .setResultTransformer(
                        Transformers.aliasToBean(Role.class));
        query.setParameter("userId", userId);
        return (List<Role>) query.list();
    }

    @Override
    public void deleteUserRole(Long userId, Long roleId) {
        Session session = getCurrentSession();
        try {
            StringBuilder builder = new StringBuilder("update user_role set ");
            builder.append(" status = 0");
            builder.append(" where ");
            builder.append(" user_id = :userId and ");
            builder.append(" role_id = :roleId ");
            Query query = session.createSQLQuery(builder.toString());
            query.setParameter("userId", userId);
            query.setParameter("roleId", roleId);
            query.executeUpdate();
        } catch (Exception e) {
            session.beginTransaction().rollback();
        }
    }

    @Override
    public int insertUserRole(Long userId, Long roleId) {
        Session session = getCurrentSession();
        try {
            StringBuilder builder = new StringBuilder("INSERT INTO user_role (user_id, role_id, status) ");
            builder.append(" VALUES (");
            builder.append(" :userId, ");
            builder.append(" :roleId, 1)");
            Query query = session.createSQLQuery(builder.toString());
            query.setParameter("userId", userId);
            query.setParameter("roleId", roleId);
            query.executeUpdate();
            return 1;
        } catch (Exception e) {
            session.beginTransaction().rollback();
        }
        return 0;
    }

}
