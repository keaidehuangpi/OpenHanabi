package cn.hanabi.modules.modules.player;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventGui;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjgl.input.Keyboard;


@ObfuscationClass
public class InvMove extends Mod {

    private final Value mode = new Value("InvMove", "Mode", 0);
    private boolean isWalking;

    public InvMove() {
        super("InvMove", Category.PLAYER);
        mode.addValue("Vanilla");
        mode.addValue("Hypixel");
    }


    @EventTarget
    public void onWindow(EventGui event){
        setKeyStat();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            isWalking = true;

            // HANABI_VERIFY
            /*
             * try { if
             * (!Hanabi.AES_UTILS.decrypt(Hanabi.HWID_VERIFY).contains(Wrapper.getHWID())) {
             * FMLCommonHandler.instance().exitJava(0, true); Client.sleep = true; } } catch
             * (Exception e) { FMLCommonHandler.instance().exitJava(0, true); Client.sleep =
             * true; }
             *
             */

            if (mode.isCurrentMode("Hypixel")) {
                try {
                    int i = 0;
                    for (; i < 8; i++) {
                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                        if (stack == null) break; // 空手
                        if (!(stack.getItem() instanceof ItemFood) && !(stack.getItem() instanceof ItemSword) && Item.getIdFromItem(stack.getItem()) != 345)
                            break; // 不能为Food Sword 指南针
                    }

                    if (i == 8 && Item.getIdFromItem(mc.thePlayer.inventory.getStackInSlot(8).getItem()) == 345) i--;


                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i));
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }

            } else {
            }

            setKeyStat();
        } else {
            if (isWalking) {
                if (mode.isCurrentMode("Hypixel")) {
                    mc.thePlayer.sendQueue
                            .addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                isWalking = false;
            }
        }
    }

    private void setKeyStat(){
        KeyBinding[] key = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump};
        KeyBinding[] array;
        for (int length = (array = key).length, i = 0; i < length; ++i) {
            KeyBinding b = array[i];
            KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
        }
    }
}
