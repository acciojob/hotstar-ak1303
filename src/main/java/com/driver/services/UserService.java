package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent())return null;

        User user = optionalUser.get();
        int userAge = user.getAge();
        SubscriptionType userSubscriptionType = user.getSubscription().getSubscriptionType();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int count = 0;
        for(WebSeries webSeries : webSeriesList){
            int ageLimit = webSeries.getAgeLimit();
            SubscriptionType webSeriesSubscriptionType = webSeries.getSubscriptionType();
            if(ageLimit <= userAge) {
                if(userSubscriptionType == SubscriptionType.ELITE ||
                        userSubscriptionType == webSeriesSubscriptionType ||
                        (userSubscriptionType.equals(SubscriptionType.PRO) && webSeriesSubscriptionType.equals(SubscriptionType.BASIC)))
                {
                    count++;
                }
            }
        }
        return count;
    }


}
