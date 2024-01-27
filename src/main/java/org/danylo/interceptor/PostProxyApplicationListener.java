package org.danylo.interceptor;

import org.danylo.annotation.PostProxy;
import org.danylo.logging.Log;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class PostProxyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            Class<?> originalClass = AopUtils.getTargetClass(bean);
            Method[] originalClassMethods = originalClass.getMethods();
            for (Method originalClassMethod : originalClassMethods) {
                if (originalClassMethod.isAnnotationPresent(PostProxy.class)) {
                    try {
                        Method beanMethod = bean.getClass().getMethod(originalClassMethod.getName(), originalClassMethod.getParameterTypes());
                        beanMethod.invoke(bean);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        Log.logger.error("Error during processing Post Proxy annotation. ", e);
                    }
                }
            }
        }
    }
}
