/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author dmin
 */
public class STFCListener extends HttpServlet
        implements ServletContextListener {

    public static ApplicationContext applicationContext;
    private static Logger logger = Logger.getLogger(STFCListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());

            logger.info("Khoi tao Spring application context OK");
        } catch (Throwable ta) {
            logger.info("Khoi tao Spring application context NOT OK", ta);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            logger.info("Shutdown Spring application context OK");
        } catch (Throwable ta) {
            logger.info("Shutdown Spring application context NOT OK", ta);
        }
    }

}
