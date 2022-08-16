package cn.hanabi.injection.mixins;

import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.combat.KillAura;
import cn.hanabi.modules.modules.render.HitAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    ///////// FLUX
    public float rotateDirection = 0;
    public float delta;
    public float shaderDelta;
    @Final
    @Shadow
    private Minecraft mc;
    @Shadow
    private float equippedProgress;
    @Shadow
    private float prevEquippedProgress;
    @Shadow
    private ItemStack itemToRender;
    @Final
    @Shadow
    private RenderManager renderManager;

    /**
     * @author
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        float f = 1.0F
                - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP entityplayersp = this.mc.thePlayer;
        float f1 = entityplayersp.getSwingProgress(partialTicks);
        float f2 = entityplayersp.prevRotationPitch
                + (entityplayersp.rotationPitch - entityplayersp.prevRotationPitch) * partialTicks;
        float f3 = entityplayersp.prevRotationYaw
                + (entityplayersp.rotationYaw - entityplayersp.prevRotationYaw) * partialTicks;
        float var2 = 1.0F
                - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP var3 = this.mc.thePlayer;
        float var4 = var3.getSwingProgress(partialTicks);

        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(entityplayersp);
        this.rotateWithPlayerRotations(entityplayersp, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (this.itemToRender != null) {
            if (this.itemToRender.getItem() == Items.filled_map) {
                this.renderItemMap(entityplayersp, f2, f, f1);
            } else if (entityplayersp.getItemInUseCount() > 0) {
                EnumAction enumaction = this.itemToRender.getItemUseAction();

                switch (enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;

                    case EAT:
                    case DRINK:
                        this.performDrinking(entityplayersp, partialTicks);
                        this.transformFirstPersonItem(f, f1);
                        break;

                    case BLOCK:
                        renderingBlocked(f, f1);
                        break;

                    case BOW:
                        this.transformFirstPersonItem(f, f1);
                        this.doBowTransformations(partialTicks, entityplayersp);
                }
            } else {
                if (((KillAura.autoBlock.getValueState() && KillAura.target != null)
                        || this.mc.gameSettings.keyBindUseItem.isKeyDown())
                        && ModManager.getModule("EveryThingBlock").isEnabled()) {
                    renderingBlocked(f, f1);
                } else {
                    this.doItemUsedTransformations(f1);
                    this.transformFirstPersonItem(f, f1);
                }

            }

            this.renderItem(entityplayersp, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!entityplayersp.isInvisible()) {
            this.renderPlayerArm(entityplayersp, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }


    private void avatar(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void renderingBlocked(float swingProgress, float equippedProgress) {
        final float hand = MathHelper.sin(MathHelper.sqrt_float(equippedProgress) * (float)Math.PI);
        final HitAnimation animations = ModManager.getModule(HitAnimation.class);

        if (!animations.isEnabled()) {
            this.transformFirstPersonItem(swingProgress, 0.0F);
            this.doBlockTransformations();
        } else {
            GL11.glTranslated(animations.posX.getValue(), animations.posY.getValue(), animations.posZ.getValue());
            if (animations.mode.isCurrentMode("Sigma")) {
                this.transformFirstPersonItem(equippedProgress, 0.0f);
                float swong = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));
                GlStateManager.rotate(-swong * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                GlStateManager.rotate(-swong * 45, 1.0F, swong/2, -0.0F);
                this.doBlockTransformations();
                GL11.glTranslated(1.2, 0.3,0.5);
                GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
            } else if (animations.mode.isCurrentMode("Debug")) {
                this.transformFirstPersonItem(0.2f, equippedProgress);
                this.doBlockTransformations();
                GlStateManager.translate(-0.5, 0.2, 0.0);
            } else if (animations.mode.isCurrentMode("Vanilla")) {
                this.transformFirstPersonItem(swingProgress, 0.0F);
                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Luna")) {
                this.transformFirstPersonItem(swingProgress, 0.0F);
                this.doBlockTransformations();
                final float sin2 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.scale(1.0f, 1.0f, 1.0f);
                GlStateManager.translate(-0.2f, 0.45f, 0.25f);
                GlStateManager.rotate(-sin2 * 20.0f, -5.0f, -5.0f, 9.0f);
            } else if (animations.mode.isCurrentMode("1.7")) {
                this.transformFirstPersonItem(swingProgress - 0.3F, equippedProgress);
                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Swang")) {
                this.transformFirstPersonItem(swingProgress / 2.0F, equippedProgress);
                float var15;
                var15 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.rotate(var15 * 30.0F / 2.0F, -var15, -0.0F, 9.0F);
                GlStateManager.rotate(var15 * 40.0F, 1.0F, -var15 / 2.0F, -0.0F);

                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Swank")) {
                this.transformFirstPersonItem(swingProgress / 2.0F, equippedProgress);
                float var15;
                var15 = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));
                GlStateManager.rotate(var15 * 30.0F, -var15, -0.0F, 9.0F);
                GlStateManager.rotate(var15 * 40.0F, 1.0F, -var15, -0.0F);

                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Swong")) {
                this.transformFirstPersonItem(swingProgress / 2.0F, 0.0F);
                float var151 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.rotate(-var151 * 40.0F / 2.0F, var151 / 2.0F, -0.0F, 9.0F);
                GlStateManager.rotate(-var151 * 30.0F, 1.0F, var151 / 2.0F, -0.0F);

                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Jigsaw")) {
                this.transformFirstPersonItem(0.1f, equippedProgress);
                this.doBlockTransformations();
                GlStateManager.translate(-0.5, 0, 0);
            } else if (animations.mode.isCurrentMode("Hanabi")) {
                this.transformFirstPersonItem(0.1f, equippedProgress);
                this.doBlockTransformations();
                float var15 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.translate(-0.0f, -0.3f, 0.4f);
                GlStateManager.rotate((-var15) * 22.5f, -9.0f, -0.0f, 9.0f);
                GlStateManager.rotate((-var15) * 10.0f, 1.0f, -0.4f, -0.5f);
            } else if (animations.mode.isCurrentMode("Jello")) {
                GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                GlStateManager.translate(0.0F, 0 * -0.6F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                float var3 = MathHelper.sin((float) (0.0F * 0.0F * Math.PI));
                float var4 = MathHelper.sin((float) (MathHelper.sqrt_float(0.0F) * Math.PI));
                GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.scale(0.4F, 0.4F, 0.4F);

                GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                int alpha = (int) Math.min(255,
                        ((System.currentTimeMillis() % 255) > 255 / 2
                                ? (Math.abs(Math.abs(System.currentTimeMillis()) % 255 - 255))
                                : System.currentTimeMillis() % 255) * 2);
                GlStateManager.translate(0.3f, -0.0f, 0.40f);
                GlStateManager.rotate(0.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.translate(0, 0.5f, 0);

                GlStateManager.rotate(90, 1.0f, 0.0f, -1.0f);
                GlStateManager.translate(0.6f, 0.5f, 0);
                GlStateManager.rotate(-90, 1.0f, 0.0f, -1.0f);

                GlStateManager.rotate(-10, 1.0f, 0.0f, -1.0f);
                GlStateManager.rotate(mc.thePlayer.isSwingInProgress ? -alpha / 5f : 1, 1.0f, -0.0f, 1.0f);
            } else if (animations.mode.isCurrentMode("Chill")) {
                this.transformFirstPersonItem(swingProgress / 2.0f - 0.18f, 0.0f);
                GL11.glRotatef(hand * 60.0f / 2.0f, -hand / 2.0f, -0.0f, -16.0f);
                GL11.glRotatef(-hand * 30.0f, 1.0f, hand / 2.0f, -1.0f);
                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Tiny Whack")) {
                this.transformFirstPersonItem(swingProgress / 2.0f - 0.18f, 0.0f);
                GL11.glRotatef(-hand * 40.0f / 2.0f, hand / 2.0f, -0.0f, 9.0f);
                GL11.glRotatef(-hand * 30.0f, 1.0f, hand / 2.0f, -0.0f);
                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Long Hit")) {
                this.transformFirstPersonItem(swingProgress, 0.0f);
                this.doBlockTransformations();
                final float var19 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.translate(-0.05f, 0.6f, 0.3f);
                GlStateManager.rotate(-var19 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-var19 * 70.0f, 1.5f, -0.4f, -0.0f);
            } else if (animations.mode.isCurrentMode("Butter")) {
                this.transformFirstPersonItem(swingProgress * 0.5f, 0.0f);
                GlStateManager.rotate(-hand * -74.0f / 4.0f, -8.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-hand * 15.0f, 1.0f, hand / 2.0f, -0.0f);
                this.doBlockTransformations();
                GL11.glTranslated(1.2, 0.3, 0.5);
                GL11.glTranslatef(-1.0f, this.mc.thePlayer.isSneaking() ? -0.1f : -0.2f, 0.2f);
            } else if (animations.mode.isCurrentMode("Slide")) {
                this.transformFirstPersonItem(0, 0.0f);
                this.doBlockTransformations();
                float var9 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.translate(-0.05f, -0.0f, 0.35f);
                GlStateManager.rotate(-var9 * (float) 60.0 / 2.0f, -15.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-var9 * (float) 70.0, 1.0f, -0.4f, -0.0f);
            } else if (animations.mode.isCurrentMode("Lucky")) {
                this.transformFirstPersonItem(0, 0.0f);
                this.doBlockTransformations();
                float var9 = MathHelper.sin(MathHelper.sqrt_float(equippedProgress) * 0.3215927f);
                GlStateManager.translate(-0.05f, -0.0f, 0.3f);
                GlStateManager.rotate(-var9 * (float) 60.0 / 2.0f, -15.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-var9 * (float) 70.0, 1.0f, -0.4f, -0.0f);
            } else if (animations.mode.isCurrentMode("Ohare")) {
                float f6 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GL11.glTranslated(-0.05D, 0.0D, -0.25);
                this.transformFirstPersonItem(swingProgress / 2, 0.0f);
                GlStateManager.rotate(-f6 * 60.0F, 2.0F, -f6 * 2, -0.0f);
                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Wizzard")) {
                float f6 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * 3.1));
                this.transformFirstPersonItem(swingProgress / 3, 0.0f);
                GlStateManager.rotate(f6 * 30.0F / 1.0F, f6 / -1.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f6 * 10.0F / 10.0F, -f6 / -1.0F, 1.0F, 0.0F);
                GL11.glTranslated(0.0D, 0.4D, 0.0D);
                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Lennox")) {
                float f6 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * 3.1));
                GL11.glTranslated(0.0D, 0.125D, -0.1D);
                this.transformFirstPersonItem(swingProgress / 3, 0.0F);
                GlStateManager.rotate(-f6 * 75.0F / 4.5F, f6 / 3.0F, -2.4F, 5.0F);
                GlStateManager.rotate(-f6 * 75.0F, 1.5F, f6 / 3.0F, -0.0F);
                GlStateManager.rotate(f6 * 72.5F / 2.25F, f6 / 3.0F, -2.7F, 5.0F);
                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Leaked")) {
                this.transformFirstPersonItem(swingProgress, 0);
                this.doBlockTransformations();
                GlStateManager.rotate(-MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI)) * 30.0F, 0.5F, 0.5F, 0);
            } else if (animations.mode.isCurrentMode("Avatar")) {
                this.avatar(swingProgress, equippedProgress);
                this.doBlockTransformations();
            } else if (animations.mode.isCurrentMode("Push")) {
                this.transformFirstPersonItem(swingProgress, 0.0F);
                this.doBlockTransformations();
                GlStateManager.rotate(-MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI)) * 35.0F, -8.0F, -0.0F, 9.0F);
                GlStateManager.rotate(-MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI)) * 10.0F, 1.0F, -0.4F, -0.5F);
            }
            else if (animations.mode.isCurrentMode("Skid")) {
                this.transformFirstPersonItem(swingProgress * 0.5f, 0.0f);
                GlStateManager.rotate(-hand * 10.0f, 0.0f, 15.0f, 300.0f);
                GlStateManager.rotate(-hand * 10.0f, 300.0f, hand / 2.0f, 1.0f);
                this.doBlockTransformations();
                GL11.glTranslated(1.2, 0.2, 0.1);
                GL11.glTranslatef(-2.1f, -0.2f, 0.1f);
            }
            else if (animations.mode.isCurrentMode("Slide2")) {
                this.transformFirstPersonItem(swingProgress, equippedProgress);
                this.doBlockTransformations();
                GL11.glTranslatef(0.1f, -0.1f, 0.3f);
                GlStateManager.translate(0.1f, -0.1f, 0.4f);
            }
            else if (animations.mode.isCurrentMode("Mix")) {
                this.transformFirstPersonItem(swingProgress, equippedProgress / 40.0f);
                this.doBlockTransformations();
            }
            else if (animations.mode.isCurrentMode("SlideT")) {
                this.transformFirstPersonItem(swingProgress, 1.0f);
                this.doBlockTransformations();
                GL11.glTranslatef(0.6f, 0.3f, 0.7f);
                final float slide = MathHelper.sin(equippedProgress * equippedProgress * 5.1415925f);
                GlStateManager.translate(-0.52f, -0.1f, -0.2f);
                GlStateManager.rotate(slide * -19.0f, 25.0f, -0.4f, -5.0f);
            }
            else if (animations.mode.isCurrentMode("SlideA")) {
                this.transformFirstPersonItem(swingProgress * 0.5f, 0.0f);
                GlStateManager.rotate(-hand * -74.0f / 4.0f, -8.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-hand * 15.0f, 1.0f, hand / 2.0f, -0.0f);
                this.doBlockTransformations();
                GL11.glTranslated(1.2, 0.3, 0.5);
                GL11.glTranslatef(-1.0f, this.mc.thePlayer.isSneaking() ? -0.1f : -0.2f, 0.2f);
            }
            else if (animations.mode.isCurrentMode("Epic")) {
                this.transformFirstPersonItem(swingProgress, equippedProgress);
                this.doBlockTransformations();
                GlStateManager.translate(0.0f, 0.0f, 0.0f);
                GlStateManager.rotate(5.0f, 50.0f, 100.0f, 50.0f);
            }
            else if (animations.mode.isCurrentMode("Punch")) {
                this.transformFirstPersonItem(swingProgress, 0.0f);
                this.doBlockTransformations();
                GlStateManager.translate(-0.0f, 0.4f, 0.1f);
                GlStateManager.rotate(-hand * 35.0f, -8.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-hand * 10.0f, 1.0f, -0.4f, -0.5f);
            }
        }

    }

    public float getRotateDirection() {// AllitemRotate->Rotate
        rotateDirection = rotateDirection + delta;
        if (rotateDirection > 360)
            rotateDirection = 0;
        return rotateDirection;
    }

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_);

    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack,
                                    ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void rotateArroundXAndY(float f2, float f3);

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_,
                                          float p_178097_4_);

    /**
     * @author mojang
     */
    @Overwrite
    private void transformFirstPersonItem(float equipProgress, float swingProgress)
    {

        GL11.glTranslatef(0.56f, -0.52f, -0.72f);
        GL11.glTranslatef(0.0f, equipProgress * -0.6f, 0.0f);
        GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
        if (swingProgress > 0.0) {
            final float f = MathHelper.sin((float) (swingProgress * swingProgress * Math.PI));
            final float f2 = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));
            GL11.glRotatef(f * -20.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(f2 * -20.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(f2 * -80.0f, 1.0f, 0.0f, 0.0f);
        }
        float scale = 0.4f;
        if (ModManager.getModule(HitAnimation.class).isEnabled()) {
            scale *= ModManager.getModule(HitAnimation.class).itemScale.getValue().floatValue();
        }
        GL11.glScalef(scale, scale, scale);

        /*
        GlStateManager.translate(ModManager.getModule(HitAnimation.class).posX.getValue(),ModManager.getModule(HitAnimation.class).posY.getValue(),ModManager.getModule(HitAnimation.class).posZ.getValue());
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);

         */
    }

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer var1);

    @Shadow
    protected abstract void doItemUsedTransformations(float f1);

    @Shadow
    protected abstract void doBowTransformations(float p_178098_1_, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayersp, float partialTicks);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float p_178104_2_);
}
