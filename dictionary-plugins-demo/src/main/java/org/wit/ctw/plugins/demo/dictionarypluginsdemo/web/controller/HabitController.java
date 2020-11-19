package org.wit.ctw.plugins.demo.dictionarypluginsdemo.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wit.ctw.plugins.demo.dictionarypluginsdemo.web.domain.Habit;
import org.wit.ctw.plugins.demo.dictionarypluginsdemo.web.mapper.HabitDao;

import java.util.List;

/**
 * @description habitcontroller
 * @project dictionary-plugins-demo
 * @classname HabitController
 * @date 2020/11/19 16:50
 */
@RestController
public class HabitController {

    @Autowired
    HabitDao habitDao;

    @GetMapping("/habit/queryList")
    public List<Habit> list() {
        return habitDao.queryList();
    }
}
