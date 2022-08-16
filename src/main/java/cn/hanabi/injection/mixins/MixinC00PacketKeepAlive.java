package cn.hanabi.injection.mixins;

import cn.hanabi.injection.interfaces.IC00PacketKeepAlive;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(C00PacketKeepAlive.class)
public class MixinC00PacketKeepAlive implements IC00PacketKeepAlive {
    @Shadow
    private int key;

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void setKey(int b) {
        key = b;
    }


}
