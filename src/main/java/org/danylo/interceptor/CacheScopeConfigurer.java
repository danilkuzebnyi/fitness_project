package org.danylo.interceptor;

import org.apache.commons.lang3.tuple.Pair;
import org.danylo.annotation.CacheScope;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheScopeConfigurer implements Scope {
    private final Map<String, Pair<Object, Long>> beans = new ConcurrentHashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object bean = objectFactory.getObject();
        int beanLifetime = bean.getClass().getAnnotation(CacheScope.class).lifetime();
        long currentTime = Instant.now().getEpochSecond();
        if (!beans.containsKey(name) || currentTime - beans.get(name).getValue() > beanLifetime) {
            beans.put(name, Pair.of(bean, currentTime));
        }
        return beans.get(name).getKey();
    }

    @Override
    public Object remove(String name) {
        return null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
