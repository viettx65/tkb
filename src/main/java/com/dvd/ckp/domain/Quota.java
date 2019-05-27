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
import javax.persistence.Transient;

import com.dvd.ckp.excel.annotation.ExcelColumn;
import com.dvd.ckp.excel.annotation.ExcelEntity;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.DateTimeUtils;
import com.dvd.ckp.utils.StringUtils;
import java.io.Serializable;

/**
 *
 * @author viettx
 *
 */
@Entity
@Table(name = "sch_quota")
@NamedQueries({
    @NamedQuery(name = "Quota.search", query = "SELECT o FROM Quota o WHERE o.status=1 and  staffID = :staffID and year = :year ORDER BY createDate")
    ,
		@NamedQuery(name = "Quota.getAllQuota", query = "SELECT o FROM Quota o WHERE o.status=1 ORDER BY createDate")})
@ExcelEntity(dataStartRowIndex = 9, signalConstant = "Quota")
public class Quota implements Serializable {

    /**
     * Table: sch_quota Columns: quota_id bigint(20) AI PK staff_id bigint(20)
     * total int(11) lecture int(11) other_work int(11) description varchar(200)
     * status int(11) create_date timestamp year varchar(20)
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quota_id", unique = false, nullable = false)
    private Long quotaID;
    @Column(name = "staff_id", unique = false, nullable = false)
    private String staffCode;
    @Column(name = "total", unique = false, nullable = false)
    private Integer total;
    @Transient
    private String totalValue;
    @Column(name = "lecture", unique = false, nullable = false)
    private Integer lecture;
    @Transient
    private String lectureValue;
    @Column(name = "other_work", unique = false, nullable = false)
    private Integer otherWork;
    @Transient
    private String otherWorkValue;
    @Column(name = "description", unique = false, nullable = true)
    private String description;
    @Column(name = "status", unique = false, nullable = true)
    private Integer status;
    @Column(name = "create_date", unique = false, nullable = true)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "year", unique = false, nullable = false)
    private String year;
    @Transient
    private Date dteYear;
    @Transient
    private int index;
    @Transient
    private String staffName;
    @Transient
    private String error;

    /**
     * @return the quotaID
     */
    public Long getQuotaID() {
        return quotaID;
    }

    /**
     * @param quotaID the quotaID to set
     */
    public void setQuotaID(Long quotaID) {
        this.quotaID = quotaID;
    }

    /**
     * @return the staffCode
     */
    @ExcelColumn(name = "C", nullable = false)
    public String getStaffCode() {
        return staffCode;
    }

    /**
     * @param staffCode the staffCode to set
     */
    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    /**
     * @return the total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    @ExcelColumn(name = "E", nullable = false)
    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    /**
     * @return the lecture
     */
    public Integer getLecture() {
        return lecture;
    }

    /**
     * @param lecture the lecture to set
     */
    public void setLecture(Integer lecture) {
        this.lecture = lecture;
    }

    @ExcelColumn(name = "F", nullable = false)
    public String getLectureValue() {
        return lectureValue;
    }

    public void setLectureValue(String lectureValue) {
        this.lectureValue = lectureValue;
    }

    /**
     * @return the otherWork
     */
    public Integer getOtherWork() {
        return otherWork;
    }

    /**
     * @param otherWork the otherWork to set
     */
    public void setOtherWork(Integer otherWork) {
        this.otherWork = otherWork;
    }

    @ExcelColumn(name = "G", nullable = false)
    public String getOtherWorkValue() {
        return otherWorkValue;
    }

    public void setOtherWorkValue(String otherWorkValue) {
        this.otherWorkValue = otherWorkValue;
    }

    /**
     * @return the description
     */
    @ExcelColumn(name = "H", nullable = true)
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the year
     */
    @ExcelColumn(name = "B", nullable = true)
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the dteYear
     */
    public Date getDteYear() {
        Date vdtTime = new Date();
        if (StringUtils.isValidString(year)) {
            vdtTime = DateTimeUtils.convertStringToTime(year + "0101", Constants.FORMAT_DATE);
        }
        return vdtTime;
    }

    /**
     * @param dteYear the dteYear to set
     */
    public void setDteYear(Date dteYear) {
        this.dteYear = dteYear;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
