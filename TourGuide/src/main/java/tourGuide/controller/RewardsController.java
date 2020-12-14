package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tourGuide.service.RewardsService;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

@RestController
public class RewardsController {

    @Autowired
    private RewardsService rewardsService;
    private final Logger logger = LoggerFactory.getLogger(RewardsController.class);

    //TODO: Add async solution? Use WebClient?
    @RequestMapping("/reward-points")
    public String getRewardPoints(@RequestParam String attractionId, @RequestParam String userID) {
        logger.debug("Request made to getRewardPoints");
        return JsonStream.serialize(rewardsService.getRewardPoints(UUID.fromString(attractionId), UUID.fromString(userID)));
    }

    @RequestMapping("/trip-deals")
    public String getTripDeals(@RequestParam String tripPricerApiKey, @RequestParam String userID, @RequestParam String numberOfAdults,
                               @RequestParam String numberOfChildren, @RequestParam String tripDuration,
                               @RequestParam String cumulativeRewardPoints) {
        List<Provider> providers = rewardsService.getTripDeals(tripPricerApiKey, UUID.fromString(userID),
                Integer.parseInt(numberOfAdults), Integer.parseInt(numberOfChildren), Integer.parseInt(tripDuration),
                Integer.parseInt(cumulativeRewardPoints));
        logger.debug("Request made to getTripDeals");
        return JsonStream.serialize(providers);
    }

}
