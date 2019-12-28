package com.mycomp.controller;

import com.mycomp.data.Hotel;
import com.mycomp.data.Sort;
import com.mycomp.data.json.HotelJson;
import com.mycomp.service.data.HotelDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/hotels", produces = "application/json")
public class HotelController {

    private final HotelDataService hotelDataService;

    @Autowired
    public HotelController(HotelDataService hotelDataService) {
        this.hotelDataService = hotelDataService;
    }

    @GetMapping(value = "/city")
    public ResponseEntity<List<HotelJson>> city(
            @RequestParam(value = "city") String city,
            @RequestParam(value = "priceSorting", defaultValue = "ASC", required = false) Sort sort
    ) {
        List<HotelJson> hotelByCityList = hotelDataService.findByCity(city, sort)
                .stream()
                .map(Hotel::toJson)
                .collect(Collectors.toList());
        return ResponseEntity.ok(hotelByCityList);
    }

    @GetMapping(value = "/room")
    public ResponseEntity<List<HotelJson>> room(
            @RequestParam(value = "room") String room,
            @RequestParam(value = "priceSorting", defaultValue = "ASC", required = false) Sort sort
    ) {
        List<HotelJson> hotelByCityList = hotelDataService.findByRoom(room, sort)
                .stream()
                .map(Hotel::toJson)
                .collect(Collectors.toList());
        return ResponseEntity.ok(hotelByCityList);
    }
}
