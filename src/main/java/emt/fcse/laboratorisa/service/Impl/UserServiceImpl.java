package emt.fcse.laboratorisa.service.Impl;

import emt.fcse.laboratorisa.Model.ActivationToken;
import emt.fcse.laboratorisa.Model.Employee;
import emt.fcse.laboratorisa.Model.Exception.TokenExpiredOrNonExisting;
import emt.fcse.laboratorisa.Model.Exception.UserAlreadyExistsException;
import emt.fcse.laboratorisa.Model.Exception.UserNotFoundException;
import emt.fcse.laboratorisa.Model.Exception.WrongCredentialsException;
import emt.fcse.laboratorisa.Model.Role;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.Model.dto.UserDto;
import emt.fcse.laboratorisa.repository.ActivationTokenRepository;
import emt.fcse.laboratorisa.repository.EmployeeRepository;
import emt.fcse.laboratorisa.repository.RoleRepository;
import emt.fcse.laboratorisa.repository.UserRepository;
import emt.fcse.laboratorisa.service.EmailService;
import emt.fcse.laboratorisa.service.EmployeeService;
import emt.fcse.laboratorisa.service.UserService;
import emt.fcse.laboratorisa.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    @Value("${firstname}")
    private String adminFirstName;
    @Value("${lastname}")
    private String adminLastName;
    @Value("${email}")
    private String adminEmail;
    @Value("${password}")
    private String adminPassword;


    @EventListener(ApplicationReadyEvent.class)
    public void initDb() {
        System.out.println("hello world, I have just started up");

        Role role = new Role();
        role.setRole("ROLE_USER");
        Role role2 = new Role();
        role2.setRole("ROLE_EMPLOYEE");
        Role role3 = new Role();
        role3.setRole("ROLE_ADMIN");
        Role role4 = new Role();
        role4.setRole("ROLE_MANAGER");
        roleRepository.save(role);
        roleRepository.save(role2);
        roleRepository.save(role3);
        roleRepository.save(role4);

        Employee admin = new Employee();

        admin.setEnabled(true);
        admin.setFirstName(adminFirstName);
        admin.setLastName(adminLastName);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setEmail(adminEmail);
        Role roleAdmin = roleRepository.findByRole("ROLE_ADMIN");
        Role roleUser = roleRepository.findByRole("ROLE_USER");
        Role roleEmployee = roleRepository.findByRole("ROLE_EMPLOYEE");
        Role roleManager = roleRepository.findByRole("ROLE_MANAGER");

        admin.addRole(roleAdmin);
        admin.addRole(roleUser);
        admin.addRole(roleEmployee);
        admin.addRole(roleManager);
        employeeRepository.save(admin);

        String firstName = "User";
        String lastName = "Userl";
        String password = "password";

        for(int i = 0; i < 20; i++){
            UserDto dto = new UserDto();
            String firstNameDto = firstName + i;
            String lastNameDto = lastName + i;
            String emailDto = firstNameDto + "." + lastNameDto + "@email.com";

            dto.setFirstName(firstNameDto);
            dto.setLastName(lastNameDto);
            dto.setEmail(emailDto);
            dto.setPassword(password);
            dto.setMatchingPassword(password);

            Employee employee = new Employee();
            employee.setEnabled(true);
            employee.setEmail(dto.getEmail());
            employee.setPassword(passwordEncoder.encode(dto.getPassword()));
            employee.setFirstName(dto.getFirstName());
            employee.setLastName(dto.getLastName());
            Role userRole = roleRepository.findByRole("ROLE_USER");
            Role employeeRole = roleRepository.findByRole("ROLE_EMPLOYEE");
            employee.addRole(userRole);
            employee.addRole(employeeRole);
            employee.setManager(admin);
            employeeRepository.save(employee);
        }

    }

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ActivationTokenRepository activationTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder, PasswordGenerator passwordGenerator, EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordGenerator = passwordGenerator;
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
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
        user.setRoles(new HashSet<>(Arrays.asList(roleRepository.findByRole("ROLE_USER"))));

        token.setUser(user);

        User newUser = userRepository.save(user);

        emailService.sendSimpleMessage(user.getEmail(),
                "Activate your account",
                "In order to complete your registration, you need to activate your account. Click on http://www.localhost:8080/user/activate?token=" + token.getToken());
        System.out.println("Registered user " + user.getEmail() + " with password " + user.getPassword() +  " and with token " + token.getToken());
        return newUser;
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

    @Override
    public User findByUserEmail(String email) throws UserNotFoundException {
        if(email == null)
            throw new UserNotFoundException();

        User user = userRepository.findByEmail(email);
        if(user == null)
            throw new UserNotFoundException();

        return user;
    }

    @Override
    public User updateUserAccount(User newUser, User oldUser) throws UserAlreadyExistsException, UserNotFoundException {

        //  check if user's new email is not taken by some other user
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if(existingUser != null && !oldUser.getId().equals(existingUser.getId()))
            throw new UserAlreadyExistsException();

        oldUser.setLastName(newUser.getLastName());
        oldUser.setFirstName(newUser.getFirstName());
        oldUser.setEmail(newUser.getEmail());
        userRepository.save(oldUser);

        return oldUser;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(s);
        if(user == null)
            throw new UserNotFoundException();
        return user;
    }

    @Override
    public void changePassword(User user, String oldPassword, String newPassword) throws WrongCredentialsException {
        if(!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new WrongCredentialsException();

        if(newPassword == null || newPassword.trim().length() == 0)
            throw new WrongCredentialsException();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
