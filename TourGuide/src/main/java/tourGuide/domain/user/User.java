package tourGuide.domain.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

public class User {
	private final UUID userId;
	private final String userName;
	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;
	private List<VisitedLocation> visitedLocations = new ArrayList<>();
	private List<UserReward> userRewards = new ArrayList<>();
//	private List<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>();
//	private List<UserReward> userRewards = new CopyOnWriteArrayList<>();
	private UserPreferences userPreferences = new UserPreferences();
	private List<Provider> tripDeals = new ArrayList<>();

	// Lock for thread synchronization for visitedLocations list
//	ReadWriteLock visitedLocationLock = new ReentrantReadWriteLock();
//	Lock readVisitedLocationLock = visitedLocationLock.readLock();
//	Lock writeVisitedLocationLock = visitedLocationLock.writeLock();

	// Lock for thread synchronization for userRewards list
//	ReadWriteLock userRewardLock = new ReentrantReadWriteLock();
//	Lock readUserRewardLock = visitedLocationLock.readLock();
//	Lock writeUserRewardLock = visitedLocationLock.writeLock();

	private Lock visitedLocationsLock = new ReentrantLock();
	private Lock userRewardLock = new ReentrantLock();

	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

	public UUID getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
		this.latestLocationTimestamp = latestLocationTimestamp;
	}
	
	public Date getLatestLocationTimestamp() {
		return latestLocationTimestamp;
	}

//	public void addToVisitedLocations(VisitedLocation visitedLocation) {
//		writeVisitedLocationLock.lock();
//		visitedLocations.add(visitedLocation);
//		writeVisitedLocationLock.unlock();
//	}
//
//	public List<VisitedLocation> getVisitedLocations() {
//		readVisitedLocationLock.lock();
//		try {
//			return new ArrayList<>(visitedLocations);
//		} finally {
//			readVisitedLocationLock.unlock();
//		}
//	}
//
//	public void clearVisitedLocations() {
//		writeVisitedLocationLock.lock();
//		visitedLocations.clear();
//		writeVisitedLocationLock.unlock();
//	}
//
//	public void addUserReward(UserReward userReward) {
//		writeUserRewardLock.lock();
//		if(userRewards.stream().filter(r -> !r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
//			userRewards.add(userReward);
//		}
//		writeUserRewardLock.unlock();
//	}
//
//	public List<UserReward> getUserRewards() {
//		readUserRewardLock.lock();
//		try {
//			return userRewards;
//		} finally {
//			readUserRewardLock.unlock();
//		}
//	}



//	public void addToVisitedLocations(VisitedLocation visitedLocation) {
//		visitedLocationsLock.lock();
//		visitedLocations.add(visitedLocation);
//		visitedLocationsLock.unlock();
//	}
//
//	public List<VisitedLocation> getVisitedLocations() {
//		visitedLocationsLock.lock();
//		try {
//			return new ArrayList<>(visitedLocations);
//		} finally {
//			visitedLocationsLock.unlock();
//		}
//	}
//
//	public void clearVisitedLocations() {
//		visitedLocationsLock.lock();
//		visitedLocations.clear();
//		visitedLocationsLock.unlock();
//	}
//
//	public void addUserReward(UserReward userReward) {
//		userRewardLock.lock();
//		if(userRewards.stream().filter(r -> !r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
//			userRewards.add(userReward);
//		}
//		userRewardLock.unlock();
//	}
//
//	public List<UserReward> getUserRewards() {
//		userRewardLock.lock();
//		try {
//			return userRewards;
//		} finally {
//			userRewardLock.unlock();
//		}
//	}


//	public void addToVisitedLocations(VisitedLocation visitedLocation) {
//		visitedLocations.add(visitedLocation);
//	}
//
//	public List<VisitedLocation> getVisitedLocations() {
//		return new ArrayList<>(visitedLocations);
//	}
//
//	public void clearVisitedLocations() {
//		visitedLocations.clear();
//	}
//
//	public void addUserReward(UserReward userReward) {
//		if(userRewards.stream().filter(r -> !r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
//			userRewards.add(userReward);
//		}
//	}
//
//	public List<UserReward> getUserRewards() {
//		return userRewards;
//	}


	public void addToVisitedLocations(VisitedLocation visitedLocation) {
		visitedLocationsLock.lock();
		visitedLocations.add(visitedLocation);
		visitedLocationsLock.unlock();
	}

	public List<VisitedLocation> getVisitedLocations() {
		visitedLocationsLock.lock();
		try {
			return new ArrayList<>(visitedLocations);
		} finally {
			visitedLocationsLock.unlock();
		}
	}

	public void clearVisitedLocations() {
		visitedLocationsLock.lock();
		visitedLocations.clear();
		visitedLocationsLock.unlock();
	}

	public synchronized void addUserReward(UserReward userReward) {
		if(userRewards.stream().filter(r -> !r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
			userRewards.add(userReward);
		}
	}

	public List<UserReward> getUserRewards() {
		return userRewards;
	}



	public UserPreferences getUserPreferences() {
		return userPreferences;
	}
	
	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	public VisitedLocation getLastVisitedLocation() {
		return visitedLocations.get(visitedLocations.size() - 1);
	}
	
	public void setTripDeals(List<Provider> tripDeals) {
		this.tripDeals = tripDeals;
	}
	
	public List<Provider> getTripDeals() {
		return tripDeals;
	}

}
