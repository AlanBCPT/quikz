package com.example.quikz;

import com.example.quikz.ws.GameWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuikzApplicationTest {
    @Autowired
    private GameWebSocketHandler handler;

    @Test
    void applicationContextStarts() {
        assertThat(handler).isNotNull();
    }
}
