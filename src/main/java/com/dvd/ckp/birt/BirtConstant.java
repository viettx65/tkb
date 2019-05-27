/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.birt;

/**
 *
 * @author dmin
 */
public interface BirtConstant {

    String PARAM_REPORT = "reportName";
    String PARAM_FILE_NAME = "fileName";
    String PARAM_EXTENSION = "extension";
    String PARAM_TIME = "systime";
    String EXCEL_EXTENSION = "xlsx";
    String BIRT_IMAGES = "/BIRT/images";
    String BIRT_REPORTS = "/BIRT/reports";
    String EXCEL_EMITTER_ID = "uk.co.spudsoft.birt.emitters.excel.XlsxEmitter";
    String EXCEL_EMITTER_OPTION = "ExcelEmitter.SingleSheet";

    String BIRT_SERVLET = "BirtServlet?";
    String BIRT_EQUAL = "=";
    String BIRT_AND = "&";
    String PRD_ID = "yyyyMMdd";
    String PRD_MONTH = "yyyyMM";
    String FORMAT_YEAR = "yyyy";
    String FORMAT_MONTH = "MM/yyyy";

    Integer TYPE_ANY = 0;
    Integer TYPE_STRING = 1;
    Integer TYPE_FLOAT = 2;
    Integer TYPE_DECIMAL = 3;
    Integer TYPE_DATE_TIME = 4;
    Integer TYPE_BOOLEAN = 5;
    Integer TYPE_INTEGER = 6;
    Integer TYPE_DATE = 7;
    Integer TYPE_TIME = 8;

    Integer TYPE_PARAM = 1;
    Integer TYPE_DISPLAY_TEXT = 2;

    String PARAM_SEPERATOR = ":_:";

}
