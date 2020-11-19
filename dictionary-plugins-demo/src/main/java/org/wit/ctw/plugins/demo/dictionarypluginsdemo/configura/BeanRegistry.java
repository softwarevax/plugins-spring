package org.wit.ctw.plugins.demo.dictionarypluginsdemo.configura;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wit.ctw.plugin.dictionary.mybatis.interceptors.DictionaryInterceptor;
import org.wit.ctw.plugin.dictionary.mybatis.interceptors.database.DatabaseTable;
import org.wit.ctw.plugin.dictionary.mybatis.interceptors.loader.DatabaseLoader;

import javax.sql.DataSource;

/**
 * @description 注册bean到spring容器
 * @project dictionary-plugins-demo
 * @classname BeanRegistry
 * @date 2020/11/19 16:36
 */
@Configuration
public class BeanRegistry {

    @Bean
    public DictionaryInterceptor dictionaryInterceptor(DataSource dataSource) {
        // config 配置表
        DatabaseTable config = new DatabaseTable();
        String[] configColumn = new String[]{"label", "value", "type"};
        String[] configCondition = new String[]{"1 = 1"};
        String configTableName = "sys_config";
        config.setColumn(configColumn);
        config.setConditions(configCondition);
        config.setTableName(configTableName);
        // user 用户表
        DatabaseTable user = new DatabaseTable();
        String[] userColumn = new String[]{"id", "name"};
        String userTableName = "app_user";
        user.setColumn(userColumn);
        user.setTableName(userTableName);
        DatabaseLoader dbLoader = new DatabaseLoader(dataSource);
        dbLoader.addDictionaryTable(config);
        dbLoader.addDictionaryTable(user);
        DictionaryInterceptor interceptor = new DictionaryInterceptor(dbLoader);
        return interceptor;
    }
}
