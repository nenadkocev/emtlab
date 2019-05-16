package emt.fcse.laboratorisa.repository;

import emt.fcse.laboratorisa.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
