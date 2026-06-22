package com.dc3.infra.aspect;

import cn.hutool.core.util.StrUtil;
import com.dc3.common.context.TenantContext;
import com.dc3.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@Order(1)
public class TenantSecurityAspect {

    private static final Logger log = LoggerFactory.getLogger(TenantSecurityAspect.class);

    @Around("execution(* com.dc3.repository.mapper..*.*(..))")
    public Object aroundMapper(ProceedingJoinPoint pjp) throws Throwable {
        String contextTenantId = TenantContext.getTenantId();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Object[] args = pjp.getArgs();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if ("tenantId".equals(param.getName()) && param.getType() == String.class) {
                String paramTenantId = (String) args[i];
                if (StrUtil.isNotBlank(paramTenantId)) {
                    if (StrUtil.isBlank(contextTenantId)) {
                        log.error("Mapper 方法 {} 收到 tenantId={} 但当前上下文为空，疑似越权调用",
                                method.getName(), paramTenantId);
                        throw new BusinessException(403, "租户身份校验失败");
                    }
                    if (!contextTenantId.equals(paramTenantId)) {
                        log.error("检测到严重越权：方法={}, 参数租户={}, 上下文租户={}",
                                method.getName(), paramTenantId, contextTenantId);
                        throw new BusinessException(403, "无权访问其他租户数据");
                    }
                } else {
                    args[i] = contextTenantId;
                }
            }
        }
        return pjp.proceed(args);
    }
}
