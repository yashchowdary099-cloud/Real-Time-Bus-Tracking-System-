package com.bustracking.controller;

import com.bustracking.dto.BusDto;
import com.bustracking.dto.RouteDto;
import com.bustracking.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHomePageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("passenger/home"))
                .andExpect(model().attributeExists("activeBuses", "activeRoutes", "activeTrips"));
    }

    @Test
    void testLiveTrackingPageLoads() throws Exception {
        mockMvc.perform(get("/track"))
                .andExpect(status().isOk())
                .andExpect(view().name("passenger/track"))
                .andExpect(model().attributeExists("buses", "routes", "locations"));
    }

    @Test
    void testRoutesPageLoads() throws Exception {
        mockMvc.perform(get("/routes"))
                .andExpect(status().isOk())
                .andExpect(view().name("passenger/routes"))
                .andExpect(model().attributeExists("routes"));
    }

    @Test
    void testSearchPageLoads() throws Exception {
        mockMvc.perform(get("/search").param("q", "Sathyabama"))
                .andExpect(status().isOk())
                .andExpect(view().name("passenger/search"))
                .andExpect(model().attributeExists("routes", "query"));
    }
}
