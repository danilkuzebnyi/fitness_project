package org.danylo.context;

public class ExcelContextTestClass {
    private final ExcelContextService service;
    private final ExcelContextController controller;

    public ExcelContextTestClass(ExcelContextService service, ExcelContextController controller) {
        this.service = service;
        this.controller = controller;
    }

    public void sayHello() {
        System.out.println("Hello, Excel context is loaded!");
    }

    public void callChildren() {
        service.printGreeting();
        controller.printGreeting();
    }
}
