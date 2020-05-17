package org.wit.ctw.plugin.dictionary.mybatis.interceptors;


import org.wit.ctw.plugin.dictionary.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DictionaryAnalysis {

    public static Map<Field, DictionaryEntity> getMarkedField(Object obj) {
        if(obj == null) {return new HashMap<>();}
        Map<Field, DictionaryEntity> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            Dictionary dictionary = field.getAnnotation(Dictionary.class);
            if(dictionary == null) {
                continue;
            }
            DictionaryEntity dictEntity = new DictionaryEntity();
            if(StringUtils.isEmpty(dictionary.column())) {
                dictEntity.setColumn(DictionaryPluginsManage.KEY_COLUMN);
            } else {
                dictEntity.setColumn(dictionary.column());
            }
            if(StringUtils.isEmpty(dictionary.value())) {
                dictEntity.setValue(DictionaryPluginsManage.VALUE_COLUMN);
            } else {
                dictEntity.setValue(dictionary.value());
            }
            dictEntity.setCondition(handleCondition(dictionary.conditions()));
            dictEntity.setProperty(dictionary.property());
            dictEntity.setTable(dictionary.table());
            map.put(field, dictEntity);
        }
        return map;
    }

    /**
     * conditionArr[0] a = b
     * conditionArr[1] c = d
     * @param conditionArr
     * @return
     */
    private static Map<String, Object> handleCondition(String[] conditionArr) {
        Map<String, Object> conditionMap = new HashMap<>();
        if(conditionArr == null || conditionArr.length == 0) {
            return conditionMap;
        }
        for(String condition : conditionArr) {
            String[] subCondition = condition.split("=");
            if(subCondition.length != 2) {
                continue;
            }
            conditionMap.put(subCondition[0].trim(), subCondition[1].trim());
        }
        return conditionMap;
    }
}
