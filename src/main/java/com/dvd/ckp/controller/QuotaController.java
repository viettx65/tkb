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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import com.dvd.ckp.business.service.QuotaService;
import com.dvd.ckp.business.service.StaffServices;

import com.dvd.ckp.domain.Quota;
import com.dvd.ckp.domain.Staff;
import com.dvd.ckp.excel.ExcelReader;
import com.dvd.ckp.excel.ExcelWriter;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.DateTimeUtils;
import com.dvd.ckp.utils.FileUtils;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;
import com.dvd.ckp.utils.StyleUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zul.A;

/**
 *
 * @author viettx
 * @since 01/06/2018
 * @version 1.0
 */
public class QuotaController extends GenericForwardComposer<Component> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(QuotaController.class);
    @WireVariable
    protected StaffServices staffService;

    @WireVariable
    protected QuotaService quotaService;

    @Wire
    private Grid gridQuota;
    @Wire
    private Combobox cbFilterName;
    @Wire
    private Datebox year;
    @Wire
    private Checkbox show;
    ListModelList<Staff> listDataStaff;

    private ListModelList<Quota> listDataModel;
    private List<Quota> lstQuota;
    private List<Quota> lstQuotaFilter;
    private int insertOrUpdate = 0;

    private static final String SAVE_PATH = "/Quota/";

    private static final String EXPORT_TEMP = "quota_data.xlsx";
    private static final String EXPORT_NAME = "Danhsachbanghi.xlsx";

    private static final String IMPORT_PATH = "file/template/import/import_quota.xlsx";

    private static final String ERROR_PATH = "error_quota_data.xlsx";
    private static final String ERROR_PATH_TEMP = "file/template/error/temp/";
    private static final String ERROR_FILE_NAME = "Danhsachloi.xlsx";
    @Wire
    private Label linkFileName;
    @Wire
    private Textbox hiddenFileName;
    @Wire
    private Textbox hdFileName;
    @Wire
    private A btnSave;
    @Wire
    private A btnCancel;
    @Wire
    private Label title;
    @Wire
    private A linkError;

    private List<Staff> listStaff;

    List<Quota> listImport;
    List<Quota> listError;
    List<Quota> listSucces;
    private boolean isShow = false;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lstQuotaFilter = new ArrayList<>();

        quotaService = (QuotaService) SpringUtil.getBean(SpringConstant.QUOTA_SERVICES);
        staffService = (StaffServices) SpringUtil.getBean(SpringConstant.STAFF_SERVICES);

        lstQuota = new ArrayList<>();
        listStaff = staffService.getAllData();
        listDataStaff = new ListModelList<>(listStaff);
        cbFilterName.setModel(listDataStaff);

        listError = new ArrayList<>();
        listSucces = new ArrayList<>();

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
                Labels.getLabel("message.confirm.delete.title"), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new EventListener() {
            @Override
            public void onEvent(Event e) {
                if (Messagebox.ON_YES.equals(e.getName())) {
                    Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
                    List<Component> lstCell = rowSelected.getChildren();
                    Quota c = rowSelected.getValue();
//                    Quota quota = getDataInRow(lstCell);
//                    quota.setQuotaID(c.getQuotaID());
                    c.setStatus(0);
                    c.setCreateDate(new Date());
                    if (isShow) {
                        quotaService.save(c);
                    } else {
                        listSucces.remove(rowSelected.getIndex());
                    }
                    reloadGrid();
                    StyleUtils.setDisableComponent(lstCell, 4);
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
     * Save
     *
     * @param event
     */
    public void onSave(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        Quota c = rowSelected.getValue();
        Quota quota = getDataInRow(lstCell);
        quota.setStatus(com.dvd.ckp.utils.Constants.STATUS_ACTIVE);
        quota.setCreateDate(new Date());
        quota.setQuotaID(c.getQuotaID());
        quota.setTotalValue(String.valueOf(quota.getTotal()));
        quota.setLectureValue(String.valueOf(quota.getLecture()));
        quota.setOtherWorkValue(String.valueOf(quota.getOtherWork()));
        if (isShow) {
            if (insertOrUpdate == 1) {
                lstQuota.add(quota);
            } else {
                quota.setCreateDate(new Date());
            }
            quotaService.save(quota);
        } else {
            if (insertOrUpdate == 1) {
                listSucces.add(quota);
            } else {
                listSucces.set(rowSelected.getIndex(), quota);
            }
        }

        StyleUtils.setDisableComponent(lstCell, 4);
        reloadGrid();
        insertOrUpdate = 0;
    }

    /**
     *
     * @param event
     */
    public void onAdd(ForwardEvent event) {
        Quota newItem = new Quota();
        newItem.setStatus(1);
        listDataModel.add(0, newItem);
        gridQuota.setActivePage(com.dvd.ckp.utils.Constants.FIRST_INDEX);
        gridQuota.setModel(listDataModel);
        gridQuota.renderAll();
        List<Component> lstCell = gridQuota.getRows().getChildren().get(0).getChildren();
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
    private Quota getDataInRow(List<Component> lstCell) {
        Quota quota = new Quota();
        Datebox dtyear = (Datebox) lstCell.get(1).getFirstChild();
        Combobox cbStaff = (Combobox) lstCell.get(2).getFirstChild();
        Textbox txtTotal = (Textbox) lstCell.get(3).getFirstChild();
        Textbox txtLecture = (Textbox) lstCell.get(4).getFirstChild();
        Textbox txtOtherWork = (Textbox) lstCell.get(5).getFirstChild();
        Textbox txtDescription = (Textbox) lstCell.get(6).getFirstChild();

        quota.setYear(DateTimeUtils.convertDateToString(dtyear.getValue(), Constants.FORMAT_YYY));
        quota.setTotal(Integer.valueOf(txtTotal.getValue()));
        quota.setLecture(Integer.valueOf(txtLecture.getValue()));
        quota.setOtherWork(Integer.valueOf(txtOtherWork.getValue()));
        quota.setDescription(txtDescription.getValue());
        quota.setStaffCode(cbStaff.getSelectedItem().getValue());

        return quota;
    }

    /**
     * Reload grid
     */
    private void reloadGrid() {
        List<Quota> vlstData = new ArrayList<>();
        lstQuotaFilter.clear();
        if (isShow) {
            Quota quota = new Quota();
            List<Quota> list = quotaService.onSearch(quota);
            if (list != null && !list.isEmpty()) {
                for (Quota q : list) {
                    q.setTotalValue(String.valueOf(q.getTotal()));
                    q.setLectureValue(String.valueOf(q.getLecture()));
                    q.setOtherWorkValue(String.valueOf(q.getOtherWork()));
                }
                vlstData.addAll(list);
                lstQuota.addAll(list);
                lstQuotaFilter.addAll(list);
            }
        } else {
            vlstData.addAll(listSucces);
            lstQuotaFilter.addAll(listSucces);
        }
        listDataModel = new ListModelList<>(vlstData);
        gridQuota.setModel(listDataModel);
        setDataDefaultInGrid();
    }

    /**
     *
     */
    public void onChange$cbFilterName() {
        Quota quota = new Quota();
        String staffCode = null;
        if (cbFilterName.getSelectedItem() != null) {
            staffCode = cbFilterName.getSelectedItem().getValue();
        }
        String date = null;
        if (year.getValue() != null) {
            date = DateTimeUtils.convertDateToString(year.getValue(), Constants.FORMAT_YYY);
        }
        quota.setStaffCode(staffCode);
        quota.setYear(date);
        filter(quota);
    }

    /**
     *
     */
    public void onChange$year() {
        Quota quota = new Quota();
        String staffCode = null;
        if (cbFilterName.getSelectedItem() != null) {
            staffCode = cbFilterName.getSelectedItem().getValue();
        }
        String date = null;
        if (year.getValue() != null) {
            date = DateTimeUtils.convertDateToString(year.getValue(), Constants.FORMAT_YYY);
        }
        quota.setStaffCode(staffCode);
        quota.setYear(date);
        filter(quota);
    }

    /**
     *
     * @param quota
     */
    private void filter(Quota quota) {
        List<Quota> vlstData = new ArrayList<>();
        List<Quota> lst = new ArrayList<>();

        lstQuotaFilter.clear();
        if (isShow) {
            lst.clear();
            lst.addAll(lstQuota);
        } else {
            lst.clear();
            lst.addAll(listImport);
        }
        if (quota.getYear() == null && !StringUtils.isValidString(quota.getStaffCode())) {
            vlstData.addAll(lst);
            lstQuotaFilter.addAll(lst);
        } else {
            if (!lst.isEmpty()) {
                for (Quota item : lst) {
                    if (StringUtils.isValidString(quota.getStaffCode())
                            && quota.getStaffCode().equals(item.getStaffCode())) {
                        vlstData.add(item);
                        lstQuotaFilter.add(item);
                    }
                    if (quota.getYear() != null && quota.getYear().equals(item.getYear())) {
                        vlstData.add(item);
                        lstQuotaFilter.add(item);
                    }
                }
            }
        }
        listDataModel = new ListModelList<>(vlstData);
        gridQuota.setModel(listDataModel);
        setDataDefaultInGrid();
    }

    /**
     *
     * @param event
     */
    public void onCancelUpload(ForwardEvent event) {
        List<Quota> vlstData = new ArrayList<>();
        listDataModel = new ListModelList<>(vlstData);
        gridQuota.setModel(listDataModel);
        setDataDefaultInGrid();
        btnCancel.setVisible(false);
        btnSave.setVisible(false);
        title.setVisible(false);
        linkError.setLabel("");
        linkFileName.setValue("");
    }

    /**
     *
     * @param event
     */
    public void onExport(ForwardEvent event) {
        ExcelWriter<Quota> excelWriter = new ExcelWriter<>();
        try {
            int index = 0;
            for (Quota quota : lstQuotaFilter) {
                index++;
                quota.setIndex(index);
                quota.setStaffName(getStaffName(quota.getStaffCode()));
            }
            String pathFileInput = session.getWebApp().getRealPath("file/template/export/") + EXPORT_TEMP;
            String pathFileOut = session.getWebApp().getRealPath("file/export/") + EXPORT_TEMP;
            excelWriter.setFileOutName(EXPORT_NAME);
            Map<String, Object> beans = new HashMap<>();
            beans.put("data", lstQuotaFilter);
            beans.put("day", DateTimeUtils.getTime(new Date(), 1));
            beans.put("month", DateTimeUtils.getTime(new Date(), 2));
            beans.put("year", DateTimeUtils.getTime(new Date(), 3));
            excelWriter.write(beans, pathFileInput, pathFileOut);
            File file = new File(excelWriter.getFileExport());
            Filedownload.save(file, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }

    }

    /**
     *
     * @param event
     */
    public void onExportError(ForwardEvent event) {
        ExcelWriter<Quota> excelWriter = new ExcelWriter<>();
        try {
            int index = 0;
            for (Quota quota : listError) {
                index++;
                quota.setIndex(index);
                quota.setStaffName(getStaffName(quota.getStaffCode()));
            }
            String pathFileInput = session.getWebApp().getRealPath("file/template/error/") + ERROR_PATH;
            String pathFileOut = session.getWebApp().getRealPath("file/template/error/temp/") + ERROR_PATH;
            excelWriter.setFileOutName(ERROR_FILE_NAME);
            Map<String, Object> beans = new HashMap<>();
            beans.put("data", listError);
            beans.put("day", DateTimeUtils.getTime(new Date(), 1));
            beans.put("month", DateTimeUtils.getTime(new Date(), 2));
            beans.put("year", DateTimeUtils.getTime(new Date(), 3));
            excelWriter.write(beans, pathFileInput, pathFileOut);
            File file = new File(excelWriter.getFileExport());
            Filedownload.save(file, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }

    }

    /**
     *
     * @param staffCode
     * @return
     */
    String getStaffName(String staffCode) {
        if (listStaff != null && !listStaff.isEmpty()) {
            for (Staff item : listStaff) {
                if (StringUtils.isValidString(staffCode)
                        && staffCode.equals(item.getStaffCode())) {
                    return item.getStaffName();
                }
            }
        }
        return "";
    }

    /**
     *
     * @param evt
     */
    public void onUpload$uploadbtn(UploadEvent evt) {
        Media media = evt.getMedia();

        if (media == null) {
            Messagebox.show(Labels.getLabel("uploadExcel.selectFile"), Labels.getLabel("ERROR"), Messagebox.OK,
                    Messagebox.ERROR);
            return;
        }
        final String vstrFileName = media.getName();
        try {

            FileUtils fileUtils = new FileUtils();
            fileUtils.setKey(SAVE_PATH);
            fileUtils.saveFile(media, session);
            hdFileName.setValue(vstrFileName);
            linkFileName.setValue(vstrFileName);
            hiddenFileName.setValue(fileUtils.getFilePathOutput());
            ExcelReader<Quota> reader = new ExcelReader<>();
            listImport = reader.read(fileUtils.getFilePathOutput(), Quota.class);
            validateImport();
            listDataModel = new ListModelList<>(listSucces);
            gridQuota.setModel(listDataModel);
            btnCancel.setVisible(true);
            btnSave.setVisible(true);
            setDataDefaultInGrid();
            lstQuotaFilter.clear();
            lstQuotaFilter.addAll(listSucces);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     */
    private void validateImport() {
        int numberError = 0;
        listError.clear();
        listSucces.clear();
        if (listImport != null && !listImport.isEmpty()) {
            for (Quota item : listImport) {
                if (!StringUtils.isValidString(item.getYear())) {
                    item.setError(Labels.getLabel("schedule.error.year"));
                    listError.add(item);
                    continue;
                }
                if (!StringUtils.regexNumber(item.getYear())) {
                    item.setError(Labels.getLabel("schedule.error.number", new String[]{Labels.getLabel("schedule.year")}));
                    listError.add(item);
                    continue;
                }
                if (!StringUtils.isValidString(item.getStaffCode())) {
                    item.setError(Labels.getLabel("schedule.error.staff"));
                    listError.add(item);
                    continue;
                }
                if (!isStaffExist(listStaff, item.getStaffCode())) {
                    item.setError(Labels.getLabel("schedule.error.staff.code.exist"));
                    listError.add(item);
                    continue;
                }
                if (!StringUtils.isValidString(item.getTotalValue())) {
                    item.setError(Labels.getLabel("schedule.error.total"));
                    listError.add(item);
                    continue;
                }
                if (!StringUtils.regexNumber(item.getTotalValue())) {
                    item.setError(Labels.getLabel("schedule.error.number", new String[]{Labels.getLabel("scheudle.quota")}));
                    listError.add(item);
                    continue;
                }
                if (!StringUtils.isValidString(item.getLectureValue())) {
                    item.setError(Labels.getLabel("schedule.error.lecture"));
                    listError.add(item);
                    continue;
                }
                if (!StringUtils.regexNumber(item.getLectureValue())) {
                    item.setError(Labels.getLabel("schedule.error.number", new String[]{Labels.getLabel("schedule.quota.lecture")}));
                    listError.add(item);
                    continue;
                }
                if (!StringUtils.isValidString(item.getOtherWorkValue())) {
                    item.setError(Labels.getLabel("schedule.error.otherWork"));
                    listError.add(item);
                    continue;
                }
                if (!StringUtils.regexNumber(item.getOtherWorkValue())) {
                    item.setError(Labels.getLabel("schedule.error.number", new String[]{Labels.getLabel("schedule.quota.otherWork")}));
                    listError.add(item);
                    continue;
                }
                listSucces.add(item);
            }
            numberError = listError.size();
            if (numberError != 0) {
                title.setVisible(true);
                title.setValue(Labels.getLabel("schedule.error.title", new String[]{"" + numberError}));
                linkError.setVisible(true);
                linkError.setLabel(ERROR_FILE_NAME);
            }
        }
    }

    /**
     *
     * @param event
     */
    public void onSaveImport(ForwardEvent event) {
        Messagebox.show(Labels.getLabel("schedule.quota.upload.comfirm"), Labels.getLabel("comfirm"),
                Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            @Override
            public void onEvent(Event e) {
                if (Messagebox.ON_YES.equals(e.getName())) {
                    try {
                        Quota quota = new Quota();
                        List<Quota> listData = quotaService.onSearch(quota);
                        List<Quota> lstUpdate = new ArrayList<>();
                        List<Quota> lstImport = new ArrayList<>();
                        if (listSucces != null && !listSucces.isEmpty()) {
                            int numberSucces;
                            for (Quota item : listSucces) {
                                item.setStatus(Constants.STATUS_ACTIVE);
                                item.setCreateDate(new Date());
                                Quota q = getQuota(item, listData);
                                if (q != null) {
                                    q.setTotal(Integer.valueOf(item.getTotalValue()));
                                    q.setLecture(Integer.valueOf(item.getLectureValue()));
                                    q.setOtherWork(Integer.valueOf(item.getOtherWorkValue()));
                                    lstUpdate.add(q);
                                } else {
                                    lstImport.add(item);
                                }
                                item.setTotal(Integer.valueOf(item.getTotalValue()));
                                item.setLecture(Integer.valueOf(item.getLectureValue()));
                                item.setOtherWork(Integer.valueOf(item.getOtherWorkValue()));
                            }
                            numberSucces = listSucces.size();
                            quotaService.importData(lstUpdate);
                            quotaService.importData(lstImport);
                            btnCancel.setVisible(false);
                            btnSave.setVisible(false);
                            title.setValue("");
                            linkError.setLabel("");
                            linkFileName.setValue("");
//                            int numRow = numberSucces;
//                            if (numberError == 0) {
                            Messagebox.show(Labels.getLabel("schedule.quota.upload.sucess", new String[]{"" + numberSucces}), Labels.getLabel("comfirm"), Messagebox.OK,
                                    Messagebox.INFORMATION);
//                            } else {
//                                Messagebox.show(Labels.getLabel("schedule.quota.upload.sucess.with.error", new String[]{"" + numberTotal,
//                                    "" + numberSucces, "" + numberError}), Labels.getLabel("comfirm"), Messagebox.OK,
//                                        Messagebox.INFORMATION);
//                            }

                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        });

    }

    /**
     *
     *
     * @param quota
     * @param listData
     * @return
     */
    private Quota getQuota(Quota quota, List<Quota> listData) {
        if (listData != null && !listData.isEmpty()) {
            for (Quota item : listData) {
                if (quota.getYear().equals(item.getYear())
                        && quota.getStaffCode().equals(item.getStaffCode())) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param event
     */
    public void onDownloadFile(ForwardEvent event) {
        try {
            String pathFileInput = session.getWebApp().getRealPath(IMPORT_PATH);

            File file = new File(pathFileInput);
            Filedownload.save(file, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
    }

    public void onFileError(ForwardEvent event) {
        try {

            ExcelWriter<Quota> excelWriter = new ExcelWriter<>();

            int index = 0;
            for (Quota quota : lstQuotaFilter) {
                index++;
                quota.setIndex(index);
                quota.setStaffName(getStaffName(quota.getStaffCode()));
            }
            String pathFileInput = session.getWebApp().getRealPath(ERROR_PATH);
            String pathFileOut = session.getWebApp().getRealPath(ERROR_PATH_TEMP) + ERROR_FILE_NAME;
            Map<String, Object> beans = new HashMap<>();
            beans.put("data", listError);
            beans.put("day", DateTimeUtils.getTime(new Date(), 1));
            beans.put("month", DateTimeUtils.getTime(new Date(), 2));
            beans.put("year", DateTimeUtils.getTime(new Date(), 3));
            excelWriter.write(beans, pathFileInput, pathFileOut);
            File file = new File(pathFileOut);
            Filedownload.save(file, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     */
    private void setDataDefaultInGrid() {
        gridQuota.renderAll();
        List<Component> lstRows = gridQuota.getRows().getChildren();
        for (int i = 0; i < lstRows.size(); i++) {
            Quota quota = listDataModel.get(i);
            Component row = lstRows.get(i);
            List<Component> lstCell = row.getChildren();
            setComboboxStaff(lstCell, getStaffDefault(quota.getStaffCode(), 2), 2);

        }

    }

    /**
     *
     * @param staff
     * @param type
     * @return
     */
    private List<Staff> getStaffDefault(String staff, int type) {
        List<Staff> staffSelected = new ArrayList<>();
        List<Staff> lstStaff = listStaff;

        if (staff != null && lstStaff != null && !lstStaff.isEmpty()) {
            for (Staff item : lstStaff) {
                if (staff.equals(item.getStaffCode())) {
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
     * @param selectedIndex
     * @param columnIndex
     */
    private void setComboboxStaff(List<Component> lstCell, List<Staff> selectedIndex, int columnIndex) {
        Combobox cbxStaff = null;
        Component component = lstCell.get(columnIndex).getFirstChild();
        List<Staff> lstStaff = listStaff;

        if (component != null && component instanceof Combobox) {
            cbxStaff = (Combobox) component;
            ListModelList listDataModelParam = new ListModelList(lstStaff);
            listDataModelParam.setSelection(selectedIndex);
            cbxStaff.setModel(listDataModelParam);
        }
    }

    /**
     *
     */
    public void onCheck$show() {
        if (show.isChecked()) {

            isShow = true;

        } else {
            isShow = false;
            List<Quota> vlstData = new ArrayList<>();
            if (listSucces != null && !listSucces.isEmpty()) {
                vlstData.addAll(listSucces);
            }
            listDataModel = new ListModelList<>(vlstData);
            gridQuota.setModel(listDataModel);
            setDataDefaultInGrid();
        }
        reloadGrid();
    }

    private boolean isStaffExist(List<Staff> listStaff, String staffCode) {
        if (listStaff != null && !listStaff.isEmpty()) {
            for (Staff item : listStaff) {
                if (staffCode.equals(item.getStaffCode())) {
                    return true;
                }
            }
        }
        return false;
    }
}
