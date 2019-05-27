package com.dvd.ckp.business.service;

import java.util.List;

import com.dvd.ckp.domain.Staff;

public interface StaffServices {

    public List<Staff> getAllData();

    public Staff getStaffByID(String staffCode);

    public void save(Staff staff);

    public int update(Staff staff);

    public int detele(Staff staff);

    public void importData(List<Staff> staff);

    public void delete(Long billDetailID);

}
