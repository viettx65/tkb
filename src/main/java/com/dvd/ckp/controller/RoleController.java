/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import com.dvd.ckp.business.service.ObjectService;
import com.dvd.ckp.business.service.RoleService;
import com.dvd.ckp.domain.Role;
import com.dvd.ckp.domain.Object;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;
import com.dvd.ckp.utils.StyleUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

/**
 *
 * @author daond
 */
public class RoleController extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(RoleController.class);
    @WireVariable
    protected RoleService roleService;
    @WireVariable
    protected ObjectService objectService;
    @Wire
    private Grid lstRole;
    @Wire
    private Textbox txtFilterRoleCode;
    @Wire
    private Textbox txtFilterRoleName;
    ListModelList<Role> listDataModel;
    private List<Role> lstRoles;
    @Wire
    private Grid lstObject;
    ListModelList<Object> listObjectDataModel;
    private List<Object> lstObjects;

    Long roleIdSelect;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        roleService = (RoleService) SpringUtil.getBean(SpringConstant.ROLE_SERVICES);
        objectService = (ObjectService) SpringUtil.getBean(SpringConstant.OBJECT_SERVICES);
        lstRoles = new ArrayList<>();
        lstObjects = new ArrayList<>();
        List<Role> vlstRole = roleService.getAllRole();
        if (vlstRole != null && !vlstRole.isEmpty()) {
            lstRoles.addAll(vlstRole);
        }
        listDataModel = new ListModelList(lstRoles);
        lstRole.setModel(listDataModel);
        if (vlstRole != null && !vlstRole.isEmpty() && vlstRole.get(0) != null) {
            Role role = vlstRole.get(0);
            roleIdSelect = role.getRoleId();
            List<Object> vlstObject = roleService.getObjectsByRole(String.valueOf(roleIdSelect));
            if (vlstObject != null && !vlstObject.isEmpty() && vlstObject.size() > 0) {
                lstObjects.addAll(vlstObject);
            }
            listObjectDataModel = new ListModelList(lstObjects);
            lstObject.setModel(listObjectDataModel);
        }
    }

    /**
     * Edit row
     *
     * @param event
     */
    public void onEdit(ForwardEvent event) {

        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        StyleUtils.setEnableComponent(lstCell, 5);
    }

    /**
     * Cancel
     *
     * @param event
     */
    public void onCancel(ForwardEvent event) {

        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        StyleUtils.setDisableComponent(lstCell, 5);
        reloadGrid();

    }

    /**
     * Save
     *
     * @param event
     */
    public void onSave(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        Role c = rowSelected.getValue();
        Role role = getDataInRow(lstCell);
        role.setRoleId(c.getRoleId());
        role.setStatus(Constants.STATUS_ACTIVE);
        roleService.insertOrUpdateRole(role);
        StyleUtils.setDisableComponent(lstCell, 5);
        reloadGrid();
    }

    /**
     * Add row
     */
    public void onClick$add() {
        Role role = new Role();
        listDataModel.add(0, role);
        lstRole.setActivePage(Constants.FIRST_INDEX);
        lstRole.setModel(listDataModel);
        lstRole.renderAll();
        List<Component> lstCell = lstRole.getRows().getChildren().get(0).getChildren();
        StyleUtils.setEnableComponent(lstCell, 5);
    }

    public void onClick$addObject() {
        Role role = new Role();
        listDataModel.add(0, role);
        lstRole.setActivePage(Constants.FIRST_INDEX);
        lstRole.setModel(listDataModel);
        lstRole.renderAll();
        List<Component> lstCell = lstRole.getRows().getChildren().get(0).getChildren();
        StyleUtils.setEnableComponent(lstCell, 5);
    }

    /**
     * Get object customer
     *
     * @param lstCell
     * @return
     */
    private Role getDataInRow(List<Component> lstCell) {
        Role role = new Role();
        Textbox txtRoleCode = (Textbox) lstCell.get(1).getFirstChild();
        Textbox txtRoleName = (Textbox) lstCell.get(2).getFirstChild();
        Textbox txtDes = (Textbox) lstCell.get(3).getFirstChild();
        role.setRoleCode(txtRoleCode.getValue());
        role.setRoleName(txtRoleName.getValue());
        role.setDescription(txtDes.getValue());
        return role;
    }

    /**
     * Reload grid
     */
    private void reloadGrid() {
        List<Role> vlstRole = roleService.getAllRole();
        listDataModel = new ListModelList(vlstRole);
        lstRole.setModel(listDataModel);
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
        lstRole.setModel(listDataModel);

    }

    /**
     *
     *
     * @param event
     */
    public void onViewObjectByRole(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        Role c = rowSelected.getValue();
        roleIdSelect = c.getRoleId();
        reloadGridObject(String.valueOf(roleIdSelect));
    }

    /*Reload grid Object*/
    private void reloadGridObject(String roleId) {
        List<Object> vlstObject = roleService.getObjectsByRole(roleId);
        listObjectDataModel = new ListModelList(vlstObject);
        lstObject.setModel(listObjectDataModel);
    }

    /**
     * Delete
     *
     * @param event
     */
    public void onDelete(ForwardEvent event) {
        Messagebox.show("Bạn có chắc chắn muốn xóa không?", "Xác nhận", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event e) {
                if (Messagebox.ON_YES.equals(e.getName())) {
                    Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
                    Role c = rowSelected.getValue();
                    c.setStatus(Constants.STATUS_INACTIVE);
                    roleService.insertOrUpdateRole(c);
                    reloadGrid();
                    if (c.getRoleId() == roleIdSelect) {
                        if (lstRoles != null && !lstRoles.isEmpty() && lstRoles.get(0) != null) {
                            Role role = lstRoles.get(0);
                            roleIdSelect = role.getRoleId();
                            reloadGridObject(String.valueOf(roleIdSelect));
                        }
                    }
                }
            }
        });
    }

    /**
     * Delete
     *
     * @param event
     */
    public void onDeleteObject(ForwardEvent event) {
        Messagebox.show("Bạn có chắc chắn muốn xóa không?", "Xác nhận", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event e) {
                if (Messagebox.ON_YES.equals(e.getName())) {
                    Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
                    Object c = rowSelected.getValue();
                    c.setStatus(Constants.STATUS_INACTIVE);
                    roleService.deleteRoleObject(roleIdSelect, c.getObjectId());
                    reloadGridObject(String.valueOf(roleIdSelect));
                }
            }
        });
    }
}
