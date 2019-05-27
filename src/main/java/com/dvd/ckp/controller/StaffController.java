/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.dvd.ckp.business.service.StaffServices;
import com.dvd.ckp.business.service.UtilsService;

import com.dvd.ckp.component.MyListModel;
import com.dvd.ckp.domain.Param;
import com.dvd.ckp.domain.Staff;
import com.dvd.ckp.excel.ExcelReader;
import com.dvd.ckp.excel.ExcelWriter;
import com.dvd.ckp.excel.domain.StaffExcel;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.DateTimeUtils;
import com.dvd.ckp.utils.FileUtils;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;
import com.dvd.ckp.utils.StyleUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author viettx
 */
public class StaffController extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(StaffController.class);
    @WireVariable
    protected StaffServices staffService;

    @WireVariable
    protected UtilsService utilsService;
    @Wire
    private Grid gridStaff;
    @Wire
    private Combobox cbFilterName;
    MyListModel<Staff> listDataStaff;

    private ListModelList<Staff> listDataModel;
    private List<Staff> lstStaff;
    private List<Staff> lstStaffFilter;
    private int insertOrUpdate = 0;

    private Window staff;

    private static final String SAVE_PATH = "/Staff/";

    private Label linkFileName;
    private Textbox hiddenFileName;
    private Textbox hdFileName;

    public Textbox txtTotalRow;
    public Textbox txtTotalRowSucces;
    public Textbox txtTotalRowError;

    public Button btnCancel;
    public Button errorList;
    // public Window uploadPump;
//    private ListModelList<Param> listParamPositionModel;
    private List<Param> lstPosition;
    private List<Param> lstDepartment;

    List<StaffExcel> lstError = new ArrayList<StaffExcel>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lstStaffFilter = new ArrayList<>();
        staffService = (StaffServices) SpringUtil.getBean(SpringConstant.STAFF_SERVICES);
        utilsService = (UtilsService) SpringUtil.getBean(SpringConstant.UTILS_SERVICES);

        lstStaff = new ArrayList<Staff>();
        List<Staff> vlstStaff = staffService.getAllData();
        if (vlstStaff != null) {
            lstStaff.addAll(vlstStaff);
            lstStaffFilter.addAll(vlstStaff);
        }
        listDataModel = new ListModelList<>(lstStaff);
        gridStaff.setModel(listDataModel);
        listDataStaff = new MyListModel<>(lstStaff);
        cbFilterName.setModel(listDataStaff);

        lstDepartment = new ArrayList<>();
        lstDepartment = utilsService.getParamByKey(com.dvd.ckp.utils.Constants.PARAM_POSITION);

        lstPosition = new ArrayList<>();
        lstPosition = utilsService.getParamByKey(com.dvd.ckp.utils.Constants.PARAM_DEPARTMENT);
