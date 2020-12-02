package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.domain.user.UserPreferences;
import tourGuide.repository.UserRepository;
import tourGuide.domain.user.User;
import tourGuide.tracker.Tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    boolean testMode = true;

//    public final Tracker tracker;
//    ExecutorService executorService = Executors.newFixedThreadPool(1000);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

        if(testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            userRepository.initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        //TODO: fix tangled up Tracker dependencies
//        tracker = new Tracker(this);
//		addShutDownHook();
    }

    public List<String> getAllUsersNames() {
        List<String> usersNames = new ArrayList<>();
        List<User> users = new ArrayList<>(userRepository.getInternalUserMap().values());
        users.stream().forEach(u -> usersNames.add(u.getUserName()));
        return usersNames;
    }

    public User getUser(String userName) {
        return userRepository.getInternalUserMap().get(userName);
    }

    public List<User> getAllUsers() {
        return userRepository.getInternalUserMap().values().stream().collect(Collectors.toList());
    }

    public void addUser(User user) {
        if(!userRepository.getInternalUserMap().containsKey(user.getUserName())) {
            userRepository.getInternalUserMap().put(user.getUserName(), user);
        }
    }

    public UserPreferences getUserPreferences(String userName) {
        if(userRepository.getInternalUserMap().containsKey(userName)) {
            return userRepository.getInternalUserMap().get(userName).getUserPreferences();
        }
        return null;
    }

    public void setUserPreferences(String userName, UserPreferences userPreferences) {
        User user = getUser(userName);
        user.setUserPreferences(userPreferences);
//        userRepository.getInternalUserMap().put(user.getUserId().toString(), user);
        //TODO: How to send preferences to TripPricer?

    }

//    private void addShutDownHook() {
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
////                tracker.stopTracking();
//            }
//        });
//	}
}
