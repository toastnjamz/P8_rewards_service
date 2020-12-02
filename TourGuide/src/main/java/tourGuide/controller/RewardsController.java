package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.domain.user.User;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tripPricer.Provider;

import java.util.List;

@RestController
public class RewardsController {

    @Autowired
    private RewardsService rewardsService;

    @Autowired
    private UserService userService;

    @RequestMapping("/rewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(rewardsService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/trip-deals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = rewardsService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return userService.getUser(userName);
    }
}
