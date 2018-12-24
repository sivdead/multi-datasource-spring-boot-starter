package com.sivdead.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

/**
 * @author 敬文
 * @since 0.1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private Map<Object, Object> targetDataSources;

    /**
     * 当前datasource的key
     */
    private static final ThreadLocal<String> datasourceKey = new InheritableThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return datasourceKey.get();
    }

    static void setDatasourceKey(String datasource) {
        datasourceKey.set(datasource);
    }

    Map<Object, Object> getTargetDataSources() {
        return targetDataSources;
    }

    public void setTargetDataSources(Map<Object, Object> targetDataSources) {

        super.setTargetDataSources(targetDataSources);
        this.targetDataSources = targetDataSources;
    }
}
