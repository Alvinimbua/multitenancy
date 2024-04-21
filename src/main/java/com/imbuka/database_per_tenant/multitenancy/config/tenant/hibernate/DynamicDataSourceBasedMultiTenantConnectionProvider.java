package com.imbuka.database_per_tenant.multitenancy.config.tenant.hibernate;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.imbuka.database_per_tenant.multitenancy.repository.TenantRepository;
import com.imbuka.database_per_tenant.multitenancy.entity.Tenant;
import com.imbuka.database_per_tenant.util.EncryptionService;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.Serial;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class DynamicDataSourceBasedMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    @Serial
    private static final long serialVersionUID = -460277105706399638L;

    private static final String TENANT_POOL_NAME_SUFFIX = "DataSource";

    private final EncryptionService encryptionService;

    @Qualifier("masterDataSource")
    private final DataSource masterDataSource;


    @Qualifier("masterDataSourceProperties")
    private final DataSourceProperties dataSourceProperties;

    private final TenantRepository masterTenantRepository;

    @Value("${multitenancy.tenant.datasource.url-prefix}")
    private String urlPrefix;

    @Value("${multitenancy.datasource-cache.maximumSize:100}")
    private Long maximumSize;

    @Value("${multitenancy.datasource-cache.expireAfterAccess:10}")
    private Integer expireAfterAccess;

    @Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;

    private LoadingCache<String, DataSource> tenantDataSources;

    @PostConstruct
    private void createCache() {
        tenantDataSources = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES)
                .removalListener((RemovalListener<String, DataSource>) (tenantId, datasource, removalClause) -> {
                    HikariDataSource ds = (HikariDataSource) datasource;
                    assert ds != null;
                    ds.close();
                    log.info("Closed datasource: {}", ds.getPoolName());
                })
                .build(tenantId -> {
                            Tenant tenant = masterTenantRepository.findByTenantId(tenantId)
                                    .orElseThrow(() -> new RuntimeException("No Such tenant:" + tenantId));
                            return createAndConfigureDataSource(tenant);
                        }

                );
    }
    @Override
    protected DataSource selectAnyDataSource() {
        return masterDataSource;
    }

    @Override
    protected DataSource selectDataSource(Object o) {
        return tenantDataSources.get((String) o);
    }

    private DataSource createAndConfigureDataSource(Tenant tenant) {
        String decryptPassword = encryptionService.decrypt(tenant.getPassword(),secret,salt);

        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        ds.setUsername(tenant.getDb());
        ds.setPassword(decryptPassword);
        ds.setJdbcUrl(urlPrefix + tenant.getDb());

        ds.setPoolName(tenant.getTenantId() + TENANT_POOL_NAME_SUFFIX);

        log.info("Configured datasource: {}", ds.getPoolName());
        return  ds;
    }
}
