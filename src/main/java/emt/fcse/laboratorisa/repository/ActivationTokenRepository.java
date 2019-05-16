package emt.fcse.laboratorisa.repository;

import emt.fcse.laboratorisa.Model.ActivationToken;
import emt.fcse.laboratorisa.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
    ActivationToken findByToken(String token);

    ActivationToken findByUser(User user);
}
