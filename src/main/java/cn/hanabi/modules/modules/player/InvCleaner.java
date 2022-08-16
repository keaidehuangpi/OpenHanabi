package cn.hanabi.modules.modules.player;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventTick;
import cn.hanabi.injection.interfaces.IItemSword;
import cn.hanabi.injection.interfaces.IItemTools;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.InvUtils;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ObfuscationClass

public class InvCleaner extends Mod {
    public static Value<Boolean> keepTools = new Value<>("InvCleaner", "Tools", false);
    public static Value<Boolean> keepArmor = new Value<>("InvCleaner", "Armor", false);
    public static Value<Boolean> keepBow = new Value<>("InvCleaner", "Bow", false);
    public static Value<Boolean> keepBucket = new Value<>("InvCleaner", "Bucket", false);
    public static Value<Boolean> keepArrow = new Value<>("InvCleaner", "Arrow", false);
    public static Value<Boolean> inInv = new Value<>("InvCleaner", "OnlyInv", false);
    private final Value<Boolean> noMove = new Value("InvCleaner", "No Move", false);
    private final Value<Boolean> sort = new Value("InvCleaner", "Sort", false);
    public static TimeHelper delayTimer = new TimeHelper();
    private final Value<Double> delay = new Value<>("InvCleaner", "Delay", 80.0D, 0.0D,
            1000.0D, 10.0D);
    public Value<Boolean> toggle = new Value<>("InvCleaner", "Auto Toggle", false);
    private double handitemAttackValue;
    private int currentSlot = 9;

    public InvCleaner() {
        super("InvCleaner", Category.PLAYER);
    }

    public static boolean isShit(int slot) {
        ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();

        if (itemStack == null)
            return false;

        if (itemStack.getItem() == Items.stick)
            return true;

        if (itemStack.getItem() == Items.egg)
            return true;

        if (itemStack.getItem() == Items.bone)
            return true;

        if (itemStack.getItem() == Items.bowl)
            return true;

        if (itemStack.getItem() == Items.glass_bottle)
            return true;

        if (itemStack.getItem() == Items.string)
            return true;

        if (itemStack.getItem() == Items.flint && getItemAmount(Items.flint) > 1)
            return true;

        if (itemStack.getItem() == Items.compass && getItemAmount(Items.compass) > 1)
            return true;

        if (itemStack.getItem() == Items.feather)
            return true;

        if (itemStack.getItem() == Items.fishing_rod)
            return true;

        // buckets
        if (itemStack.getItem() == Items.bucket && !keepBucket.getValue())
            return true;

        if (itemStack.getItem() == Items.lava_bucket && !keepBucket.getValue())
            return true;

        if (itemStack.getItem() == Items.water_bucket && !keepBucket.getValue())
            return true;

        if (itemStack.getItem() == Items.milk_bucket && !keepBucket.getValue())
            return true;

        // arrow
        if (itemStack.getItem() == Items.arrow && !keepArrow.getValue())
            return true;

        if (itemStack.getItem() == Items.snowball)
            return true;

        if (itemStack.getItem() == Items.fish)
            return true;

        if (itemStack.getItem() == Items.experience_bottle)
            return true;

        // tools
        if (itemStack.getItem() instanceof ItemTool && (!keepTools.getValue() || !isBestTool(itemStack)))
            return true;

        // sword
        if (itemStack.getItem() instanceof ItemSword && (!keepTools.getValue() || !isBestSword(itemStack)))
            return true;

        // armour
        if (itemStack.getItem() instanceof ItemArmor && (!keepArmor.getValue() || !isBestArmor(itemStack)))
            return true;

        // bow
        if (itemStack.getItem() instanceof ItemBow && (!keepBow.getValue() || !isBestBow(itemStack)))
            return true;

        if (itemStack.getItem().getUnlocalizedName().contains("potion")) {
            return isBadPotion(itemStack);
        }

        return false;
    }

    private static int getItemAmount(Item shit) {
        int result = 0;

        for (Slot item : mc.thePlayer.inventoryContainer.inventorySlots) {
            Slot slot = item;

            if (slot.getHasStack() && slot.getStack().getItem() == shit)
                result++;
        }
        return result;
    }

