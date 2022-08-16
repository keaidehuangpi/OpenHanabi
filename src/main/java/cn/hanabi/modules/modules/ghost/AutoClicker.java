package cn.hanabi.modules.modules.ghost;

import cn.hanabi.events.EventMouse;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.injection.interfaces.IEntityLivingBase;
import cn.hanabi.injection.interfaces.IKeyBinding;
import cn.hanabi.injection.interfaces.IMinecraft;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.Random;

public class AutoClicker extends Mod {

    public static boolean isClicking = false;
    private final TimeHelper left = new TimeHelper();
    private final TimeHelper right = new TimeHelper();
    public boolean isDone = true;
    public int timer;
    public Value<Double> maxCps = new Value<>("AutoClicker", "Max CPS", 12d, 1d, 20d, 1d);
    public Value<Double> minCps = new Value<>("AutoClicker", "MinC PS", 8d, 1d, 20d, 1d);
    public Value<Boolean> blockHit = new Value<>("AutoClicker", "Block Hit", false);
    public Value<Boolean> jitter = new Value<>("AutoClicker", "Jitter", false);
    Random random = new Random();

    public AutoClicker() {
        super("AutoClicker", Category.GHOST);
    }

    @Override
    public void onEnable() {
        isDone = true;
        timer = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        isDone = true;
        super.onDisable();
    }

    private long getDelay() {
        return (long) (this.maxCps.getValueState().intValue() + this.random.nextDouble()
                * (this.minCps.getValueState().intValue() - this.maxCps.getValueState().intValue()));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer != null) {
            isClicking = false;

            if (this.minCps.getValueState().intValue() > this.maxCps.getValueState().intValue()) {
                this.minCps.setValueState(this.maxCps.getValueState());
            }

            //YAY, Block Animation
            if (((IKeyBinding) mc.gameSettings.keyBindAttack).getPress() && mc.thePlayer.isUsingItem()) {
                this.swingItemNoPacket();
            }

            if (((IKeyBinding) mc.gameSettings.keyBindAttack).getPress() && !mc.thePlayer.isUsingItem()) {
                if (this.left.isDelayComplete(1000 / (double) this.getDelay())) {

                    if (this.jitter.getValueState()) {
                        jitter(this.random);
                    }

                    ((IMinecraft) mc).setClickCounter(0);
                    ((IMinecraft) mc).runCrinkMouse();

                    isClicking = true;
                    left.reset();
                }
            }
        }

        if (!isDone) {
            switch (this.timer) {
                case 0: {
                    ((IKeyBinding) mc.gameSettings.keyBindUseItem).setPress(false);
                    break;
                }
                case 1:
                case 2: {
                    ((IKeyBinding) mc.gameSettings.keyBindUseItem).setPress(true);
                    break;
                }
                case 3: {
                    ((IKeyBinding) mc.gameSettings.keyBindUseItem).setPress(false);
                    isDone = true;
                    this.timer = -1;
                }
            }
            ++this.timer;
        }
    }

    public void swingItemNoPacket() {
        if (!mc.thePlayer.isSwingInProgress || mc.thePlayer.swingProgressInt >= ((IEntityLivingBase) mc.thePlayer).runGetArmSwingAnimationEnd() / 2
                || mc.thePlayer.swingProgressInt < 0) {
            mc.thePlayer.swingProgressInt = -1;
            mc.thePlayer.isSwingInProgress = true;
        }
    }

    @EventTarget
    public void onCrink(EventMouse event) {
        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack != null && this.blockHit.getValueState()) {
            if (stack.getItem() instanceof ItemSword && !mc.thePlayer.isUsingItem()) {
                if (!isDone || this.timer > 0)
                    return;
                isDone = false;
            }
        }
    }

    public void jitter(Random rand) {
        if (rand.nextBoolean()) {
            if (rand.nextBoolean()) {
                mc.thePlayer.rotationPitch -= (float) (rand.nextFloat() * 0.6);
            } else {
                mc.thePlayer.rotationPitch += (float) (rand.nextFloat() * 0.6);
            }
        } else if (rand.nextBoolean()) {
            mc.thePlayer.rotationYaw -= (float) (rand.nextFloat() * 0.6);
        } else {
            mc.thePlayer.rotationYaw += (float) (rand.nextFloat() * 0.6);
        }
    }
}
