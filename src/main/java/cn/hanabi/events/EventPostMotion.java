package cn.hanabi.events;

import com.darkmagician6.eventapi.events.Event;

public class EventPostMotion implements Event {

    public EventPostMotion(float pitch) {
        //Memory prevPitch and Pitch for rotation animation
        EventPreMotion.RPPITCH = EventPreMotion.RPITCH;
        EventPreMotion.RPITCH = pitch;
    }
}
