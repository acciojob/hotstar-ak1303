package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        //userId, subscription type, no of screen
        int userId = subscriptionEntryDto.getUserId();
        String subType = subscriptionEntryDto.getSubscriptionType().toString();
        int noOfScreenRequired =  subscriptionEntryDto.getNoOfScreensRequired();

        int subCost;
        if(subType.equals("BASIC")) {
            subCost = 500 + noOfScreenRequired*(200);
        }
        else if(subType.equals("PRO")) {
            subCost = 800 + noOfScreenRequired*(250);
        }
        else {
            subCost = 1000 + noOfScreenRequired*(350);
        }

        User user = userRepository.findById(userId).get();

        Subscription subscription  = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setStartSubscriptionDate(new Date());
        subscription.setNoOfScreensSubscribed(noOfScreenRequired);
        subscription.setTotalAmountPaid(subCost);
        subscription.setUser(user);//foreign key userId in subscription will not be null now

        user.setSubscription(subscription);
        userRepository.save(user);
        return subCost;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)) {
            throw new Exception("Already the best Subscription");
        }
        else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.PRO)) {
            //800+250*screen  1000+350*screen  => 200+100*screen
            int noOfScreen = user.getSubscription().getNoOfScreensSubscribed();
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
            user.getSubscription().setTotalAmountPaid(user.getSubscription().getTotalAmountPaid()+(200+noOfScreen*100));
            userRepository.save(user);
            return (200+noOfScreen*100);
        }else {
            //500+200*screen 800+250*screen => 300+50*screen
            int noOfScreen = user.getSubscription().getNoOfScreensSubscribed();
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
            user.getSubscription().setTotalAmountPaid(user.getSubscription().getTotalAmountPaid()+(300+noOfScreen*50));
            userRepository.save(user);
            return (300+noOfScreen*50);
        }
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
         List<Subscription> subscriptionList= subscriptionRepository.findAll();
         int revenue = 0;
        for (Subscription subscription : subscriptionList) {
            revenue += subscription.getTotalAmountPaid();
        }
         return revenue;
    }

}
