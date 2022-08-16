package cn.hanabi.modules.modules.movement.LongJump;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventMove;
import cn.hanabi.events.EventPostMotion;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.rotation.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;


@ObfuscationClass
public class LongJump_Bow {

    final Minecraft mc = Minecraft.getMinecraft();
    private int stage, bowTick, bowSlot, startSlot;
    private double speed, last;
    private boolean dmged;
    private boolean jumped;


    public void onEnable() {
        stage = 0;
        bowTick = 0;
        bowSlot = -1;
        startSlot = mc.thePlayer.inventory.currentItem;
        speed = 0;
        last = 0;
        jumped = false;
        dmged = false;
    }


    public void onDisable() {
        PlayerUtil.setSpeed(0);
        if (bowTick != 0)
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        if (mc.thePlayer.inventory.currentItem != startSlot)
            mc.thePlayer.inventory.currentItem = startSlot;
    }


    public void onPre(EventPreMotion e) {
        if (!jumped && !mc.thePlayer.onGround && mc.thePlayer.motionY > 0) {
            jumped = true;
        }

        if (getBowCount() == 0 || getArrowCount() == 0) {
            ClientUtil.sendClientMessage("You need bow & arrows", Notification.Type.ERROR);
            ModManager.getModule("LongJump").set(false);
            return;
        } else {
            if (stage == 0) {
                stage = 1;
            }
        }

        switch (stage) {
            case 1: {
                for (int i = 0; i < 9; i++) {
                    final ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                    if (stack != null && stack.getItem() instanceof ItemBow) {
                        bowSlot = i;
                        break;
                    }
                }

                if (bowSlot == -1) {
                    for (int i = 9; i < 36; i++) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            int count = 0;
                            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBow) {
                                for (int a = 36; a < 45; a++) {
                                    if (mc.thePlayer.inventoryContainer.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(a), new ItemStack(Item.getItemById(261)), true)) {
                                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, a - 36, 2, mc.thePlayer);
                                        count++;
                                        break;
                                    }
                                }
                                if (count == 0) {
                                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 7, 2, mc.thePlayer);
                                }
                                break;
                            }
                        }
                    }
                }

                for (int i = 0; i < 9; i++) {
                    final ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                    if (stack != null && stack.getItem() instanceof ItemBow) {
                        bowSlot = i;
                        break;
                    }
                }

                if (bowSlot == -1) {
                    ClientUtil.sendClientMessage("Unknow exception , maybe your inventory is in desync", Notification.Type.ERROR);
                    ModManager.getModule("LongJump").set(false);
                    return;
                }

                stage = 2;
                break;
            }
            case 2: {

                if (mc.thePlayer.inventory.currentItem != bowSlot) {
                    mc.thePlayer.inventory.currentItem = bowSlot;
                    bowTick = 0;
                    return;
                }

                if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {

                    new Rotation(mc.thePlayer.rotationYaw, -90).apply(e);

                    switch (++bowTick) {
                        case 1: {
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                            break;
                        }
                        case 5: {
                            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            stage = 3;
                            break;
                        }
                    }
                }
                break;
            }
            case 3: {
                if (mc.thePlayer.hurtTime >= 9 && !dmged) {
                    mc.thePlayer.inventory.currentItem = startSlot;
                    dmged = true;
                    stage = 4;
                }
                break;
            }
        }

        if (stage > 5) {
            last = PlayerUtil.getLastDist();
        }

    }


    public void onPost(EventPostMotion e) {
    }


    public void onMove(EventMove event) {
        if (!dmged && stage > 0)
            PlayerUtil.setSpeed(event, 0);
        else {
            if (stage == 4) {
                event.setY(mc.thePlayer.motionY = PlayerUtil.getBaseJumpHeight() * 1.95F);
                speed = PlayerUtil.getBaseMoveSpeed() * 1.3;
            } else if (stage >= 4 && stage != 5) {
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, mc.thePlayer.motionY, 0)).isEmpty() || mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround)
                    ModManager.getModule("LongJump").set(false);
                event.setY(mc.thePlayer.motionY = mc.thePlayer.motionY + (.005F - (speed / PlayerUtil.getBaseMoveSpeed()) * .005F));
                speed = last - last / 159;
            }

            stage++;
            speed = Math.max(speed, PlayerUtil.getBaseMoveSpeed());
            MoveUtils.setMotion(event, speed);
        }


    }


    private int getArrowCount() {
        int arrowCount = 0;
        for (int i = 0; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() == Item.getItemById(262)) {
                    arrowCount += is.stackSize;
                }
            }
        }
        return arrowCount;
    }

    private int getBowCount() {
        int bowCount = 0;
        for (int i = 0; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBow) {
                    bowCount += is.stackSize;
                }
            }
        }
        return bowCount;
    }
}

