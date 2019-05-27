/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import com.dvd.ckp.excel.annotation.ExcelColumn;
import com.dvd.ckp.excel.annotation.ExcelEntity;

/**
 *
 * @author viettx
 */
@Entity
@Table(name = "sch_schedule")
@NamedQueries({ @NamedQuery(name = "Schedule.getAllSchedule", query = "SELECT o FROM Quota o ORDER BY createDate") })
@ExcelEntity(dataStartRowIndex = 17, signalConstant = "Schedule")
public class Schedule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Table: sch_schedule Columns: id bigint(20) AI PK thematic varchar(500) lesson
	 * int(11) coefficient double from_date varchar(50) to_date varchar(50) staff_id
	 * bigint(20) create_date date class text d_from_date date d_to_date date day
	 * varchar(20) month varchar(20) year varchar(20) sch_group int(11)
	 *
	 */
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	@Column(name = "thematic", unique = true, nullable = false)
	private String thematic;
	@Column(name = "lesson", unique = false, nullable = false)
	private Double lesson;
	@Column(name = "coefficient", unique = false, nullable = false)
	private Double coefficient;
	@Column(name = "from_date", unique = false, nullable = false)
	private String fromDate;
	@Column(name = "to_date", unique = false, nullable = true)
	private String toDate;
	@Column(name = "d_from_date", unique = false, nullable = false)
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date fromDateValue;
	@Column(name = "d_to_date", unique = false, nullable = true)
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date toDateValue;
	@Column(name = "day", unique = false, nullable = false)
	private String day;
	@Column(name = "month", unique = false, nullable = false)
	private String month;
	@Column(name = "year", unique = false, nullable = false)
	private String year;
	@Column(name = "class", unique = false, nullable = false)
	private String classValue;
	@Column(name = "create_date", unique = false, nullable = false)
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date createDate;
	@Column(name = "staff_id", unique = false, nullable = false)
	private String staffCode;
	@Column(name = "sch_group", unique = false, nullable = false)
	private Integer group;
	@Transient
	private Double total;
	@Transient
	private int index;
	@Transient
	private String staffName;
	@Transient
	private String error;
	@Transient
	private String dayValue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ExcelColumn(name = "C", nullable = false)
	public String getThematic() {
		return thematic;
	}

	public void setThematic(String thematic) {
		this.thematic = thematic;
	}

	@ExcelColumn(name = "D", nullable = false)
	public Double getLesson() {
		return lesson;
	}

	public void setLesson(Double lesson) {
		this.lesson = lesson;
	}

	@ExcelColumn(name = "H", nullable = false)
	public Double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}

	@ExcelColumn(name = "E", nullable = false)
	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	@ExcelColumn(name = "G", nullable = false)
	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	@ExcelColumn(name = "F", nullable = false)
	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Date getFromDateValue() {
		return fromDateValue;
	}

	public void setFromDateValue(Date fromDateValue) {
		this.fromDateValue = fromDateValue;
	}

	public Date getToDateValue() {
		return toDateValue;
	}

	public void setToDateValue(Date toDateValue) {
		this.toDateValue = toDateValue;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getClassValue() {
		return classValue;
	}

	public void setClassValue(String classValue) {
		this.classValue = classValue;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Double getTotal() {
		total = lesson * coefficient;
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDayValue() {
		return dayValue;
	}

	public void setDayValue(String dayValue) {
		this.dayValue = dayValue;
	}

}
