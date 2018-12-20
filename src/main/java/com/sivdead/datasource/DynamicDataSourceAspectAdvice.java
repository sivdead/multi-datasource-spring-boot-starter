package com.sivdead.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;


/**
 * @author 敬文
 * @since 0.1.0
 */
@Aspect
@Order(-10)
public abstract class DynamicDataSourceAspectAdvice {

    @Pointcut
    public abstract void pointcut();

    /**
     * 根据mapper类上的注解来判断使用自身的数据源还是翼销售的数据源
     *
     * @param pjp 切点相关信息
     * @return 执行结果
     * @throws Throwable 抛出异常
     */
    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        DataSource dataSource = AnnotationUtils.findAnnotation(signature.getMethod().getDeclaringClass(), DataSource.class);
        if (dataSource != null) {
            DynamicDataSource.setDatasourceKey(dataSource.source());
        }
        return pjp.proceed();
    }
}
