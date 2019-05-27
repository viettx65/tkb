/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import com.dvd.ckp.business.service.ClassService;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import com.dvd.ckp.business.service.StaffServices;

import com.dvd.ckp.domain.SClass;
import com.dvd.ckp.domain.Staff;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;
import com.dvd.ckp.utils.StyleUtils;
import java.util.Date;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 *
 * @author viettx
 */
public class ClassController extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(ClassController.class);
    @WireVariable
    protected StaffServices staffService;

    @WireVariable
    protected ClassService classService;

    @Wire
    private Grid gridClass;
    @Wire
    private Textbox txtClassNameFilter;

    private ListModelList<SClass> listDataModel;
    private List<SClass> lstClass;
    private List<SClass> lstClassFilter;
    private List<Staff> lstStaff;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lstClassFilter = new ArrayList<>();
        staffService = (StaffServices) SpringUtil.getBean(SpringConstant.STAFF_SERVICES);

        classService = (ClassService) SpringUtil.getBean(SpringConstant.CLASS_SERVICES);

        lstClass = new ArrayList<>();
        List<SClass> vlstClass = classService.getAllClass();
        if (vlstClass != null) {
            lstClass.addAll(vlstClass);
            lstClassFilter.addAll(vlstClass);
        }
        listDataModel = new ListModelList<>(lstClass);
        gridClass.setModel(listDataModel);
        lstStaff = staffService.getAllData();
        setDataDefaultInGrid();
    }

    /**
     * Edit row
     *
     * @param event
     */
    public void onEdit(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        StyleUtils.setEnableComponent(lstCell, 4);
    }

    /**
     *
     * @param event
     */
    public void onDelete(ForwardEvent event) {
        Messagebox.show(Labels.getLabel("message.confirm.delete.content"),
                Labels.getLabel("message.confirm.delete.title"), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, (Event e) -> {
            if (Messagebox.ON_YES.equals(e.getName())) {
                Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
                List<Component> lstCell = rowSelected.getChildren();
                SClass c = rowSelected.getValue();
                SClass cl = getDataInRow(lstCell);
                cl.setClassCode(c.getClassCode());
                cl.setCreateDate(new Date());

                classService.delete(cl);
                lstClassFilter.remove(getIndexFilter(c.getClassCode()));
                lstClass.remove(getIndex(c.getClassCode()));
                StyleUtils.setDisableComponent(lstCell, 4);
                reloadGrid();
            }
        });

    }

    /**
     * Cancel
     *
     * @param event
     */
    public void onCancel(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        StyleUtils.setDisableComponent(lstCell, 4);
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
        SClass c = rowSelected.getValue();
        SClass cl = getDataInRow(lstCell);
        cl.setStatus(com.dvd.ckp.utils.Constants.STATUS_ACTIVE);
        cl.setCreateDate(new Date());
        cl.setClassCode(c.getClassCode());
        cl.setCreateDate(new Date());
        classService.save(cl);
        lstClassFilter.add(cl);
        lstClass.add(cl);

        StyleUtils.setDisableComponent(lstCell, 4);
        reloadGrid();
    }

    /**
     *
     * @param event
     */
    public void onAdd(ForwardEvent event) {
        SClass newItem = new SClass();
        newItem.setStatus(1);
        listDataModel.add(0, newItem);
        gridClass.setActivePage(com.dvd.ckp.utils.Constants.FIRST_INDEX);
        gridClass.setModel(listDataModel);
        gridClass.renderAll();
        List<Component> lstCell = gridClass.getRows().getChildren().get(0).getChildren();
        StyleUtils.setEnableComponent(lstCell, 4);
        setDataDefaultInGrid();
    }

    /**
     *
     * @param lstCell
     * @return
     */
    private SClass getDataInRow(List<Component> lstCell) {
        SClass c = new SClass();
        Textbox txtClassName = (Textbox) lstCell.get(1).getFirstChild();
        Textbox txtAddress = (Textbox) lstCell.get(2).getFirstChild();
        Combobox cbxStaff = (Combobox) lstCell.get(3).getFirstChild();
        Datebox dtFromDate = (Datebox) lstCell.get(4).getFirstChild();
        Datebox dtToDate = (Datebox) lstCell.get(5).getFirstChild();

        c.setClassName(txtClassName.getValue().trim());
        c.setAddress(txtAddress.getValue().trim());
        c.setFromDate(dtFromDate.getValue());
        c.setToDate(dtToDate.getValue());
        c.setStaff(cbxStaff.getSelectedItem().getValue());
        return c;
    }

    /**
     * Reload grid
     */
    private void reloadGrid() {
        List<SClass> vlstData = new ArrayList<>();
        List<SClass> list = classService.getAllClass();
        if (list != null && !list.isEmpty()) {
            vlstData.addAll(list);
        }
        listDataModel = new ListModelList<>(vlstData);
        gridClass.setModel(listDataModel);
        setDataDefaultInGrid();
    }

    /**
     *
     */
    public void onChange$txtClassNameFilter() {
        SClass c = new SClass();
        String className = txtClassNameFilter.getValue().trim();
        c.setClassName(className);
        filter(c);

    }

    public void enterSearch() {
        txtClassNameFilter.addEventListener(Events.ON_OK, new EnterSearchListener());
    }

    class EnterSearchListener implements EventListener<Event> {

        @Override
        public void onEvent(Event t) throws Exception {
            SClass c = new SClass();
            String className = txtClassNameFilter.getValue().trim();
            c.setClassName(className);
            filter(c);
        }

    }

    private void filter(SClass c) {
        List<SClass> vlstData = new ArrayList<>();
        if (!StringUtils.isValidString(c.getClassName())) {
            vlstData.addAll(lstClass);
            lstClassFilter.clear();
            lstClassFilter.addAll(lstClass);
        } else {
            if (lstClass != null && !lstClass.isEmpty()) {
                lstClass.stream().filter((item) -> (item.getClassName().matches(".*" + c.getClassName() + ".*"))).map((item) -> {
                    vlstData.add(item);
                    return item;
                }).map((item) -> {
                    lstClassFilter.clear();
                    return item;
                }).forEachOrdered((item) -> {
                    lstClassFilter.add(item);
                });
            }
        }
        listDataModel = new ListModelList<>(vlstData);
        gridClass.setModel(listDataModel);
        setDataDefaultInGrid();
    }

    /**
     *
     * @param id
     * @return
     */
    private int getIndex(Long id) {
        if (lstClass != null && !lstClass.isEmpty()) {
            for (SClass c : lstClass) {
                if (id.equals(c.getClassCode())) {
                    return lstClass.indexOf(c);
                }
            }
        }
        return -1;
    }

    /**
     *
     * @param id
     * @return
     */
    private int getIndexFilter(Long id) {
        if (lstClassFilter != null && !lstClassFilter.isEmpty()) {
            for (SClass c : lstClassFilter) {
                if (id.equals(c.getClassCode())) {
                    return lstClassFilter.indexOf(c);
                }
            }
        }
        return -1;
    }

    private void setDataDefaultInGrid() {
        gridClass.renderAll();
        List<Component> lstRows = gridClass.getRows().getChildren();
        if (lstRows != null && !lstRows.isEmpty()) {
            for (int i = 0; i < lstRows.size(); i++) {
                SClass c = listDataModel.get(i);
                Component row = lstRows.get(i);
                List<Component> lstCell = row.getChildren();
                setComboboxStaff(lstCell, getStaffDefault(c.getStaff()), 3);
            }
        }
    }

    /**
     *
     * @param id
     * @return
     */
    private List<Staff> getStaffDefault(Long staff) {
        List<Staff> staffSelected = new ArrayList<>();
        List<Staff> listStaff = lstStaff;

        if (staff != null && listStaff != null && !listStaff.isEmpty()) {
            for (Staff item : listStaff) {
                if (staff.equals(item.getStaffId())) {
                    staffSelected.add(item);
                    break;
                }
            }
        }
        return staffSelected;
    }

    /**
     *
     * @param lstCell
     * @param s
     * @param columnIndex
     */
    private void setComboboxStaff(List<Component> lstCell, List<Staff> selectedIndex, int columnIndex) {
        Combobox cbxStaff;
        Component component = lstCell.get(columnIndex).getFirstChild();
        List<Staff> listStaff = lstStaff;

        if (component != null && component instanceof Combobox) {
            cbxStaff = (Combobox) component;
            ListModelList listDataModelParam = new ListModelList(listStaff);
            listDataModelParam.setSelection(selectedIndex);
            cbxStaff.setModel(listDataModelParam);
        }
    }

}
