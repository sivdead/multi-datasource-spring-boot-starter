package com.sivdead.datasource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author 敬文
 * @date 2018/12/20
 */
@Configuration
@EnableConfigurationProperties(MultiDatasourceProperties.class)
@ConditionalOnClass({DataSource.class})
public class MultiDatasourceAutoConfiguration {


    @ConditionalOnMissingBean({DataSource.class})
    @ConditionalOnProperty(prefix = "multi-datasource",name = "enable")
    @Bean
    public DataSource multiDatasource(MultiDatasourceProperties properties){
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object,Object> dataSourceMap = new Hashtable<>();
        for (Map.Entry<String, DataSourceProperties> entry : properties.getDatasourceMap().entrySet()) {
            dataSourceMap.put(entry.getKey(),createDatasource(entry.getValue()));
        }
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        if(properties.getDefaultDatasource() != null){
            dynamicDataSource.setDefaultTargetDataSource(properties.getDefaultDatasource());
        }

        return dynamicDataSource;
    }

    private DataSource createDatasource(DataSourceProperties properties) {
        if (properties == null){
            return null;
        }
        Class<? extends DataSource> datasourceType = properties.getType();
        if (datasourceType == null) {
            datasourceType = DataSourceBuilder.findType(ClassUtils.getDefaultClassLoader());
        }
        return properties.initializeDataSourceBuilder().type(datasourceType).build();
    }
}
