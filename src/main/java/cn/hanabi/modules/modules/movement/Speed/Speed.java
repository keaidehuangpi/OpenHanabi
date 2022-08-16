package cn.hanabi.modules.modules.movement.Speed;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Wrapper;
import cn.hanabi.events.*;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import io.netty.util.internal.ThreadLocalRandom;
import org.lwjgl.input.Keyboard;


@ObfuscationClass
public class Speed extends Mod {

    public static Value<String[]> mode = new Value<String[]>("Speed", "Mode", 1)
            .LoadValue(new String[]{"GudHop", "Hypixel", "AAC", "Mineplex", "Verus"});
    public static Value<Double> fall = new Value<>("Speed", "Falling Timer", 1.0, 0.9, 4.0, 0.01);
    private final Speed_Hypixel modeGlobalHypixel = new Speed_Hypixel();
    private final Speed_GudHop modeGudHop = new Speed_GudHop();
    private final Speed_AAC modeAAC = new Speed_AAC();
    private final Speed_Mineplex modeMineplex = new Speed_Mineplex();
    private final Speed_Verus modeVerus = new Speed_Verus();
    public Value<Boolean> lagback = new Value<>("Speed", "Lag Back Checks", true);
    public Value<Boolean> autodisable = new Value<>("Speed", "Auto Disable", true);
    public Value<Boolean> toggle = new Value<>("Speed", "Key Toggle Timer", true);

    public Speed() {
        super("Speed", Category.MOVEMENT);
    }


    @EventTarget
    public void onReload(EventWorldChange e) {
        if (autodisable.getValueState()) {
            this.set(false);
        }
    }


    @EventTarget
    public void onPacket(EventPacket e) {
        if (mode.isCurrentMode("Hypixel")) {
            modeGlobalHypixel.onPacket(e);
        }
    }

    @EventTarget
    public void onStep(EventStep e) {
        if (mode.isCurrentMode("Hypixel")) {
            modeGlobalHypixel.onStep(e);
        }
    }

    @EventTarget
    public void onUpdate(EventPreMotion e) {

        this.setDisplayName(mode.getModeAt(mode.getCurrentMode()));

        Wrapper.getTimer().timerSpeed = mc.thePlayer.fallDistance < 2 ? (float) (toggle.getValue() ? (float) (Keyboard.isKeyDown(Keyboard.KEY_LMENU) ? (1f + (ThreadLocalRandom.current().nextDouble(fall.getValue() - 1, fall.getValue() - 0.89f))) : 1.0f) : (1f + (ThreadLocalRandom.current().nextDouble(fall.getValue() - 1, fall.getValue() - 0.89f)))) : 1.0F;

        if (mode.isCurrentMode("GudHop")) {
            modeGudHop.onPre(e);
        } else
            if (mode.isCurrentMode("Hypixel")) {
                modeGlobalHypixel.onPre(e);
                //        }else if (mode.isCurrentMode("Test")) {
//            return;
            } else
                if (mode.isCurrentMode("AAC")) {
                    modeAAC.onPre(e);
                } else
                    if (mode.isCurrentMode("Mineplex")) {
                        modeMineplex.onUpdate();
                    }
        /*
         * if (mode.isCurrentMode("Hypixel")) { Hypixel.onPre(e); return; }
         */
    }

    @EventTarget
    public void onPost(EventPostMotion e) {
        if (mode.isCurrentMode("Hypixel")) {
            modeGlobalHypixel.onPost(e);
        }
    }

    @EventTarget
    public void onJump(EventJump e) {
        if (mode.isCurrentMode("Hypixel")) {
            modeGlobalHypixel.onJump(e);
        }
    }

    @EventTarget
    public void onPullback(EventPullback e) {
        if (lagback.getValueState()) {
            ClientUtil.sendClientMessage("(LagBackCheck) Speed Disabled", Notification.Type.WARNING);
            set(false);
        }
        /*
         * if (mode.isCurrentMode("HypixelGlobal")) { GlobalHypixel.onPullback(e);
         * return; }
         */
        if (mode.isCurrentMode("Hypixel")) {
            modeGlobalHypixel.onPullback(e);
        }
    }

    @EventTarget
    public void onLoop(EventLoop e){
        if (mode.isCurrentMode("Hypixel")) {
            modeGlobalHypixel.onLoop(e);
        }
    }

    @EventTarget
    public void onMove(EventMove em) {
        /*
         * if (mode.isCurrentMode("HypixelGlobal")) { setDisplayName("HypixelGlobal");
         * GlobalHypixel.onMove(em); return; }
         */

        if (mode.isCurrentMode("Hypixel")) {
            modeGlobalHypixel.onMove(em);
        }else if(mode.isCurrentMode("Verus")){
            modeVerus.onMove(em);
        }
//        if (mode.isCurrentMode("Test")) {
//            return;
//        }
        /*
         * if (mode.isCurrentMode("Hypixel")) { setDisplayName("Hypixel");
         * Hypixel.onMove(em); return; }
         */


    }

    @Override
    public void onDisable() {
        Wrapper.getTimer().timerSpeed = 1.0f;

        if (mode.isCurrentMode("Test")) {
        } else
            if (mode.isCurrentMode("Hypixel")) {
                modeGlobalHypixel.onDisable();
            } else
                if (mode.isCurrentMode("AAC")) {
                    modeAAC.onDisable();
                }
    }

    @Override
    public void onEnable() {
        if (mode.isCurrentMode("Hypixel")) {
            modeGlobalHypixel.onEnable();
        } else
            if (mode.isCurrentMode("AAC")) {
                modeAAC.onEnable();
            }
    }


}
