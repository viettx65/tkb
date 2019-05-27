/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import com.dvd.ckp.domain.Role;
import com.dvd.ckp.domain.Object;
import java.util.List;

/**
 *
 * @author daond
 */
public interface RoleDAO {

    List<Role> getAllRole();

    void insertOrUpdateRole(Role role);

    List<Object> getObjectsByRole(String roleId);

    void deleteRoleObject(Long roleId, Long objectId);

}