//        listParamPositionModel = new ListModelList<Param>(lstPosition);
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

    public void onDelete(ForwardEvent event) {
        Messagebox.show(Labels.getLabel("message.confirm.delete.content"),
                Labels.getLabel("message.confirm.delete.title"), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new EventListener() {
            @Override
            public void onEvent(Event e) {
                if (Messagebox.ON_YES.equals(e.getName())) {
                    Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
                    List<Component> lstCell = rowSelected.getChildren();
                    Staff c = rowSelected.getValue();
                    Staff staff = getDataInRow(lstCell);
                    staff.setStaffId(c.getStaffId());
                    staff.setStatus(0);
                    staff.setCreateDate(new Date());
                    staffService.detele(staff);
                    lstStaffFilter.remove(getIndexStaffFilter(c.getStaffId()));
                    lstStaff.remove(getIndexStaff(c.getStaffId()));
                    StyleUtils.setDisableComponent(lstCell, 4);
                    reloadGrid();
                }
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
     * Cancel
     *
     * @param event
     */
    /**
     * Save
     *
     * @param event
     */
    public void onSave(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        Staff c = rowSelected.getValue();
        Staff vstaff = getDataInRow(lstCell);
        vstaff.setStatus(com.dvd.ckp.utils.Constants.STATUS_ACTIVE);
        vstaff.setCreateDate(new Date());
        vstaff.setStaffId(c.getStaffId());
        if (insertOrUpdate == 1) {
            staffService.save(vstaff);
            lstStaffFilter.add(vstaff);
            lstStaff.add(vstaff);
        } else {
            vstaff.setCreateDate(new Date());
            staffService.update(vstaff);
        }
        StyleUtils.setDisableComponent(lstCell, 4);
        reloadGrid();
        insertOrUpdate = 0;
    }

    /**
     * Add row
     */
    public void onAdd(ForwardEvent event) {
        Staff newItem = new Staff();
        newItem.setStatus(1);
        listDataModel.add(0, newItem);
        gridStaff.setActivePage(com.dvd.ckp.utils.Constants.FIRST_INDEX);
        gridStaff.setModel(listDataModel);
        gridStaff.renderAll();
        List<Component> lstCell = gridStaff.getRows().getChildren().get(0).getChildren();
        StyleUtils.setEnableComponent(lstCell, 4);
        insertOrUpdate = 1;
        setDataDefaultInGrid();
    }

    /**
     * Get object customer
     *
     * @param lstCell
     * @return
     */
    private Staff getDataInRow(List<Component> lstCell) {
        Staff staff = new Staff();
        Textbox txtStaffCode = (Textbox) lstCell.get(1).getFirstChild();
        Textbox txtStaffName = (Textbox) lstCell.get(2).getFirstChild();
        Datebox dateBirthday = (Datebox) lstCell.get(3).getFirstChild();
        Textbox txtPhone = (Textbox) lstCell.get(4).getFirstChild();
        Textbox txtEmail = (Textbox) lstCell.get(5).getFirstChild();
        Textbox txtAddress = (Textbox) lstCell.get(6).getFirstChild();

        Combobox cbxPosition = (Combobox) lstCell.get(7).getFirstChild();
        Combobox cbxDepartment = (Combobox) lstCell.get(8).getFirstChild();

        staff.setStaffCode(txtStaffCode.getValue());
        staff.setStaffName(txtStaffName.getValue());
        staff.setPhone(txtPhone.getValue());
        staff.setAddress(txtAddress.getValue());
        staff.setEmail(txtEmail.getValue());
        staff.setBirthday(dateBirthday.getValue());
        staff.setDepartment(cbxDepartment.getSelectedItem().getValue());
        staff.setPosition(cbxPosition.getSelectedItem().getValue());
        return staff;
    }

    /**
     * Reload grid
     */
    private void reloadGrid() {
        List<Staff> vlstData = new ArrayList<Staff>();
        List<Staff> list = staffService.getAllData();
        if (list != null && !list.isEmpty()) {
            vlstData.addAll(list);
        }
        listDataModel = new ListModelList<Staff>(vlstData);
        gridStaff.setModel(listDataModel);
        setDataDefaultInGrid();
    }

    public void onChange$cbFilterName() {
        Staff staff = new Staff();
        Long staffID = null;
        if (cbFilterName.getSelectedItem() != null) {
            staffID = cbFilterName.getSelectedItem().getValue();
        }

        staff.setStaffId(staffID);

        filter(staff);
    }

    private void filter(Staff staff) {
        List<Staff> vlstData = new ArrayList<>();
        if (staff.getStaffId() == null) {
            vlstData.addAll(lstStaff);
            lstStaffFilter.clear();
            lstStaffFilter.addAll(lstStaff);
        } else {
            if (lstStaff != null && !lstStaff.isEmpty()) {
                for (Staff item : lstStaff) {
                    if (staff.getStaffId() != null && staff.getStaffId().equals(item.getStaffId())) {
                        vlstData.add(item);
                        lstStaffFilter.clear();
                        lstStaffFilter.add(item);
                    }
                }
            }
        }
        listDataModel = new ListModelList<Staff>(vlstData);
        gridStaff.setModel(listDataModel);
        setDataDefaultInGrid();
    }

//    public void onExport(Event event) {
//        ExcelWriter<Staff> excelWriter = new ExcelWriter<Staff>();
//        try {
//            int index = 0;
//            for (Staff staff : lstStaffFilter) {
//                index++;
//                staff.setIndex(index);
//                staff.setBirthdayString(
//                        DateTimeUtils.convertDateToString(staff.getBirthday(), Constants.FORMAT_DATE_DD_MM_YYY));
//            }
//            String pathFileInput = Constants.PATH_FILE + "file/template/export/staff_data_export.xlsx";
//            String pathFileOut = Constants.PATH_FILE + "file/export/staff_data_export.xlsx";
//
//            excelWriter.write(lstStaffFilter, pathFileInput, pathFileOut);
//            File file = new File(pathFileOut);
//            Filedownload.save(file, null);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            logger.error(e.getMessage(), e);
//        }
//
//    }
    public void onImport(ForwardEvent event) {

        final Window windownUpload = (Window) Executions.createComponents("/manager/uploadStaff.zul", staff, null);
        windownUpload.doModal();
        windownUpload.setBorder(true);
        windownUpload.setBorder("normal");
        windownUpload.setClosable(true);
        windownUpload.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {
                reloadGrid();

            }
        });
    }

    public void onUpload$uploadbtn(UploadEvent evt) {
        Media media = evt.getMedia();

        if (media == null) {
            Messagebox.show(Labels.getLabel("uploadExcel.selectFile"), Labels.getLabel("ERROR"), Messagebox.OK,
                    Messagebox.ERROR);
            return;
        }
        final String vstrFileName = media.getName();

        FileUtils fileUtils = new FileUtils();
        fileUtils.setKey(SAVE_PATH);
        fileUtils.saveFile(media, session);
        hdFileName.setValue(vstrFileName);
        linkFileName.setValue(vstrFileName);
        hdFileName.setValue(vstrFileName);
        hiddenFileName.setValue(fileUtils.getFilePathOutput());
        oSave();
    }

    public void oSave() {
        int numberSucces = 0;
        int numberRowError = 1;
        try {
            ExcelReader<StaffExcel> reader = new ExcelReader<>();
            String filePath = hiddenFileName.getValue();
            List<StaffExcel> listData = reader.read(filePath, StaffExcel.class);
            List<Staff> vlstData = new ArrayList<>();
            if (listData != null && !listData.isEmpty()) {
                for (StaffExcel staff : listData) {

                    if (!StringUtils.isValidString(staff.getStaffCode())) {
                        staff.setDescription(
                                Labels.getLabel("pump.not.number", new String[]{Labels.getLabel("pump.capacity")}));
                        staff.setIndex(numberRowError);
                        lstError.add(staff);
                        numberRowError++;
                        continue;
                    }

                    if (!StringUtils.isValidString(staff.getStaffName())) {
                        staff.setDescription(
                                Labels.getLabel("pump.not.number", new String[]{Labels.getLabel("pump.capacity")}));
                        staff.setIndex(numberRowError);
                        lstError.add(staff);
                        numberRowError++;
                        continue;
                    }

                    if (!StringUtils.isValidString(staff.getPhone())) {
                        staff.setDescription(
                                Labels.getLabel("pump.not.number", new String[]{Labels.getLabel("pump.capacity")}));
                        staff.setIndex(numberRowError);
                        lstError.add(staff);
                        numberRowError++;
                        continue;
                    }

                    if (!StringUtils.isValidString(staff.getEmail())) {
                        staff.setDescription(
                                Labels.getLabel("pump.not.number", new String[]{Labels.getLabel("pump.capacity")}));
                        staff.setIndex(numberRowError);
                        lstError.add(staff);
                        numberRowError++;
                        continue;
                    }
                    Staff item = new Staff();
                    item.setStaffCode(staff.getStaffCode());
                    item.setStaffName(staff.getStaffName());
                    item.setPhone(staff.getPhone());
                    item.setEmail(staff.getEmail());
                    item.setBirthday(DateTimeUtils.convertStringToTime(replateBirthDay(staff.getBirthday()),
                            Constants.FORMAT_DATE_DDMMYYYY));
                    item.setAddress(staff.getAddress());
                    item.setStatus(1);
                    vlstData.add(item);
                    numberSucces++;
                }
            }
            txtTotalRow.setValue(String.valueOf(vlstData.size()));
            txtTotalRowSucces.setValue(String.valueOf(numberSucces));

            if (lstError != null && !lstError.isEmpty()) {
                errorList.setVisible(true);
                txtTotalRowError.setValue(String.valueOf(lstError.size()));
            }
            staffService.importData(vlstData);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void onDownloadFile(ForwardEvent event) {
        try {
            String pathFileInput = Constants.PATH_FILE + "file/template/import/import_staff_data.xlsx";

            File file = new File(pathFileInput);
            Filedownload.save(file, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
    }

    public void onDownloadFileError(ForwardEvent event) {
        ExcelWriter<StaffExcel> writer = new ExcelWriter<>();
        try {
            String pathFileOutput = Constants.PATH_FILE + "file/export/error/error_staff_data.xlsx";
            String pathFileInput = Constants.PATH_FILE + "file/template/error/error_staff_data.xlsx";
            Map<String, Object> beans = new HashMap<>();
            beans.put("data", lstError);
            writer.write(beans, pathFileInput, pathFileOutput);
            File file = new File(pathFileOutput);
            Filedownload.save(file, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
    }

    private String replateBirthDay(String value) {
        return value.replace("/", "");
    }

    private int getIndexStaff(Long staffID) {
        if (lstStaff != null && !lstStaff.isEmpty()) {
            for (Staff staff : lstStaff) {
                if (staffID.equals(staff.getStaffId())) {
                    return lstStaff.indexOf(staff);
                }
            }
        }
        return -1;
    }

    private int getIndexStaffFilter(Long staffID) {
        if (lstStaffFilter != null && !lstStaffFilter.isEmpty()) {
            for (Staff staff : lstStaffFilter) {
                if (staffID.equals(staff.getStaffId())) {
                    return lstStaffFilter.indexOf(staff);
                }
            }
        }
        return -1;
    }

    private void setDataDefaultInGrid() {
        gridStaff.renderAll();
        List<Component> lstRows = gridStaff.getRows().getChildren();
        if (lstRows != null && !lstRows.isEmpty()) {
            for (int i = 0; i < lstRows.size(); i++) {
                Staff staff = listDataModel.get(i);
                Component row = lstRows.get(i);
                List<Component> lstCell = row.getChildren();
                setComboboxPositionParam(lstCell, getParamPositionDefault(staff.getPosition(), 7), 7);
                setComboboxParam(lstCell, getParamDefault(staff.getDepartment(), 8), 8);
            }
        }
    }

    // Ham nay tach lam 2 
    private List<Param> getParamDefault(Long paramValue, int type) {
        List<Param> paramSelected = new ArrayList<>();
        List<Param> lstParam = null;
        lstParam = lstPosition;
        if (paramValue != null && lstParam != null && !lstParam.isEmpty()) {
            for (Param vParam : lstParam) {
                if (paramValue.equals(vParam.getParamValue())) {
                    paramSelected.add(vParam);
                    break;
                }
            }
        }
        return paramSelected;
    }

    private List<Param> getParamPositionDefault(Long paramValue, int type) {
        List<Param> paramSelected = new ArrayList<>();
        List<Param> lstParam = null;
        lstParam = lstDepartment;
        if (paramValue != null && lstParam != null && !lstParam.isEmpty()) {
            for (Param vParam : lstParam) {
                if (paramValue.equals(vParam.getParamValue())) {
                    paramSelected.add(vParam);
                    break;
                }
            }
        }
        return paramSelected;
    }

    // Ham nay tach lam 2 ko swthi gi ca
    private void setComboboxParam(List<Component> lstCell, List<Param> selectedIndex, int columnIndex) {
        Combobox cbxParam = null;
        Component component = lstCell.get(columnIndex).getFirstChild();
        List<Param> lstParam = null;
        lstParam = lstPosition;
        if (component != null && component instanceof Combobox) {
            cbxParam = (Combobox) component;
            MyListModel listDataModelParam = new MyListModel(lstParam);
            listDataModelParam.setSelection(selectedIndex);
            cbxParam.setModel(listDataModelParam);
//            cbxParam.setTooltiptext(selectedIndex.get(com.dvd.ckp.utils.Constants.FIRST_INDEX).getParamName());
        }
    }

    private void setComboboxPositionParam(List<Component> lstCell, List<Param> selectedIndex, int columnIndex) {
        Combobox cbxParam = null;
        Component component = lstCell.get(columnIndex).getFirstChild();
        List<Param> lstParam = null;

        lstParam = lstDepartment;

        if (component != null && component instanceof Combobox) {
            cbxParam = (Combobox) component;
            MyListModel listDataModelParam = new MyListModel(lstParam);
            listDataModelParam.setSelection(selectedIndex);
            cbxParam.setModel(listDataModelParam);
//            cbxParam.setTooltiptext(selectedIndex.get(com.dvd.ckp.utils.Constants.FIRST_INDEX).getParamName());
        }
    }
}
