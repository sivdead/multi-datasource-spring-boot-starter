package com.sivdead.datasource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author 敬文
 * @date 2018/12/20
 */
@ConfigurationProperties(prefix = "multi-datasource")
class MultiDatasourceProperties implements InitializingBean {

    private boolean enable;

    private String defaultDatasource;

    private Map<String, DataSourceProperties> datasourceMap;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDefaultDatasource() {
        return defaultDatasource;
    }

    public void setDefaultDatasource(String defaultDatasource) {
        this.defaultDatasource = defaultDatasource;
    }

    public Map<String, DataSourceProperties> getDatasourceMap() {
        return datasourceMap;
    }

    public void setDatasourceMap(Map<String, DataSourceProperties> datasourceMap) {
        this.datasourceMap = datasourceMap;
    }

    @Override
    public void afterPropertiesSet() {
        if (enable && (datasourceMap == null || datasourceMap.isEmpty())){
            throw new NullPointerException("datasourceMap can not be null");
        }
    }
}
