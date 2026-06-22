package com.dc3.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

public final class TenantContext {

    private static final TransmittableThreadLocal<String> TENANT_ID = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<String> TRACE_ID = new TransmittableThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static void clearTenantId() {
        TENANT_ID.remove();
    }

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static String getTraceId() {
        return TRACE_ID.get();
    }

    public static void clearTraceId() {
        TRACE_ID.remove();
    }

    public static void clearAll() {
        TENANT_ID.remove();
        TRACE_ID.remove();
    }
}
