/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import com.dvd.ckp.business.dao.UserDAO;
import com.dvd.ckp.domain.Role;
import com.dvd.ckp.domain.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author dmin
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Transactional(readOnly = true)
    @Override
    public User getUserByName(String pstrUserName) {
        return userDAO.getUserByName(pstrUserName);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUser() {
        return userDAO.getAllUser();
    }

    @Transactional
    @Override
    public void insertOrUpdateUser(User user) {
        userDAO.insertOrUpdateUser(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> getRoleByUser(String userId, int type) {
        return userDAO.getRoleByUser(userId, type);
    }

    @Transactional
    @Override
    public void deleteUserRole(Long userId, Long roleId) {
        userDAO.deleteUserRole(userId, roleId);
    }

    @Override
    public int insertUserRole(Long userId, Long roleId) {
        return userDAO.insertUserRole(userId, roleId);
    }

}
