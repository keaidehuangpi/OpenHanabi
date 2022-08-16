package cn.hanabi.modules.modules.movement.LongJump;

import cn.hanabi.events.EventMove;
import cn.hanabi.events.EventPostMotion;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.events.EventPullback;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;

public class LongJump extends Mod {
    private final Value<Boolean> lagback = new Value<>("LongJump", "Lag Back Checks", false);

    private static final Value<Boolean> visual = new Value<>("LongJump", "Visual Fly", false);

    Value<String> mode = new Value<String>("LongJump", "Mode", 0).
            LoadValue(new String[]{"Bow", "Damage", "NonDMG"});


    LongJump_Bow bow = new LongJump_Bow();
    LongJump_DMG dmg = new LongJump_DMG();
    LongJump_NonDMG nonDMG = new LongJump_NonDMG();


    public LongJump() {
        super("LongJump", Category.MOVEMENT);
    }

    @EventTarget
    private void onLagBack(EventPullback e) {
        if (lagback.getValueState()) {
            ClientUtil.sendClientMessage("(LagBackCheck) LongJump Disabled", Notification.Type.WARNING);
            set(false);
        }
    }


    @EventTarget
    private void onMove(EventMove e) {
        if (mode.isCurrentMode("Bow")) {
            bow.onMove(e);
        }

        if (mode.isCurrentMode("Damage")) {
            dmg.onMove(e);
        }

        if (mode.isCurrentMode("NonDMG")) {
            nonDMG.onMove(e);
        }
    }


    @EventTarget
    private void onPre(EventPreMotion e) {
        if (visual.getValue()){
            mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
        }

        if (mode.isCurrentMode("Bow")) {
            bow.onPre(e);
        }

        if (mode.isCurrentMode("Damage")) {
            dmg.onPre(e);
        }
    }


    @EventTarget
    private void onPost(EventPostMotion e) {
        if (mode.isCurrentMode("Bow")) {
            bow.onPost(e);
        }
    }


    @Override
    public void onEnable() {
        if (mode.isCurrentMode("Bow")) {
            bow.onEnable();
        }

        if (mode.isCurrentMode("Damage")) {
            dmg.onEnable();
        }

        if (mode.isCurrentMode("NonDMG")) {
            nonDMG.onEnable();
        }
    }

    @Override
    public void onDisable() {
        if (mode.isCurrentMode("Bow")) {
            bow.onDisable();
        }

        if (mode.isCurrentMode("Damage")) {
            dmg.onDisable();
        }

        if (mode.isCurrentMode("NonDMG")) {
            nonDMG.onDisable();
        }
    }

}
