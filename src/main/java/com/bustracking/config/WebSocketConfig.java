package com.bustracking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Spring WebSocket with STOMP Configuration
 * Enables real-time pub/sub location broadcasting to passenger and admin clients.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Topic prefix for outbound broadcasts from server to subscribers
        registry.enableSimpleBroker("/topic");
        // Inbound prefix for messages from clients to server handlers
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint with SockJS fallback
        registry.addEndpoint("/ws-bus-tracking")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Native WebSocket endpoint for non-SockJS STOMP clients
        registry.addEndpoint("/ws-bus-tracking")
                .setAllowedOriginPatterns("*");
    }
}
