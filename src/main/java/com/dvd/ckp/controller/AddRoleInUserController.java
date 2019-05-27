/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import com.dvd.ckp.business.service.UserService;
import com.dvd.ckp.domain.Role;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

/**
 *
 * @author daond
 */
public class AddRoleInUserController extends GenericForwardComposer {

    @Wire
    private Longbox lgbUserId;

    private static final Logger logger = Logger.getLogger(RoleController.class);
    @WireVariable
    protected UserService userService;
    @Wire
    private Grid lstRoleNotOwnUser;
    @Wire
    private Textbox txtFilterRoleCode;
    @Wire
    private Textbox txtFilterRoleName;
    ListModelList<Role> listDataModel;
    private List<Role> lstRoles;
    private Long lngUserId;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        userService = (UserService) SpringUtil.getBean(SpringConstant.USER_SERVICES);
        lstRoles = new ArrayList<>();
        lngUserId = lgbUserId.getValue();
        List<Role> vlstRole = userService.getRoleByUser(String.valueOf(lngUserId), 2);
        if (vlstRole != null) {
            lstRoles.addAll(vlstRole);
        }
        listDataModel = new ListModelList(lstRoles);
        lstRoleNotOwnUser.setModel(listDataModel);
    }

    public void onChange$txtFilterRoleCode() {
        Role role = new Role();
        String vstrRoleCode = txtFilterRoleCode.getValue();
        role.setRoleCode(vstrRoleCode);
        String vstrRoleName = txtFilterRoleName.getValue();
        role.setRoleName(vstrRoleName);
        filter(role);
    }

    public void onChange$txtFilterRoleName() {
        Role role = new Role();
        String vstrRoleCode = txtFilterRoleCode.getValue();
        role.setRoleCode(vstrRoleCode);
        String vstrRoleName = txtFilterRoleName.getValue();
        role.setRoleName(vstrRoleName);
        filter(role);
    }

    private void filter(Role role) {
        List<Role> vlstRole = new ArrayList<>();
        if (lstRoles != null && !lstRoles.isEmpty() && role != null) {
            if (!StringUtils.isValidString(role.getRoleCode())
                    && !StringUtils.isValidString(role.getRoleName())) {
                vlstRole.addAll(lstRoles);
            } else {
                for (Role c : lstRoles) {
                    //tim theo ma va ten
                    if (StringUtils.isValidString(role.getRoleCode()) && StringUtils.isValidString(role.getRoleName())) {
                        if ((StringUtils.isValidString(c.getRoleCode()) && c.getRoleCode().toLowerCase().contains(role.getRoleCode().toLowerCase()))
                                && (StringUtils.isValidString(c.getRoleName()) && c.getRoleName().toLowerCase().contains(role.getRoleName().toLowerCase()))) {
                            vlstRole.add(c);
                        }
                    } //tim theo role code
                    else if (StringUtils.isValidString(role.getRoleCode()) && !StringUtils.isValidString(role.getRoleName())) {
                        if (StringUtils.isValidString(c.getRoleCode()) && c.getRoleCode().toLowerCase().contains(role.getRoleCode().toLowerCase())) {
                            vlstRole.add(c);
                        }
                    } //tim theo role name
                    else if (!StringUtils.isValidString(role.getRoleCode()) && StringUtils.isValidString(role.getRoleName())) {
                        if (StringUtils.isValidString(c.getRoleName()) && c.getRoleName().toLowerCase().contains(role.getRoleName().toLowerCase())) {
                            vlstRole.add(c);
                        }
                    }
                }
            }
        }
        listDataModel = new ListModelList(vlstRole);
        lstRoleNotOwnUser.setModel(listDataModel);

    }

    /**
     * Reset passowrd user
     *
     * @param event
     */
    public void onAddRoleInUser(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        Role c = rowSelected.getValue();
        int result = userService.insertUserRole(lngUserId, c.getRoleId());
        String vstrRoleName = c.getRoleName();
        if (result == 1) {
            Messagebox.show(Labels.getLabel("user.add.role.in.user.success", new String[]{vstrRoleName}), Labels.getLabel("login.change.password.title.message"), Messagebox.OK, Messagebox.INFORMATION);
            reloadGrid();
        } else {
            Messagebox.show(Labels.getLabel("user.add.role.in.user.error", new String[]{vstrRoleName}), Labels.getLabel("user.add.role.in.user.error"), Messagebox.OK, Messagebox.INFORMATION);
        }
        //Tat popup
    }

    /**
     * Reload grid
     */
    private void reloadGrid() {
        List<Role> vlstRole = userService.getRoleByUser(String.valueOf(lngUserId), 2);
        listDataModel = new ListModelList(vlstRole);
        lstRoleNotOwnUser.setModel(listDataModel);
    }
}
