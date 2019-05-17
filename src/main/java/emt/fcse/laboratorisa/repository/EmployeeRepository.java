package emt.fcse.laboratorisa.repository;

import emt.fcse.laboratorisa.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("from Employee")
    List<Employee> getAll();

    List<Employee> getAllByManager_Id(Long managerId);

    List<Employee> getAllByManager_Email(String managerEmail);

    @Modifying
    void removeByEmail(String email);
}
