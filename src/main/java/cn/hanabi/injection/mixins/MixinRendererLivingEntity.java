package cn.hanabi.injection.mixins;

import cn.hanabi.events.EventPreMotion;
import cn.hanabi.events.EventRenderLivingEntity;
import cn.hanabi.injection.interfaces.IRendererLivingEntity;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.render.ESP;
import cn.hanabi.modules.modules.render.Thermal;
import cn.hanabi.utils.OutlineUtils;
import cn.hanabi.utils.RenderUtil;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends MixinRender implements IRendererLivingEntity {


    @Shadow
    @Final
    private static Logger logger;
    @Shadow
    protected boolean renderOutlines = false;
    @Shadow
    protected ModelBase mainModel;

    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At("HEAD"), cancellable = true)
    public void onChat(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        if (ModManager.getModule("Nametags").isEnabled() && entity instanceof EntityPlayer)
            ci.cancel();
    }

    @Shadow
    protected abstract float interpolateRotation(float par1, float par2, float par3);

    @Shadow
    protected abstract float getSwingProgress(T livingBase, float partialTickTime);

    @Shadow
    protected abstract void renderLivingAt(T entityLivingBaseIn, double x, double y, double z);

    @Shadow
    protected abstract void rotateCorpse(T bat, float p_77043_2_, float p_77043_3_, float partialTicks);

    @Shadow
    protected abstract float handleRotationFloat(T livingBase, float partialTicks);

    @Shadow
    protected abstract void preRenderCallback(T entitylivingbaseIn, float partialTickTime);

    @Shadow
    protected abstract boolean setScoreTeamColor(EntityLivingBase entityLivingBaseIn);

    @Shadow
    protected abstract void unsetScoreTeamColor();


    @Shadow
    protected abstract void renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_);

    @Shadow
    protected abstract boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks);

    @Shadow
    protected abstract void unsetBrightness();


    public void doRenderModel(Object entitylivingbaseIn, float a, float b, float c, float d, float e, float scaleFactor) {
        this.renderModel((T) entitylivingbaseIn, a, b, c, d, e, scaleFactor);
    }

    public void doRenderLayers(Object entitylivingbaseIn, float a, float b, float partialTicks, float d, float e, float f, float g) {
        this.renderLayers((T) entitylivingbaseIn, a, b, partialTicks, d, e, f, g);
    }

	/*
	@Overwrite
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
		this.mainModel.isRiding = entity.isRiding();
		this.mainModel.isChild = entity.isChild();

		try {
			float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
			float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
			float f2 = f1 - f;

			if (entity.isRiding() && entity.ridingEntity instanceof EntityLivingBase) {
				EntityLivingBase entitylivingbase = (EntityLivingBase) entity.ridingEntity;
				f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset,
						partialTicks);
				f2 = f1 - f;
				float f3 = MathHelper.wrapAngleTo180_float(f2);

				if (f3 < -85.0F) {
					f3 = -85.0F;
				}

				if (f3 >= 85.0F) {
					f3 = 85.0F;
				}

				f = f1 - f3;

				if (f3 * f3 > 2500.0F) {
					f += f3 * 0.2F;
				}
			}

			float f8 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            float f7 = this.handleRotationFloat(entity, partialTicks);
			this.rotateCorpse(entity, f8, f, partialTicks);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(-1.0F, -1.0F, 1.0F);
			this.preRenderCallback(entity, partialTicks);
			float f4 = 0.0625F;
			GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
			float f5 = entity.prevLimbSwingAmount
					+ (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
			float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

			if(entity instanceof EntityPlayer) {
                EventRenderLivingEntity pre = new EventRenderLivingEntity(entity, true, f6, f5, f7, f2, f8, f, f4);
                EventManager.call(pre);

                if(pre.isCancelled()) {
                	return;
                }

            }

			if (entity.isChild()) {
				f6 *= 3.0F;
			}

			if (f5 > 1.0F) {
				f5 = 1.0F;
			}

			GlStateManager.enableAlpha();
			this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f7, f2, f8, 0.0625F, entity);

			if (this.renderOutlines) {
				boolean flag1 = this.setScoreTeamColor(entity);
                this.renderModel(entity, f6, f5, f7, f2, f8, 0.0625F);

				if (flag1) {
					this.unsetScoreTeamColor();
				}
			} else {
				boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                this.renderModel(entity, f6, f5, f7, f2, f8, 0.0625F);

				if (flag) {
					this.unsetBrightness();
				}

				GlStateManager.depthMask(true);

				if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
                	this.renderLayers(entity, f6, f5, partialTicks, f7, f2, f8, 0.0625F);
				}
			}

			GlStateManager.disableRescaleNormal();
		} catch (Exception exception) {
			logger.error((String) "Couldn\'t render entity", (Throwable) exception);
		}

		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();

		if (!this.renderOutlines) {
			//super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}


	}



    /**
     * @author
     */

    /**
     * @author
     */
    @Overwrite
    protected void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor) {
        boolean flag = !entitylivingbaseIn.isInvisible();
        boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);

        if (flag || flag1) {
            if (!this.bindEntityTexture(entitylivingbaseIn)) {
                return;
            }

            if (flag1) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            if (ModManager.getModule(Thermal.class).isEnabled() && entitylivingbaseIn != Minecraft.getMinecraft().thePlayer) {
                GL11.glPushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
                RenderUtil.color(Thermal.renderColor.getColor());
                this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }


            try {
                if (ModManager.getModule(ESP.class).isEnabled() && entitylivingbaseIn != Minecraft.getMinecraft().thePlayer && entitylivingbaseIn instanceof EntityPlayer && ModManager.getModule(ESP.class).mode.isCurrentMode("OutLine")) {
                    GL11.glPushMatrix();
                    GlStateManager.depthMask(true);
                    if (Minecraft.getMinecraft().theWorld != null) {
                        RenderUtil.color(ESP.esp.getColor());
                        this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
                        OutlineUtils.renderOne();
                        RenderUtil.color(ESP.esp.getColor());
                        this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
                        OutlineUtils.renderTwo();
                        RenderUtil.color(ESP.esp.getColor());
                        this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
                        RenderUtil.color(ESP.esp.getColor());
                        OutlineUtils.renderThree();
                        OutlineUtils.renderFour();
                        RenderUtil.color(ESP.esp.getColor());
                        this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
                        OutlineUtils.renderFive();
                    }

                    GL11.glColor4f(1, 1, 1, 1);
                    GL11.glPopMatrix();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);

            if (flag1) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
    }


    /**
     * @author
     */
    @Overwrite
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        this.mainModel.isRiding = entity.isRiding();

        this.mainModel.isChild = entity.isChild();

        try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f2 = f1 - f;

            if (this.mainModel.isRiding && entity.ridingEntity instanceof EntityLivingBase) {
                EntityLivingBase entitylivingbase = (EntityLivingBase) entity.ridingEntity;
                f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                f2 = f1 - f;
                float f3 = MathHelper.wrapAngleTo180_float(f2);

                if (f3 < -85.0F) {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F) {
                    f3 = 85.0F;
                }

                f = f1 - f3;

                if (f3 * f3 > 2500.0F) {
                    f += f3 * 0.2F;
                }
            }

            //Created by Thread on 2020年12月11日21:25:25;

            float f8 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

            if (entity instanceof EntityPlayerSP && entityYaw != 0) {
                f8 = interpolateRotation(EventPreMotion.RPPITCH, EventPreMotion.RPITCH, partialTicks);
            }

            this.renderLivingAt(entity, x, y, z);
            float f7 = this.handleRotationFloat(entity, partialTicks);
            this.rotateCorpse(entity, f7, f, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(entity, partialTicks);
            float f4 = 0.0625F;
            GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
            float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

            if (entity instanceof EntityPlayer) {
                EventRenderLivingEntity pre = new EventRenderLivingEntity(entity, true, f6, f5, f7, f2, f8, f, f4);
                EventManager.call(pre);

                if (pre.isCancelled()) {
                    return;
                }
            }

            if (entity.isChild()) {
                f6 *= 3.0F;
            }

            if (f5 > 1.0F) {
                f5 = 1.0F;
            }


            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f7, f2, f8, 0.0625F, entity);

            if (this.renderOutlines) {
                boolean flag1 = this.setScoreTeamColor(entity);
                this.renderModel(entity, f6, f5, f7, f2, f8, 0.0625F);

                if (flag1) {
                    this.unsetScoreTeamColor();
                }
            } else {
                boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                // REN WU MO XING
                this.renderModel(entity, f6, f5, f7, f2, f8, 0.0625F);

                if (flag) {
                    this.unsetBrightness();
                }

                GlStateManager.depthMask(true);

                if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
                    this.renderLayers(entity, f6, f5, partialTicks, f7, f2, f8, 0.0625F);
                }
            }

            GlStateManager.disableRescaleNormal();
        } catch (Exception exception) {
            logger.error("Couldn't render entity", exception);
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        if (!this.renderOutlines) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
        EventRenderLivingEntity post = new EventRenderLivingEntity(entity, false);
        EventManager.call(post);
    }
}
