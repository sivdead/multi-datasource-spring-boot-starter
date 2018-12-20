package com.sivdead.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author 敬文
 * @since 0.1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 当前datasource的key
     */
    private static final ThreadLocal<String> datasourceKey = new InheritableThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return datasourceKey.get();
    }

    static void setDatasourceKey(String datasource){
        datasourceKey.set(datasource);
    }
}
