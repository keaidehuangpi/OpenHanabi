package cn.hanabi.injection.mixins;

import cn.hanabi.Hanabi;
import cn.hanabi.events.EventRender;
import cn.hanabi.injection.interfaces.IEntityRenderer;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.ghost.Reach;
import cn.hanabi.modules.modules.render.NoFov;
import cn.hanabi.modules.modules.render.WorldColor;
import com.darkmagician6.eventapi.EventManager;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

import static cn.hanabi.modules.modules.ghost.Reach.getReach;
import static cn.hanabi.modules.modules.ghost.Reach.maxreach;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IEntityRenderer {
    @Final
    @Shadow
    public static int shaderCount;
    @Final
    @Shadow
    private static Logger logger;
    @Shadow
    private final FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
    @Final
    @Shadow
    private final DynamicTexture lightmapTexture = new DynamicTexture(16, 16);
    @Final
    @Shadow
    private final int[] lightmapColors = this.lightmapTexture.getTextureData();
    @Shadow
    private Minecraft mc;
    @Shadow
    private Entity pointedEntity;
    @Shadow
    private int shaderIndex;
    @Shadow
    private boolean useShader;
    @Shadow
    private ShaderGroup theShaderGroup;
    @Final
    @Shadow
    private IResourceManager resourceManager;
    @Shadow
    private float fogColorRed;
    @Shadow
    private float fogColorGreen;
    @Shadow
    private float fogColorBlue;
    @Shadow
    private float farPlaneDistance;
    @Shadow
    private float fovModifierHandPrev;
    @Shadow
    private float bossColorModifier;
    @Shadow
    private float fovModifierHand;
    @Shadow
    private float bossColorModifierPrev;
    @Shadow
    private float torchFlickerX;

    @Shadow
    private boolean lightmapUpdateNeeded;

    @Shadow
    private double cameraZoom;

    @Shadow
    private double cameraYaw;

    @Shadow
    private double cameraPitch;

    @Shadow
    protected abstract float getFOVModifier(float partialTicks, boolean useFOVSetting);

    @Shadow
    protected abstract void hurtCameraEffect(float partialTicks);

    @Shadow
    protected abstract void setupViewBobbing(float partialTicks);

    @Shadow
    protected abstract void orientCamera(float partialTicks);

    @Shadow
    private int rendererUpdateCount;

    @Shadow
    private boolean debugView;

    @Shadow
    private int debugViewDirection;

    public void setupCameraTransform(float partialTicks, int pass) {
        this.farPlaneDistance = (float) (this.mc.gameSettings.renderDistanceChunks * 16);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        float f = 0.07F;

        if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((float) (-(pass * 2 - 1)) * f, 0.0F, 0.0F);
        }

        if (this.cameraZoom != 1.0D) {
            GlStateManager.translate((float) this.cameraYaw, (float) (-this.cameraPitch), 0.0F);
            GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0D);
        }

        Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();

        if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((float) (pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(partialTicks);

        if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(partialTicks);
        }

        float f1 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;

        if (f1 > 0.0F) {
            int i = 20;

            if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
                i = 7;
            }

            float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
            f2 = f2 * f2;
            GlStateManager.rotate(((float) this.rendererUpdateCount + partialTicks) * (float) i, 0.0F, 1.0F, 1.0F);
            GlStateManager.scale(1.0F / f2, 1.0F, 1.0F);
            GlStateManager.rotate(-((float) this.rendererUpdateCount + partialTicks) * (float) i, 0.0F, 1.0F, 1.0F);
        }

        this.orientCamera(partialTicks);

        if (this.debugView) {
            switch (this.debugViewDirection) {
                case 0:
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 1:
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 2:
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 3:
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case 4:
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    public void runSetupCameraTransform(float partialTicks, int pass) {
        this.setupCameraTransform(partialTicks, pass);
    }

    public void loadShader2(ResourceLocation resourceLocationIn) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            try {
                this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager,
                        this.mc.getFramebuffer(), resourceLocationIn);
                this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.useShader = true;
            } catch (IOException | JsonSyntaxException ioexception) {
                logger.warn("Failed to load shader: " + resourceLocationIn, ioexception);
                this.shaderIndex = shaderCount;
                this.useShader = false;
            }
        }
    }

    @Inject(method = "renderWorldPass", at =
            @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableFog()V", shift = Shift.AFTER))
    private void eventRender3D(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo) {
        EventRender eventRender = new EventRender(pass, partialTicks, finishTimeNano);
        EventManager.call(eventRender);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }


    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void injectHurtCameraEffect(CallbackInfo callbackInfo) {
        if (ModManager.getModule("NoHurtCam").isEnabled())
            callbackInfo.cancel();
    }

    @Inject(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;distanceTo(Lnet/minecraft/util/Vec3;)D"), cancellable = true)
    private void cameraClip(float partialTicks, CallbackInfo callbackInfo) {
        if (ModManager.getModule("ViewClip").isEnabled()) {
            callbackInfo.cancel();

        }
        if (ModManager.getModule("ViewClip").isEnabled()) {

            Entity entity = this.mc.getRenderViewEntity();
            float f = entity.getEyeHeight();

            if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPlayerSleeping()) {
                f = (float) ((double) f + 1D);
                GlStateManager.translate(0F, 0.3F, 0.0F);

                if (!this.mc.gameSettings.debugCamEnable) {
                    BlockPos blockpos = new BlockPos(entity);
                    IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
                    net.minecraftforge.client.ForgeHooksClient.orientBedCamera(this.mc.theWorld, blockpos, iblockstate, entity);

                    GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
                    GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
                }
            } else if (this.mc.gameSettings.thirdPersonView > 0) {
                /**
                 * Third person distance temp
                 */
                float thirdPersonDistanceTemp = 4.0F;
                float thirdPersonDistance = 4.0F;
                double d3 = thirdPersonDistanceTemp + (thirdPersonDistance - thirdPersonDistanceTemp) * partialTicks;

                if (this.mc.gameSettings.debugCamEnable) {
                    GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
                } else {
                    float f1 = entity.rotationYaw;
                    float f2 = entity.rotationPitch;

                    if (this.mc.gameSettings.thirdPersonView == 2)
                        f2 += 180.0F;

                    if (this.mc.gameSettings.thirdPersonView == 2)
                        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

                    GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
                    GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
                }
            } else
                GlStateManager.translate(0.0F, 0.0F, -0.1F);

            if (!this.mc.gameSettings.debugCamEnable) {
                float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
                float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                float roll = 0.0F;
                if (entity instanceof EntityAnimal) {
                    EntityAnimal entityanimal = (EntityAnimal) entity;
                    yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
                }

                Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
                net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup event = new net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup((EntityRenderer) (Object) this, entity, block, partialTicks, yaw, pitch, roll);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
                GlStateManager.rotate(event.roll, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(event.pitch, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(event.yaw, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.translate(0.0F, -f, 0.0F);
            double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
            double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
            double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
            boolean cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
        }
    }


    @Shadow
    private float getNightVisionBrightness(final EntityLivingBase entitylivingbaseIn, final float partialTicks) {
        final int i = entitylivingbaseIn.getActivePotionEffect(Potion.nightVision).getDuration();
        return (i > 200) ? 1.0f : (0.7f + MathHelper.sin((float) ((i - partialTicks) * Math.PI * 0.2f)) * 0.3f);
    }

    /**
     * @author mojang
     */
    @Overwrite
    private void updateLightmap(float partialTicks) {
        if (this.lightmapUpdateNeeded) {
            this.mc.mcProfiler.startSection("lightTex");
            World world = this.mc.theWorld;

            if (world != null) {
                float f = world.getSunBrightness(1.0F);
                float f1 = f * 0.95F + 0.05F;

                final WorldColor worldColor = ModManager.getModule(WorldColor.class);

                if (worldColor.isEnabled()) {
                    final int color = new Color(worldColor.r.getValue().intValue(), worldColor.g.getValue().intValue(), worldColor.b.getValue().intValue(), worldColor.a.getValue().intValue()).getRGB();
                    final int r = color >> 16 & 0xFF;
                    final int g = color >> 8 & 0xFF;
                    final int b = color & 0xFF;
                    final int a = color >> 24 & 0xFF;
                    for (int i = 0; i < 256; ++i) {
                        this.lightmapColors[i] = (a << 24 | r << 16 | g << 8 | b);
                    }
                } else {
                    for (int i = 0; i < 256; ++i) {
                        float f2 = world.provider.getLightBrightnessTable()[i / 16] * f1;
                        float f3 = world.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);

                        if (world.getLastLightningBolt() > 0) {
                            f2 = world.provider.getLightBrightnessTable()[i / 16];
                        }

                        float f4 = f2 * (f * 0.65F + 0.35F);
                        float f5 = f2 * (f * 0.65F + 0.35F);
                        float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
                        float f8 = f4 + f3;
                        float f9 = f5 + f6;
                        float f10 = f2 + f7;
                        f8 = f8 * 0.96F + 0.03F;
                        f9 = f9 * 0.96F + 0.03F;
                        f10 = f10 * 0.96F + 0.03F;

                        if (this.bossColorModifier > 0.0F) {
                            float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
                            f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
                            f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
                            f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
                        }

                        if (world.provider.getDimensionId() == 1) {
                            f8 = 0.22F + f3 * 0.75F;
                            f9 = 0.28F + f6 * 0.75F;
                            f10 = 0.25F + f7 * 0.75F;
                        }

                        if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                            float f15 = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
                            float f12 = 1.0F / f8;

                            if (f12 > 1.0F / f9) {
                                f12 = 1.0F / f9;
                            }

                            if (f12 > 1.0F / f10) {
                                f12 = 1.0F / f10;
                            }

                            f8 = f8 * (1.0F - f15) + f8 * f12 * f15;
                            f9 = f9 * (1.0F - f15) + f9 * f12 * f15;
                            f10 = f10 * (1.0F - f15) + f10 * f12 * f15;
                        }

                        if (f8 > 1.0F) {
                            f8 = 1.0F;
                        }

                        if (f9 > 1.0F) {
                            f9 = 1.0F;
                        }

                        if (f10 > 1.0F) {
                            f10 = 1.0F;
                        }

                        float f16 = this.mc.gameSettings.gammaSetting;
                        float f17 = 1.0F - f8;
                        float f13 = 1.0F - f9;
                        float f14 = 1.0F - f10;
                        f17 = 1.0F - f17 * f17 * f17 * f17;
                        f13 = 1.0F - f13 * f13 * f13 * f13;
                        f14 = 1.0F - f14 * f14 * f14 * f14;
                        f8 = f8 * (1.0F - f16) + f17 * f16;
                        f9 = f9 * (1.0F - f16) + f13 * f16;
                        f10 = f10 * (1.0F - f16) + f14 * f16;
                        f8 = f8 * 0.96F + 0.03F;
                        f9 = f9 * 0.96F + 0.03F;
                        f10 = f10 * 0.96F + 0.03F;

                        if (f8 > 1.0F) {
                            f8 = 1.0F;
                        }

                        if (f9 > 1.0F) {
                            f9 = 1.0F;
                        }

                        if (f10 > 1.0F) {
                            f10 = 1.0F;
                        }

                        if (f8 < 0.0F) {
                            f8 = 0.0F;
                        }

                        if (f9 < 0.0F) {
                            f9 = 0.0F;
                        }

                        if (f10 < 0.0F) {
                            f10 = 0.0F;
                        }

                        int j = 255;
                        int k = (int) (f8 * 255.0F);
                        int l = (int) (f9 * 255.0F);
                        int i1 = (int) (f10 * 255.0F);
                        this.lightmapColors[i] = j << 24 | k << 16 | l << 8 | i1;
                    }
                }

                this.lightmapTexture.updateDynamicTexture();
                this.lightmapUpdateNeeded = false;
                this.mc.mcProfiler.endSection();
            }
        }
    }


    /**
     * @author
     */
    @Overwrite
    private void updateFovModifierHand() {
        float f = 1.0F;

        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) this.mc.getRenderViewEntity();
            f = abstractclientplayer.getFovModifier();
        }

        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand += (f - this.fovModifierHand) * 0.5F;

        if (this.fovModifierHand > 1.5F) {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F) {
            this.fovModifierHand = 0.1F;
        }
        // Client
        if (ModManager.getModule("NoFov").isEnabled())
            this.fovModifierHand = NoFov.fovspoof.getValueState().floatValue();

    }


    /**
     * @author
     */
    @Overwrite
    public void getMouseOver(float p_getMouseOver_1_) {
        Entity entity = this.mc.getRenderViewEntity();
        if (entity != null && this.mc.theWorld != null) {
            this.mc.mcProfiler.startSection("pick");

            this.mc.pointedEntity = null;
            double d0 = ModManager.getModule(Reach.class).isEnabled() ? maxreach.getValueState() : (double) this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = entity.rayTrace(ModManager.getModule(Reach.class).isEnabled() ? maxreach.getValueState() : d0, p_getMouseOver_1_);
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(p_getMouseOver_1_);
            boolean flag = false;
            if (this.mc.playerController.extendedReach()) {
                d0 = 6.0D;
                d1 = 6.0D;
            } else if (d0 > 3.0D) {
                flag = true;
            }

            if (this.mc.objectMouseOver != null) {
                d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
            }

            if (ModManager.getModule(Reach.class).isEnabled()) {
                d1 = getReach();

                final MovingObjectPosition movingObjectPosition = entity.rayTrace(d1, p_getMouseOver_1_);

                if (movingObjectPosition != null) d1 = movingObjectPosition.hitVec.distanceTo(vec3);
            }

            Vec3 vec31 = entity.getLook(p_getMouseOver_1_);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            this.pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (Entity entity1 : list) {
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        this.pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity1 == entity.ridingEntity && !entity.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                this.pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            this.pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > (ModManager.getModule(Reach.class).isEnabled() ? getReach() : 3.0D)) {
                this.pointedEntity = null;
                this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (this.pointedEntity != null && (d2 < d1 || (this.mc.objectMouseOver == null))) {
                this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);
                if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                    this.mc.pointedEntity = this.pointedEntity;
                }
            }

            this.mc.mcProfiler.endSection();
        }
    }

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"
    ))
    private void onPostRenderHUD(float partialTicks, long nanoTime, CallbackInfo ci) {
        Hanabi.INSTANCE.hudWindowMgr.draw();
    }

}
