package emt.fcse.laboratorisa;

import emt.fcse.laboratorisa.Model.ActivationToken;
import emt.fcse.laboratorisa.Model.Exception.UserNotFoundException;
import emt.fcse.laboratorisa.Model.Exception.WrongCredentialsException;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.Model.dto.UserDto;
import emt.fcse.laboratorisa.repository.ActivationTokenRepository;
import emt.fcse.laboratorisa.repository.UserRepository;
import emt.fcse.laboratorisa.service.EmailService;
import emt.fcse.laboratorisa.service.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LaboratorisaApplicationTests {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @BeforeEach
    public void removeFromDatabase(){
        userRepository.deleteAll();
        activationTokenRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    @DisplayName("PasswordMatchesAnnotation")
    @Ignore
    public void testPasswordMatchesAnnotation(){
        // TODO:
    }

    @Test
    @Ignore
    @DisplayName("TestingEmail")
    public void testblabla(){
        emailService.sendSimpleMessage("nkocevv@gmail.com", "Testing message", "Zdravo nenad");
    }

    @Test
    @DisplayName("UserRegistrationAndActivationTest")
    @Transactional
    public void testblabla1(){
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
    @Transactional
    public void userRegistrationtest1(){
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
    @Transactional
    public void blabla1(){
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
