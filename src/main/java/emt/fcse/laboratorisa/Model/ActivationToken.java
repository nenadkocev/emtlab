package emt.fcse.laboratorisa.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class ActivationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    private Long id;

    private String token;

    @OneToOne
    @MapsId
    private User user;

    private LocalDateTime expiryDate;

    public ActivationToken() {
        this.setExpiryDate();
    }

    private void setExpiryDate(){
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
