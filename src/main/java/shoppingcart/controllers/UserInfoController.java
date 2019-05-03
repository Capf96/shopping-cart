package shoppingcart.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shoppingcart.utils.WebUtils;

import java.security.Principal;

@Controller
@RequestMapping("/userInfo")
public class UserInfoController {
    @GetMapping
    public String userInfo(Model model, Principal principal) {

        // After user login successfully.
        String userName = principal.getName();

        System.out.println("User Name: " + userName);

        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        String userInfo = WebUtils.toString(loggedUser);
        model.addAttribute("userInfo", userInfo);

        return "userInfoPage";
    }
}
