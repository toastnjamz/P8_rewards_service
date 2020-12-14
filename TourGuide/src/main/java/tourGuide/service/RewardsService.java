package tourGuide.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rewardCentral.RewardCentral;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class RewardsService {

	private final RewardCentral rewardsCentral;
	private final TripPricer tripPricer;
	private final Logger logger = LoggerFactory.getLogger(RewardsService.class);

	@Autowired
	public RewardsService(RewardCentral rewardCentral, TripPricer tripPricer) {
		this.rewardsCentral = rewardCentral;
		this.tripPricer = tripPricer;
	}

	//TODO: Add async solution? Use WebClient?
	public int getRewardPoints(UUID attractionId, UUID userId) {
		return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
	}

	public List<Provider> getTripDeals(String tripPricerApiKey, UUID userID, int numberOfAdults, int numberOfChildren,
									   int tripDuration, int cumulativeRewardPoints) {
		return tripPricer.getPrice(tripPricerApiKey, userID, numberOfAdults, numberOfChildren, tripDuration, cumulativeRewardPoints);
	}

}
