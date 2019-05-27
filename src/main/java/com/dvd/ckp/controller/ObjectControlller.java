/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import com.dvd.ckp.business.service.ObjectService;
import com.dvd.ckp.business.service.UtilsService;
import com.dvd.ckp.domain.Object;
import com.dvd.ckp.domain.Param;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StyleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

/**
 *
 * @author daond
 */
public class ObjectControlller extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(ObjectControlller.class);
    @WireVariable
    protected ObjectService objectService;
    @WireVariable
    protected UtilsService utilsService;
    @Wire
    private Grid lstObject;
    ListModelList<Object> listDataModel;
    private List<Object> lstObjects;

    @Wire
    private Combobox cbxObjectFilter;

    Param defaultParam;
    List<Param> lstType;

    private final int indexObjectCode = 1;
    private final int indexObjectName = 2;
    private final int indexObjectType = 3;
    private final int indexobjectPath = 3;
    private final int indexObjectParentId = 4;
    private final int indexObjectIcon = 5;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        objectService = (ObjectService) SpringUtil.getBean(SpringConstant.OBJECT_SERVICES);
        utilsService = (UtilsService) SpringUtil.getBean(SpringConstant.UTILS_SERVICES);
        lstObjects = new ArrayList<>();
        List<Object> vlstUser = objectService.getAllObject();
        if (vlstUser != null) {
            lstObjects.addAll(vlstUser);
        }
        listDataModel = new ListModelList(lstObjects);
        lstObject.setModel(listDataModel);
        cbxObjectFilter.setModel(listDataModel);

        defaultParam = new Param();
        defaultParam.setParamValue(Constants.DEFAULT_ID);
        defaultParam.setParamName(Labels.getLabel("option"));

        lstType = utilsService.getParamByKey(Constants.PRAM_OBJECT);
        if (lstType == null) {
            lstType = new ArrayList<>();
        }
        lstType.add(Constants.FIRST_INDEX, defaultParam);
    }

    /**
     * Edit row
     *
     * @param event
     */
    public void onEdit(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        Object object = rowSelected.getValue();
        setComboboxParam(lstCell, getParamDefault(object.getObjectType(), indexObjectType), indexObjectType);
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
    public boolean onSave(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        Object c = rowSelected.getValue();
        Object object = getDataInRow(lstCell);
        object.setObjectId(c.getObjectId());

        object.setStatus(1);
        object.setCreateDate(new Date());
        objectService.insertOrUpdateObject(object);
        StyleUtils.setDisableComponent(lstCell, 5);
        reloadGrid();

        return true;
    }

    /**
     * Delate
     *
     * @param event
     */
    public void onDelete(ForwardEvent event) {
        Messagebox.show("Bạn có chắc chắn muốn xóa không?", "Xác nhận", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event e) {
                if (Messagebox.ON_YES.equals(e.getName())) {
                    Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
                    Object c = rowSelected.getValue();
                    c.setStatus(Constants.STATUS_INACTIVE);
                    objectService.insertOrUpdateObject(c);
                    reloadGrid();
                }
            }
        });
    }

    /**
     * Add row
     */
    public void onClick$add() {
        Object object = new Object();
        listDataModel.add(0, object);
        lstObject.setActivePage(Constants.FIRST_INDEX);
        lstObject.setModel(listDataModel);
        lstObject.renderAll();
        List<Component> lstCell = lstObject.getRows().getChildren().get(0).getChildren();
        setDataDefaultInGrid();
        StyleUtils.setEnableComponent(lstCell, 5);
    }

    /**
     * Get object customer
     *
     * @param lstCell
     * @return
     */
    private Object getDataInRow(List<Component> lstCell) {
        Object object = new Object();
        Component component;
        Textbox txtObjectCode;
        Textbox txtObjectName;
        Textbox txtPath;
        Textbox txtIcon;
        Combobox cbxObjectType = null;
        component = lstCell.get(indexObjectCode).getFirstChild();
        if (component != null && component instanceof Textbox) {
            txtObjectCode = (Textbox) component;
            object.setObjectCode(txtObjectCode.getValue());
        }
        component = lstCell.get(indexObjectName).getFirstChild();
        if (component != null && component instanceof Textbox) {
            txtObjectName = (Textbox) component;
            object.setObjectName(txtObjectName.getValue());
        }
        component = lstCell.get(indexObjectType).getFirstChild();
        if (component != null && component instanceof Combobox) {
            cbxObjectType = (Combobox) component;
            object.setObjectType(cbxObjectType.getSelectedItem().getValue());
        }
        component = lstCell.get(indexobjectPath).getFirstChild();
        if (component != null && component instanceof Textbox) {
            txtPath = (Textbox) component;
            object.setPath(txtPath.getValue());
        }
        component = lstCell.get(indexObjectParentId).getFirstChild();
        if (component != null && component instanceof Textbox) {
//            txtCustomerAddress = (Textbox) component;
            object.setParentId(cbxObjectType.getSelectedItem().getValue());
        }
        component = lstCell.get(indexObjectIcon).getFirstChild();
        if (component != null && component instanceof Textbox) {
            txtIcon = (Textbox) component;
            object.setIcon(txtIcon.getValue());
        }
        return object;
    }

    /**
     * Reload grid
     */
    private void reloadGrid() {
        List<Object> vlstObject = objectService.getAllObject();
        listDataModel = new ListModelList(vlstObject);
        lstObject.setModel(listDataModel);
        cbxObjectFilter.setModel(listDataModel);
        setDataDefaultInGrid();
    }

    public void onSelect$cbxObjectFilter() {
        Long vstrObjectId = null;
        if (cbxObjectFilter.getSelectedItem() != null) {
            vstrObjectId = cbxObjectFilter.getSelectedItem().getValue();
        }
        filter(vstrObjectId);
    }

    private void filter(Long pstrCustomerId) {
        List<Object> vlstCustomer = new ArrayList<>();
        if (lstObjects != null && !lstObjects.isEmpty()) {
            if (pstrCustomerId == null) {
                vlstCustomer.addAll(lstObjects);
            } else {
                for (Object o : lstObjects) {
                    if (pstrCustomerId.equals(o.getObjectId())) {
                        vlstCustomer.add(o);
                    }
                }
            }
        }
        listDataModel = new ListModelList(vlstCustomer);
        lstObject.setModel(listDataModel);
    }

    private void setDataDefaultInGrid() {
        lstObject.renderAll();
        List<Component> lstRows = lstObject.getRows().getChildren();
        if (lstRows != null && !lstRows.isEmpty()) {
            for (int i = 0; i < lstRows.size(); i++) {
                Object object = listDataModel.get(i);
                Component row = lstRows.get(i);
                List<Component> lstCell = row.getChildren();
                setComboboxParam(lstCell, getParamDefault(object.getObjectType(), indexObjectType), indexObjectType);
            }
        }
    }

    private List<Param> getParamDefault(Long paramValue, int type) {
        List<Param> paramSelected = new ArrayList<>();
        List<Param> lstParam = null;
        switch (type) {
            case indexObjectType:
                lstParam = lstType;
                break;
            default:
                break;
        }
        if (paramValue != null && lstParam != null && !lstParam.isEmpty()) {
            for (Param vParam : lstParam) {
                if (paramValue.equals(vParam.getParamValue())) {
                    paramSelected.add(vParam);
                    break;
                }
            }
        }
        if (paramSelected.isEmpty()) {
            paramSelected.add(defaultParam);
        }
        return paramSelected;
    }

    private void setComboboxParam(List<Component> lstCell, List<Param> selectedIndex, int columnIndex) {
        Combobox cbxParam = null;
        Component component = lstCell.get(columnIndex).getFirstChild();
        List<Param> lstParam = null;
        switch (columnIndex) {
            case indexObjectType:
                lstParam = lstType;
                break;
            default:
                break;
        }
        if (component != null && component instanceof Combobox) {
            cbxParam = (Combobox) component;
            ListModelList listDataModelParam = new ListModelList(lstParam);
            listDataModelParam.setSelection(selectedIndex);
            cbxParam.setModel(listDataModelParam);
            cbxParam.setTooltiptext(selectedIndex.get(Constants.FIRST_INDEX).getParamName());
        }
    }
}
