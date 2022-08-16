package cn.hanabi.modules.modules.render;

import cn.hanabi.Wrapper;
import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventRenderLivingEntity;
import cn.hanabi.injection.interfaces.IRendererLivingEntity;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.Opacity;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class Chams extends Mod {

    public Opacity hue = new Opacity(0);

    public Value<Boolean> rainbow = new Value<>("Chams", "Rainbow", false);
    public Value<Boolean> flat = new Value<>("Chams", "Flat", false);

    public Chams() {
        super("Chams", Category.RENDER);
    }

    @EventTarget
    public void onRender3D(EventRender event) {
        hue.interp(255.0F, 1);
        if (hue.getOpacity() >= 255.0F) {
            hue.setOpacity(0.0F);
        }
    }

    @EventTarget(value = Priority.HIGH)
    public void onRenderEntity(EventRenderLivingEntity event) {
        boolean rainbow = this.rainbow.getValueState();
        if (event.getEntity() instanceof EntityPlayer && event.getEntity() != mc.thePlayer && event.isPre()) {
            if (rainbow) {
                event.setCancelled(true);

                try {
                    Render renderer = mc.getRenderManager().getEntityRenderObject(event.getEntity());
                    if (renderer != null && mc.getRenderManager().renderEngine != null && renderer instanceof RendererLivingEntity) {
                        GL11.glPushMatrix();
                        GL11.glDisable(2929);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(3042);

                        if (this.flat.getValueState()) {
                            GlStateManager.disableLighting();
                        }

                        Color color = Color.getHSBColor(this.hue.getOpacity() / 255.0F, 0.8F, 1.0F);
                        this.glColor(1f, color.getRed(), color.getGreen(), color.getBlue());
                        ((IRendererLivingEntity) renderer).doRenderModel(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getRotationYawHead(), event.getRotationPitch(), event.getOffset());

                        GL11.glEnable(2929);

                        float hue = 255.0F - this.hue.getOpacity();
                        if (hue > 255.0F) {
                            hue = 1.0F;
                        }

                        if (hue < 0.0F) {
                            hue = 255.0F;
                        }

                        Color color1 = Color.getHSBColor(hue / 255.0F, 0.8F, 1.0F);
                        this.glColor(1f, color1.getRed(), color1.getGreen(), color1.getBlue());
                        ((IRendererLivingEntity) renderer).doRenderModel(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getRotationYawHead(), event.getRotationPitch(), event.getOffset());
                        GL11.glEnable(3553);
                        GL11.glDisable(3042);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                        if (this.flat.getValueState()) {
                            GlStateManager.enableLighting();
                        }

                        GL11.glPopMatrix();
                        ((IRendererLivingEntity) renderer).doRenderLayers(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), Wrapper.getTimer().renderPartialTicks, event.getAgeInTicks(), event.getRotationYawHead(), event.getRotationPitch(), event.getOffset());
                        GL11.glPopMatrix();

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                GL11.glEnable(32823);
                GL11.glPolygonOffset(1.0F, -1100000.0F);
            }
        } else if (!rainbow && event.getEntity() instanceof EntityPlayer && event.isPost()) {
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0F, 1100000.0F);
        }
    }

    public void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569F * (float) redRGB;
        float green = 0.003921569F * (float) greenRGB;
        float blue = 0.003921569F * (float) blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }
}
