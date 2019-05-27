/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author viettx
 */
@Entity
@Table(name = "sch_class")
@NamedQueries({
    @NamedQuery(name = "SClass.getAll", query = "SELECT o FROM SClass o ORDER BY createDate")})
public class SClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_code", unique = true, nullable = false)
    private Long classCode;
    @Column(name = "class_name", unique = true, nullable = false)
    private String className;
    @Column(name = "staff_id", unique = true, nullable = false)
    private Long staff;
    @Column(name = "from_date", unique = true, nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fromDate;
    @Column(name = "to_date", unique = true, nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date toDate;
    @Column(name = "status", unique = true, nullable = false)
    private int status;
    @Column(name = "create_date", unique = true, nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "address", unique = true, nullable = false)
    private String address;

    public Long getClassCode() {
        return classCode;
    }

    public void setClassCode(Long classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getStaff() {
        return staff;
    }

    public void setStaff(Long staff) {
        this.staff = staff;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
