package cn.hanabi.modules.modules.player;

import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;


public class ChestStealer extends Mod {
    public static boolean isChest;
    public static Value<Boolean> onlychest = new Value<>("ChestStealer", "Only Chest", Boolean.TRUE);
    private final Value<Double> delay = new Value("ChestStealer", "Delay", 30.0D, 0.0D,
            1000.0D, 10.0D);
    private final Value<Boolean> trash = new Value<>("ChestStealer", "Pick Trash", Boolean.TRUE);
    private final Value<Boolean> tools = new Value<>("ChestStealer", "Tools", Boolean.TRUE);
    private final int[] itemHelmet;
    private final int[] itemChestplate;
    private final int[] itemLeggings;
    private final int[] itemBoots;
    // private Value<Boolean> hypixel = new Value("ChestStealer_Hypixel",
    // Boolean.valueOf(true));
    TimeHelper time2 = new TimeHelper();
    TimeHelper time = new TimeHelper();

    public ChestStealer() {
        super("ChestStealer", Category.PLAYER);
        this.itemHelmet = new int[]{298, 302, 306, 310, 314};
        this.itemChestplate = new int[]{299, 303, 307, 311, 315};
        this.itemLeggings = new int[]{300, 304, 308, 312, 316};
        this.itemBoots = new int[]{301, 305, 309, 313, 317};
    }

    public static boolean isContain(int[] arr, int targetValue) {
        return ArrayUtils.contains(arr, targetValue);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        this.setDisplayName("Delay " + delay.getValueState());
        if (!isChest && onlychest.getValueState())
            return;
        if (mc.thePlayer.openContainer != null) {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest c = (ContainerChest) mc.thePlayer.openContainer;

                if (isChestEmpty(c)) {
                    mc.thePlayer.closeScreen();
                }

                for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                    if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                        if (time.isDelayComplete(delay.getValueState() + new Random().nextInt(100))
                                && (itemIsUseful(c, i) || trash.getValueState())) {
                            if (new Random().nextInt(100) > 80)
                                continue; // Random
                            mc.playerController.windowClick(c.windowId, i, 0, 1, mc.thePlayer);
                            this.time.reset();
                        }
                    }
                }
            }
        }

    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                if (itemIsUseful(c, i) || trash.getValueState()) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isPotionNegative(ItemStack itemStack) {
        ItemPotion potion = (ItemPotion) itemStack.getItem();

        List<PotionEffect> potionEffectList = potion.getEffects(itemStack);

        return potionEffectList.stream().map(potionEffect -> Potion.potionTypes[potionEffect.getPotionID()])
                .anyMatch(Potion::isBadEffect);
    }

    private boolean itemIsUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();

        if ((item instanceof ItemAxe || item instanceof ItemPickaxe) && tools.getValueState()) {
            return true;
        }
        if (item instanceof ItemFood)
            return true;
        if (item instanceof ItemEnderPearl)
            return true;
        if (item instanceof ItemPotion && !isPotionNegative(itemStack))
            return true;
        if (item instanceof ItemSword && isBestSword(c, itemStack))
            return true;
        if (item instanceof ItemArmor && isBestArmor(c, itemStack))
            return true;

        return item instanceof ItemBlock;
    }

    private float getSwordDamage(ItemStack itemStack) {
        float damage = 0f;
        Optional attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (attributeModifier.isPresent()) {
            damage = (float) ((AttributeModifier) attributeModifier.get()).getAmount();
        }
        return damage += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
    }

    private boolean isBestSword(ContainerChest c, ItemStack item) {
        float itemdamage1 = getSwordDamage(item);
        float itemdamage2 = 0f;
        for (int i = 0; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                float tempdamage = getSwordDamage(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
                if (tempdamage >= itemdamage2)
                    itemdamage2 = tempdamage;
            }
        }
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                float tempdamage = getSwordDamage(c.getLowerChestInventory().getStackInSlot(i));
                if (tempdamage >= itemdamage2)
                    itemdamage2 = tempdamage;
            }
        }
        return itemdamage1 == itemdamage2;
    }

    private boolean isBestArmor(ContainerChest c, ItemStack item) {
        float itempro1 = ((ItemArmor) item.getItem()).damageReduceAmount;
        float itempro2 = 0f;
        if (isContain(itemHelmet, Item.getIdFromItem(item.getItem()))) { // 头盔
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemHelmet,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemHelmet,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemChestplate, Item.getIdFromItem(item.getItem()))) { // 胸甲
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemChestplate,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemChestplate,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemLeggings, Item.getIdFromItem(item.getItem()))) { // 腿子
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemLeggings,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemLeggings,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemBoots, Item.getIdFromItem(item.getItem()))) { // 鞋子
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemBoots,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemBoots,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        return itempro1 == itempro2;
    }
}
