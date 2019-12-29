package com.mycomp;

import com.mycomp.service.LimiterService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class HotelControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LimiterService limiterService;

    @Value("${endpoint.city.requests.limit.every.5.seconds}")
    private int cityRequestLimit;
    @Value("${endpoint.room.requests.limit.every.10.seconds}")
    private int roomRequestLimit;
    @Value("${endpoint.any.requests.limit.per.10.seconds:50}")
    private int defaultRequestLimit;

    @Test
    public void testCityApi() throws Exception {
        waitForCityBucketIsEmptyAndUnlocked();
        waitForRoomBucketIsEmptyAndUnlocked();
        mvc.perform(get("/api/v1/hotels/city?city=Bangkok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(7)));
    }

    @Test
    public void testCityApi_WithPriceSorting() throws Exception {
        waitForCityBucketIsEmptyAndUnlocked();
        waitForRoomBucketIsEmptyAndUnlocked();
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
    public void testCityApi_RateLimiter() throws Exception {
        waitForCityBucketIsEmptyAndUnlocked();
        waitForRoomBucketIsEmptyAndUnlocked();
        int limit = (cityRequestLimit > 0) ? cityRequestLimit : defaultRequestLimit;
        for (int i = 1; i <= limit + 5; i++) {
            int finalI = i;
            await().pollDelay(10, TimeUnit.MILLISECONDS).until(() -> {
                if (finalI > limit) {
                    mvc.perform(get("/api/v1/hotels/city?city=Bangkok"))
                            .andExpect(status().isTooManyRequests());
                    mvc.perform(get("/api/v1/hotels/room?room=Superior"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$").isArray())
                            .andExpect(jsonPath("$.length()", Matchers.equalTo(9)));
                } else {
                    mvc.perform(get("/api/v1/hotels/city?city=Bangkok"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$").isArray())
                            .andExpect(jsonPath("$.length()", Matchers.equalTo(7)));
                }
                return true;
            });
        }
    }

    @Test
    public void testRoomApi() throws Exception {
        waitForCityBucketIsEmptyAndUnlocked();
        waitForRoomBucketIsEmptyAndUnlocked();
        mvc.perform(get("/api/v1/hotels/room?room=Superior"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(9)));
    }

    @Test
    public void testRoomApi_WithPriceSorting() throws Exception {
        waitForCityBucketIsEmptyAndUnlocked();
        waitForRoomBucketIsEmptyAndUnlocked();
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

    @Test
    public void testRoomApi_RateLimiter() throws Exception {
        waitForCityBucketIsEmptyAndUnlocked();
        waitForRoomBucketIsEmptyAndUnlocked();
        int limit = (roomRequestLimit > 0) ? roomRequestLimit : defaultRequestLimit;
        for (int i = 1; i <= limit + 5; i++) {
            int finalI = i;
            await().pollDelay(10, TimeUnit.MILLISECONDS).until(() -> {
                if (finalI > limit) {
                    mvc.perform(get("/api/v1/hotels/room?room=Superior"))
                            .andExpect(status().isTooManyRequests());
                    mvc.perform(get("/api/v1/hotels/city?city=Bangkok"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$").isArray())
                            .andExpect(jsonPath("$.length()", Matchers.equalTo(7)));
                } else {
                    mvc.perform(get("/api/v1/hotels/room?room=Superior"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$").isArray())
                            .andExpect(jsonPath("$.length()", Matchers.equalTo(9)));
                }
                return true;
            });
        }
    }

    private void waitForCityBucketIsEmptyAndUnlocked() {
        await().atMost(5 * 2, TimeUnit.SECONDS).until(() -> !limiterService.getCityBucketLock().get() && limiterService.getCityBucket().isEmpty());
    }

    private void waitForRoomBucketIsEmptyAndUnlocked() {
        await().atMost(10 * 2, TimeUnit.SECONDS).until(() -> !limiterService.getRoomBucketLock().get() && limiterService.getRoomBucket().isEmpty());
    }
}
