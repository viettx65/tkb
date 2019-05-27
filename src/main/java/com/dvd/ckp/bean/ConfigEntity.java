/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.bean;

/**
 *
 * @author admin
 */
public class ConfigEntity {

    private Integer timeOut;
    private String recipient;
    private String mailSend;
    private String password;
    private String content;
    private String title;
    private String attachment;
    private String port;
    private String host;
    private String starttls;
    private String cc;
    private String bcc;

    private String pathUpload;
    private String uploadDocument;

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMailSend() {
        return mailSend;
    }

    public void setMailSend(String mailSend) {
        this.mailSend = mailSend;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getStarttls() {
        return starttls;
    }

    public void setStarttls(String starttls) {
        this.starttls = starttls;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getPathUpload() {
        return pathUpload;
    }

    public void setPathUpload(String pathUpload) {
        this.pathUpload = pathUpload;
    }

    public String getUploadDocument() {
        return uploadDocument;
    }

    public void setUploadDocument(String uploadDocument) {
        this.uploadDocument = uploadDocument;
    }
    
    

    @Override
    public String toString() {
        return "ConfigEntity{" + "timeOut=" + timeOut + ", recipient="
                + recipient + ", mailSend=" + mailSend + ", password="
                + password + ", content=" + content + ", title="
                + title + ", attachment=" + attachment + ", port="
                + port + ", host=" + host + ", starttls="
                + starttls + ", cc=" + cc + ", bcc=" + bcc + '}';
    }

}
