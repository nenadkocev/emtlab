package emt.fcse.laboratorisa.service.Impl;

import emt.fcse.laboratorisa.Model.Employee;
import emt.fcse.laboratorisa.Model.Exception.UserAlreadyExistsException;
import emt.fcse.laboratorisa.Model.Exception.UserNotFoundException;
import emt.fcse.laboratorisa.Model.Role;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.repository.EmployeeRepository;
import emt.fcse.laboratorisa.repository.RoleRepository;
import emt.fcse.laboratorisa.repository.UserRepository;
import emt.fcse.laboratorisa.service.EmployeeService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepository.getAll();
    }

    @Override
    public List<Employee> getAllEmployeesForManager(Employee employee) {
        return employeeRepository.getAllByManager_Id(employee.getId());
    }

    @Override
    public List<Employee> getAllEmployeesForManager(String managerEmail) {
        return employeeRepository.getAllByManager_Email(managerEmail);
    }

    @Override
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void removeEmployee(Employee employee) {
        employeeRepository.delete(employee);
    }

//    @Override
//    public Employee makeUserEmployee(User user) {
//        Employee employee = new Employee();
//        employee.setPassword(user.getPassword());
//        employee.setEmail(user.getEmail());
//        employee.setFirstName(user.getFirstName());
//        employee.setLastName(user.getLastName());
//        Set<Role> roles = user.getRoles();
//        roles.add(roleRepository.findByRole("ROLE_EMPLOYEE"));
//        employeeRepository.save(employee);
//
//        return employee;
//    }

    @Override
    public Employee makeUserEmployee(User user) {
        Employee employee = new Employee();
        employee.setId(user.getId());
        Set<Role> roles = user.getRoles();
        roles.add(roleRepository.findByRole("ROLE_EMPLOYEE"));
        userRepository.save(user);
        employeeRepository.save(employee);

        return employee;
    }

    @Override
    @Transactional
    public void removeEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        employeeRepository.removeById(id);
    }

    @Override
    public Employee updateEmployee(String oldEmail, String newEmail, String firstName, String lastName, String managerEmail) {
        Employee employee = employeeRepository.findByEmail(oldEmail)
                .orElseThrow(UserNotFoundException::new);

        if(newEmail != null && newEmail.trim().length() > 0){
            boolean exists = employeeRepository.findByEmail(newEmail).isPresent();
            if(exists)
                throw new UserAlreadyExistsException();
            employee.setEmail(newEmail);
        }

        if(firstName != null && firstName.trim().length() > 0){
            employee.setFirstName(firstName);
        }

        if(lastName != null && lastName.trim().length() > 0){
            employee.setLastName(lastName);
        }

        if(managerEmail != null && managerEmail.trim().length() > 0){
            Employee manager = employeeRepository.findByEmail(managerEmail)
                    .orElseThrow(UserNotFoundException::new);
            Role managerRole = roleRepository.findByRole("ROLE_MANAGER");
            if(!manager.getRoles().contains(managerRole))
                throw new UserNotFoundException(); // should throw another exception

            employee.setManager(manager);
            manager.getEmployees().add(employee);
            employeeRepository.save(manager);
        }
        return employeeRepository.save(employee);
    }

    @Override
    public void removeEmployee(String employeeEmail) {
        employeeRepository.removeByEmail(employeeEmail);
    }


}
