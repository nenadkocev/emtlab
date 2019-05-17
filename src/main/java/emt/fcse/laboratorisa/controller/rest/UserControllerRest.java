package emt.fcse.laboratorisa.controller.rest;

import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.Model.dto.UserDto;
import emt.fcse.laboratorisa.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//
//@RestController
//@RequestMapping(value = "/user")
public class UserControllerRest {

    private final UserService userService;

    public UserControllerRest(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(value = "/login")
    public void login(@RequestParam(value = "email", required = true) String email,
                      @RequestParam(value = "password", required = true) String password){
        User user = userService.login(email, password);
    }

    @PostMapping(value = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody UserDto userDto){
        User user = this.userService.registerNewUser(userDto);

    }

    @RequestMapping(value = "/activate")
    public void activateAccount(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        User user = this.userService.activateAccount(token);
        response.sendRedirect("/user/login");
    }
}
