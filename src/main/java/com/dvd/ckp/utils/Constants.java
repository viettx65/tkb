/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.utils;

import com.dvd.ckp.domain.Param;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dmin
 */
public interface Constants {

    //Session
    String USER_TOKEN = "userToken";

    //Page
    String PAGE_HOME = "/index.zul";
    String PAGE_LOGIN = "/login.zul";
    String PAGE_CHANGE_PASSWORD = "/changepassword.zul";
    String PASSWORD_PATTERN = "(?=.*[0-9])([a-z])([A-Z])(?=\\S+$).{8,}";
    String RESET_RANDOM_PASSWORD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    Integer STATUS_INACTIVE = 0;
    Integer STATUS_ACTIVE = 1;
    Long DEFAULT_ID = 0l;
    Long SPECIAL_ID = 9999l;
    Integer FIRST_INDEX = 0;

    //Key param
    String PRAM_OBJECT = "OBJECT";
    String PARAM_DEPARTMENT = "DEPARTMENT";
    String HOC_VI = "HOC_VI";
    public static String PARAM_POSITION = "POSITION";

    Integer USER_ADMIN = 1;
    String ROLE_APPROVE = "approve";

    String[] PARAM_TYPE = {PRAM_OBJECT, PARAM_DEPARTMENT, HOC_VI, PARAM_POSITION};

    Integer USER_TYPE = 0;//0: Nguoi dung, 1: admin

    public static String getParamFromType(String type) {
        String result = "";
        switch (type) {

            case HOC_VI:
                result = "Học vị";
                break;
            case PARAM_DEPARTMENT:
                result = "Đơn vị";
                break;
            case PARAM_POSITION:
                result = "Chức vụ";
                break;
            default:
                break;
        }
        return result;
    }

    public static List<Param> getParamKey() {
        List<Param> vlstParam = new ArrayList<>();
        Param p;
        try {
            for (int i = 0; i < PARAM_TYPE.length; i++) {
                p = new Param();
                p.setParamKey(PARAM_TYPE[i]);
                p.setParamKeyName(getParamFromType(PARAM_TYPE[i]));
                vlstParam.add(p);
            }
        } catch (Exception e) {
        }
        return vlstParam;
    }

    String MESSAGE_POSTION_END_CENTER = "end_center";
    Integer MESSAGE_TIME_CLOSE = 3000;
    public static final String PATH_FILE = "/media/WORK/source/TKB/Source/Schedule/src/main/webapp/";

    public static final String PATH_FILE_UPLOAD = "/media/WORK/source/TKB/Source/Schedule/src/main/webapp/file/upload/";
    public static final String FILE_EXTENSION_XLSX = "xlsx";

    public static final String FORMAT_DATE = "yyyyMMdd";
    public static final String FORMAT_HOUR = "HH:MM:ss";
    public static final String FORMAT_DATE_DDMMYYYY = "ddMMyyyy";
    public static final String FORMAT_DATE_DD_MM_YYY = "dd/MM/yyyy";
    public static final String FORMAT_FULL = "dd/MM/yyyy HH:mm:ss";

    public static final String FORMAT_YYY = "yyyy";

    public static final String TRUE = "true";
    public static final String UTF8 = "UTF-8";
    public static final String HTML_UTF_8 = "text/html; charset=UTF-8";
    public static final String SMTP = "smtp";
    public static final String SMTP_STARTTLS = "mail.smtp.starttls.enable";
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String SMTP_USER = "mail.smtp.user";
    public static final String SMTP_PASSWORD = "mail.smtp.password";
    public static final String SMTP_PORT = "mail.smtp.port";
    public static final String SMTP_AUTH = "mail.smtp.auth";

    public static final String PROPERTIES_TIME = "time.out";
    public static final String PROPERTIES_MAIL = "mail.send";
    public static final String PROPERTIES_PASSWORD = "password";
    public static final String PROPERTIES_CONTENT = "content";
    public static final String PROPERTIES_TITLE = "title";
    public static final String PROPERTIES_ATTACHMENT = "attachment";
    public static final String PROPERTIES_RECIPIENT = "recipient";
    public static final String MAIL_CC = "mail.cc";
    public static final String MAIL_BCC = "mail.bcc";
    
    public static final String XLS = ".xls";
    public static final String XLSX = ".xlsx";
}
