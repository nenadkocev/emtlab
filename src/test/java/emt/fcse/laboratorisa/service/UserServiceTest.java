package emt.fcse.laboratorisa.service;

import emt.fcse.laboratorisa.Model.ActivationToken;
import emt.fcse.laboratorisa.Model.Exception.UserNotFoundException;
import emt.fcse.laboratorisa.Model.Exception.WrongCredentialsException;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.Model.dto.UserDto;
import emt.fcse.laboratorisa.repository.ActivationTokenRepository;
import emt.fcse.laboratorisa.repository.RoleRepository;
import emt.fcse.laboratorisa.repository.UserRepository;
import emt.fcse.laboratorisa.util.PasswordGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;


class UserServiceTest {

    private EmailService emailService;
    private UserService userService;
    private PasswordGenerator passwordGenerator;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ActivationTokenRepository activationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

//    @BeforeEach
//    public void setUp(){
//        MockitoAnnotations.initMocks(this);
//
//        passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
//                .useDigits(true)
//                .useLower(true)
//                .useUpper(true)
//                .usePunctuation(true)
//                .build();
//
//        emailService = new EmailServiceImpl();
//        userService = new UserServiceImpl(userRepository, roleRepository, activationTokenRepository, emailService, passwordEncoder, passwordGenerator);
//    }

    @Test
    @DisplayName("UserRegistrationAndActivationTest")
    public void test1(){
        UserDto userDto = new UserDto();
        userDto.setEmail("nkocevv@gmail.com");
        userDto.setFirstName("Nenad");
        userDto.setLastName("Kocev");
        userDto.setPassword("nenadKocev123");
        userDto.setMatchingPassword("nenadKocev123");
        userService.registerNewUser(userDto);

        User user = userRepository.findByEmail(userDto.getEmail());
        ActivationToken activationToken = activationTokenRepository.findByUser(user);

        userService.activateAccount(activationToken.getToken());

        User user1 = userRepository.findByEmail(userDto.getEmail());
        assertEquals(true, user1.getEnabled());

        assertEquals(user1.getId(), userService.login("nkocevv@gmail.com", "nenadKocev123").getId());
    }

    @Test
    @DisplayName("UserRegistrationNoActivation")
    public void test2(){
        UserDto userDto = new UserDto();
        userDto.setEmail("nkocevv@gmail.com");
        userDto.setFirstName("Nenad");
        userDto.setLastName("Kocev");
        userDto.setPassword("nenadKocev123");
        userDto.setMatchingPassword("nenadKocev123");
        userService.registerNewUser(userDto);

        User user = userRepository.findByEmail(userDto.getEmail());
        assertEquals(false, user.getEnabled());

        assertThrows(UserNotFoundException.class, () -> userService.login("nkocevv@gmail.com", "nenadKocev123"));
    }

    @Test
    @DisplayName("UserPasswordChange")
    public void test3(){
        UserDto userDto = new UserDto();
        userDto.setEmail("nkocevv@gmail.com");
        userDto.setFirstName("Nenad");
        userDto.setLastName("Kocev");
        userDto.setPassword("nenadKocev123");
        userDto.setMatchingPassword("nenadKocev123");
        userService.registerNewUser(userDto);

        User user = userRepository.findByEmail(userDto.getEmail());
        ActivationToken activationToken = activationTokenRepository.findByUser(user);

        userService.activateAccount(activationToken.getToken());

        User loggedUser = userService.login(user.getEmail(), userDto.getPassword());
        assertEquals(loggedUser.getId(), user.getId());

        userService.forgotPassword(user.getEmail());

        assertThrows(WrongCredentialsException.class, () -> userService.login(user.getEmail(), userDto.getPassword()));
    }
}