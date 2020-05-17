package org.wit.ctw.plugin.dictionary.mybatis.interceptors;

import org.wit.ctw.plugin.dictionary.mybatis.interceptors.loader.DictionaryLoader;
import org.wit.ctw.plugin.dictionary.utils.BeanUtils;
import org.wit.ctw.plugin.dictionary.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 字典表插件管理, 统一处理
 */
public class DictionaryPluginsManage {

    /**
     * key列名
     */
    public static final String KEY_COLUMN = "label";

    /**
     * value列名
     */
    public static final String VALUE_COLUMN = "value";

    /**
     * 存放所有的缓存
     */
    private Map<DictionaryTable, List<Map<String, Object>>> cache;

    /**
     * 缓存来源, database, redis等
     */
    private List<DictionaryLoader> dictLoaders;

    public DictionaryPluginsManage() {
        this.dictLoaders = new ArrayList<>();
        this.cache = new HashMap<>();
    }

    /**
     * 添加缓存来源
     * @param loader
     */
    public void addLoader(DictionaryLoader loader) {
        this.dictLoaders.add(loader);
    }

    /**
     * 加载所有缓存，并组合统一管理, 重新调用刷新缓存
     * @return
     */
    public Map<DictionaryTable, List<Map<String, Object>>> compositeCache() {
        for(DictionaryLoader loader : dictLoaders) {
            Map<DictionaryTable, List<Map<String, Object>>> loaderCache = loader.dictLoader();
            cache.putAll(loaderCache);
        }
        return this.cache;
    }

    public void resultWrapper(List<Object> result) {
        if(result == null || result.size() <= 0) {
            return;
        }
        if(this.cache.size() == 0) {
            // 加载缓存
            this.compositeCache();
        }
        //this.compositeCache();
        // 处理查询返回的结果
        this.handleDictionary(result);
    }

    private void handleDictionary(List<Object> result) {
        Map<Field, DictionaryEntity> markedFieldMap = DictionaryAnalysis.getMarkedField(result.get(0));
        Collection<Field> markedField = markedFieldMap.keySet();
        for(Object obj : result) {
            for(Field field : markedField) {
                // 获取字段注解的信息
                DictionaryEntity entity = markedFieldMap.get(field);
                // 如果没有配置property，则设置当前属性为缓存查询出的结果
                String propertyName = StringUtils.isEmpty(entity.getProperty()) ? field.getName() : entity.getProperty();
                // 属性值, 字典的key[sex] eg: sex:男  ===> sex
                Object propertyVal = BeanUtils.get(obj, field.getName());
                if(propertyVal == null) { // 如果本身没有值，则直接返回
                    continue;
                }
                Map<String, Object> conditions = entity.getCondition();
                conditions.put((String) entity.getValue(), propertyVal);
                Object dictVal = queryCache(entity);
                if(dictVal == null) {
                    continue;
                }
                BeanUtils.set(obj, propertyName, dictVal, field.getType());
            }
        }
    }

    private Object queryCache(DictionaryEntity dict) {
        if(dict == null) {return null;}
        String tableName = dict.getTable();
        List<Map<String, Object>> propCache = getTableCache(tableName);
        Map<String, Object> conditions = dict.getCondition();
        for(Map<String, Object> cache : propCache) {
            boolean flag = true;
            Iterator<Map.Entry<String, Object>> it = conditions.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                // 如果查不到缓存，跳过
                if(!cache.containsKey(key)) {
                    flag = false;
                    continue;
                }
                if(!cache.get(key).equals(entry.getValue())) {
                    flag = false;
                }
            }
            if(flag) {
               return cache.get(dict.getColumn());
            }
        }
        return null;
    }

    /**
     * 如果tableName不为空，则取表tableName对应的缓存，否则取全部缓存
     * @param tableName
     * @return
     */
    private List<Map<String, Object>> getTableCache(String tableName) {
        List<Map<String, Object>> tableCache = new ArrayList<>();
        Iterator<Map.Entry<DictionaryTable, List<Map<String, Object>>>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DictionaryTable, List<Map<String, Object>>> entry = iterator.next();
            if(StringUtils.hasText(tableName) && tableName.equals(entry.getKey().name())) {
                tableCache.addAll(entry.getValue());
            } else {
                tableCache.addAll(entry.getValue());
            }
        }
        return tableCache;
    }
}
