package com.armin.controller;

import com.armin.ws.WebSocket;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/webSocket")
public class WebSocketController {
    @Resource
    private WebSocket webSocket;
    @PostMapping("/sentMessage")
    public void sentMessage(String userId,String message){
        try {
            webSocket.sendMessageByUserId(userId,message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

