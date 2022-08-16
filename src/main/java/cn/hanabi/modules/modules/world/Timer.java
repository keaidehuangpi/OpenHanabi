package cn.hanabi.modules.modules.world;

import cn.hanabi.Wrapper;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;

public class Timer extends Mod {
    // 我认为还是最大拉到5好，有时候有用
    public Value<Double> speed = new Value<>("Timer", "TimerSpeed", 1.0, 0.1, 2.0, 0.01);

    public Timer() {
        super("Timer", Category.WORLD);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        this.setDisplayName(" " + speed.getValue());
        Wrapper.getTimer().timerSpeed = 1f + speed.getValue().floatValue() - 1f;
    }

    @Override
    public void onDisable() {
        Wrapper.getTimer().timerSpeed = 1F;
    }
}
