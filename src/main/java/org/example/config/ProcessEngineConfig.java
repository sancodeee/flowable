package org.example.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 流程引擎配置
 * 设置数据源和整合flowable的自定义引擎配置类SpringProcessEngineConfiguration，这样在启动项目后会自动为我们创建好flowable所需的数据库。
 *
 * @author wangsen_a
 * @date 2024/02/21
 */
@Configuration
public class ProcessEngineConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    private final DataSource dataSource;

    @Autowired
    public ProcessEngineConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        // 配置数据源和事务管理器
        engineConfiguration.setDataSource(dataSource);
        engineConfiguration.setDatabaseSchemaUpdate("true");
        engineConfiguration.setTransactionManager(dataSourceTransactionManager());

        // 配置流程图中的字体，防止中文乱码
        engineConfiguration.setActivityFontName("宋体");
        engineConfiguration.setLabelFontName("宋体");
        engineConfiguration.setAnnotationFontName("宋体");
    }
}
