/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvd.ckp.business.dao.ScheduleDAO;
import com.dvd.ckp.domain.Schedule;

/**
 *
 * @author viettx
 * @version 1.0
 * @since 01/06/2018
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleDAO scheduleDAO;

    @Transactional(readOnly = true)
    @Override
    public List<Schedule> onSearch(Schedule schedule) {
        // TODO Auto-generated method stub
        return scheduleDAO.onSearch(schedule);
    }

    @Transactional
    @Override
    public void save(Schedule schedule) {
        // TODO Auto-generated method stub
        scheduleDAO.save(schedule);

    }

    @Transactional
    @Override
    public void importData(List<Schedule> listData) {
        // TODO Auto-generated method stub
        scheduleDAO.importData(listData);
    }

    @Transactional
    @Override
    public void delete(Schedule schedule) {
        scheduleDAO.delete(schedule);
    }

    @Transactional
    @Override
    public void updateData(List<Schedule> listData) {
        scheduleDAO.updateData(listData);
    }

    @Transactional
    @Override
    public void delete(List<Schedule> schedules) {
        scheduleDAO.delete(schedules);
    }

}
