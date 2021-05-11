package com.docstorage.docapp.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.docstorage.docapp.domain.User;
import com.docstorage.docapp.service.UserService;

@Controller
public class RegistrationController {
	
	@Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
    		@RequestParam("password2") String password2,
    		@Valid User user, 
    		BindingResult bindingResult, 
    		Model model) {
        
        boolean isConfirmEmpty = StringUtils.isEmpty(password2);

        if (isConfirmEmpty) {
            model.addAttribute("password2Error", "Password confirmation cannot be empty");
        }
    	
        boolean differentPasswords = user.getPassword() != null && !user.getPassword().equals(user.getPassword2());
        
    	if (differentPasswords) {
            model.addAttribute("passwordError", "Passwords are different!");
        }
    	
    	if (bindingResult.hasErrors()||differentPasswords) {
    		Map<String, String> errors = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errors);

            return "registration";
        }
    	
        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "This user already exists!");
            return "registration";
        }

        return "redirect:/login";
    }
    
    @GetMapping("/activate/{code}")
    public String activate(
    		@PathVariable String code, 
    		Model model
    		) {
        boolean isActivated = userService.activateUser(code);

        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found!");
        }

        return "login";
    }
}
