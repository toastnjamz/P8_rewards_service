package tourGuide.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tourGuide.service.RewardsService;

import java.util.UUID;

@RestController
@Api(description="Rewards microservice for TourGuide")
public class RewardsController {

    @Autowired
    private RewardsService rewardsService;
    private final Logger logger = LoggerFactory.getLogger(RewardsController.class);


    /**
     * Gets the user's current reward points
     * @param attractionId
     * @param userId
     * @return int of reward points
     */
    @RequestMapping("/reward-points")
    public Integer getRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId) {
        logger.debug("Request made to getRewardPoints");
        Integer rewardsPoints = new Integer(rewardsService.getRewardPoints(attractionId, userId));
        return rewardsPoints;
    }

}
