package emt.fcse.laboratorisa.controller;

import emt.fcse.laboratorisa.Model.Exception.NotAuthenticatedException;
import emt.fcse.laboratorisa.Model.Exception.WrongCredentialsException;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.Model.dto.UserDto;
import emt.fcse.laboratorisa.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@CrossOrigin(value = "*")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register")
    public String register(Model model){
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "user/register";
    }

    @PostMapping(value = "/register")
    public String register(@ModelAttribute("user") UserDto userDto, Model model){
        userService.registerNewUser(userDto);
        return "user/confirmRegistration";
    }

    @RequestMapping(value = "/activate")
    public void activateAccount(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        User user = this.userService.activateAccount(token);
        response.sendRedirect("/login");
    }

    @RequestMapping(value = "/login")
    public String login(){
        return "user/login";
    }

    @PostMapping(value = "/index")
    public String index(@ModelAttribute("user") User newUser, Authentication authentication, Model model) throws NotAuthenticatedException {
        if(authentication == null || !authentication.isAuthenticated())
            throw new NotAuthenticatedException();
        User oldUser = userService.findByUserEmail(authentication.getName());

        userService.updateUserAccount(newUser, oldUser);
        model.addAttribute("user", newUser);
        return "user/index";
    }

    @RequestMapping(value = "/index")
    public String index(Authentication authentication, Model model){
        User user = userService.findByUserEmail(authentication.getName());
        model.addAttribute("user", user);
        return "user/index";
    }

    @RequestMapping(value = "/changepassword")
    public String changePassword(){
        return "user/changepassword";
    }

    @PostMapping(value = "/changepassword")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword, Authentication authentication){

        if(authentication == null || !authentication.isAuthenticated()){
            throw new NotAuthenticatedException();
        }

        User user = userService.findByUserEmail(authentication.getName());
        userService.changePassword(user, oldPassword, newPassword);
        authentication.setAuthenticated(false);
        return "user/login";
    }

    @RequestMapping(value = "/forgotpassword")
    public String resetPassword(){
        return "user/forgotpassword";
    }

    @PostMapping(value = "/forgotpassword")
    public void resetPassword(@RequestParam(value = "email", required = true) String email) throws WrongCredentialsException {
        userService.forgotPassword(email);
    }
}
