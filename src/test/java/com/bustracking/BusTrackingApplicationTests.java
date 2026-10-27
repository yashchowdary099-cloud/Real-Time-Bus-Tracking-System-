package com.bustracking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BusTrackingApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context, configuration beans, and JPA repositories bootstrap properly
    }
}
