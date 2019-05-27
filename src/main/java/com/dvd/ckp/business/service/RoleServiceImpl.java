/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import com.dvd.ckp.business.dao.RoleDAO;
import com.dvd.ckp.domain.Object;
import com.dvd.ckp.domain.Role;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author daond
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Transactional(readOnly = true)
    @Override
    public List<Role> getAllRole() {
        return roleDAO.getAllRole();
    }

    @Transactional
    @Override
    public void insertOrUpdateRole(Role role) {
        roleDAO.insertOrUpdateRole(role);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Object> getObjectsByRole(String roleId) {
        return roleDAO.getObjectsByRole(roleId);
    }

    @Transactional
    @Override
    public void deleteRoleObject(Long roleId, Long objectId) {
        roleDAO.deleteRoleObject(roleId, objectId);
    }

}
