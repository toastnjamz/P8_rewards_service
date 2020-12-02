package tourGuide.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gpsUtil.GpsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.domain.user.User;
import tourGuide.domain.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class RewardsService {

	private final Logger logger = LoggerFactory.getLogger(RewardsService.class);

	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;

	//TODO: Add back in later
//	private LocationService gpsUtilService;
	private GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	private final TripPricer tripPricer = new TripPricer();

	//TODO: Does this go here like this? Have a reference to userService -> testUserRepository instead?
	private static final String tripPricerApiKey = "test-server-api-key";

//	private int numOfCores = Runtime.getRuntime().availableProcessors();
//	private int waitTime = 20;
	//  Number of threads = Number of Available Cores * Target CPU utilization * (1 + Wait time / Service time)
//	private int numOfThreads = numOfCores * (1 + waitTime )
//	private ExecutorService executorService = Executors.newFixedThreadPool(100);

	//TODO: Add back in later
//	public RewardsService(LocationService gpsUtilService, RewardCentral rewardCentral) {
//		this.gpsUtilService = gpsUtilService;
//		this.rewardsCentral = rewardCentral;
//	}

	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

//	//TODO: Optimize and maybe parallelize?
//	public void calculateRewards(User user) throws ExecutionException, InterruptedException {
//		long startTime = System.currentTimeMillis();
//
//		List<VisitedLocation> userLocations = user.getVisitedLocations();
//		//TODO: Will need to be a call to Location microservice
//		List<Attraction> attractions = gpsUtil.getAttractions();
//		for(VisitedLocation visitedLocation : userLocations) {
//			for(Attraction attraction : attractions) {
//				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
//					if(nearAttraction(visitedLocation, attraction)) {
//						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user.getUserId()).get()));
//					}
//				}
//			}
//		}
//		logger.debug("All rewards calculated in: " + (System.currentTimeMillis() - startTime + " MS"));
//	}


	public void calculateRewards(User user) {
		long startTime = System.currentTimeMillis();
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();

		//TODO
//		user.getVisitedLocations().stream().forEach(location -> attractions.stream());

		userLocations.stream()
				.forEach(visitedLocation -> attractions.stream()
						.filter(attraction -> nearAttraction(visitedLocation, attraction))
						.forEach(attraction ->  {
							if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
								try {
									user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user.getUserId()).get()));
								} catch (InterruptedException e) {
									e.printStackTrace();
								} catch (ExecutionException e) {
									e.printStackTrace();
								}
							}
						}));
		logger.debug("All rewards calculated in: " + (System.currentTimeMillis() - startTime + " MS"));
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

//	public int getRewardPoints(Attraction attraction, UUID userId) {
//		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userId);
//	}

	@Async
	public CompletableFuture<Integer> getRewardPoints(Attraction attraction, UUID userId) {
		return CompletableFuture.completedFuture(rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userId));
	}

	public void addRewardPointsToUser(UserReward userReward, Attraction attraction, User user) {

	}

	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}
}
