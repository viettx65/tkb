/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.business.dao;

import java.util.List;

import com.dvd.ckp.domain.Schedule;

/**
 *
 * @author viettx
 */
public interface ScheduleDAO {

    List<Schedule> onSearch(Schedule schedule);

    void save(Schedule schedule);

    void importData(List<Schedule> listData);

    void updateData(List<Schedule> listData);

    void delete(Schedule schedule);

    void delete(List<Schedule> schedules);
}
