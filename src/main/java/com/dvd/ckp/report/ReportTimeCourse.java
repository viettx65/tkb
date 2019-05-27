/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.report;

import com.dvd.ckp.bean.UserToken;
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
import java.util.Map;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 *
 * @author dmin
 */
public class ReportTimeCourse extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(ReportTimeCourse.class);

    @Wire
    private Iframe ifrReportMonth;

    @Wire
    private Datebox dteFromDate;
    @Wire
    private Window reportTimeCourse;
//    @Wire
//    private Datebox dteToDate;

    UserToken userToken;

    private String reportName = "report_time_course.rptdesign";
    private String fileName = "BaoCaoKhoiLuongGiangDay";

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        session = Sessions.getCurrent();

        userToken = (UserToken) session.getAttribute(Constants.USER_TOKEN);

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
//        paramMap.put(dteFromDate.getName(), dteFromDate.getValue());
//        paramMap.put(dteToDate.getName(), DateTimeUtils.convertDateToString(dteToDate.getValue(), BirtConstant.PRD_ID));
//        paramMap.put(dteToDate.getName(), dteToDate.getValue());
        return paramMap;
    }

    private boolean validateParam() {

        if (dteFromDate.getValue() == null) {
            Clients.showNotification(Labels.getLabel("schedule.error.fromdate"), Clients.NOTIFICATION_TYPE_ERROR, dteFromDate, Constants.MESSAGE_POSTION_END_CENTER, Constants.MESSAGE_TIME_CLOSE, Boolean.TRUE);
//            Messagebox.show("Tháng không được để trống", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return false;
        }

//        if (dteToDate.getValue() == null) {
//            Clients.showNotification(Labels.getLabel("schedule.error.todate"), Clients.NOTIFICATION_TYPE_ERROR, dteToDate, Constants.MESSAGE_POSTION_END_CENTER, Constants.MESSAGE_TIME_CLOSE, Boolean.TRUE);
////            Messagebox.show("Tháng không được để trống", "Lỗi", Messagebox.OK, Messagebox.ERROR);
//            return false;
//        }
        return true;
    }

    public void onSendMail(ForwardEvent event) {

        Map<String, Object> arguments = new HashMap();
        arguments.put("email", userToken.getEmail());
        final Window windownUpload = (Window) Executions.createComponents("/manager/include/sentMail.zul",
                reportTimeCourse, arguments);
        windownUpload.doModal();
        windownUpload.setBorder(true);
        windownUpload.setBorder("normal");
        windownUpload.setClosable(true);
    }

}
