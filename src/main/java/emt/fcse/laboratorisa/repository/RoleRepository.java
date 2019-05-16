package emt.fcse.laboratorisa.repository;

import emt.fcse.laboratorisa.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);
}
