package emt.fcse.laboratorisa.service;

import emt.fcse.laboratorisa.Model.Exception.UserAlreadyExistsException;
import emt.fcse.laboratorisa.Model.Exception.UserNotFoundException;
import emt.fcse.laboratorisa.Model.Exception.WrongCredentialsException;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.Model.dto.UserDto;

import java.util.List;

public interface UserService {

    User registerNewUser(UserDto userDto) throws UserAlreadyExistsException;

    void deleteNonactivatedUsers();

    User activateAccount(String token);

    User login(String email, String password) throws UserNotFoundException, WrongCredentialsException;

    void forgotPassword(String email) throws UserNotFoundException;

    User findByUserEmail(String email) throws UserNotFoundException;

    User updateUserAccount(User newUser, User oldUser) throws UserNotFoundException, UserAlreadyExistsException;

    void changePassword(User user, String oldPassword, String newPassword);

    List<User> getAllUsers();
}
