package emt.fcse.laboratorisa.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Employee extends User {

    @ManyToOne
    private Employee manager;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY , cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private List<Employee> employees;

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
