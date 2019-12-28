package com.mycomp.service.data;

import com.mycomp.data.Hotel;
import com.mycomp.data.HotelRepository;
import com.mycomp.data.Sort;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelDataService {

    private final HotelRepository hotelRepository;

    public HotelDataService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> findAll() {
        return IterableUtils.toList(hotelRepository.findAll());
    }

    public List<Hotel> findByCity(String city) {
        return findByCity(city, Sort.ASC);
    }

    public List<Hotel> findByCity(String city, Sort sort) {
        if (Sort.ASC == sort) {
            return hotelRepository.findByCityOrderByPriceAsc(city);
        }
        return hotelRepository.findByCityOrderByPriceDesc(city);
    }

    public List<Hotel> findByRoom(String room) {
        return findByRoom(room, Sort.ASC);
    }

    public List<Hotel> findByRoom(String city, Sort sort) {
        if (Sort.ASC == sort) {
            return hotelRepository.findByRoomOrderByPriceAsc(city);
        }
        return hotelRepository.findByRoomOrderByPriceDesc(city);
    }

    public Hotel save(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public List<Hotel> saveAll(List<Hotel> hotelList) {
        return IterableUtils.toList(hotelRepository.saveAll(hotelList));
    }


    public void delete(Hotel hotel) {
        hotelRepository.delete(hotel);
    }

    public void deleteAll() {
        hotelRepository.deleteAll();
    }

}
