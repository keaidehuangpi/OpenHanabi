package cn.hanabi.injection.mixins;

import cn.hanabi.injection.interfaces.IEntityPlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntity implements IEntityPlayer {
    @Shadow
    private int itemInUseCount;

    @Shadow
    protected float speedInAir;

    @Shadow
    public abstract GameProfile getGameProfile();

    @Override
    public void setSpeedInAir(float i) {
        speedInAir = i;
    }


    @Override
    public void setItemInUseCount(int i) {
        itemInUseCount = i;
    }
}
