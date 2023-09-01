package com.sky.task;

import com.sky.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketTask {
    @Autowired
    WebSocketServer webSocketServer;

    // @Scheduled(cron = "0/5 * * * * ?")
    // public void sendMessageToClient() {
    //     webSocketServer.sendToAllClient("hello world");
    // }
}
