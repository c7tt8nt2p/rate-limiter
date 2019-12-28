package com.mycomp.data.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycomp.data.Hotel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelJson {
    @JsonProperty("CITY")
    private String city;
    @JsonProperty("HOTELID")
    private int hotelId;
    @JsonProperty("ROOM")
    private String room;
    @JsonProperty("PRICE")
    private double price;

    public Hotel toModel() {
        Hotel hotel = new Hotel();
        hotel.setCity(this.getCity());
        hotel.setHotelId(this.getHotelId());
        hotel.setRoom(this.getRoom());
        hotel.setPrice(this.getPrice());
        return hotel;
    }

}
