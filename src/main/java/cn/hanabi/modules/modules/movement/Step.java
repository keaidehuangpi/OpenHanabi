package cn.hanabi.modules.modules.movement;

import aLph4anTi1eaK_cN.Annotation.ObfuscationMethod;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventStep;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.injection.interfaces.IMinecraft;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.movement.Speed.Speed;
import cn.hanabi.utils.BlockUtils;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;


@ObfuscationMethod
public class Step extends Mod {
    public Value<String> mode = new Value<>("Step", "Mode", 0);
    public Value<Double> height = new Value<>("Step", "Height", 1.0D, 1.0D, 1.5D, 0.5D);
    public Value<Double> delay = new Value<>("Step", "Delay", 0.0D, 0.0D, 1000.0D, 50.0D);
    TimeHelper timer = new TimeHelper();
    boolean resetTimer;


    public Step() {
        super("Step", Category.MOVEMENT);
        mode.LoadValue(new String[]{"Vanilla", "NCP", "Hyt", "Test"});
    }

    @Override
    public void onEnable() {
        resetTimer = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.stepHeight = 0.625f;
        }
        ((IMinecraft) mc).getTimer().timerSpeed = 1.0F;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        setDisplayName(this.mode.getModeAt(this.mode.getCurrentMode()));
        if (((IMinecraft) mc).getTimer().timerSpeed < 1 && mc.thePlayer.onGround) {
            ((IMinecraft) mc).getTimer().timerSpeed = 1;
        }
    }

    @EventTarget
    public void onStep(EventStep event) {
        if (BlockUtils.isInLiquid() || ModManager.getModule(Speed.class).isEnabled()) {
            mc.thePlayer.stepHeight = 0.5F;
            return;
        }
        if (mode.isCurrentMode("Vanilla")) {
            event.setHeight(mc.thePlayer.stepHeight = height.getValueState().floatValue());
        }
        if (mode.isCurrentMode("NCP")) {
            if (event.getEventType() == EventType.PRE) {
                if (this.resetTimer) {
                    this.resetTimer = false;
                    Wrapper.getTimer().timerSpeed = 1.0f;
                }
                if (mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.isKeyDown() && timer.isDelayComplete(delay.getValue())) {
                    event.setHeight(mc.thePlayer.stepHeight = height.getValue().floatValue());
                }
            }
            if (event.getEventType() == EventType.POST) {
                final double realHeight = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
                if (realHeight >= .625) {
                    timer.reset();
                    Wrapper.getTimer().timerSpeed = .4F;
                    resetTimer = true;
                    doNCPStep(realHeight);
                }
            }
        }


    }


    @ObfuscationMethod
    private void doNCPStep(double height) {

        final double posX = mc.thePlayer.posX, posY = mc.thePlayer.posY, posZ = mc.thePlayer.posZ;

        Wrapper.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        mc.thePlayer.setSprinting(false);

        if (height <= 1) {

            final float[] values = {
                    .42F,
                    .75F
            };

            if (height != 1) {
                values[0] *= height;
                values[1] *= height;

                if (values[0] > .425) values[0] = .425F;
                if (values[1] > .78) values[1] = .78F;
                if (values[1] < .49) values[1] = .49F;
            }

            if (values[0] == .42) values[0] = .41999998688698F;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + values[0], posZ, false));

            if (posY + values[1] < posY + height)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + values[1], posZ, false));
        } else if (height <= 1.5) {

            final float[] values = {
                    .41999998688698F,
                    .7531999805212F,
                    1.00133597911215F,
                    1.06083597911215F,
                    0.9824359775862711F
            };

            for (double val : values)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + val, posZ, false));
        }

        Wrapper.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        mc.thePlayer.stepHeight = 0.625F;
    }
}

