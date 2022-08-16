package cn.hanabi.modules.modules.render;

import cn.hanabi.Wrapper;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.events.EventRender2D;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.modules.world.AntiBot;
import cn.hanabi.utils.GLUtil;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.util.Map;


public class ArrowEsp extends Mod {
    private int alpha;
    private final Value<Double> size = new Value<>("ArrowEsp", "Size", 10d, 5d, 25d, 0.1d);
    private final Value<Double> radius = new Value<>("ArrowEsp", "Radius", 45d, 10d, 200d, 1d);
    private final EntityListener entityListener = new EntityListener();
    private final Value<Boolean> players = new Value<>("ArrowEsp", "Players", true);
    private final Value<Boolean> animals = new Value<>("ArrowEsp", "Animals", true);
    private final Value<Boolean> mobs = new Value<>("ArrowEsp", "Mobs", false);
    private final Value<Boolean> invisibles = new Value<>("ArrowEsp", "Invisibles", false);
    private final Value<Boolean> passives = new Value<>("ArrowEsp", "Passives", true);

    public ArrowEsp() {
        super("ArrowEsp", Category.RENDER);
    }

    @Override
    public void onEnable() {
        alpha = 0;
    }

    @EventTarget
    public void onRender3D(EventPreMotion event) {
        entityListener.render3d();
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        mc.theWorld.loadedEntityList.forEach(o -> {
            if (o instanceof EntityLivingBase && isValid((EntityLivingBase) o)) {
                EntityLivingBase entity = (EntityLivingBase) o;
                Vec3 pos = entityListener.getEntityLowerBounds().get(entity);

                if (pos != null) {
                    float x = (Display.getWidth() / 2f) / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale);
                    float y = (Display.getHeight() / 2f) / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale);
                    float yaw = getRotations(entity) - mc.thePlayer.rotationYaw;
                    GLUtil.startSmooth();
                    GlStateManager.translate(x, y, 0);
                    GlStateManager.rotate(yaw, 0, 0, 1);
                    GlStateManager.translate(-x, -y, 0);
                    RenderUtil.drawTracerPointer(x, y - radius.getValue().floatValue(), size.getValue().floatValue(), 2, 1, getColor(entity, alpha).getRGB());
                    GlStateManager.translate(x, y, 0);
                    GlStateManager.rotate(-yaw, 0, 0, 1);
                    GlStateManager.translate(-x, -y, 0);
                    GLUtil.endSmooth();
                }
            }
        });
    }

    private boolean isOnScreen(Vec3 pos) {
        if (pos.xCoord > -1 && pos.zCoord < 1)
            return pos.xCoord / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) >= 0 && pos.xCoord / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) <= Display.getWidth() && pos.yCoord / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) >= 0 && pos.yCoord / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) <= Display.getHeight();

        return false;
    }

    private boolean isValid(EntityLivingBase entity) {
        return !AntiBot.isBot(entity) && entity != mc.thePlayer && isValidType(entity) && entity.getEntityId() != -1488 && entity.isEntityAlive() && (!entity.isInvisible() || invisibles.getValueState());
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (players.getValueState() && entity instanceof EntityPlayer) || (mobs.getValueState() && (entity instanceof EntityMob || entity instanceof EntitySlime) || (passives.getValueState() && (entity instanceof EntityVillager || entity instanceof EntityGolem)) || (animals.getValueState() && entity instanceof EntityAnimal));
    }

    private float getRotations(EntityLivingBase ent) {
        final double x = ent.posX - mc.thePlayer.posX;
        final double z = ent.posZ - mc.thePlayer.posZ;
        return (float) (-(Math.atan2(x, z) * 57.29577951308232));
    }

    private Color getColor(EntityLivingBase player, int alpha) {
        float f = mc.thePlayer.getDistanceToEntity(player);
        float f1 = 50;
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);

        int design = HUD.design.getColor();
        float red = (float) (design >> 16 & 255) / 255.0F;
        float green = (float) (design >> 8 & 255) / 255.0F;
        float blue = (float) (design & 255) / 255.0F;

        final Color clr = HUD.hudMode.isCurrentMode("Simple") ? new Color(1f, 1f, 1f, (f2)) : new Color(red , green , blue , f2);
        return new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), (int) ((255 - clr.getAlpha()) / 1.3f));
    }

    public static class EntityListener {
        private final Map<Entity, Vec3> entityUpperBounds = Maps.newHashMap();
        private final Map<Entity, Vec3> entityLowerBounds = Maps.newHashMap();

        private void render3d() {
            if (!entityUpperBounds.isEmpty()) {
                entityUpperBounds.clear();
            }
            if (!entityLowerBounds.isEmpty()) {
                entityLowerBounds.clear();
            }
            for (Entity e : mc.theWorld.loadedEntityList) {
                Vec3 bound = getEntityRenderPosition(e);
                bound.add(new Vec3(0, e.height + 0.2, 0));
                Vec3 upperBounds = RenderUtil.to2D(bound.xCoord, bound.yCoord, bound.zCoord), lowerBounds = RenderUtil.to2D(bound.xCoord, bound.yCoord - 2, bound.zCoord);
                if (upperBounds != null && lowerBounds != null) {
                    entityUpperBounds.put(e, upperBounds);
                    entityLowerBounds.put(e, lowerBounds);
                }
            }
        }

        private Vec3 getEntityRenderPosition(Entity entity) {
            double partial = Wrapper.getTimer().renderPartialTicks;

            double x = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partial) - mc.getRenderManager().viewerPosX;
            double y = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partial) - mc.getRenderManager().viewerPosY;
            double z = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partial) - mc.getRenderManager().viewerPosZ;

            return new Vec3(x, y, z);
        }

        public Map<Entity, Vec3> getEntityLowerBounds() {
            return entityLowerBounds;
        }
    }
}
