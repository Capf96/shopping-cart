package shoppingcart.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

@Service
public class JSessionId {
    public HttpHeaders getJSessionId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "JSESSIONID=" + RequestContextHolder.currentRequestAttributes().getSessionId());
        return headers;
    }
}
