package cn.hanabi.modules.modules.combat;

import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;


// TODO: Combine into InvCleaner sort mode?
public class AutoSword extends Mod {
    public static TimeHelper publicItemTimer = new TimeHelper();
    public Value<Double> slot = new Value<>("AutoSword", "Slot", 1d, 1d, 9d, 1d);
    TimeHelper time = new TimeHelper();

    public AutoSword() {
        super("AutoSword", Category.COMBAT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!publicItemTimer.isDelayComplete(300)) return;

        this.setDisplayName("Slot " + slot.getValueState().intValue());
        if (!time.isDelayComplete(1000L) || (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory)))
            return;

        int best = -1;
        float swordDamage = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword) {
                    float swordD = getSharpnessLevel(is);
                    if (swordD > swordDamage) {
                        swordDamage = swordD;
                        best = i;
                    }
                }
            }
        }
        final ItemStack current = mc.thePlayer.inventoryContainer.getSlot(slot.getValueState().intValue() + 35).getStack();
        if (best != -1 && (current == null || !(current.getItem() instanceof ItemSword) || swordDamage > getSharpnessLevel(current))) {

            /*
             * try { if
             * (!Hanabi.AES_UTILS.decrypt(Hanabi.HWID_VERIFY).contains(Wrapper.getHWID())) {
             * FMLCommonHandler.instance().exitJava(0, true); Client.sleep = true; } } catch
             * (Exception e) { FMLCommonHandler.instance().exitJava(0, true); Client.sleep =
             * true; }
             *
             */
            publicItemTimer.reset();
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, best, slot.getValueState().intValue() - 1, 2, mc.thePlayer);
            time.reset();
        }
    }

    public boolean isBestWeapon(ItemStack stack) {
        float damage = getDamage(stack);
        for (int i = 9; i < 36; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getDamage(is) > damage && (is.getItem() instanceof ItemSword))
                    return false;
            }
        }
        return stack.getItem() instanceof ItemSword;

    }

    private float getDamage(ItemStack stack) {
        float damage = 0;
        Item item = stack.getItem();
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword) item;
            damage += sword.getDamageVsEntity();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f +
                EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
        return damage;
    }

    public void getBestWeapon(int slot) {
        for (int i = 9; i < 36; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (isBestWeapon(is) && getDamage(is) > 0 && (is.getItem() instanceof ItemSword)) {
                    swap(i, slot - 36);
                    break;
                }
            }
        }
    }

    protected void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }

    private float getSharpnessLevel(ItemStack stack) {
        float damage = ((ItemSword) stack.getItem()).getDamageVsEntity();
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
        return damage;
    }
}