package cn.hanabi.modules.modules.player;

import cn.hanabi.events.EventTick;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.InvUtils;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

enum ArmorType {
    BOOTS, LEGGINGS, CHEST_PLATE, HELMET
}


public class AutoArmor extends Mod {
    public static boolean isDone = false;
    private final TimeHelper timer = new TimeHelper();
    private final TimeHelper glitchFixer = new TimeHelper();
    // idk why this not same name as InvCleaner
    private final Value<Boolean> openInv = new Value("AutoArmor", "Sort In Inv", false);
    private final Value<Boolean> noMove = new Value("AutoArmor", "No Move", false);
    private final Value<Double> delay = new Value("AutoArmor", "Delay", 150.0D, 0.0D,
            1000.0D, 10.0D);

    public AutoArmor() {
        super("AutoArmor", Category.PLAYER);

    }

    @EventTarget
    public void onTick(EventTick event) {
        if(noMove.getValue()&&MoveUtils.isMoving())
            return;

        if (openInv.getValueState()) {
            if (!(mc.currentScreen instanceof GuiInventory))
                return;
        } else {
            // Glitch Fix
            if (mc.currentScreen != null)
                this.glitchFixer.reset();

            if (!this.glitchFixer.isDelayComplete(300))
                return;
        }

        if (!timer.isDelayComplete(delay.getValue()))
            return;

        if (mc.thePlayer.capabilities.isCreativeMode
                || (mc.currentScreen != null && !openInv.getValueState())) {
            timer.reset();
            return;
        }

        int slot;

        for (ArmorType armorType : ArmorType.values()) {
            if ((slot = this.findArmor(armorType,
                    InvUtils.getArmorScore(mc.thePlayer.inventory.armorItemInSlot(armorType.ordinal())))) != -1) {
                // set
                isDone = false;
                if (mc.thePlayer.inventory.armorItemInSlot(armorType.ordinal()) != null) {
                    dropArmor(armorType.ordinal());
                    timer.reset();
                    return;
                }
                warmArmor(slot);
                timer.reset();
                return;
            } else {
                isDone = true;
            }
        }
    }

    private int findArmor(ArmorType armorType, float minimum) {
        float best = 0;
        int result = -1;

        for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (InvUtils.getArmorScore(itemStack) < 0 || InvUtils.getArmorScore(itemStack) <= minimum
                    || InvUtils.getArmorScore(itemStack) < best || !isValid(armorType, itemStack))
                continue;

            best = InvUtils.getArmorScore(itemStack);
            result = i;
        }

        return result;
    }

    private boolean isValid(ArmorType type, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemArmor))
            return false;

        ItemArmor armor = (ItemArmor) itemStack.getItem();

        if (type == ArmorType.HELMET && armor.armorType == 0)
            return true;

        if (type == ArmorType.CHEST_PLATE && armor.armorType == 1)
            return true;

        if (type == ArmorType.LEGGINGS && armor.armorType == 2)
            return true;

        return type == ArmorType.BOOTS && armor.armorType == 3;
    }

    private void warmArmor(int slot_In) {
        if (slot_In >= 0 && slot_In <= 8) { // 0-8 is hotbar
            clickSlot(slot_In + 36, 0, true);
        } else {
            clickSlot(slot_In, 0, true);
        }
    }

    private void clickSlot(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton,
                shiftClick ? 1 : 0, mc.thePlayer);
    }

    private void dropArmor(int armorSlot) {
        int slot = InvUtils.armorSlotToNormalSlot(armorSlot);
        if (!InvUtils.isFull()) {

            this.clickSlot(slot, 0, true);
        } else {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4,
                    mc.thePlayer);
        }
    }
}