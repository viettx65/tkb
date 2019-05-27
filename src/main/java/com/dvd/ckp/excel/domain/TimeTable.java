/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.excel.domain;

import com.dvd.ckp.excel.annotation.ExcelColumn;
import com.dvd.ckp.excel.annotation.ExcelEntity;
import java.util.Date;

/**
 *
 * @author viettx
 */
@ExcelEntity(dataStartRowIndex = 14, signalConstant = "TimeTable")
public class TimeTable {

    /**
     * time_id	bigint(20) AI PK special	varchar(2000) num_season	int(11)
     * from_date	varchar(50) to_date	varchar(50) staff_id	bigint(20) status
     * int(11) create_date	date conversion_season	double
     */
    private Long timeID;
    private String special;
    private Integer numSeason;
    private Double coefficient;
    private String fromDate;
    private String staffCode;
    private String toDate;
    private Integer status;
    private Date createDate;
    private Double conversionSeason;

    public Long getTimeID() {
        return timeID;
    }

    public void setTimeID(Long timeID) {
        this.timeID = timeID;
    }

    @ExcelColumn(name = "C", nullable = false)
    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    @ExcelColumn(name = "D", nullable = false)
    public Integer getNumSeason() {
        return numSeason;
    }

    public void setNumSeason(Integer numSeason) {
        this.numSeason = numSeason;
    }

    @ExcelColumn(name = "F", nullable = true)
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    @ExcelColumn(name = "I", nullable = true)
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffID(String staffCode) {
        this.staffCode = staffCode;
    }

    @ExcelColumn(name = "G", nullable = true)
    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return new Date();
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @ExcelColumn(name = "K", nullable = true)
    public Double getConversionSeason() {
        return conversionSeason;
    }

    public void setConversionSeason(Double conversionSeason) {
        this.conversionSeason = conversionSeason;
    }

    @ExcelColumn(name = "E", nullable = true)
    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return "TimeTable{" + "timeID=" + timeID
                + ", special=" + special + ", numSeason="
                + numSeason + ", fromDate="
                + fromDate + ", staffCode="
                + staffCode + ", toDate="
                + toDate + ", status="
                + status + ", createDate="
                + createDate + ", conversionSeason=" + conversionSeason + '}';
    }

}
