package cn.hanabi.injection.interfaces;

import net.minecraft.util.ResourceLocation;

public interface IEntityRenderer {
    void runSetupCameraTransform(float partialTicks, int pass);

    void setupCameraTransform(float partialTicks, int pass);

    void loadShader2(ResourceLocation resourceLocationIn);
}
