### 1、注册mybatis插件
```
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
```
### 2、使用字典插件
使用注解后
1、createUserId、updateUserId由原来的用户id改为用户姓名
2、state由原来的状态ON改为进行中
```
public class Habit {

    @Dictionary(table ="app_user", column = "name", value = "id")
    private String createUserId;
    
    /**
     * 当表没有加载时, 从全部缓存中查询, 效率较低
     */
    @Dictionary(table ="app_user", column = "name", value = "id")
    private String updateUserId;

    private String stateLabel;

    // 默认值，默认取加载的第一张表，默认替换当前属性，默认字典表的column为label, 默认字典表的value为value， 默认没有查询条件
    //@Dictionary(table ="sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = habit_state"})
    @Dictionary(property = "stateLabel")
    private String state;

}
```
