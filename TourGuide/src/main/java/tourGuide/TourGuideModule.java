package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.repository.UserRepository;
import tourGuide.service.LocationService;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;

@Configuration
public class TourGuideModule {

	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}

	@Bean
	public UserRepository getTestUserRepository() {
		return new UserRepository();
	}

	@Bean
	public UserService getUserService() {
		return new UserService(getTestUserRepository());
	}

	@Bean
	public LocationService getGpsUtilService() {
		return new LocationService(getRewardsService(), getUserService());
	}

	//TODO: Add back in later
//	@Bean
//	public RewardsService getRewardsService() {
//		return new RewardsService(getGpsUtilService(), getRewardCentral());
//	}

	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsUtil(), getRewardCentral());
	}

	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
}
