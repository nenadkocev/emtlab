package emt.fcse.laboratorisa.scheduled;

import emt.fcse.laboratorisa.repository.UserRepository;
import emt.fcse.laboratorisa.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final UserService userService;

    public ScheduledTasks(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 */24 * * * *")
    public void deleteNonActivatedUsers(){
        userService.deleteNonactivatedUsers();
    }

    
}
