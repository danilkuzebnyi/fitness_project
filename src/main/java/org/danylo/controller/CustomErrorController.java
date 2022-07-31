package org.danylo.controller;

import org.danylo.logging.ChromeLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    private final ChromeLog chromeLog;

    @Autowired
    public CustomErrorController(ChromeLog chromeLog) {
        this.chromeLog = chromeLog;
    }

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        String returnedPage = "";
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            chromeLog.setUp();
            chromeLog.printLogs();
            chromeLog.tearDown();

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                returnedPage = "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                returnedPage = "error/500";
            } else {
                returnedPage = "error/403,405";
            }
        }
        return returnedPage;
    }
}
