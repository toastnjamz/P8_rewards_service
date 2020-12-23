package tourGuide.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rewardCentral.RewardCentral;
import tourGuide.domain.ProviderListWrapper;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class RewardsService {

	private final RewardCentral rewardsCentral;
	private final TripPricer tripPricer;

	@Autowired
	public RewardsService(RewardCentral rewardCentral, TripPricer tripPricer) {
		this.rewardsCentral = rewardCentral;
		this.tripPricer = tripPricer;
	}

	//TODO: Add async solution? Use WebClient?
	public int getRewardPoints(UUID attractionId, UUID userId) {
		return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
	}

	public List<Provider> getTripDeals(String tripPricerApiKey, UUID userId, int numberOfAdults, int numberOfChildren,
											int tripDuration, int cumulativeRewardPoints) {
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, userId, numberOfAdults, numberOfChildren, tripDuration, cumulativeRewardPoints);
		return providers;
	}

}
