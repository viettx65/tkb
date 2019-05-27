/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.utils;

import com.dvd.ckp.listener.STFCListener;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author dmin
 */
public class SpringUtils {

    public static ApplicationContext getAppCxt() {
        return STFCListener.applicationContext;
    }

    public static Object getBean(String bname) {
        return getAppCxt().getBean(bname);
    }

    public static <T> T getBean(String bname, Class<T> type) {
        return (T) getAppCxt().getBean(bname, type);
    }

}
