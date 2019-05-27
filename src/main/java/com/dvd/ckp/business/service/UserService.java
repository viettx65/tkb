/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import com.dvd.ckp.domain.Role;
import com.dvd.ckp.domain.User;
import java.util.List;

/**
 *
 * @author dmin
 */
public interface UserService {

    User getUserByName(String pstrUserName);

    List<User> getAllUser();

    void insertOrUpdateUser(User user);

    List<Role> getRoleByUser(String userId, int type);

    int insertUserRole(Long userId, Long roleId);

    void deleteUserRole(Long userId, Long roleId);

}
