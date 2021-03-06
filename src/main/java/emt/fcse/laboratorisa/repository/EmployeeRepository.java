package emt.fcse.laboratorisa.repository;

import emt.fcse.laboratorisa.Model.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("from Employee")
    List<Employee> getAll();

    @Query("from Employee")
    List<Employee> getAll(Pageable pageable);


    List<Employee> getAllByManager_Id(Long managerId);

    List<Employee> getAllByManager_Email(String managerEmail);

    List<Employee> getAllByManager_Email(String managerEmail, Pageable pageable);

    Optional<Employee> findByEmail(String email);

    @Modifying
    void removeByEmail(String email);

    @Modifying
    void removeById(Long id);
}
