package com.sky.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Feat: Send message with WebSocket
@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {
    private static Map<String, Session> sessionMap = new HashMap();

    // invoke after session connection
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("session connected, sid: {}", sid);
        sessionMap.put(sid, session);
    }

    // invoke after client sends message
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("client send message, sid: {}, message: {}", sid, message);
    }

    // close session connection
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("session closed, sid: {}", sid);
        sessionMap.remove(sid);
    }

    // send message to all client
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
