/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.report;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Iframe;

import com.dvd.ckp.birt.BirtConstant;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.DateTimeUtils;

/**
 *
 * @author dmin
 */
public class ReportPerformMonth extends GenericForwardComposer {
    
    private static final Logger logger = Logger.getLogger(ReportPerformMonth.class);
    
    @Wire
    private Iframe ifrReportMonth;
    @Wire
    private Datebox dteFromDate;
    @Wire
    private Datebox dteToDate;
    
    private String reportName = "report_perform.rptdesign";
    private String fileName = "Bao_Cao_Thuc_Hien_Cac_Thang";
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        dteFromDate.setValue(DateTimeUtils.addMonth(-6));
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
        paramMap.put(dteFromDate.getName(), DateTimeUtils.convertDateToString(dteFromDate.getValue(), BirtConstant.PRD_MONTH));
        paramMap.put(dteToDate.getName(), DateTimeUtils.convertDateToString(dteToDate.getValue(), BirtConstant.PRD_MONTH));
        
        return paramMap;
    }
    
    private boolean validateParam() {
        
        if (dteFromDate.getValue() == null) {
            Clients.showNotification(Labels.getLabel("fromdate.empty"), Clients.NOTIFICATION_TYPE_ERROR, dteFromDate, Constants.MESSAGE_POSTION_END_CENTER, Constants.MESSAGE_TIME_CLOSE, Boolean.TRUE);
//            Messagebox.show("Tháng không được để trống", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return false;
        }
        if (dteToDate.getValue() == null) {
            Clients.showNotification(Labels.getLabel("fromdate.empty"), Clients.NOTIFICATION_TYPE_ERROR, dteToDate, Constants.MESSAGE_POSTION_END_CENTER, Constants.MESSAGE_TIME_CLOSE, Boolean.TRUE);
//            Messagebox.show("Tháng không được để trống", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return false;
        }
        
        return true;
    }
    
}
