package cn.hanabi.modules.modules.movement;

import cn.hanabi.events.EventPacket;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;


public class HighJump extends Mod {

    private final Value<String> mode = new Value<>("HighJump", "Mode", 0);
    private final Value<Double> boost = new Value<>("HighJump", "Boost", 0.5D, 0.1D, 5.0D, 0.05D);
    int counter = 0;
    int counter2 = 0;
    TimeHelper wait = new TimeHelper();

    public HighJump() {
        super("HighJump", Category.MOVEMENT);
        mode.addValue("Vanilla");
        mode.addValue("Hypixel");
    }

    @Override
    public void onEnable() {
        counter = 0;
        counter2 = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


    @EventTarget
    public void onPacket(EventPacket e) {
        if ((mc.thePlayer.onGround) && (mc.gameSettings.keyBindForward.isPressed())
                && (this.wait.isDelayComplete(500L) && mode.isCurrentMode("Vanilla"))) {
            mc.thePlayer.motionY = this.boost.getValueState();
            this.wait.reset();
        }

        boolean blockUnderneath = false;
        int i = 0;
        while (i < mc.thePlayer.posY + 2.0) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                blockUnderneath = true;
            }
            ++i;
        }
        if (mode.isCurrentMode("Hypixel")) {
            if (!blockUnderneath) {
                if (e.getPacket() instanceof C03PacketPlayer) {
                    if (mc.thePlayer.fallDistance > 8.0F) {
                        mc.thePlayer.motionY = this.boost.getValueState();
                    }
                }
            }
        }
    }
}