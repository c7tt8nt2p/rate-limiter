package com.mycomp.data;

import com.mycomp.data.json.HotelJson;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private int hotelId;
    private String room;
    private double price;

    public HotelJson toJson() {
        HotelJson hotelJson = new HotelJson();
        hotelJson.setCity(getCity());
        hotelJson.setHotelId(getHotelId());
        hotelJson.setRoom(getRoom());
        hotelJson.setPrice(getPrice());
        return hotelJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
