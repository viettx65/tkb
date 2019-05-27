package com.dvd.ckp.business.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dvd.ckp.domain.Staff;

@Repository
public class StaffDAOImpl implements StaffDAO {

    private static final Logger logger = Logger.getLogger(StaffDAOImpl.class);
    @Autowired
    SessionFactory sessionFactory;

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Staff> getData() {
        // TODO Auto-generated method stub
        try {
            Query query = getCurrentSession().getNamedQuery("Staff.getAllStaff");
            List<Staff> lstData = query.list();
            if (lstData != null && !lstData.isEmpty()) {
                return lstData;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void save(Staff staff) {
        try {
            getCurrentSession().saveOrUpdate(staff);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public int update(Staff staff) {
        try {
            StringBuilder strSQL = new StringBuilder("update staff set ");
            strSQL.append(" staff_code = :code, ");
            strSQL.append(" staff_name = :name, ");
            strSQL.append(" phone = :phone, ");
            strSQL.append(" email = :email, ");
            strSQL.append(" address = :address, ");
            strSQL.append(" birthday = :birthday, ");
            strSQL.append(" department = :department, ");
            strSQL.append(" position = :position, ");
            strSQL.append(" status = :status, ");
            strSQL.append(" create_date = :createDate ");
            strSQL.append(" where staff_id = :id");

            Query query = getCurrentSession().createSQLQuery(strSQL.toString());
            query.setParameter("code", staff.getStaffCode());
            query.setParameter("name", staff.getStaffName());
            query.setParameter("phone", staff.getPhone());
            query.setParameter("email", staff.getEmail());
            query.setParameter("address", staff.getAddress());
            query.setParameter("birthday", staff.getBirthday());
            query.setParameter("department", staff.getDepartment());
            query.setParameter("position", staff.getPosition());
            query.setParameter("status", staff.getStatus());
            query.setParameter("createDate", staff.getCreateDate());
            query.setParameter("id", staff.getStaffId());
            int numberRow = query.executeUpdate();
            return numberRow;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public int delete(Staff staff) {
        try {
            StringBuilder strSQL = new StringBuilder("update staff set ");
            strSQL.append(" status = :status ");
            strSQL.append(" where staff_id = :id");
            Query query = getCurrentSession().createSQLQuery(strSQL.toString());
            query.setParameter("status", staff.getStatus());
            query.setParameter("id", staff.getStaffId());
            int numberRow = query.executeUpdate();
            return numberRow;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public void importData(List<Staff> vlstData) {
        Session session = getCurrentSession();
        try {

            if (vlstData != null && !vlstData.isEmpty()) {
                for (Staff staff : vlstData) {
                    session.saveOrUpdate(staff);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.beginTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public Staff getStaffByID(String staffCode) {
        try {
            Query query = getCurrentSession().getNamedQuery("Staff.getStaffByID");
            query.setParameter("staffCode", staffCode);
            List<Staff> lstData = query.list();
            if (lstData != null && !lstData.isEmpty()) {
                Staff staff = (Staff) lstData.get(0);
                return staff;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
