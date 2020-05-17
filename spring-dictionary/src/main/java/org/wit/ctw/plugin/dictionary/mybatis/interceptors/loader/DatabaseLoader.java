package org.wit.ctw.plugin.dictionary.mybatis.interceptors.loader;


import org.wit.ctw.plugin.dictionary.mybatis.interceptors.DictionaryTable;
import org.wit.ctw.plugin.dictionary.mybatis.interceptors.database.DatabaseTable;
import org.wit.ctw.plugin.dictionary.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库方式加载字典表
 */
public class DatabaseLoader implements DictionaryLoader {

    /**
     * key列名
     */
    public static final String KEY_COLUMN = "label";

    /**
     * value列名
     */
    public static final String VALUE_COLUMN = "value";

    /**
     * 表名
     */
    public static final String TABLE_NAME = "sys_config";

    /**
     * 数据源，默认获取当前使用的数据源
     */
    private DataSource dataSource;

    /**
     * 通过字典表dictTable加载的字典缓存
     */
    private Map<DictionaryTable, List<Map<String, Object>>> dbCache;

    public DatabaseLoader(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dbCache = new LinkedHashMap<>();
    }

    /**
     * 新增字典表
     * @param dictTable
     * @return
     */
    @Override
    public boolean addDictionaryTable(DictionaryTable dictTable) {
        if(dictTable == null) {
            return false;
        }
        if(!(dictTable instanceof DatabaseTable)) {
            return false;
        }
        DatabaseTable dbTable = (DatabaseTable) dictTable;
        String tableName =  dbTable.getTableName();
        if(StringUtils.isEmpty(tableName)) {
            dbTable.setTableName(TABLE_NAME);
        }
        if(dbTable.getColumn() == null || dbTable.getColumn().length < 2) {
            dbTable.setColumn(new String[] {KEY_COLUMN, VALUE_COLUMN});
        }
        List<String> existTable = dbCache.keySet().stream().map(table -> table.name()).collect(Collectors.toList());
        if(existTable.contains(tableName)) {
            return false;
        }
        List<Map<String, Object>> cache = new ArrayList<>();
        this.dbCache.put(dbTable, cache);
        return true;
    }

    @Override
    public Map<DictionaryTable, List<Map<String, Object>>> dictLoader() {
        if(this.dbCache.size() == 0) {return this.dbCache;}
        Iterator<Map.Entry<DictionaryTable, List<Map<String, Object>>>> iterator = dbCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DictionaryTable, List<Map<String, Object>>> entry = iterator.next();
            if(entry.getKey() instanceof DatabaseTable) {
                // 表名
                DatabaseTable table = (DatabaseTable) entry.getKey();
                // 缓存
                List<Map<String, Object>> cache = queryCache(table);
                this.dbCache.put(table, cache);
            }
        }
        return this.dbCache;
    }

    private List<Map<String, Object>> queryCache(DatabaseTable table) {
        if(table == null) {return new ArrayList<>();}
        List<Map<String, Object>> cache = new ArrayList<>();
        StringBuffer sb = new StringBuffer(" SELECT ");
        String[] columns = table.getColumn();
        for(String col : columns) {
            sb.append(col).append(",");
        }
        sb = sb.delete(sb.length() - 1, sb.length());
        sb.append(" FROM ").append(table.getTableName()).append(" WHERE 1 = 1 ");
        String[] condition = table.getConditions();
        if(condition != null && condition.length > 0) {
            for(String con : condition) {
                sb.append(" AND ").append(con);
            }
        }
        return executeSql(sb.toString());
    }

    private List<Map<String, Object>> executeSql(String sql) {
        if(this.dataSource == null) {return new ArrayList<>();}
        List<Map<String, Object>> cache = new ArrayList<>();
        try(Connection conn = this.dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql);
            ResultSet rs = stat.executeQuery()) {
            ResultSetMetaData metaData = stat.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                // row
                Map<String, Object> row = new LinkedHashMap<>();
                for(int i = 1; i <= columnCount; i++) {
                    // column name
                    String columnName = metaData.getColumnName(i);
                    row.put(columnName, rs.getObject(i));
                }
                cache.add(row);
            }
        } catch (Exception e) {
        }
        return cache;
    }
}
