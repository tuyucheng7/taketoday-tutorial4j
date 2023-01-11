package cn.tuyucheng.taketoday.springsecuredsockets.controllers;

import cn.tuyucheng.taketoday.springsecuredsockets.transfer.socket.Message;
import cn.tuyucheng.taketoday.springsecuredsockets.transfer.socket.OutputMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static cn.tuyucheng.taketoday.springsecuredsockets.Constants.SECURED_CHAT;
import static cn.tuyucheng.taketoday.springsecuredsockets.Constants.SECURED_CHAT_HISTORY;
import static cn.tuyucheng.taketoday.springsecuredsockets.Constants.SECURED_CHAT_ROOM;
import static cn.tuyucheng.taketoday.springsecuredsockets.Constants.SECURED_CHAT_SPECIFIC_USER;

@Controller
public class SocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private static final Logger log = LoggerFactory.getLogger(SocketController.class);

    @MessageMapping(SECURED_CHAT)
    @SendTo(SECURED_CHAT_HISTORY)
    public OutputMessage sendAll(Message msg) throws Exception {
        OutputMessage out = new OutputMessage(msg.getFrom(), msg.getText(), new SimpleDateFormat("HH:mm").format(new Date()));
        return out;
    }

    /**
     * Example of sending message to specific user using 'convertAndSendToUser()' and '/queue'
     */
    @MessageMapping(SECURED_CHAT_ROOM)
    public void sendSpecific(@Payload Message msg, Principal user, @Header("simpSessionId") String sessionId) throws Exception {
        OutputMessage out = new OutputMessage(msg.getFrom(), msg.getText(), new SimpleDateFormat("HH:mm").format(new Date()));
        simpMessagingTemplate.convertAndSendToUser(msg.getTo(), SECURED_CHAT_SPECIFIC_USER, out);
    }
}
