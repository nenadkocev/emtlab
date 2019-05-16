package emt.fcse.laboratorisa.service;

import emt.fcse.laboratorisa.Model.ActivationToken;
import emt.fcse.laboratorisa.Model.Exception.TokenExpiredOrNonExisting;
import emt.fcse.laboratorisa.Model.Exception.UserAlreadyExistsException;
import emt.fcse.laboratorisa.Model.Exception.UserNotFoundException;
import emt.fcse.laboratorisa.Model.Exception.WrongCredentialsException;
import emt.fcse.laboratorisa.Model.Role;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.Model.dto.UserDto;
import emt.fcse.laboratorisa.repository.ActivationTokenRepository;
import emt.fcse.laboratorisa.repository.RoleRepository;
import emt.fcse.laboratorisa.repository.UserRepository;
import emt.fcse.laboratorisa.util.PasswordGenerator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;

    @PostConstruct
    void init(){
        Role user = new Role();
        user.setRole("USER");
        roleRepository.save(user);
    }

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ActivationTokenRepository activationTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder, PasswordGenerator passwordGenerator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public User registerNewUser(UserDto userDto) throws UserAlreadyExistsException {
        if(userRepository.findByEmail(userDto.getEmail()) != null)
            throw new UserAlreadyExistsException();

        if(!userDto.getPassword().equals(userDto.getMatchingPassword())){
            throw new WrongCredentialsException();
        }

        ActivationToken token = new ActivationToken();
        token.setToken(UUID.randomUUID().toString());

        User user = new User();
        user.setActivationToken(token);
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRoles(new HashSet<>(Arrays.asList(roleRepository.findByRole("USER"))));

        token.setUser(user);

        userRepository.save(user);

//        emailService.sendSimpleMessage(user.getEmail(),
//                "Activate your account",
//                "In order to complete your registration, you need to activate your account. Click on http://www.localhost:8080/user/activate?token=" + token.getToken());
        System.out.println("Registered user " + user.getEmail() + " with token " + token.getToken());
        return user;
    }

    @Override
    public void deleteNonactivatedUsers() {
        LocalDateTime now = LocalDateTime.now();
        activationTokenRepository.findAll().stream()
                .filter(token -> token.getExpiryDate().isBefore(now))
                .forEach(token -> {
                    userRepository.delete(token.getUser());
                    activationTokenRepository.delete(token);
                });
    }

    @Override
    public User activateAccount(String token) {
        ActivationToken activationToken = activationTokenRepository.findByToken(token);
        if(activationToken == null || activationToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new TokenExpiredOrNonExisting();

        User user = activationToken.getUser();
        user.setEnabled(true);
        user.setActivationToken(null);
        activationTokenRepository.delete(activationToken);
        return user;
    }

    @Override
    public User login(String email, String password) throws UserNotFoundException, WrongCredentialsException {
        User user = userRepository.findByEmail(email);
        if(user == null || !user.getEnabled())
            throw new UserNotFoundException();

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new WrongCredentialsException();
        }

        return user;
    }

    @Override
    public void forgotPassword(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null || !user.getEnabled())
            throw new UserNotFoundException();

        String newPassword = passwordGenerator.generate(12);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailService.sendSimpleMessage(
                email,
                "Password change",
                "You requested password change, your new generated password is " + newPassword
        );
    }
}
