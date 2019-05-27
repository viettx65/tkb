/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import com.schedule.email.Mail;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author viettx
 */
public class MailController extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(MailController.class);
    @Wire
    private Window sentEmail;
    @Wire
    private Textbox txtMailSend;
    @Wire
    private Textbox txtRecipient;
    @Wire
    private Textbox txtContent;
    @Wire
    private Textbox txtSubject;
    @Wire
    private Textbox txtPass;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

    }

    public void onSendMail(ForwardEvent event) {
        Mail mail = new Mail();
        boolean isSend = mail.sendMail(txtMailSend.getValue(), txtRecipient.getValue(),
                txtContent.getValue(), txtSubject.getValue(), txtPass.getValue());
        if (isSend) {
            Messagebox.show(Labels.getLabel("send.success", new String[]{txtRecipient.getValue()}), Labels.getLabel("comfirm"), Messagebox.OK,
                    Messagebox.INFORMATION);
        } else {
            Messagebox.show(Labels.getLabel("send.error"), Labels.getLabel("error"), Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    public void onCancel(ForwardEvent event) {
        sentEmail.detach();
    }
}
