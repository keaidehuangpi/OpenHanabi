package cn.hanabi.modules.modules.render;

import cn.hanabi.Wrapper;
import cn.hanabi.events.EventRender;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import me.yarukon.palette.ColorValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ESP extends Mod {
    public static Value<String> mode = new Value("ESP", "Mode", 0);
    private final Value<Boolean> invisible = new Value<>("ESP", "Invisible", false);

    public static ColorValue esp = new ColorValue("Esp Color", 0.5f, 1f, 1f, 1f, true, false, 10f);


    public ESP() {
        super("ESP", Category.RENDER);
        mode.LoadValue(new String[]{"Box", "2D", "OutLine"});

    }

    public void renderBox(Entity entity, double r, double g, double b) {
        if ((entity.isInvisible() && !invisible.getValueState())) {
            return;
        }

        double x = entity.lastTickPosX
                + (entity.posX - entity.lastTickPosX) * Wrapper.getTimer().renderPartialTicks
                - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double y = entity.lastTickPosY
                + (entity.posY - entity.lastTickPosY) * Wrapper.getTimer().renderPartialTicks
                - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double z = entity.lastTickPosZ
                + (entity.posZ - entity.lastTickPosZ) * Wrapper.getTimer().renderPartialTicks
                - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        double width = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX - 0.1;
        double height = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY
                + 0.25;
        RenderUtil.drawEntityESP(x, y, z, width, height, esp.getColor(), 1f,
                1f, 1f, 0f, 1f);
    }

    @EventTarget
    public void onRender(EventRender event) {
        if (mode.isCurrentMode("Box")) {
            this.setDisplayName("Box");
            for (Object o : mc.theWorld.loadedEntityList) {
                if (o instanceof EntityPlayer) {
                    EntityPlayer ent = (EntityPlayer) o;
                    if (ent != mc.thePlayer && !ent.isDead) {
                        renderBox(ent, 1, 1, 1);
                    }
                }
            }
        } else if (mode.isCurrentMode("2D")) {
            this.setDisplayName("2D");
            this.doOther2DESP();
        }
    }

    private boolean isValid(EntityLivingBase entity) {
        return entity != mc.thePlayer && (!(entity.getHealth() <= 0.0F) && (entity instanceof EntityPlayer));
    }

    private void doOther2DESP() {
        for (final EntityPlayer entity : mc.theWorld.playerEntities) {
            if (entity.isInvisible() && !invisible.getValueState()) {
                return;
            }

            if (isValid(entity)) {
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glDisable(2929);
                GL11.glNormal3f(0.0f, 1.0f, 0.0f);
                GlStateManager.enableBlend();
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(3553);
                final float partialTicks = Wrapper.getTimer().renderPartialTicks;
                final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
                final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
                final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
                float SCALE = 0.035f;
                SCALE /= 2.0f;
                final float xMid = (float) x;
                final float yMid = (float) y + entity.height + 0.5f - (entity.isChild() ? (entity.height / 2.0f) : 0.0f);
                final float zMid = (float) z;
                GlStateManager.translate((float) x, (float) y + entity.height + 0.5f - (entity.isChild() ? (entity.height / 2.0f) : 0.0f), (float) z);
                GL11.glNormal3f(0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(-SCALE, -SCALE, -SCALE);
                final Tessellator tesselator = Tessellator.getInstance();
                final WorldRenderer worldRenderer = tesselator.getWorldRenderer();
                final double xLeft = -30.0;
                final double xRight = 30.0;
                final double yUp = 15.0;
                final double yDown = 140.0;
                RenderUtil.drawRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, ClientUtil.reAlpha(new Color(255, 255, 255).getRGB(), 0.2f));
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GlStateManager.disableBlend();
                GL11.glDisable(3042);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glNormal3f(1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
        }
    }

}
