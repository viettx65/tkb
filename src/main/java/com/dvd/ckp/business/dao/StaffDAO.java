package com.dvd.ckp.business.dao;

import java.util.List;

import com.dvd.ckp.domain.Staff;

public interface StaffDAO {

    public List<Staff> getData();
    
    public Staff getStaffByID(String staffCode);

    public void save(Staff staff);

    public int update(Staff staff);

    public int delete(Staff staff);

    public void importData(List<Staff> vlstData);



}
