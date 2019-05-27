package com.dvd.ckp.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dvd.ckp.business.dao.StaffDAO;
import com.dvd.ckp.domain.Staff;

public class StaffSeviceImpl implements StaffServices {

    @Autowired
    StaffDAO staffDAO;

    @Override
    public List<Staff> getAllData() {
        // TODO Auto-generated method stub
        return staffDAO.getData();
    }

    @Override
    public void save(Staff staff) {
        // TODO Auto-generated method stub
        staffDAO.save(staff);

    }

    @Override
    public int update(Staff staff) {
        // TODO Auto-generated method stub
        return staffDAO.update(staff);
    }

    @Override
    public int detele(Staff staff) {
        // TODO Auto-generated method stub
        return staffDAO.delete(staff);
    }

    @Override
    public void importData(List<Staff> staff) {
        // TODO Auto-generated method stub
        staffDAO.importData(staff);

    }

    @Override
    public Staff getStaffByID(String staffCode) {
        return staffDAO.getStaffByID(staffCode);
    }

    @Override
    public void delete(Long billDetailID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
