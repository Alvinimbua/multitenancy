package com.imbuka.database_per_tenant.multitenancy.config.tenant.hibernate;

import com.imbuka.database_per_tenant.multitenancy.util.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * CurrentTenantIdentifierResolver --> encapsulates a strategy for resolving which tenant to use for a specific request,
 * MultiTenantConnectionProvider --> encapsulates a strategy for selecting an appropriate database connection for that tenant.
 */

@Component("currentTenantIdentifierResolver")
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<Object> {
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getTenantId();
        return !ObjectUtils.isEmpty(tenantId) ? tenantId : "BOOTSTRAP";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
