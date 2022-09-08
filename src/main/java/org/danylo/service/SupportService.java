package org.danylo.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import org.danylo.logging.Log;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class SupportService {
    private final static String SID_ACCOUNT = "ACb50273d10883c91dd980f2eca132d191";
    private final static String AUTH_ID = "90d05c3f5b38755fad53025fe12a5947";
    private final static String FROM_NUMBER= "+15177779568";

    static {
        Twilio.init(SID_ACCOUNT, AUTH_ID);
    }

    public void callToNumber(String toNumber) {
        try {
            Call.creator(new PhoneNumber(toNumber), new PhoneNumber(FROM_NUMBER),
                    new URI("http://demo.twilio.com/docs/voice.xml")).create();
        } catch (URISyntaxException | ApiException e) {
            Log.logger.info(e.getMessage());
        }
    }
}