package com.example.budd_server.config;

import com.example.budd_server.utils.MediaStreamHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MediaStreamHandler mediaStreamHandler;

    public WebSocketConfig(MediaStreamHandler mediaStreamHandler) {
        this.mediaStreamHandler = mediaStreamHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mediaStreamHandler, "/media-stream").setAllowedOrigins("*");
    }
}
