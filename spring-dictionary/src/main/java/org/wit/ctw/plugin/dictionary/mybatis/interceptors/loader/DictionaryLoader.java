package org.wit.ctw.plugin.dictionary.mybatis.interceptors.loader;


import org.wit.ctw.plugin.dictionary.mybatis.interceptors.DictionaryTable;

import java.util.List;
import java.util.Map;

public interface DictionaryLoader {

    /**
     * 新增字典表
     * @param dictTable
     * @return
     */
    boolean addDictionaryTable(DictionaryTable dictTable);

    /**
     * 加载缓存
     * @return
     */
    Map<DictionaryTable, List<Map<String, Object>>> dictLoader();
}
