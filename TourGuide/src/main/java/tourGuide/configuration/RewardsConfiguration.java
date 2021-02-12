package tourGuide.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rewardCentral.RewardCentral;

@Configuration
public class RewardsConfiguration {

	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}

}
