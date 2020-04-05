package com.ldm.async;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class EventModel {
    private EventType type;
    private int actorId;
    private int entityType;
    private int entityId;
    private int entityOwnerId;
    private Map<String, String> exts = new HashMap();
}
