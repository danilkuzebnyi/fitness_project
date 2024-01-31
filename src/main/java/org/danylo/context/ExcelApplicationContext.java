package org.danylo.context;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class ExcelApplicationContext extends GenericApplicationContext {

    public ExcelApplicationContext(String fileName) {
        ExcelBeanDefinitionReader excelBeanDefinitionReader = new ExcelBeanDefinitionReader(this);
        excelBeanDefinitionReader.loadBeanDefinitions(new ClassPathResource(fileName));
        refresh();
    }
}
