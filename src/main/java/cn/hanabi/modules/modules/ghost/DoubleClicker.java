package cn.hanabi.modules.modules.ghost;

import cn.hanabi.events.EventMouse;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.ReflectionUtils;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;

import java.util.ArrayList;

public class DoubleClicker extends Mod {

    public Value<Double> delay = new Value<>("DoubleClicker", "Delay", 50.0d, 0.0d, 500.0d, 5.0d);
    public Value<Double> random = new Value<>("DoubleClicker", "Random", 50.0d, 0.0d, 250.0d, 1.0d);
    ArrayList<Long> clickTimes = new ArrayList<>();
    private boolean skip = false;


    public DoubleClicker() {
        super("DoubleClicker", Category.GHOST);
    }


    @EventTarget
    public void onClick(EventUpdate e) {
        for (int i = 0; i < this.clickTimes.size(); ++i) {
            long time = this.clickTimes.get(i);
            this.skip = true;
            ReflectionUtils.setLeftClickCounter(0);
            ReflectionUtils.clickMouse();
            this.clickTimes.remove(i);
        }
    }


    @EventTarget
    public void Click(EventMouse event) {
        if (this.skip) {
            this.skip = false;
            return;
        }
        if (mc.theWorld == null) {
            return;
        }
        if (mc.thePlayer == null) {
            return;
        }
        if (!mc.thePlayer.isEntityAlive()) {
            return;
        }
        if (mc.thePlayer.isUsingItem() && !mc.thePlayer.isBlocking()) {
            return;
        }
        long MS = this.getCurrentMS();
        double Delay = delay.getValueState();
        double RDelay = this.randomInt(random.getValueState().intValue());
        this.clickTimes.add((long) ((double) MS + Delay + RDelay));
    }

    @Override
    public void onDisable() {
        clickTimes.clear();
    }


}
