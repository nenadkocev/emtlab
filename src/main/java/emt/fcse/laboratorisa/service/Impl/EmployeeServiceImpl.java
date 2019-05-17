package emt.fcse.laboratorisa.service.Impl;

import emt.fcse.laboratorisa.Model.Employee;
import emt.fcse.laboratorisa.Model.Role;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.repository.EmployeeRepository;
import emt.fcse.laboratorisa.repository.RoleRepository;
import emt.fcse.laboratorisa.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, RoleRepository roleRepository) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
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

    @Override
    public Employee makeUserEmployee(User user) {
        Employee employee = new Employee();
        employee.setPassword(user.getPassword());
        employee.setEmail(user.getEmail());
        employee.setFirstName(user.getFirstName());
        employee.setLastName(user.getLastName());
        Set<Role> roles = user.getRoles();
        roles.add(roleRepository.findByRole("ROLE_EMPLOYEE"));
        employeeRepository.save(employee);

        return employee;
    }

    @Override
    public void removeEmployee(String employeeEmail) {
        employeeRepository.removeByEmail(employeeEmail);
    }
}
