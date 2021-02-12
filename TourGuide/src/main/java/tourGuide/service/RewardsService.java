package tourGuide.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rewardCentral.RewardCentral;

@Service
public class RewardsService {

	private final RewardCentral rewardsCentral;

	@Autowired
	public RewardsService(RewardCentral rewardCentral) {
		this.rewardsCentral = rewardCentral;
	}

	public int getRewardPoints(UUID attractionId, UUID userId) {
		return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
	}

}
