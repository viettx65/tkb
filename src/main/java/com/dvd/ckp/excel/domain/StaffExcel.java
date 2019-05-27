package com.dvd.ckp.excel.domain;

import com.dvd.ckp.excel.annotation.ExcelColumn;
import com.dvd.ckp.excel.annotation.ExcelEntity;

@ExcelEntity(dataStartRowIndex = 9, signalConstant = "StaffExcel")
public class StaffExcel {

    private int index;
    private String staffCode;
    private String staffName;
    private String phone;
    private String email;
    private String address;
    private String birthday;
    private String description;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @ExcelColumn(name = "B", nullable = true)
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @ExcelColumn(name = "C", nullable = true)
    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    @ExcelColumn(name = "D", nullable = true)
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @ExcelColumn(name = "E", nullable = true)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ExcelColumn(name = "F", nullable = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ExcelColumn(name = "G", nullable = true)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
