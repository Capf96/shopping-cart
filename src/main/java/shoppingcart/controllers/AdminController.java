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
@RequestMapping("/admin")
public class AdminController {
    @GetMapping
    public String adminPage(Model model, Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        String userInfo = WebUtils.toString(loggedUser);
        model.addAttribute("userInfo", userInfo);

        return "adminPage";
    }
}
