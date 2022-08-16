package cn.hanabi.modules.modules.ghost;

import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;


public class Hitbox extends Mod {


    public static Value<Double> minsize = new Value<>("Hitbox", "Min-Size", 0.1d, 0.1d, 0.8d, 0.01d);
    public static Value<Double> maxsize = new Value<>("Hitbox", "Max-Size", 0.25d, 0.1d, 1.0d, 0.01d);

    public Hitbox() {
        super("Hitbox", Category.GHOST);
    }

    public static float getSize() {
        double min = Math.min(minsize.getValue(), maxsize.getValue());
        double max = Math.max(minsize.getValue(), maxsize.getValue());
        return (float) (ModManager.getModule("Hitbox").isEnabled() ? Math.random() * (max - min) + min : 0.1f);
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        this.setDisplayName("Size: " + maxsize.getValueState());
    }
}
