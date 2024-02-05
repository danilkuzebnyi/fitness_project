package org.danylo.service;

import org.apache.commons.lang3.RandomUtils;
import org.danylo.annotation.CacheScope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@CacheScope(lifetime = 3)
public class NumberService {
    private int number;

    @PostConstruct
    public void init() {
        number = RandomUtils.nextInt(0, 1000);
    }

    public int getNumber() {
        return number;
    }
}
