package cn.hanabi.injection.mixins;

import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.IntBuffer;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Shadow
    private boolean noColor;
    @Shadow
    private IntBuffer rawIntBuffer;
    @Shadow
    private int vertexFormatIndex;

    @Shadow
    public abstract int getColorIndex(int p_178978_4_);
}
