package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        for(WebSeries webSeries : webSeriesList){
            if(webSeries.getSeriesName().equals(webSeriesEntryDto.getSeriesName())){
                throw new Exception("Series is already present");
            }
        }

        int productionHouseId =  webSeriesEntryDto.getProductionHouseId();
        ProductionHouse productionHouse = productionHouseRepository.findById(productionHouseId).get();
        double productionHouseRating = productionHouse.getRatings();
        int len = productionHouse.getWebSeriesList().size();
        double newRating = (productionHouseRating*len + webSeriesEntryDto.getRating())/(len+1);
        productionHouse.setRatings(newRating);

        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(productionHouse);

        productionHouse.getWebSeriesList().add(webSeries);
        WebSeries webSeries1 = webSeriesRepository.save(webSeries);
        productionHouseRepository.save(productionHouse);

        return webSeries1.getId();
    }

}
