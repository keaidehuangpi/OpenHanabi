package cn.hanabi.modules.modules.world;

import cn.hanabi.Wrapper;
import cn.hanabi.events.EventChat;
import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.Colors;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TimeHelper;
import com.darkmagician6.eventapi.EventTarget;
import me.yarukon.palette.ColorValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HideAndSeek extends Mod {
    public static List<EntityLivingBase> kids = new ArrayList();
    public TimeHelper timer = new TimeHelper();

    public static ColorValue esp = new ColorValue("HideAndSeek Color", 0.5f, 1f, 1f, 1f, true, false, 10f);

    public HideAndSeek() {
        super("HideAndSeek", Category.WORLD);
    }

    public void onEnable() {
        kids.clear();
    }

    public void onDisable() {
        kids.clear();
    }

    @EventTarget
    public void onChat(EventChat e) {
        if (e.getMessage().contains("躲猫猫")) timer.reset();
    }

    @EventTarget
    public void onRender(EventRender e) {
        for (EntityLivingBase entity : kids) {
            if (entity == null) {
                return;
            }
            Color color = new Color(Colors.DARKRED.c);

            mc.getRenderManager();
            double x = entity.lastTickPosX
                    + (entity.posX - entity.lastTickPosX) * Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
            mc.getRenderManager();
            double y = entity.lastTickPosY
                    + (entity.posY - entity.lastTickPosY) * Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
            mc.getRenderManager();
            double z = entity.lastTickPosZ
                    + (entity.posZ - entity.lastTickPosZ) * Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
            if (entity instanceof EntityPlayer) {
                double d = entity.isSneaking() ? 0.25 : 0.0;
                double mid = 0.275;
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                double rotAdd = -0.25 * (Math.abs(entity.rotationPitch) / 90.0f);
                GL11.glTranslated(0.0, rotAdd, 0.0);
                GL11.glTranslated(((x -= 0.275) + 0.275), ((y += entity.getEyeHeight() - 0.225 - d) + 0.275), ((z -= 0.275) + 0.275));
                GL11.glRotated((-entity.rotationYaw % 360.0f), 0.0, 1.0, 0.0);
                GL11.glTranslated((-(x + 0.275)), (-(y + 0.275)), (-(z + 0.275)));
                GL11.glTranslated((x + 0.275), (y + 0.275), (z + 0.275));
                GL11.glRotated(entity.rotationPitch, 1.0, 0.0, 0.0);
                GL11.glTranslated((-(x + 0.275)), (-(y + 0.275)), (-(z + 0.275)));
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glColor4f((color.getRed() / 255.0f), (color.getGreen() / 255.0f), (color.getBlue() / 255.0f), 1.0f);
                GL11.glLineWidth(1.0f);
                RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - 0.0025, y - 0.0025, z - 0.0025, x + 0.55 + 0.0025, y + 0.55 + 0.0025, z + 0.55 + 0.0025));
                GL11.glColor4f((color.getRed() / 255.0f), (color.getGreen() / 255.0f), (color.getBlue() / 255.0f), 0.5f);
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x - 0.0025, y - 0.0025, z - 0.0025, x + 0.55 + 0.0025, y + 0.55 + 0.0025, z + 0.55 + 0.0025));
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            } else {
                double width = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX;
                double height = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY + 0.25;
                float red = 1.0f;
                float green = 0.0f;
                float blue = 0.0f;
                float alpha = 0.5f;
                float lineRed = 0.0f;
                float lineGreen = 0.5f;
                float lineBlue = 1.0f;
                float lineAlpha = 1.0f;
                float lineWdith = 2.0f;
                RenderUtil.drawEntityESP(x, y, z, width, height, esp.getColor(), 0.0f, 0.5f, 1.0f, 0.0f, 2.0f);
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase &&
                    !(entity instanceof EntityPlayer) &&
                    !(entity instanceof EntityArmorStand) &&
                    !(entity instanceof EntityWither) &&
                    !kids.contains(entity) &&
                    !entity.getName().contains("\247c\247l") &&
                    timer.isDelayComplete(5000)) {
                double pos = (entity.posY - (int) entity.posY);
                if (pos > 0.1 && (pos + "").length() > 8) {
                    kids.add((EntityLivingBase) entity);
                    PlayerUtil.tellPlayer("\247b[Hanabi]\247a检测到一个异常动物:" + entity.getName());
                }
            }
        }

        kids.removeIf(entity -> entity.isDead || entity.getHealth() < 0);
    }
}
