package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.domain.NearbyAttraction;
import tourGuide.domain.user.User;
import tourGuide.tracker.Tracker;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LocationService {

    private final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private int attractionProximityRange = 200;
    private int numberOfClosestAttractions = 5;
    private GpsUtil gpsUtil;

    //TODO: Do I need RewardsService, since I'll be cutting it away, or can I just add RewardsCentral to the GPS microservice?
    //Method call to http REST call instead
    //Endpoint rewards/
    //Call external services this way too
    private RewardsService rewardsService;
    private UserService userService;

    public final Tracker tracker;

    ExecutorService executorService = Executors.newFixedThreadPool(2000);

    public LocationService(RewardsService rewardsService, UserService userService) {
        gpsUtil = new GpsUtil();
        this.rewardsService = rewardsService;
        this.userService = userService;

        tracker = new Tracker(userService ,this);
        addShutDownHook();
    }

    public VisitedLocation getUserLocation(User user) {
        VisitedLocation visitedLocation = null;
        try {
            visitedLocation = (user.getVisitedLocations().size() > 0) ?
                    user.getLastVisitedLocation() :
                    trackUserLocation(user);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Unable to get user location for UUID: " + user.getUserId().toString(), e);
//            e.printStackTrace();
        }
        return visitedLocation;
    }

//    public VisitedLocation trackUserLocation(User user) {
//        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
//        user.addToVisitedLocations(visitedLocation);
//        rewardsService.calculateRewards(user);
//        return visitedLocation;
//    }

    //Concurrent solution for above trackUserLocation() method
    public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();

        CompletableFuture<VisitedLocation> completableFuture = CompletableFuture.supplyAsync(() -> {
            return gpsUtil.getUserLocation(user.getUserId());
        }, executorService).
                thenApplyAsync(visitedLocation -> {
                    user.addToVisitedLocations(visitedLocation);
                    //TODO: maybe break this into a separate method?
                    rewardsService.calculateRewards(user);
                    return visitedLocation;
                });

        logger.debug("All users tracked in: " + (System.currentTimeMillis() - startTime + " MS"));
        return completableFuture.get();
    }

//    public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
//        long startTime = System.currentTimeMillis();
//
//        CompletableFuture<VisitedLocation> completableFuture = CompletableFuture.supplyAsync(() -> {
//            return gpsUtil.getUserLocation(user.getUserId());
//        }, executorService).
//                thenApplyAsync(visitedLocation -> {
//                    user.addToVisitedLocations(visitedLocation);
//                    rewardsService.calculateRewards(user);
//                    return visitedLocation;
//                });
//
//        logger.debug("All users tracked in: " + (System.currentTimeMillis() - startTime + " MS"));
//        return completableFuture.get();
//    }

    public Map<String, Location> getAllUsersLocations() {
        Map<String, Location> allUsersLocations = new HashMap<String, Location>();
        for (User user : userService.getAllUsers()) {
            allUsersLocations.put(user.getUserId().toString(), (user.getVisitedLocations().size() > 0) ?
            user.getLastVisitedLocation().location : null);
        }
        return allUsersLocations;
    }

    public List<Attraction> getAttractions() {
        return gpsUtil.getAttractions();
    }

    public List<NearbyAttraction> getClosestAttractions(VisitedLocation visitedLocation) {
        List<NearbyAttraction> nearbyAttractions = new ArrayList<>();
        Map<Double, Attraction> attractionsMap = new TreeMap<>();

        for (Attraction attraction : gpsUtil.getAttractions()) {
            attractionsMap.put(getDistance(attraction, visitedLocation.location), attraction);
        }

        attractionsMap.forEach((distance, attraction) -> {
            if(nearbyAttractions.size() < numberOfClosestAttractions) {
                NearbyAttraction nearbyAttraction = new NearbyAttraction();
                nearbyAttraction.setAttractionName(attraction.attractionName);
                nearbyAttraction.setAttractionLocation(new Location(attraction.longitude, attraction.latitude));
                nearbyAttraction.setUserLocation(visitedLocation.location);
                nearbyAttraction.setAttractionDistance(distance);
                try {
                    nearbyAttraction.setAttractionRewardPoints(rewardsService.getRewardPoints(attraction, visitedLocation.userId).get());
                } catch (InterruptedException | ExecutionException e) {
                    logger.warn("Unable to get rewards for UUID: " + visitedLocation.userId.toString(), e);
                }
                nearbyAttractions.add(nearbyAttraction);
            }
        });
        return nearbyAttractions;
    }

//    public List<NearbyAttraction> getClosestAttractions(VisitedLocation visitedLocation) {
//        List<NearbyAttraction> nearbyAttractions = new ArrayList<>();
//        Map<Double, Attraction> attractionsMap = new TreeMap<>();
//
//        for (Attraction attraction : gpsUtil.getAttractions()) {
//            attractionsMap.put(getDistance(attraction, visitedLocation.location), attraction);
//        }
//
//        attractionsMap.forEach((distance, attraction) -> {
//            if(nearbyAttractions.size() < numberOfClosestAttractions) {
//                NearbyAttraction nearbyAttraction = new NearbyAttraction();
//                nearbyAttraction.setAttractionName(attraction.attractionName);
//                nearbyAttraction.setAttractionLocation(new Location(attraction.longitude, attraction.latitude));
//                nearbyAttraction.setUserLocation(visitedLocation.location);
//                nearbyAttraction.setAttractionDistance(distance);
//                nearbyAttraction.setAttractionRewardPoints(rewardsService.getRewardPoints(attraction, visitedLocation.userId));
//                nearbyAttractions.add(nearbyAttraction);
//            }
//        });
//        return nearbyAttractions;
//    }

    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        for(Attraction attraction : gpsUtil.getAttractions()) {
            if(isWithinAttractionProximity(attraction, visitedLocation.location)) {
                nearbyAttractions.add(attraction);
            }
        }
        return nearbyAttractions;
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) > attractionProximityRange ? false : true;
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

        private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
	}

    //TODO: Add get nearAttraction()?

}
