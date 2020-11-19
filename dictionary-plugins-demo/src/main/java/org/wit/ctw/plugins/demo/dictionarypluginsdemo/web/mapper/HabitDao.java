package org.wit.ctw.plugins.demo.dictionarypluginsdemo.web.mapper;

import org.wit.ctw.plugins.demo.dictionarypluginsdemo.web.domain.Habit;

import java.util.List;


/**
 *
 * @since 2020-11-19 16:42:54
 */
public interface HabitDao {

    List<Habit> queryList();


}