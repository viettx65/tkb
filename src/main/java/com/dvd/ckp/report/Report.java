/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.ListModelList;

import com.dvd.ckp.birt.BirtConstant;
import com.dvd.ckp.business.service.StaffServices;
import com.dvd.ckp.domain.Staff;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.DateTimeUtils;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;

/**
 *
 * @author dmin
 */
public class Report extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(Report.class);
    @WireVariable
    protected StaffServices staffServices;

    private ListModelList<Staff> listDataModel;
    private List<Staff> lstStaff;

    public ListModelList<Staff> getListDataModel() {
        return listDataModel;
    }

    public void setListDataModel(ListModelList<Staff> listDataModel) {
        this.listDataModel = listDataModel;
    }

    @Wire
    private Iframe ifrReportMonth;
    @Wire
    private Datebox dteFromDate;
    @Wire
    private Combobox cbStaff;

    private final String reportName = "report.rptdesign";
    private final String fileName = "Bao_Cao_Khoi_Luong_Giang_Vien";

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        staffServices = (StaffServices) SpringUtil.getBean(SpringConstant.STAFF_SERVICES);

        lstStaff = new ArrayList<>();
        List<Staff> vlstStaff = staffServices.getAllData();
        if (vlstStaff != null) {
            lstStaff.addAll(vlstStaff);
        }
        listDataModel = new ListModelList<>(lstStaff);
        cbStaff.setModel(listDataModel);
    }

    public void onClick$btnHtml() {
        if (!validateParam()) {
            return;
        }
        HashMap paramMap = getParam();
        StringBuilder src = buildUrl(paramMap);
        ifrReportMonth.setSrc(src.toString());

    }

    public void onClick$btnExcel() {
        if (!validateParam()) {
            return;
        }

        HashMap paramMap = getParam();
        paramMap.put(BirtConstant.PARAM_EXTENSION, BirtConstant.EXCEL_EXTENSION);
        paramMap.put(BirtConstant.PARAM_FILE_NAME, fileName);
        StringBuilder src = buildUrl(paramMap);
        ifrReportMonth.setSrc(src.toString());
    }

    private StringBuilder buildUrl(HashMap paramMap) {

        StringBuilder src = new StringBuilder(BirtConstant.BIRT_SERVLET);
        for (Object key : paramMap.keySet()) {
            src.append(key);
            src.append(BirtConstant.BIRT_EQUAL);
            src.append(paramMap.get(key));
            src.append(BirtConstant.BIRT_AND);
        }
        src.append(BirtConstant.PARAM_TIME);
        src.append(BirtConstant.BIRT_EQUAL);
        src.append(System.currentTimeMillis());
        return src;
    }

    private HashMap getParam() {
        HashMap paramMap = new HashMap();
        paramMap.put(BirtConstant.PARAM_REPORT, reportName);
        paramMap.put(dteFromDate.getName(), DateTimeUtils.convertDateToString(dteFromDate.getValue(), BirtConstant.FORMAT_YEAR));
        if (StringUtils.isValidString(cbStaff.getValue()) && !Labels.getLabel("option").equals(cbStaff.getValue())) {
            paramMap.put(cbStaff.getName(), cbStaff.getSelectedItem().getValue());
        }
        return paramMap;
    }

    private boolean validateParam() {

        if (dteFromDate.getValue() == null) {
            Clients.showNotification(Labels.getLabel("schedule.year.error"), Clients.NOTIFICATION_TYPE_ERROR, dteFromDate, Constants.MESSAGE_POSTION_END_CENTER, Constants.MESSAGE_TIME_CLOSE, Boolean.TRUE);
//            Messagebox.show("Tháng không được để trống", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return false;
        }
        if (!StringUtils.isValidString(cbStaff.getValue()) || Labels.getLabel("option").equals(cbStaff.getValue())) {
            Clients.showNotification(Labels.getLabel("schedule.staff.error"), Clients.NOTIFICATION_TYPE_ERROR, cbStaff, Constants.MESSAGE_POSTION_END_CENTER, Constants.MESSAGE_TIME_CLOSE, Boolean.TRUE);
            return false;
        }
        return true;
    }

}
