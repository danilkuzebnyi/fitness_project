package org.danylo.service;

import org.danylo.dto.RecaptchaResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {
    public static final String REGISTER_ACTION = "register";
    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    private final RestTemplate restTemplate;

    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${recaptcha.siteKey}")
    private String siteKey;

    @Value("${recaptcha.threshold}")
    private float threshold;

    @Autowired
    public RecaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getSiteKey() {
        return siteKey;
    }

    public boolean validateResponse(String recaptchaResponse) {
        String url = String.format(RECAPTCHA_URL, secret, recaptchaResponse);
        RecaptchaResponseDto recaptchaResponseDto = restTemplate.getForObject(url, RecaptchaResponseDto.class);
        return recaptchaResponseDto != null && recaptchaResponseDto.isSuccess()
                && recaptchaResponseDto.getAction().equals(REGISTER_ACTION)
                && recaptchaResponseDto.getScore() > threshold;
    }
}
