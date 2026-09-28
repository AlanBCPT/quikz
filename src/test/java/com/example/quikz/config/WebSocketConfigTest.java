package com.example.quikz.config;

import com.example.quikz.protocol.GameProtocol;
import com.example.quikz.ws.GameWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketConfigTest {
    @Test
    void registersTheGameHandlerAtTheGamePath() {
        GameWebSocketHandler handler = mock(GameWebSocketHandler.class);
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);
        WebSocketHandlerRegistration registration = mock(WebSocketHandlerRegistration.class);
        when(registry.addHandler(handler, GameProtocol.WEB_SOCKET_PATH)).thenReturn(registration);
        when(registration.setAllowedOrigins("*")).thenReturn(registration);

        new WebSocketConfig(handler).registerWebSocketHandlers(registry);

        verify(registry).addHandler(handler, GameProtocol.WEB_SOCKET_PATH);
        verify(registration).setAllowedOrigins("*");
    }
}
