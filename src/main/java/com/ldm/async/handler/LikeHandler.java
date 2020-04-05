package com.ldm.async.handler;

import com.ldm.async.EventHandler;
import com.ldm.async.EventModel;
import com.ldm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class LikeHandler implements EventHandler {

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {

    }
}
