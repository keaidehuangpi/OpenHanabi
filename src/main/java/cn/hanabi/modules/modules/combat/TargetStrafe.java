package cn.hanabi.modules.modules.combat;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventMove;
import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventWorldChange;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.rotation.RotationUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ObfuscationClass
public class TargetStrafe extends Mod {

    // SkidSense
    public static boolean dire = true;
    public static Value<Double> range = new Value<>("TargetStrafe", "Strafe Range", 2.0, 0.5, 4.5, 0.1);
    public static Value<Boolean> targetkey = new Value<>("TargetStrafe", "Space Toggled", true);
    public static Value<Boolean> voidCheck = new Value<>("TargetStrafe", "Void Check", true);
    public static Value<Boolean> behindValue = new Value<>("TargetStrafe", "Behind", false);

    public static Value<Boolean> change = new Value<>("TargetStrafe", "Direction", true);
    public static Value<Boolean> render = new Value<>("TargetStrafe", "Render", true);

    public static boolean direction = true;
    public TimeHelper timer = new TimeHelper();
    private int strafe = -1;
    public float yaw;
    public TargetStrafe() {
        super("TargetStrafe", Category.COMBAT);
    }


    public static boolean canStrafe() {
        boolean press = !targetkey.getValue() || mc.gameSettings.keyBindJump.isKeyDown();
        return (KillAura.target != null) && press && (ModManager.getModule("KillAura").isEnabled() && (ModManager.getModule("Speed").isEnabled() || ModManager.getModule("Fly").isEnabled()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventTarget
    private void onChangeWorld(EventWorldChange event) {
        strafe = -1;
    }

    @EventTarget
    private void onRender3D(final EventRender event) {
        if (canStrafe() && render.getValue()) {
            EntityLivingBase target = KillAura.target;
            esp(target, event.getPartialTicks(), range.getValue());
        }
    }

    public void esp(Entity entity, float partialTicks, double rad) {
        float points = 90F;
        GlStateManager.enableDepth();
        for (double il = 0; il < 4.9E-324; il += 4.9E-324) {
            GL11.glPushMatrix();
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glEnable(2881);
            GL11.glEnable(2832);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glHint(3154, 4354);
            GL11.glHint(3155, 4354);
            GL11.glHint(3153, 4354);
            GL11.glDisable(2929);
            GL11.glLineWidth(3.5f);
            GL11.glBegin(3);
            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
            final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
            final double pix2 = 6.283185307179586;
            float speed = 5000f;
            float baseHue = System.currentTimeMillis() % (int) speed;
            while (baseHue > speed) {
                baseHue -= speed;
            }
            baseHue /= speed;
            for (int i = 0; i <= 90; ++i) {
                float max = ((float) i + (float) (il * 8)) / points;
                float hue = max + baseHue;
                while (hue > 1) {
                    hue -= 1;
                }
                final float r = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getRed();
                final float g = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getGreen();
                final float b = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getBlue();
                int color = Color.WHITE.getRGB();
                GL11.glColor3f(r, g, b);
                GL11.glVertex3d(x + rad * Math.cos(i * pix2 / points), y + il, z + rad * Math.sin(i * pix2 / points));
            }
            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
            GL11.glDisable(2848);
            GL11.glDisable(2881);
            GL11.glEnable(2832);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
            GlStateManager.color(255, 255, 255);
        }


    }


    public boolean isStrafing(EventMove event, EntityLivingBase target, double moveSpeed) {

        final boolean pressingSpace = !targetkey.getValue() || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());

        if (!isEnabled() || target == null || moveSpeed == 0 || !pressingSpace) return true;

        boolean aroundVoid = false;
        for (int x = -1; x < 1; x++)
            for (int z = -1; z < 1; z++)
                if (isVoid(x, z))
                    aroundVoid = true;

        float yaw = RotationUtil.getRotationFromEyeHasPrev(target).getYaw();

        boolean behindTarget = RotationUtil.getRotationDifference(MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(target.rotationYaw)) <= 10;

        if (mc.thePlayer.isCollidedHorizontally || (aroundVoid && voidCheck.getValue()) || (behindTarget && behindValue.getValue()))
            strafe *= -1;

        float targetStrafe = change.getValue() && mc.thePlayer.moveStrafing != 0 ? (mc.thePlayer.moveStrafing * strafe) : strafe;
        if (PlayerUtil.isBlockUnder())
            targetStrafe = 0;

        final double rotAssist = 45F / getEnemyDistance(target), moveAssist = 45F / getStrafeDistance(target);

        float mathStrafe = 0;

        if (targetStrafe > 0) {

            if ((target.getEntityBoundingBox().minY > mc.thePlayer.getEntityBoundingBox().maxY || target.getEntityBoundingBox().maxY < mc.thePlayer.getEntityBoundingBox().minY) && getEnemyDistance(target) < range.getValue().floatValue())
                yaw += -rotAssist;

            mathStrafe += -moveAssist;
        } else if (targetStrafe < 0) {

            if ((target.getEntityBoundingBox().minY > mc.thePlayer.getEntityBoundingBox().maxY || target.getEntityBoundingBox().maxY < mc.thePlayer.getEntityBoundingBox().minY) && getEnemyDistance(target) < range.getValue().floatValue())
                yaw += rotAssist;

            mathStrafe += moveAssist;
        }


        double[] doSomeMath = {
                Math.cos(Math.toRadians(yaw + 90F + mathStrafe)),
                Math.sin(Math.toRadians(yaw + 90F + mathStrafe))
        };

        double[] asLast = {
                moveSpeed * doSomeMath[0],
                moveSpeed * doSomeMath[1]
        };

        if (event != null) {
            event.setX(mc.thePlayer.motionX = asLast[0]);
            event.setZ(mc.thePlayer.motionZ =asLast[1]);
        } else {
            mc.thePlayer.motionX = asLast[0];
            mc.thePlayer.motionZ = asLast[1];
        }

        return false;
    }

    private double getEnemyDistance(EntityLivingBase target) {
        return mc.thePlayer.getDistance(target.posX, mc.thePlayer.posY, target.posZ);
    }

    private float getStrafeDistance(EntityLivingBase target) {
        return (float) Math.max((getEnemyDistance(target) - range.getValue().floatValue()), getEnemyDistance(target) - (getEnemyDistance(target) - range.getValue().floatValue() / (range.getValue().floatValue() * 2)));
    }

    private boolean isVoid(int x, int z) {

        if (mc.thePlayer.posY < 0) return true;

        int off = 0;
        while (off < mc.thePlayer.posY + 2) {
            final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(x, -off, z);
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                off += 2;
                continue;
            }
            return false;
        }
        return true;
    }

}
