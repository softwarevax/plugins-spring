package org.wit.ctw.plugin.dictionary.mybatis.interceptors;

import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.wit.ctw.plugin.dictionary.mybatis.interceptors.loader.DatabaseLoader;

import java.sql.Statement;
import java.util.List;
import java.util.Properties;

@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class DictionaryInterceptor implements Interceptor {

    DictionaryPluginsManage pluginsManage;

    public DictionaryInterceptor(DatabaseLoader dbLoader) {
        this.pluginsManage = new DictionaryPluginsManage();
        this.pluginsManage.addLoader(dbLoader);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        DefaultResultSetHandler handler = (DefaultResultSetHandler) invocation.getTarget();
        Statement statement = (Statement) invocation.getArgs()[0];
        List<Object> result = handler.handleResultSets(statement);
        this.pluginsManage.resultWrapper(result);
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
