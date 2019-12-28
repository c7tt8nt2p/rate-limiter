package com.mycomp.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends CrudRepository<Hotel, Long> {

    List<Hotel> findByCityOrderByPriceAsc(String city);

    List<Hotel> findByCityOrderByPriceDesc(String city);

    List<Hotel> findByCityIgnoreCase(String city);

    List<Hotel> findByRoomOrderByPriceAsc(String room);

    List<Hotel> findByRoomOrderByPriceDesc(String room);

    List<Hotel> findByRoomIgnoreCase(String room);
}
