package org.danylo.context;

import java.util.List;

public class BeanDefinition {
    private String className;
    private List<String> constructorArguments;

    public BeanDefinition(String clasName, List<String> constructorArguments) {
        this.className = clasName;
        this.constructorArguments = constructorArguments;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setConstructorArguments(List<String> constructorArguments) {
        this.constructorArguments = constructorArguments;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getConstructorArguments() {
        return constructorArguments;
    }
}
