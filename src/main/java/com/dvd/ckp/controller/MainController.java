/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import com.dvd.ckp.bean.UserToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.West;

import com.dvd.ckp.business.service.UserService;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.domain.Object;
import com.dvd.ckp.utils.DateTimeUtils;
import com.dvd.ckp.utils.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkmax.zul.Nav;
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.A;

/**
 *
 * @author viettx
 */
public class MainController extends SelectorComposer<Component> {

    private static final Logger logger = Logger.getLogger(MainController.class);

    @WireVariable
    protected UserService userService;
    @Wire
    Tabbox tabContent;
    @Wire
    Tabs tabs;
    @Wire
    Tabpanels tabpanels;
    @Wire
    Navbar treeMenu;

    @Wire
    Div divListFunction;

    @Wire
    West westMenu;
    @Wire
    Div showHideMenue;
    @Wire
    Span userName;
    private Session session;
    private List<Tab> lstTabs;
    private final int limitTabs = 100;

    @Wire
    Label txtUsername;
    @Wire
    Label txtFullName;
    @Wire
    Label txtLastLogin;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        session = Sessions.getCurrent();
        if (session.getAttribute(Constants.USER_TOKEN) == null) {
            Executions.sendRedirect(Constants.PAGE_LOGIN);
        } else {
            UserToken userToken = (UserToken) session.getAttribute(Constants.USER_TOKEN);
            if (userToken != null) {
                userName.appendChild(new Label(userToken.getFullName()));
                createMenu(userToken);
                txtUsername.setValue(userToken.getUserName());
                txtFullName.setValue(userToken.getFullName());
                if (userToken.getModifiedDate() != null) {
                    txtLastLogin.setValue(DateTimeUtils.convertDateToString(userToken.getModifiedDate(), Constants.FORMAT_FULL));
                }
//                createMenuHome(userToken);
            }
        }
        lstTabs = new ArrayList<>();
    }

    private void createMenu(UserToken userToken) {
        if (userToken != null) {
            List<Object> lstAllObjects = userToken.getListObject();
            List<Object> lstRootMenu = getListRootMenu(lstAllObjects);
            if (lstRootMenu != null && !lstRootMenu.isEmpty()) {

                Nav treeChildrenRoot = new Nav();
                treeChildrenRoot.setLabel(Labels.getLabel("schedule.management"));
                treeChildrenRoot.setIconSclass("z-icon-dashboard");
                treeChildrenRoot.setOpen(true);

                treeChildrenRoot.setParent(treeMenu);
                for (Object rootMenu : lstRootMenu) {
                    if (rootMenu != null) {
                        Nav itemRoot = new Nav();
                        // itemRoot.setOpen(false);
                        itemRoot.setId(rootMenu.getObjectCode());
                        itemRoot.setLabel(Labels.getLabel(rootMenu.getObjectName()));

                        // Add click event
                        List<Object> lstChilds = getListChild(lstAllObjects, rootMenu.getObjectId());
                        if (lstChilds != null && !lstChilds.isEmpty()) {
                            if (!itemRoot.isOpen()) {
                                itemRoot.setIconSclass("z-icon-folder-o");
                            } else {
                                itemRoot.setIconSclass("z-icon-folder-open-o");
                            }
                            itemRoot.setParent(treeChildrenRoot);
                            addMenuItem(lstAllObjects, lstChilds, itemRoot);
                        }

                    }
                }
            }
        }
    }

    private void addMenuItem(List<Object> lstAllObjects, List<Object> lstObjectChilds, Nav itemParent) {
        if (lstObjectChilds != null && !lstObjectChilds.isEmpty()) {
            lstObjectChilds.stream().filter((itemMenu) -> (itemMenu != null)).map((itemMenu) -> {
                Navitem item = new Navitem();
                item.setId(itemMenu.getPath());
                item.setLabel(Labels.getLabel(itemMenu.getObjectName()));
                // Add click event
                item.addEventListener(Events.ON_CLICK, new NavOnClickListener());
                item.setParent(itemParent);
                List<Object> lstChilds = getListChild(lstAllObjects, itemMenu.getObjectId());
                return lstChilds;
            }).filter((lstChilds) -> (lstChilds != null && !lstChilds.isEmpty())).forEachOrdered((lstChilds) -> {
                addMenuItem(lstAllObjects, lstChilds, itemParent);
            });
        }
    }

    private List<Object> getListRootMenu(List<Object> lstObjects) {
        List<Object> lstRootMenu = new ArrayList<>();
        if (lstObjects != null && !lstObjects.isEmpty()) {
            lstObjects.stream().filter((object) -> (object.getParentId() == null && object.getObjectType() == 1L)).forEachOrdered((object) -> {
                lstRootMenu.add(object);
            });
        }
        return lstRootMenu;
    }

    private List<Object> getListChild(List<Object> lstObjects, Long parentId) {
        List<Object> lstChilds = new ArrayList<>();
        if (lstObjects != null && !lstObjects.isEmpty()) {
            lstObjects.stream().filter((object) -> (parentId.equals(object.getParentId()) && object.getObjectType() == 1L)).forEachOrdered((object) -> {
                lstChilds.add(object);
            });
        }
        return lstChilds;
    }

    class NavOnClickListener implements EventListener {

        @Override
        public void onEvent(Event event) {
            try {
                List<Component> list = treeMenu.getChildren();
                List<Component> listSub = list.get(0).getChildren();
                if (listSub != null && !listSub.isEmpty()) {
                    for (Component item : listSub) {
                        if (item instanceof Nav) {
                            Nav nav = (Nav) item;
//                            nav.setOpen(false);
                            nav.setIconSclass("z-icon-folder-o");
                        }
                    }
                }
                Navitem treeitem = (Navitem) event.getTarget();
                Nav itemParent = (Nav) treeitem.getParent();
                itemParent.setIconSclass("z-icon-folder-open-o");
                String strUrl = treeitem.getId();
                String strId = "tab_" + treeitem.getId();
                String strTabName = treeitem.getLabel();
                if (StringUtils.isValidString(strUrl)) {
                    addTab(strUrl, strId, strTabName);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    class MenuHomeOnClickListener implements EventListener {

        @Override
        public void onEvent(Event event) {
            try {
                A menu = (A) event.getTarget();
                String strUrl = String.valueOf(menu.getAttribute("MENU_PATH"));
                String strId = "tab_" + String.valueOf(menu.getAttribute("MENU_ID"));
                String strTabName = String.valueOf(menu.getAttribute("MENU_NAME"));
                if (StringUtils.isValidString(strUrl)) {
                    addTab(strUrl, strId, strTabName);
                }

            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Listen("onClick = #logout")
    public void logout() throws IOException {
        session.invalidate();
        Executions.sendRedirect(Constants.PAGE_LOGIN);
    }

    @Listen("onClick = #showHideMenue")
    public void showHideMenue() throws IOException {
        westMenu.setOpen(!westMenu.isOpen());
    }

    private void addTab(String pstrURL, final String pstrId, String pstrTablName) {
        Include contentTabMenu;
        if (lstTabs.size() < limitTabs) {
            Tab newTab = getExistTab(pstrId, lstTabs);
            if (newTab == null) {
                newTab = new Tab(pstrTablName);
                newTab.setTooltiptext(pstrTablName);
                newTab.setId(pstrId);
                newTab.setClosable(true);
                newTab.setSelected(true);
                newTab.setParent(tabs);
                newTab.addEventListener("onClose", new EventListener() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        removeTab(pstrId);
                    }
                });
                lstTabs.add(newTab);
                Tabpanel tp = new Tabpanel();
                contentTabMenu = new Include();
                contentTabMenu.setSrc(pstrURL);
                contentTabMenu.setParent(tp);
                tp.setParent(tabpanels);
            }
            newTab.setSelected(true);
        }
    }

    private Tab getExistTab(String idNewTab, List<Tab> tabs) {

        if (tabs != null && tabs.size() > 0) {
            for (int i = 0; i < tabs.size(); i++) {
                String idTabi = tabs.get(i).getId();
                if (idNewTab.equalsIgnoreCase(idTabi)) {
                    return tabs.get(i);
                }
            }

        }

        return null;

    }

    private void removeTab(String closeId) {
        if (lstTabs != null && lstTabs.size() > 0) {
            for (int i = 0; i < lstTabs.size(); i++) {
                String idTabi = lstTabs.get(i).getId();
                if (closeId.equalsIgnoreCase(idTabi)) {
                    lstTabs.remove(lstTabs.get(i));
                }
            }

        }
    }

}
