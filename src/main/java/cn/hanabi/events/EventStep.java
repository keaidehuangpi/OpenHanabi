package cn.hanabi.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.types.EventType;

public class EventStep implements Event {
    private final EventType eventType;
    /**
     * The step height.
     */
    private float height;

    public EventStep(EventType eventType, float height) {
        this.eventType = eventType;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public EventType getEventType() {
        return eventType;
    }
}