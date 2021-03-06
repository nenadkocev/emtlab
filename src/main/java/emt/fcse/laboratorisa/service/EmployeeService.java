package emt.fcse.laboratorisa.service;


import emt.fcse.laboratorisa.Model.Employee;
import emt.fcse.laboratorisa.Model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAll();

    List<Employee> getAll(Pageable pageable);

    List<Employee> getAllEmployeesForManager(String managerEmail, Pageable pageable);

    List<Employee> getAllEmployeesForManager(Employee employee);

    List<Employee> getAllEmployeesForManager(String managerEmail);

    Employee addEmployee(Employee employee);

    void removeEmployee(Employee employee);

    void removeEmployee(String employeeEmail);

    Employee makeUserEmployee(User user);

    void removeEmployee(Long id);

    Employee updateEmployee(String oldEmail, String newEmail, String firstName, String lastName, String managerEmail);
}
