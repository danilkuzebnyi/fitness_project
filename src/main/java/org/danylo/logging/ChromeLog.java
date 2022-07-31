package org.danylo.logging;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

@Component
public class ChromeLog {
    private WebDriver driver;

    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\ChromeDriver\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("http://localhost:8081");
    }

    public void tearDown() {
        driver.quit();
    }

    public void printLogs() {
        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        List<LogEntry> logs = logEntries.getAll();
        for (LogEntry log : logs) {
            System.out.println("LOG: " + new Date(log.getTimestamp()) + " " + log.getLevel() + " " + log.getMessage());
        }
    }
}
