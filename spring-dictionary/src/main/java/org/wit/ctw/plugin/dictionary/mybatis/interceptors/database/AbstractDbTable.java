package org.wit.ctw.plugin.dictionary.mybatis.interceptors.database;

import org.wit.ctw.plugin.dictionary.mybatis.interceptors.DictionaryTable;
import org.wit.ctw.plugin.dictionary.mybatis.interceptors.DictionaryType;

public abstract class AbstractDbTable implements DictionaryTable {

    protected String tableName;

    protected DatabaseType dbType;

    @Override
    public DictionaryType dictType() {
        return DictionaryType.DATABASE;
    }

    @Override
    public String name() {
        return this.tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DatabaseType getDbType() {
        return dbType;
    }

    public void setDbType(DatabaseType dbType) {
        this.dbType = dbType;
    }
}
