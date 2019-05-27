/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dvd.ckp.domain.Schedule;
import com.dvd.ckp.utils.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author viettx
 */
@Repository
public class ScheduleDAOImpl implements ScheduleDAO {

	private static Logger logger = Logger.getLogger(ScheduleDAOImpl.class);

	@Autowired
	SessionFactory sessionFactory;

	protected final Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public List<Schedule> onSearch(Schedule schedule) {
		StringBuilder builder = new StringBuilder("SELECT o FROM Schedule o WHERE 1=1 ");
		if (StringUtils.isValidString(schedule.getStaffCode())) {
			builder.append(" and staffCode = :staffCode");
		}
		if (schedule.getFromDateValue() != null && schedule.getToDateValue() == null) {
			builder.append(" and fromDateValue = :fromDateValue");
		}
		if (schedule.getFromDateValue() != null && schedule.getToDateValue() != null) {
			builder.append(" and fromDateValue between :fromDateValue and :toDateValue ");
		}
		if (StringUtils.isValidString(schedule.getClassValue())) {
			builder.append(" and classValue like :classValue");
		}
//        if (StringUtils.isValidString(quota.getYear())) {
//            builder.append(" and year = :year");
//        }
		builder.append(" ORDER BY createDate");
		Query query = getCurrentSession().createQuery(builder.toString());
		if (StringUtils.isValidString(schedule.getStaffCode())) {
			query.setParameter("staffCode", schedule.getStaffCode());
		}
		if (StringUtils.isValidString(schedule.getClassValue())) {
			query.setParameter("classValue", "%" + schedule.getClassValue() + "%");
		}
		if (schedule.getFromDateValue() != null && schedule.getToDateValue() == null) {
			query.setParameter("fromDateValue", schedule.getFromDateValue());
		}
		if (schedule.getFromDateValue() != null && schedule.getToDateValue() != null) {
			query.setParameter("fromDateValue", schedule.getFromDateValue());
			query.setParameter("toDateValue", schedule.getToDateValue());
		}
//        if (StringUtils.isValidString(quota.getYear())) {
//            query.setParameter("year", quota.getYear());
//        }
		List<Schedule> lstSchedule = query.list();
		if (lstSchedule != null && !lstSchedule.isEmpty()) {
			return lstSchedule;
		}
		return null;
	}

	@Override
	public void save(Schedule shedule) {
		getCurrentSession().saveOrUpdate(shedule);

	}

	@Override
	public void importData(List<Schedule> listData) {
		// TODO Auto-generated method stub
		Session session = getCurrentSession();
		if (listData != null && !listData.isEmpty()) {
			listData.forEach((schedule) -> {
				session.saveOrUpdate(schedule);
			});
		}

	}

	@Override
	public void updateData(List<Schedule> listData) {
		// TODO Auto-generated method stub
		Session session = getCurrentSession();
		if (listData != null && !listData.isEmpty()) {
			listData.forEach((schedule) -> {
				session.saveOrUpdate(schedule);
			});
		}

	}

	@Override
	public void delete(Schedule schedule) {
		Session session = getCurrentSession();
		try {
			session.delete(schedule);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void delete(List<Schedule> schedules) {
		String sql = "delete from sch_schedule where id=:id";
		if (schedules != null && !schedules.isEmpty()) {
			schedules.forEach((s) -> {
				Query query = getCurrentSession().createSQLQuery(sql);
				query.setParameter("id", s.getId());
				int numberRow = query.executeUpdate();
			});
		}

	}
}
