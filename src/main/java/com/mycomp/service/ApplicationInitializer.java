package com.mycomp.service;

import com.mycomp.data.Hotel;
import com.mycomp.data.json.HotelJson;
import com.mycomp.service.data.HotelDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationInitializer {

    private final HotelDataService hotelDataService;
    private final CSVService csvService;

    @Value("${hoteldb.csv.file}")
    private String hotelDbFile;

    @Autowired
    public ApplicationInitializer(HotelDataService hotelDataService, CSVService csvService) {
        this.hotelDataService = hotelDataService;
        this.csvService = csvService;
    }

    @PostConstruct
    public void init() {
        hotelDataService.deleteAll();
        List<Hotel> hotelList = csvService.loadHotelData(hotelDbFile)
                .stream()
                .map(HotelJson::toModel)
                .collect(Collectors.toList());
        hotelDataService.saveAll(hotelList);
    }


}
