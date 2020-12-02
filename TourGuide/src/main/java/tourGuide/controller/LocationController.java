package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.domain.user.User;
import tourGuide.service.LocationService;
import tourGuide.service.UserService;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @RequestMapping("/location")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = locationService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    @RequestMapping("/all-current-locations")
    public String getAllCurrentLocations() {
        return JsonStream.serialize(locationService.getAllUsersLocations());
    }

    @RequestMapping("/nearby-attractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = locationService.getUserLocation(getUser(userName));
        return JsonStream.serialize(locationService.getClosestAttractions(visitedLocation));
    }

    private User getUser(String userName) {
        return userService.getUser(userName);
    }
}
