package org.danylo.profiling;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.danylo.logging.Log;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProfilingAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Value("${app.profiling}")
    private boolean isProfilingEnabled;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (isProfilingEnabled &&
                (beanClass.isAnnotationPresent(Profiling.class) || Arrays.stream(beanClass.getDeclaredMethods())
                                    .anyMatch(method -> method.isAnnotationPresent(Profiling.class)))) {
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.addAdvice(new MethodProfilingInterceptor(beanClass));
            return proxyFactory.getProxy();
        }
        return bean;
    }

    private static class MethodProfilingInterceptor implements MethodInterceptor {
        private final Class<?> originalClass;

        public MethodProfilingInterceptor(Class<?> originalClass) {
            this.originalClass = originalClass;
        }

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            boolean isMethodAnnotated = methodInvocation.getMethod().isAnnotationPresent(Profiling.class);
            boolean isClassAnnotated = originalClass.isAnnotationPresent(Profiling.class);
            if (isMethodAnnotated || isClassAnnotated) {
                long start = System.currentTimeMillis();
                Object returnValue = methodInvocation.proceed();
                long end = System.currentTimeMillis();
                String methodName = methodInvocation.getMethod().getName();
                String originalClassName = originalClass.getSimpleName();
                Log.logger.info(originalClassName + "#" + methodName + " was executing " + (end - start) + " ms");
                return returnValue;
            } else {
                return methodInvocation.proceed();
            }
        }
    }
}