    private static boolean isBestTool(ItemStack input) {
        for (ItemStack itemStack : InvUtils.getAllInventoryContent()) {
            if (itemStack == null)
                continue;

            if (!(itemStack.getItem() instanceof ItemTool))
                continue;

            if (itemStack == input)
                continue;

            if (itemStack.getItem() instanceof ItemPickaxe && !(input.getItem() instanceof ItemPickaxe))
                continue;

            if (itemStack.getItem() instanceof ItemAxe && !(input.getItem() instanceof ItemAxe))
                continue;

            if (itemStack.getItem() instanceof ItemSpade && !(input.getItem() instanceof ItemSpade))
                continue;

            if (getToolEffencly(itemStack) >= getToolEffencly(input))
                return false;
        }
        return true;
    }

    private static boolean isBestSword(ItemStack input) {
        for (ItemStack itemStack : InvUtils.getAllInventoryContent()) {
            if (itemStack == null)
                continue;

            if (!(itemStack.getItem() instanceof ItemSword))
                continue;

            if (itemStack == input)
                continue;

            if (getSwordAttackDamage(itemStack) >= getSwordAttackDamage(input))
                return false;
        }
        return true;
    }

    private static boolean isBestBow(ItemStack input) {
        for (ItemStack itemStack : InvUtils.getAllInventoryContent()) {
            if (itemStack == null)
                continue;

            if (!(itemStack.getItem() instanceof ItemBow))
                continue;

            if (itemStack == input)
                continue;

            if (getBowAttackDamage(itemStack) >= getBowAttackDamage(input))
                return false;
        }
        return true;
    }

    private static boolean isBestArmor(ItemStack input) {
        for (ItemStack itemStack : InvUtils.getAllInventoryContent()) {
            if (itemStack == null||!(itemStack.getItem() instanceof ItemArmor))
                continue;

            if (itemStack == input)
                continue;

            if (((ItemArmor) itemStack.getItem()).armorType != ((ItemArmor) input.getItem()).armorType)
                continue;

            if (InvUtils.getArmorScore(itemStack) >= InvUtils.getArmorScore(input))
                return false;
        }
        // oh you didnt check the armor inventory
        for (ItemStack itemStack : mc.thePlayer.inventory.armorInventory) {
            if (itemStack == null||!(itemStack.getItem() instanceof ItemArmor))
                continue;

            if (itemStack == input)
                continue;

            if (((ItemArmor) itemStack.getItem()).armorType != ((ItemArmor) input.getItem()).armorType)
                continue;

            if (InvUtils.getArmorScore(itemStack) >= InvUtils.getArmorScore(input))
                return false;
        }
        return true;
    }

    private static boolean isBadPotion(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            for (final PotionEffect o : potion.getEffects(stack)) {
                final PotionEffect effect = o;
                if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.moveSlowdown.getId()
                        || effect.getPotionID() == Potion.harm.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static double getSwordAttackDamage(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof ItemSword))
            return 0;

        ItemSword sword = (ItemSword) itemStack.getItem();

        return ((IItemSword) sword).getAttackDamage()
                +EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25
                +EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 1;
    }

    private static double getBowAttackDamage(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof ItemBow))
            return 0;

        return EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack)
                + (EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) * 0.1)
                + (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) * 0.1);
    }

    private static double getToolEffencly(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof ItemTool))
            return 0;

        IItemTools sword = (IItemTools) itemStack.getItem();

        return EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)
                + sword.getEfficiencyOnProperMaterial();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        currentSlot = 9; // EXCEPT ARMOR SLOT
        handitemAttackValue = getSwordAttackDamage(mc.thePlayer.getHeldItem());
    }

    @EventTarget
    public void onUpdate(EventTick event) {
        if (!isEnabled() || mc.currentScreen instanceof GuiChest)
            return;

        if(noMove.getValue()&&MoveUtils.isMoving())
            return;

        if (currentSlot >= 45) {
            currentSlot = 9;
            if (mc.thePlayer.ticksExisted % 40 == 0 || toggle.getValueState()) {
                InvUtils.getBestAxe();
                InvUtils.getBestPickaxe();
                InvUtils.getBestShovel();
            }
            if (toggle.getValueState()) {
                this.set(false);
                return;
            }
        }

        if (!inInv.getValueState() || mc.currentScreen instanceof GuiInventory) {
            handitemAttackValue = getSwordAttackDamage(mc.thePlayer.getHeldItem());
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(currentSlot).getStack();
            if (delayTimer.isDelayComplete(delay.getValue())) {
                if (isShit(currentSlot) && getSwordAttackDamage(itemStack) <= handitemAttackValue && itemStack != mc.thePlayer.getHeldItem()) {
                    mc.playerController.windowClick(0, currentSlot, 1, 4, mc.thePlayer);
                    delayTimer.reset();
                }
                currentSlot++;
            }
        }
    }
}