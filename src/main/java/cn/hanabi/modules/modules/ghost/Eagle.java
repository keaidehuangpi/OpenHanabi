package cn.hanabi.modules.modules.ghost;

import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class Eagle extends Mod {
    public Eagle() {
        super("Eagle", Category.GHOST);
    }

    public Block getBlockUnderPlayer(EntityPlayer player) {
        return mc.theWorld.getBlockState(new BlockPos(player.posX, player.posY - 1.0d, player.posZ)).getBlock();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!mc.thePlayer.onGround)
            return;

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir);
    }

    @Override
    public void onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }
}
