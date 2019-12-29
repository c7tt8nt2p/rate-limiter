package com.mycomp;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class HotelControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testCityApi() throws Exception {
        mvc.perform(get("/api/v1/hotels/city?city=Bangkok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(7)));
    }

    @Test
    public void testCityApi_WithPriceSorting() throws Exception {
        mvc.perform(get("/api/v1/hotels/city?city=Bangkok&priceSorting=ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(7)))
                .andExpect(jsonPath("$[0].HOTELID", Matchers.equalTo(11)))
                .andExpect(jsonPath("$[0].PRICE", Matchers.equalTo(60.0)))
                .andExpect(jsonPath("$[6].HOTELID", Matchers.equalTo(14)))
                .andExpect(jsonPath("$[6].PRICE", Matchers.equalTo(25000.0)));

        mvc.perform(get("/api/v1/hotels/city?city=Bangkok&priceSorting=DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(7)))
                .andExpect(jsonPath("$[0].HOTELID", Matchers.equalTo(14)))
                .andExpect(jsonPath("$[0].PRICE", Matchers.equalTo(25000.0)))
                .andExpect(jsonPath("$[6].HOTELID", Matchers.equalTo(11)))
                .andExpect(jsonPath("$[6].PRICE", Matchers.equalTo(60.0)));
    }

    @Test
    public void testRoomApi() throws Exception {
        mvc.perform(get("/api/v1/hotels/room?room=Superior"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(9)));
    }

    @Test
    public void testRoomApi_WithPriceSorting() throws Exception {
        mvc.perform(get("/api/v1/hotels/room?room=Superior&priceSorting=ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(9)))
                .andExpect(jsonPath("$[0].HOTELID", Matchers.equalTo(16)))
                .andExpect(jsonPath("$[0].PRICE", Matchers.equalTo(800.0)))
                .andExpect(jsonPath("$[8].HOTELID", Matchers.equalTo(20)))
                .andExpect(jsonPath("$[8].PRICE", Matchers.equalTo(4444.0)));

        mvc.perform(get("/api/v1/hotels/room?room=Superior&priceSorting=DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(9)))
                .andExpect(jsonPath("$[0].HOTELID", Matchers.equalTo(20)))
                .andExpect(jsonPath("$[0].PRICE", Matchers.equalTo(4444.0)))
                .andExpect(jsonPath("$[8].HOTELID", Matchers.equalTo(16)))
                .andExpect(jsonPath("$[8].PRICE", Matchers.equalTo(800.0)));
    }

}
