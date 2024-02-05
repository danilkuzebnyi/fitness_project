package org.danylo.service;

import org.springframework.stereotype.Service;

import javax.inject.Provider;

@Service
public class TestService {
    private final Provider<NumberService> numberService;

    public TestService(Provider<NumberService> numberService) {
        this.numberService = numberService;
    }

    public void printRandomNumber() {
        System.out.println("Number1 is " + numberService.get().getNumber());
        System.out.println("Number2 is " + numberService.get().getNumber());
    }
}
