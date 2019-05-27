/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import com.dvd.ckp.business.service.RoleService;
import com.dvd.ckp.business.service.UserService;
import com.dvd.ckp.component.MyListModel;
import com.dvd.ckp.domain.Role;
import com.dvd.ckp.domain.User;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.EncryptUtil;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;
import com.dvd.ckp.utils.StyleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author daond
 */
public class UserController extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(UserController.class);
    @WireVariable
    protected UserService userService;
    @WireVariable
    protected RoleService roleService;
    @Wire
    private Grid lstUser;
    @Wire
    private Textbox txtFilterPhone;
    ListModelList<User> listDataModel;
    MyListModel<User> listDataModelFilter;
    private List<User> lstUsers;
    private Window mainUser;
    private boolean blnAddOrEdit = false;
    @Wire
    private Combobox cbxUserFilter;

    @Wire
    private Grid lstRole;
    ListModelList<Role> listDataModelRole;
    private List<Role> lstRoles;
    @Wire
    private Textbox txtFilterRoleCode;
    @Wire
    private Textbox txtFilterRoleName;

    Long userIdSelect;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        userService = (UserService) SpringUtil.getBean(SpringConstant.USER_SERVICES);
        roleService = (RoleService) SpringUtil.getBean(SpringConstant.ROLE_SERVICES);
        lstUsers = new ArrayList<>();
        List<User> vlstUser = userService.getAllUser();
        if (vlstUser != null) {
            lstUsers.addAll(vlstUser);
        }
        listDataModel = new ListModelList(lstUsers);
        listDataModelFilter = new MyListModel(lstUsers);
        lstUser.setModel(listDataModel);
        cbxUserFilter.setModel(listDataModelFilter);

        lstRoles = new ArrayList<>();
        if (vlstUser != null && !vlstUser.isEmpty() && vlstUser.get(0) != null) {
            User user = vlstUser.get(0);
            userIdSelect = user.getUserId();
            List<Role> vlstRole = userService.getRoleByUser(String.valueOf(userIdSelect), 1);
            if (vlstRole != null && !vlstRole.isEmpty() && vlstRole.size() > 0) {
                lstRoles.addAll(vlstRole);
            }
            listDataModelRole = new ListModelList(lstRoles);
            lstRole.setModel(listDataModelRole);
        }
    }

    /**
     * Edit row
     *
     * @param event
     */
    public void onEdit(ForwardEvent event) {
        blnAddOrEdit = false;
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
        blnAddOrEdit = false;
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
    public boolean onSave(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        User c = rowSelected.getValue();
        User user = getDataInRow(lstCell);
        user.setUserId(c.getUserId());
        String vstrPassword = "";
        if (blnAddOrEdit) {
            vstrPassword = RandomStringUtils.random(8, Constants.RESET_RANDOM_PASSWORD);
            user.setPassword(EncryptUtil.encrypt(vstrPassword));
            user.setType(Constants.USER_TYPE);
        } else {
            user.setPassword(c.getPassword());
            user.setType(c.getType());
        }
        if (blnAddOrEdit) {
            for (User u : lstUsers) {
                if (u.getUserName().equalsIgnoreCase(user.getUserName())) {
                    Messagebox.show(Labels.getLabel("user.username.message.error", new String[]{u.getUserName()}), Labels.getLabel("login.change.password.title.message"), Messagebox.OK, Messagebox.INFORMATION);
                    return false;
                }
            }
        }
        user.setStatus(Constants.STATUS_ACTIVE);

        user.setCreateDate(new Date());
        userService.insertOrUpdateUser(user);
        StyleUtils.setDisableComponent(lstCell, 5);
        reloadGrid();
        if (blnAddOrEdit) {
            Messagebox.show(Labels.getLabel("user.add.password.message", new String[]{vstrPassword}), Labels.getLabel("login.change.password.title.message"), Messagebox.OK, Messagebox.INFORMATION);
        }
        return true;
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
                    User c = rowSelected.getValue();
                    c.setStatus(Constants.STATUS_INACTIVE);
                    c.setType(Constants.USER_TYPE);
                    userService.insertOrUpdateUser(c);
                    reloadGrid();
                    if (c.getUserId() == userIdSelect) {
                        if (lstUsers != null && !lstUsers.isEmpty() && lstUsers.get(0) != null) {
                            User role = lstUsers.get(0);
                            userIdSelect = role.getUserId();
                            reloadGridRole(String.valueOf(userIdSelect));
                        }
                    }
                }
            }
        });
    }

    /**
     * Add row
     */
    public void onClick$add() {
        blnAddOrEdit = true;
        User user = new User();
        listDataModel.add(0, user);
        lstUser.setActivePage(com.dvd.ckp.utils.Constants.FIRST_INDEX);
        lstUser.setModel(listDataModel);
        lstUser.renderAll();
        List<Component> lstCell = lstUser.getRows().getChildren().get(0).getChildren();
        StyleUtils.setEnableComponent(lstCell, 5);
    }

    /**
     * Get object customer
     *
     * @param lstCell
     * @return
     */
    private User getDataInRow(List<Component> lstCell) {
        User user = new User();
        Textbox txtUserName = (Textbox) lstCell.get(1).getFirstChild();
        Textbox txtFullName = (Textbox) lstCell.get(2).getFirstChild();
        Textbox txtEmail = (Textbox) lstCell.get(3).getFirstChild();
        Textbox txtPhone = (Textbox) lstCell.get(4).getFirstChild();
        Textbox txtAddress = (Textbox) lstCell.get(5).getFirstChild();
        Textbox txtCard = (Textbox) lstCell.get(6).getFirstChild();

        user.setUserName(txtUserName.getValue());
        user.setFullName(txtFullName.getValue());
        user.setEmail(txtEmail.getValue());
        user.setPhone(txtPhone.getValue());
        user.setAddress(txtAddress.getValue());
        user.setCard(txtCard.getValue());
        return user;
    }

    /**
     * Reload grid
     */
    private void reloadGrid() {
        List<User> vlstUser = userService.getAllUser();
        listDataModel = new ListModelList(vlstUser);
        lstUser.setModel(listDataModel);
        cbxUserFilter.setModel(listDataModel);
    }

    public void onSelect$cbxUserFilter() {
        Long vstrUserId = null;
        if (cbxUserFilter.getSelectedItem() != null) {
            vstrUserId = cbxUserFilter.getSelectedItem().getValue();
        }
        User user = new User();
        String vstrPhone = txtFilterPhone.getValue();
        user.setPhone(vstrPhone);
        user.setUserId(vstrUserId);
        filter(user);
    }

    public void onChange$txtFilterPhone() {
        Long vstrUserId = null;
        if (cbxUserFilter.getSelectedItem() != null) {
            vstrUserId = cbxUserFilter.getSelectedItem().getValue();
        }
        User user = new User();
        String vstrPhone = txtFilterPhone.getValue();
        user.setPhone(vstrPhone);
        user.setUserId(vstrUserId);
        filter(user);
    }

    private void filter(User user) {
        List<User> vlstCustomer = new ArrayList<>();
        if (lstUsers != null && !lstUsers.isEmpty() && user != null) {
            if ((Constants.DEFAULT_ID.equals(user.getUserId()) || user.getUserId() == null) && user.getPhone().equalsIgnoreCase("")) {
                vlstCustomer.addAll(lstUsers);
            } else {
                for (User c : lstUsers) {
                    if ((!Constants.DEFAULT_ID.equals(user.getUserId()) && user.getUserId() != null) && !user.getPhone().equalsIgnoreCase("")) {
                        if (user.getUserId().equals(c.getUserId()) && c.getPhone().toLowerCase().contains(user.getPhone().toLowerCase())) {
                            vlstCustomer.add(c);
                        }
                    } else if ((!Constants.DEFAULT_ID.equals(user.getUserId()) || user.getUserId() != null) && user.getPhone().equalsIgnoreCase("")) {
                        if (user.getUserId().equals(c.getUserId())) {
                            vlstCustomer.add(c);
                        }
                    } else if ((Constants.DEFAULT_ID.equals(user.getUserId()) || user.getUserId() == null) && !user.getPhone().equalsIgnoreCase("")) {
                        if (c.getPhone().toLowerCase().contains(user.getPhone().toLowerCase())) {
                            vlstCustomer.add(c);
                        }
                    }

                }
            }
        }
        listDataModel = new ListModelList(vlstCustomer);
        lstUser.setModel(listDataModel);

    }

    /**
     * Reset passowrd user
     *
     * @param event
     */
    public void onResetPassword(ForwardEvent event) {
        String vstrPassword = RandomStringUtils.random(8, Constants.RESET_RANDOM_PASSWORD);
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        User c = rowSelected.getValue();
        User user = getDataInRow(lstCell);
        user.setUserId(c.getUserId());
        user.setPassword(EncryptUtil.encrypt(vstrPassword));
        user.setStatus(Constants.STATUS_ACTIVE);
        user.setType(Constants.USER_TYPE);
        userService.insertOrUpdateUser(user);
        Messagebox.show(Labels.getLabel("login.change.password.content.message", new String[]{vstrPassword}), Labels.getLabel("login.change.password.title.message"), Messagebox.OK, Messagebox.INFORMATION);
    }

    /**
     * Reload grid Role
     */
    private void reloadGridRole(String userId) {
        List<Role> vlstRole = userService.getRoleByUser(userId, 1);
        listDataModelRole = new ListModelList(vlstRole);
        lstRole.setModel(listDataModelRole);
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
        listDataModelRole = new ListModelList(vlstRole);
        lstRole.setModel(listDataModelRole);

    }

    /**
     * Delate Role
     *
     * @param event
     */
    public void onDeleteRole(ForwardEvent event) {
        Messagebox.show("Bạn có chắc chắn muốn xóa không?", "Xác nhận", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event e) {
                if (Messagebox.ON_YES.equals(e.getName())) {
                    Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
                    Role c = rowSelected.getValue();
                    userService.deleteUserRole(userIdSelect, c.getRoleId());
                    reloadGridRole(String.valueOf(userIdSelect));
                }
            }
        });
    }

    /**
     * Ham reload grid Role by User khi click vao User tren grid User
     *
     * @param event
     */
    public void onViewRoleByUser(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        User c = rowSelected.getValue();
        userIdSelect = c.getUserId();
        reloadGridRole(String.valueOf(c.getUserId()));
    }

    /**
     * Open widow bill detail ol
     *
     * @param event
     */
    public void onClick$addRoleInUser() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("userSelectId", userIdSelect);
        final Window windownUpload = (Window) Executions.createComponents("/manager/include/addRoleUser.zul", mainUser, arguments);
        windownUpload.doModal();
        windownUpload.setBorder(true);
        windownUpload.setBorder("normal");
        windownUpload.setClosable(true);

        windownUpload.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                windownUpload.detach();
                reloadGridRole(String.valueOf(userIdSelect));
            }
        });

    }
}
