package cn.hanabi.modules.modules.ghost;

import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;


public class LegitVelocity extends Mod {

    public Value<Double> chance = new Value<>("LegitVelocity", "Chance", 100d, 0d, 100d, 1d);

    public Value<Double> verti = new Value<>("LegitVelocity", "Vertical", 100d, 0d, 100d, 1d);

    public Value<Double> hori = new Value<>("LegitVelocity", "Horizontal", 100d, 0d, 100d, 1d);

    public LegitVelocity() {
        super("LegitVelocity", Category.GHOST);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {

        if (mc.thePlayer.maxHurtResistantTime != mc.thePlayer.hurtResistantTime || mc.thePlayer.maxHurtResistantTime == 0) {
            return;
        }

        double random = Math.random();
        random *= 100.0;

        if (random < this.chance.getValueState().intValue()) {
            float hori = this.hori.getValueState().floatValue();
            hori /= 100.0f;
            float verti = this.verti.getValueState().floatValue();
            verti /= 100.0f;
            mc.thePlayer.motionX *= hori;
            mc.thePlayer.motionZ *= hori;
            mc.thePlayer.motionY *= verti;
        } else {
            mc.thePlayer.motionX *= 1.0f;
            mc.thePlayer.motionY *= 1.0f;
            mc.thePlayer.motionZ *= 1.0f;
        }
    }

}
