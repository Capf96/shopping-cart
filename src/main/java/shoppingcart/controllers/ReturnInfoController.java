package shoppingcart.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;


@Controller
@RequestMapping("/returnInfo")
public class ReturnInfoController {
    @GetMapping
    public ResponseEntity<String> returnInformation() {
        return ResponseEntity.status(HttpStatus.OK).body(RequestContextHolder.currentRequestAttributes().getSessionId());
    }
}
