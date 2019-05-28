package emt.fcse.laboratorisa.controller;

import emt.fcse.laboratorisa.Model.Employee;
import emt.fcse.laboratorisa.Model.Exception.NotAuthorizedException;
import emt.fcse.laboratorisa.Model.Exception.UserNotFoundException;
import emt.fcse.laboratorisa.Model.Role;
import emt.fcse.laboratorisa.Model.User;
import emt.fcse.laboratorisa.repository.UserRepository;
import emt.fcse.laboratorisa.service.EmployeeService;
import emt.fcse.laboratorisa.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    public static final int PAGE_SIZE = 20;

    private final EmployeeService employeeService;
    private final UserService userService;

    public HomeController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @RequestMapping(value = "addEmployee")
    public String addEmployee(){
        return "employee/add";
    }

    @PostMapping(value = "/editemployee")
    public String editEmployee(@RequestParam(name = "oldemail") String oldEmail,
                             @RequestParam(name = "newemail") String newemail,
                             @RequestParam(name = "firstname") String firstname,
                             @RequestParam(name = "lastname") String lastname,
                             @RequestParam(name = "manageremail") String manageremail){
        Employee employee = employeeService.updateEmployee(oldEmail, newemail, firstname, lastname, manageremail);
        return "redirect:employees";
    }

    @RequestMapping(value = "/removeemployee/{id}")
    public String removeEmployee(@PathVariable(name = "id") Long id){

        employeeService.removeEmployee(id);
        return "redirect:/employees";
    }

    @PostMapping(value = "addEmployee")
    public String addEmployee(@RequestParam(name = "email")String email, Model model){
        User user = userService.findByUserEmail(email);
        if(user == null)
            throw new UserNotFoundException();

        employeeService.makeUserEmployee(user);

        model.addAttribute("employees", employeeService.getAll());
        return "employee/employees";
    }

    @RequestMapping(value = "employees")
    public String employees(
            @RequestParam(value = "page", required = false) Integer page,
            HttpServletRequest request, Model model, Authentication authentication){
        if(!(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_MANAGER")))
            throw new NotAuthorizedException();

        if(page == null)
            page = 0;
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        List<Employee> employeeList = request.isUserInRole("ROLE_ADMIN") ?
                employeeService.getAll(pageable) :
                employeeService.getAllEmployeesForManager(authentication.getName(), pageable);

        model.addAttribute("employees", employeeList);

        return "employee/employees";
    }

}
