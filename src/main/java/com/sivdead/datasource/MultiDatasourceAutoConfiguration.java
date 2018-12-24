package com.sivdead.datasource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author 敬文
 * @date 2018/12/20
 */
@Configuration("dataSourceHealthIndicatorAutoConfiguration")
@EnableConfigurationProperties(MultiDatasourceProperties.class)
@ConditionalOnClass({DataSource.class})
@ConditionalOnProperty(prefix = "multi-datasource", name = "enable")
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
public class MultiDatasourceAutoConfiguration implements InitializingBean {

    private final Collection<DataSourcePoolMetadataProvider> metadataProviders;

    private final HealthAggregator healthAggregator;

    private DataSourcePoolMetadataProvider poolMetadataProvider;

    @Autowired
    public MultiDatasourceAutoConfiguration(Collection<DataSourcePoolMetadataProvider> metadataProviders, HealthAggregator healthAggregator) {
        this.metadataProviders = metadataProviders;
        this.healthAggregator = healthAggregator;
    }


    @ConditionalOnMissingBean({DataSource.class})
    @Bean
    public DynamicDataSource multiDatasource(MultiDatasourceProperties properties) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new Hashtable<>();
        for (Map.Entry<String, DataSourceProperties> entry : properties.getDatasourceMap().entrySet()) {
            DataSource datasource = createDatasource(entry.getValue());
            dataSourceMap.put(entry.getKey(), datasource);
        }
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        if (properties.getDefaultDatasource() != null) {
            dynamicDataSource.setDefaultTargetDataSource(properties.getDefaultDatasource());
        }

        return dynamicDataSource;
    }

    @Bean
    @ConditionalOnEnabledHealthIndicator("db")
    @ConditionalOnMissingBean(
            name = {"dbHealthIndicator"}
    )
    public HealthIndicator dbHealthIndicator(DynamicDataSource dataSource) {
        HealthIndicatorRegistry registry = new DefaultHealthIndicatorRegistry();
        dataSource.getTargetDataSources().forEach(
                (name, source) ->
                        registry.register((String) name, new DataSourceHealthIndicator((DataSource) source, getValidationQuery((DataSource) source))));
        return new CompositeHealthIndicator(this.healthAggregator, registry);
    }

    private DataSource createDatasource(DataSourceProperties properties) {
        if (properties == null) {
            return null;
        }
        Class<? extends DataSource> datasourceType = properties.getType();
        if (datasourceType == null) {
            datasourceType = DataSourceBuilder.findType(ClassUtils.getDefaultClassLoader());
        }
        return properties.initializeDataSourceBuilder().type(datasourceType).build();
    }


    @Override
    public void afterPropertiesSet() {
        this.poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(
                this.metadataProviders);
    }

    private String getValidationQuery(DataSource source) {
        DataSourcePoolMetadata poolMetadata = this.poolMetadataProvider
                .getDataSourcePoolMetadata(source);
        return (poolMetadata != null) ? poolMetadata.getValidationQuery() : null;
    }
}
