package cn.hanabi.modules.modules.movement;


import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.BlockUtils;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockLiquid;


public class WaterSpeed extends Mod {

    public static Value<Double> speed = new Value<>("WaterSpeed", "Speed ", 1.2d, 1.0d, 1.5d, 0.1d);


    public WaterSpeed() {
        super("WaterSpeed", Category.MOVEMENT);
    }


    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.isInWater() && BlockUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid) {
            mc.thePlayer.motionX *= speed.getValue();
            mc.thePlayer.motionZ *= speed.getValue();
        }
    }

}