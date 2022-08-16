package cn.hanabi.modules.modules.player;

import cn.hanabi.events.EventUpdate;
import cn.hanabi.injection.interfaces.IMinecraft;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class FastUse extends Mod {
    private final Value mode = new Value("FastUse", "Mode", 0);
    private boolean usedtimer = false;
    private final TimeHelper timer = new TimeHelper();

    public FastUse() {
        super("FastUse", Category.PLAYER);
        mode.addValue("Instant");
        mode.addValue("Timer");
    }

    @Override
    public void onEnable() {
        usedtimer = false;
        timer.reset();
    }

    @Override
    public void onDisable() {
        if (usedtimer) {
            ((IMinecraft) mc).getTimer().timerSpeed = 1F;
            usedtimer = false;
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        this.setDisplayName(mode.getModeAt(mode.getCurrentMode()));
        if (mode.isCurrentMode("Instant")) {
            if (mc.thePlayer.isUsingItem()) {
                Item usingItem = mc.thePlayer.getItemInUse().getItem();
                if (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk
                        || usingItem instanceof ItemPotion) {
                    if (timer.hasReached(750)) {
                        mc.getNetHandler()
                                .addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        mc.getNetHandler()
                                .addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getItemInUse()));
                        for (int i = 0; i < 39; ++i) {
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                        }
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        mc.getNetHandler()
                                .addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        mc.playerController.onStoppedUsingItem(mc.thePlayer);
                        timer.reset();
                    }
                }
                if (!mc.thePlayer.isUsingItem()) {
                    timer.reset();
                }
            }
        } else if (mode.isCurrentMode("Hypixel")) {
            if (mc.thePlayer.isUsingItem()) {
                Item usingItem = mc.thePlayer.getItemInUse().getItem();
                if (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk
                        || usingItem instanceof ItemPotion) {
                    if (mc.thePlayer.getItemInUseDuration() >= 1) {
                        mc.getNetHandler()
                                .addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                }
                if (!mc.thePlayer.isUsingItem()) {
                    timer.reset();
                }
            }
        } else if (mode.isCurrentMode("Timer")) {
            if (usedtimer) {
                ((IMinecraft) mc).getTimer().timerSpeed = 1F;
                usedtimer = false;
            }
            if (mc.thePlayer.isUsingItem()) {
                Item usingItem = mc.thePlayer.getItemInUse().getItem();
                if (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk
                        || usingItem instanceof ItemPotion) {
                    ((IMinecraft) mc).getTimer().timerSpeed = 1.22f;
                    usedtimer = true;
                }
            }
        }
    }
}
