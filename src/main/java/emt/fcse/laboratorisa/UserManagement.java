package emt.fcse.laboratorisa;

import emt.fcse.laboratorisa.Model.User;

public interface UserManagement {

    User registerUser(String username, String email, String password, String passwordMatch);

    User loginUser(String usernameEmail, String password);
}
