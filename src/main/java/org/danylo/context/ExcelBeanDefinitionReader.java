package org.danylo.context;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExcelBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public ExcelBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(new EncodedResource(resource));
    }

    private int loadBeanDefinitions(EncodedResource encodedResource) {
        int beansAmount = 0;
        Assert.notNull(encodedResource, "EncodedResource must not be null");
        logger.info("Loading Excel bean definitions from " + encodedResource);

        Map<String, BeanDefinition> beans;
        try {
            beans = readBeansFromFile(encodedResource.getResource().getURI().getPath());
        } catch (IOException e) {
            logger.error("Can't resolve path to resource");
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, BeanDefinition> beansEntry : beans.entrySet()) {
            String beanName = beansEntry.getKey();
            BeanDefinition beanDefinition = beansEntry.getValue();

            if (!getRegistry().containsBeanDefinition(beanName)) {
                registerBeanDefinition(beanName, beanDefinition);
                beansAmount++;
            }
        }

        logger.info("Loaded " + beansAmount + " bean definitions from " + encodedResource);

        return beansAmount;
    }

    private Map<String, BeanDefinition> readBeansFromFile(String fileName) {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.iterator();

                String beanName = cellIterator.next().getStringCellValue();
                String className = cellIterator.next().getStringCellValue();
                List<String> constructorArguments = new ArrayList<>();
                if (cellIterator.hasNext()) {
                    String constructorArgumentsCellValue = cellIterator.next().getStringCellValue();
                    constructorArguments = Arrays.asList(constructorArgumentsCellValue.split("\\s*,\\s*"));
                }
                BeanDefinition beanDefinition = new BeanDefinition(className, constructorArguments);

                beanDefinitions.put(beanName, beanDefinition);
            }
        } catch (IOException e) {
            logger.error("Error reading Excel file");
            throw new RuntimeException(e);
        }

        return beanDefinitions;
    }

    private void registerBeanDefinition(String beanName,  BeanDefinition beanDefinition) {
        try {
            String className = beanDefinition.getClassName();
            AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(
                    null, className, getBeanClassLoader()
            );
            ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
            List<String> constructorArguments = beanDefinition.getConstructorArguments();
            for (int i = 0; i < constructorArguments.size(); i++) {
                constructorArgumentValues.addIndexedArgumentValue(i, new RuntimeBeanReference(constructorArguments.get(i)));
            }
            bd.setConstructorArgumentValues(constructorArgumentValues);
            getRegistry().registerBeanDefinition(beanName, bd);
        } catch (ClassNotFoundException e) {
            logger.error("Bean class could not be loaded", e);
            throw new RuntimeException(e);
        }
    }
}
