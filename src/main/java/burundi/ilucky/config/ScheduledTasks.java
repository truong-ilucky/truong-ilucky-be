package burundi.ilucky.config;

import burundi.ilucky.model.User;
import burundi.ilucky.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {
    @Autowired
    private UserService userService;

    @Scheduled( cron = "0 0 0 * * ?")
    public void addPlays(){
        List<User> users = userService.findAll();
        for (User user : users) {
            user.setTotalPlay(user.getTotalPlay() + 5);
            user.setLastUpdate(new Date());
            userService.saveUser(user);
        }
    }
}
