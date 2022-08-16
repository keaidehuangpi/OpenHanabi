package cn.hanabi.modules.modules.combat;

import cn.hanabi.events.EventMove;
import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventPostMotion;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;


public class AutoHeal extends Mod {

    public boolean potting;

    private int slot, last;

    public Value<Boolean> healthValue = new Value<>("AutoHeal", "Health Pot", true);
    public Value<Boolean> speedValue = new Value<>("AutoHeal", "Speed Pot", true);
    public Value<Double> potHealthValue = new Value<>("AutoHeal", "Pot Health", 15d, 3d, 20d, 1d);
    private final TimeHelper timer = new TimeHelper();
    private final TimeHelper freeze = new TimeHelper();

    public AutoHeal() {
        super("AutoHeal", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        slot = -1;
        last = -1;
        timer.reset();
        potting = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        potting = false;
    }


    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook)
            timer.reset();
    }


    @EventTarget(Priority.HIGH)
    public void onUpdate(EventPreMotion event) {
        slot = getSlot();
        if (timer.isDelayComplete(1000) && KillAura.target == null) {
            final int regenId = Potion.regeneration.getId();
            if (!mc.thePlayer.isPotionActive(regenId) && !potting && mc.thePlayer.onGround && healthValue.getValue() && mc.thePlayer.getHealth() <= potHealthValue.getValue() && hasPot(regenId)) {
                int cum = hasPot(regenId, slot);
                if (cum != -1) swap(cum, slot);

                potting = true;
                timer.reset();
            }

            final int speedId = Potion.moveSpeed.getId();
            if (!mc.thePlayer.isPotionActive(speedId) && !potting && mc.thePlayer.onGround && speedValue.getValue() && hasPot(speedId)) {
                int cum = hasPot(speedId, slot);
                if (cum != -1) swap(cum, slot);

                timer.reset();
                potting = true;
            }

            if (potting) {
                last = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = slot;
                mc.thePlayer.motionY = 0.051011199999;
                event.setPitch(90);
            }
        }
    }

    @EventTarget
    public void onMove(EventMove event) {
    }

    @EventTarget
    public void onPost(EventPostMotion event) {
        if (potting) {
            if (mc.thePlayer.onGround && mc.thePlayer.inventory.getCurrentItem() != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
                mc.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
            if (last != -1) mc.thePlayer.inventory.currentItem = last;
            potting = false;
            last = -1;
        }
    }

    private int hasPot(int id, int targetSlot) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPotion) {
                    final ItemPotion pot = (ItemPotion) is.getItem();
                    if (pot.getEffects(is).isEmpty()) continue;
                    final PotionEffect effect = pot.getEffects(is).get(0);
                    if (effect.getPotionID() == id) {
                        if (ItemPotion.isSplash(is.getItemDamage()) && isBestPot(pot, is)) {
                            if (36 + targetSlot != i) return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    private boolean hasPot(int id) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPotion) {
                    final ItemPotion pot = (ItemPotion) is.getItem();
                    if (pot.getEffects(is).isEmpty()) continue;
                    final PotionEffect effect = pot.getEffects(is).get(0);
                    if (effect.getPotionID() == id) {
                        if (ItemPotion.isSplash(is.getItemDamage()) && isBestPot(pot, is)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isBestPot(ItemPotion potion, ItemStack stack) {
        if (potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1)
            return false;
        final PotionEffect effect = potion.getEffects(stack).get(0);
        int potionID = effect.getPotionID();
        int amplifier = effect.getAmplifier();
        int duration = effect.getDuration();
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPotion) {
                    final ItemPotion pot = (ItemPotion) is.getItem();
                    if (pot.getEffects(is) != null) {
                        for (PotionEffect o : pot.getEffects(is)) {
                            final PotionEffect effects = o;
                            final int id = effects.getPotionID();
                            final int ampl = effects.getAmplifier();
                            final int dur = effects.getDuration();
                            if (id == potionID && ItemPotion.isSplash(is.getItemDamage())) {
                                if (ampl > amplifier) {
                                    return false;
                                } else if (ampl == amplifier && dur > duration) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private int getSlot() {
        int spoofSlot = 8;
        for (int i = 36; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            } else if (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
                spoofSlot = i - 36;
                break;
            }
        }
        return spoofSlot;
    }

    private void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    public boolean isPotting() {
        return potting;
    }
}
