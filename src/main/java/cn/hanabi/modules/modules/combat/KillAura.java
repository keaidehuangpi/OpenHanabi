package cn.hanabi.modules.modules.combat;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.Wrapper;
import cn.hanabi.events.*;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.injection.interfaces.IEntityPlayer;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.render.ClickGUIModule;
import cn.hanabi.modules.modules.world.AntiBot;
import cn.hanabi.modules.modules.world.AutoL;
import cn.hanabi.modules.modules.world.Teams;
import cn.hanabi.utils.*;
import cn.hanabi.utils.rotation.Rotation;
import cn.hanabi.utils.rotation.RotationUtil;
import cn.hanabi.utils.rotation.VecRotation;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import me.yarukon.font.GlyphPageFontRenderer;
import me.yarukon.palette.ColorValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.*;
import net.minecraft.world.WorldSettings;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.*;

@ObfuscationClass
public class KillAura extends Mod {
    // 客户端设置
    public static Value<Boolean> autoBlock = new Value<>("KillAura", "AutoBlock", true);

    public static Value<Boolean> interact = new Value<>("KillAura", "Interact", false);

    public static Value<Boolean> dynamic = new Value<>("KillAura", "Dynamic-UnBlock", false);
    public static Value<Boolean> glich = new Value<>("KillAura", "Swing-Glich", false);

    public static Value<Double> reach = new Value<>("KillAura", "Range", 4.2, 3.0, 6.0, 0.1);
    public static Value<Double> switchsize = new Value<>("KillAura", "MaxTargets", 1.0, 1.0, 7.0, 1.0);
    // Utils
    public static List<EntityLivingBase> targets = new ArrayList<>();
    public static ArrayList<EntityLivingBase> attacked = new ArrayList<>();
    // 实体变量
    public static EntityLivingBase target = null;


    public static Value<Double> minCps = new Value<>("KillAura", "Min CPS", 6.0, 1.0, 20.0, 1.0);
    public static Value<Double> maxCps = new Value<>("KillAura", "Max CPS", 12.0, 1.0, 20.0, 1.0);
    public static Value<Double> blockReach = new Value<>("KillAura", "Block Range", 0.5, 0.0, 6.0, 0.1);
    public static Value<Boolean> attackPlayers = new Value<>("KillAura", "Players", true);
    public static Value<Boolean> attackAnimals = new Value<>("KillAura", "Animals", false);
    public static Value<Boolean> attackMobs = new Value<>("KillAura", "Mobs", false);
    public static Value<Boolean> throughblock = new Value<>("KillAura", "Through Block", true);
    public static Value<Boolean> invisible = new Value<>("KillAura", "Invisibles", false);
    public static Value<Double> fov = new Value<>("KillAura", "Fov Check", 360d, 10d, 360d, 10d);
    public static Value<Boolean> rotationStrafe = new Value<>("KillAura", "Rotation Strafe", false);
    public static Value<Double> yawxmin = new Value<>("KillAura", "Yaw X Min Offset", 0d, -1d, 1d, 0.1);
    public static Value<Double> yawxmax = new Value<>("KillAura", "Yaw X Max Offset", 0d, -1d, 1d, 0.1);
    public static Value<Double> yawzmin = new Value<>("KillAura", "Yaw Z Min Offset", 0d, -1d, 1d, 0.1);
    public static Value<Double> yawzmax = new Value<>("KillAura", "Yaw Z Max Offset", 0d, -1d, 1d, 0.1);

    public static Value<Double> pitchRand = new Value<>("KillAura", "Pitch Offset", 0d, -1d, 1d, 0.1);

    public static Value<Double> hitbox = new Value<>("KillAura", "HitBox Expand", 0.1d, 0.0d, 0.5d, 0.01d);


    public static Value<Boolean> predict = new Value<>("KillAura", "Predict", false);
    public static Value<Boolean> resolverY = new Value<>("KillAura", "Y-Resolver", false);
    public static Value<Boolean> resolverXZ = new Value<>("KillAura", "Horizon-Resolver", true);

    public static Value<Boolean> xRandom = new Value<>("KillAura", "X Random", false);
    public static Value<Boolean> zRandom = new Value<>("KillAura", "Z Random", false);
    public static Value<Boolean> pitchRandom = new Value<>("KillAura", "Pitch Random", false);

    public static Value<Double> resolvers = new Value<>("KillAura", "Resolver Accuracy", 3d, 1d, 12d, 1d);
    public static Value<Double> predictor = new Value<>("KillAura", "Pre Attack Fov", 1.1d, 1d, 3d, 0.01d);
    public static Value<Double> smooth = new Value<>("KillAura", "Rotation Smooth", 0d, 0d, 100d, 1d);

    public static Value<Double> blockRate = new Value<>("KillAura", "Block Rate", 100d, 10d, 100d, 5d);

    public static Value<Boolean> kbBlock = new Value<>("KillAura", "ExtraKB When Hurt", false);
    public static Value<Boolean> hurtBlock = new Value<>("KillAura", "Block When Hurt", false);

    public static Random random = new Random();
    // 开关
    public static boolean isBlocking = false;
    public static EntityLivingBase needHitBot = null;
    public static Value<Boolean> namecheck = new Value<>("KillAura", "Check Name", true);

    public static Value<Double> reverse = new Value<>("KillAura", "Rotation Reverse Chance", 0d, 0.0, 100.0, 5.0);
    public static Value<Double> outpoint = new Value<>("KillAura", "Rotation Mistake Chance", 0d, 0.0, 100.0, 5.0);
    public static double animation = 0;
    public static Rotation serverRotation = new Rotation(0, 0);
    private final TimeHelper switchTimer = new TimeHelper();
    public Value<Double> switchDelay = new Value<>("KillAura", "Switch Delay", 50d, 0d, 2000d, 10d);
    public Value<Double> mistake = new Value<>("KillAura", "Mistakes", 0.0, 0.0, 20.0, 1);
    // public Value<Double> hurttime = new Value<>("KillAura", "Hurt Time", 10.0, 1.0, 10.0, 1.0);
    public Value<Boolean> autodisable = new Value<>("KillAura", "Auto Disable", true);
    public Value<Boolean> targetHUD = new Value<>("KillAura", "Show Target", true);
    public Value<Boolean> forcerise = new Value<>("KillAura", "Slient", true);
    public Value<Boolean> esp = new Value<>("KillAura", "ESP", true);
    public Value<Boolean> morekb = new Value<>("KillAura", "More KB", false);
    public Value<Boolean> pre = new Value<>("KillAura", "Pre Hit", false);
    public Value<Boolean> block = new Value<>("KillAura", "Pre Block", false);

    public Value<Boolean> aacbot = new Value<>("KillAura", "AAC Bot Check", false);
    public Value<Boolean> mult = new Value<>("KillAura", "Multi", false);


    //Exploit
    public Value<Boolean> force = new Value<>("KillAura", "Force Update", false);
    public Value<Boolean> version = new Value<>("KillAura", "Attack Fix", false);


    public Value<Boolean> hitableCheck = new Value<>("KillAura", "Hitable Check", true);
    public Value<Boolean> canTurn = new Value<>("KillAura", "Rotate Head", true);
    public Value<Double> minTurn = new Value<>("KillAura", "Min Turn Head Speed", 60.0, 1.0, 180.0, 1.0);
    public Value<Double> maxTurn = new Value<>("KillAura", "Max Turn Head Speed", 60.0, 1.0, 180.0, 1.0);
    public Value<Boolean> witherPriority = new Value<>("KillAura", "Wither Priority", true);
    public Value<String> blockMode = new Value<>("KillAura", "Block Mode", 0);
    public Value<String> SensitivityMode = new Value<>("KillAura", "S-Fix Mode", 0);
    public Value<String> EspMode = new Value<>("KillAura", "ESP Mode", 0);
    public Value<String> hudMode = new Value<>("KillAura", "TargetHUD", 0);
    public Value<String> priority = new Value<>("KillAura", "Priority", 1);


    //Color Moment
    public ColorValue boxColor = new ColorValue("Target ESP Color", 0.5f, 1f, 1f, 1f, false, false, 10f);

    public int index;
    // TimeHelper
    public TimeHelper attacktimer = new TimeHelper();
    public TimeHelper rotationTimer = new TimeHelper();
    public DecimalFormat format = new DecimalFormat("0.0");
    //Render
    float lastHealth = 0;
    double delay = 0;
    boolean step = false;
    // 转头
    float[] lastRotations;
    //KillAura Logic
    double cps;
    //Render
    boolean nulltarget = false;
    private double healthBarWidth;
    private double healthBarWidth2;
    private double hudHeight;

    public static int killCount = 0;

    //other stuff

    public KillAura() {
        super("KillAura", Category.COMBAT);
        priority.LoadValue(new String[]{"Angle", "Range", "Armor", "Health", "Fov", "Hurt Time"});
        hudMode.LoadValue(new String[]{"Simple", "Fancy", "Flat", "Test"});
        EspMode.LoadValue(new String[]{"Box", "Circle", "New", "Cylinder", "ExeterCross"});
        blockMode.LoadValue(new String[]{"Simple", "Always", "Exploit"});
        SensitivityMode.LoadValue(new String[]{"None", "Normal", "Prefect"});
        attacked = new ArrayList<>();
    }

    public static double getRandomDoubleInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    public static double RandomFloat(float minFloat, float maxFloat) {
        return minFloat >= maxFloat ? minFloat : new Random().nextFloat() * (maxFloat - minFloat) + minFloat;
    }

    public static long randomClickDelay(final double minCPS, final double maxCPS) {
        return (long) ((Math.random() * (1000 / minCPS - 1000 / maxCPS + 1)) + 1000 / maxCPS);
    }

    private boolean isInMenu() {
        final GuiScreen currentScreen = mc.currentScreen;
        return currentScreen != null;
    }

    public static boolean isValidEntityType(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            if (entity != mc.thePlayer && !mc.thePlayer.isDead
                    && !(entity instanceof EntityArmorStand || entity instanceof EntitySnowman)) {

                if (entity instanceof EntityPlayer && attackPlayers.getValueState()) {

                    if (!mc.thePlayer.canEntityBeSeen(entity) && !throughblock.getValueState())
                        return false;

                    if (entity.isInvisible() && !invisible.getValueState())
                        return false;

                    return !AntiBot.isBot(entity) && !Teams.isOnSameTeam(entity);
                }

                if ((entity instanceof EntityMob || entity instanceof EntitySlime) && attackMobs.getValueState()) {
                    return !AntiBot.isBot(entity);
                }

                if (entity instanceof EntityWither)
                    return !Teams.isOnSameTeam(entity);

                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)
                        && attackAnimals.getValueState()) {
                    return !AntiBot.isBot(entity);
                }
            }
        }

        return false;
    }

    public static boolean isValidEntity(Entity entity) {
        // friend check
        if (FriendManager.getFriends().size() > 0) {
            if (FriendManager.isFriend(entity.getName()))
                return false;
        }

        // Fov check
        if (!AimUtil.isVisibleFOV(entity, fov.getValue().floatValue()))
            return false;

        if (entity instanceof EntityLivingBase) {
            if (entity.isDead || ((EntityLivingBase) entity).getHealth() <= 0f) {

                if (attacked.contains(entity)) {
                    killCount++;

                    if (ModManager.getModule("AutoL").isEnabled()) {
                        if (AutoL.wdr.getValueState() && !AutoL.wdred.contains(target.getName())) {
                            AutoL.wdred.add(target.getName());
                            mc.thePlayer.sendChatMessage("/wdr " + target.getName() + " ka fly reach nokb jesus ac");
                        }
                        mc.thePlayer.sendChatMessage(AutoL.getAutoLMessage(entity.getName()));
                    }

                    attacked.remove(entity);
                }

                return false;
            }

            if (mc.thePlayer.getDistanceToEntity(entity) < (reach.getValueState() + blockReach.getValueState())) {


                if (entity != mc.thePlayer && !mc.thePlayer.isDead
                        && !(entity instanceof EntityArmorStand || entity instanceof EntitySnowman)) {

                    if (entity instanceof EntityPlayer && attackPlayers.getValueState()) {

                        if (!mc.thePlayer.canEntityBeSeen(entity) && !throughblock.getValueState())
                            return false;

                        if (entity.isInvisible() && !invisible.getValueState())
                            return false;

                        return !AntiBot.isBot(entity) && !Teams.isOnSameTeam(entity);
                    }

                    if ((entity instanceof EntityMob || entity instanceof EntitySlime) && attackMobs.getValueState()) {
                        if (entity instanceof EntityWither)
                            return !Teams.isOnSameTeam(entity);

                        if (entity.getName().contains(mc.thePlayer.getName()) && namecheck.getValue())
                            return false;

                        return !AntiBot.isBot(entity);
                    }

                    if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)
                            && attackAnimals.getValueState()) {

                        if (entity.getName().contains(mc.thePlayer.getName()) && namecheck.getValue())
                            return false;

                        return !AntiBot.isBot(entity);
                    }
                }
            }
        }
        return false;
    }


    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs((angle1 - angle2)) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
        Color[] colors = new Color[]{new Color(0, 81, 179), new Color(0, 153, 255), new Color(47, 154, 241)};
        float progress = health / maxHealth;
        return blendColors(fractions, colors, progress).brighter();
    }

    public static int[] getFractionIndices(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = getFractionIndices(fractions, progress);
            float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
            Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            return blend(colorRange[0], colorRange[1], 1.0f - weight);
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        return new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);
    }

    @EventTarget
    public void onReload(EventWorldChange e) {
        if (autodisable.getValueState()) {
            this.set(false);
        }
    }


    @EventTarget
    private void onStrafe(EventStrafe event) {
        if (rotationStrafe.getValueState()) event.yaw = lastRotations[0];
    }

    @EventTarget
    private void onJump(EventJump event) {
        if (rotationStrafe.getValueState()) event.yaw = lastRotations[0];
    }

    private void update() {
        // 初始化变量
        if (!targets.isEmpty() && index >= targets.size())
            index = 0; // 超过Switch限制

        for (EntityLivingBase ent : targets) {
            // 添加实体
            if (isValidEntity(ent))
                continue;
            targets.remove(ent);
        }

        // Switch结束

        getTarget(); // 拿实体

        if (targets.size() == 0) { // 实体数量为0停止攻击
            target = null;
        } else {

            target = targets.get(index);// 设置攻击的Target
            if (mc.thePlayer.getDistanceToEntity(target) > reach.getValueState()) {
                target = targets.get(0);
            } else {
                if (TargetManager.getTarget().size() > 0) {
                    for (EntityLivingBase ent : targets) {
                        for (int i = 0; i < TargetManager.getTarget().size(); i++) {
                            if (ent.getName().contains((String) TargetManager.getTarget().get(i))) {
                                targets.removeIf(entity -> !TargetManager.isTarget(entity.getName()));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {
        if (!rotationStrafe.getValue()) return;

        update(); // 拿实体

        needHitBot = null;

        // Switch开始
        if (target != null) {
            // Switch开始
            if (switchTimer.isDelayComplete(switchDelay.getValueState())
                    && targets.size() > 1) {
                switchTimer.reset();
                ++index;
            }
            float[] realLastRot = lastRotations;
            rotation(realLastRot);
        } else {
            targets.clear();
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                    && autoBlock.getValueState() && isBlocking) {
                unBlock(true);
            }
            lastRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        }
    }

    @EventTarget
    public void onPre(EventPreMotion event) {
        boolean toggled = ModManager.getModule("Scaffold").isEnabled();

        if (blockMode.isCurrentMode("Always") && mc.thePlayer.getItemInUseCount() == 0)
            isBlocking = false;

        if (!rotationStrafe.getValue()) {
            update(); // 拿实体

            needHitBot = null;

            // Switch开始
            if (target != null) {
                // Switch开始
                if (switchTimer.isDelayComplete(switchDelay.getValueState())
                        && targets.size() > 1) {
                    switchTimer.reset();
                    ++index;
                }
                float[] realLastRot = lastRotations;
                rotation(realLastRot);

                if (target != null && canTurn.getValue()) {
                    if (toggled) return;
                    if (!forcerise.getValueState()) {
                        mc.thePlayer.rotationYaw = lastRotations[0];
                        mc.thePlayer.rotationPitch = lastRotations[1];
                    } else {
                        event.setYaw(lastRotations[0]);
                        event.setPitch(lastRotations[1]);
                        if (force.getValue()) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.isOnGround()));
                            event.setCancel(true);
                        }
                    }
                    if (mc.gameSettings.thirdPersonView != 0 && !(mult.getValue() && targets.size() > 1)) {
                        mc.thePlayer.rotationYawHead = event.getYaw();
                        mc.thePlayer.renderYawOffset = event.getYaw();
                    }
                }

            } else {
                targets.clear();
                if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                        && autoBlock.getValueState() && isBlocking) {
                    unBlock(true);
                }
                lastRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            }

        } else {
            if (target != null && canTurn.getValue()) {
                if (toggled) return;

                if (!forcerise.getValueState()) {
                    mc.thePlayer.rotationYaw = lastRotations[0];
                    mc.thePlayer.rotationPitch = lastRotations[1];
                } else {
                    event.setYaw(lastRotations[0]);
                    event.setPitch(lastRotations[1]);
                    if (force.getValue()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.isOnGround()));
                        event.setCancel(true);
                    }

                }
                if (mc.gameSettings.thirdPersonView != 0 && !(mult.getValue() && targets.size() > 1)) {
                    mc.thePlayer.rotationYawHead = event.getYaw();
                    mc.thePlayer.renderYawOffset = event.getYaw();
                }
            }
        }

        if (pre.getValue()) {
            if (target != null) {
                while (cps > 0) {
                    doAttack(); // 攻击 TODO 在onUpdate/PreMotion/PostMotion 进行攻击
                    cps--;
                }
            }
        }

    }

    @EventTarget
    public void onPost(EventPostMotion event) {
        boolean toggled = ModManager.getModule("Scaffold").isEnabled();

        if (!pre.getValue()) {
            if (target != null) {
                while (cps > 0) {
                    doAttack(); // 攻击 TODO 在onUpdate/PreMotion/PostMotion 进行攻击
                    cps--;
                }
            }
        }
    }

    private void doAttack() {
        // 算CPS - Delay
        if (target != null) {
            boolean isInRange = mc.thePlayer.getDistanceToEntity(target) <= reach.getValueState();
            // hitable check
            boolean hitable = (!canTurn.getValue()) || (!hitableCheck.getValue());
            if (!hitable) {
                // do ray trace
                hitable = RotationUtil.isFaced(target, reach.getValueState(), RotationUtil.convert(lastRotations));
            }
            if (!hitable) {
                return;
            }

            if (blockMode.isCurrentMode("Exploit") || (blockMode.isCurrentMode("Simple") && PlayerUtil.isMoving2())) {
                if (mc.thePlayer.isBlocking() || mc.thePlayer.getHeldItem() != null
                        && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getValueState() && isBlocking) { // 格挡
                    unBlock(!mc.thePlayer.isBlocking() && !autoBlock.getValueState()
                            && mc.thePlayer.getItemInUseCount() > 0);
                }
            }

            if (isInRange) {
                if (random.nextInt(100) < mistake.getValueState().intValue()) // 随机Mistakes)
                    mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());
                else
                    attack(); // 攻击传参miss
            }

            // 开格挡
            if (target != null
                    && (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                    && autoBlock.getValueState() || mc.thePlayer.isBlocking())
                    && !isBlocking) { // 格挡
                if (new Random().nextInt(100) <= blockRate.getValue() && (!hurtBlock.getValue() || mc.thePlayer.hurtResistantTime > 0)) { //HurtTime Check && BlockRate
                    if (!blockMode.isCurrentMode("Vanilla"))
                        doBlock(true);
                }
            }
        }
    }

    public void rotation(float[] realLastRot) {
        serverRotation.setYaw(lastRotations[0]);
        serverRotation.setPitch(lastRotations[1]);

        if (rotationTimer.isDelayComplete((100 - smooth.getValueState()) * RandomUtils.nextFloat(2.6f, 3.1f))) {
            rotationTimer.reset();
            lastRotations = RotationUtil.getNeededRotations(AimUtil.getLocation(targets.get(index).getEntityBoundingBox().expand(hitbox.getValue(), hitbox.getValue(), hitbox.getValue())), new Vec3(0, 0, 0));

            double minTurnSpeed = Math.min(maxTurn.getValue(), minTurn.getValue());
            double maxTurnSpeed = Math.max(maxTurn.getValue(), minTurn.getValue());

            if (!force.getValue())
                lastRotations = RotationUtil.convertBack(RotationUtil.limitAngleChange(RotationUtil.convert(realLastRot), RotationUtil.convert(lastRotations)
                        , (float) (Math.random() * (maxTurnSpeed - minTurnSpeed) + minTurnSpeed)));

            switch (SensitivityMode.getModeAt(SensitivityMode.getCurrentMode())) {
                case "Normal": {
                    lastRotations = AimUtil.NormalFix(lastRotations);
                    break;
                }
                case "Prefect": {
                    lastRotations = AimUtil.PrefectFix(lastRotations);
                }
            }
        }
    }

    private void attack() {
        ArrayList<EntityLivingBase> list = new ArrayList<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if ((entity instanceof EntityMob || entity instanceof EntityAnimal) && entity.isInvisible() && mc.thePlayer.getDistanceToEntity(entity) < reach.getValueState()) {
                list.add((EntityLivingBase) entity);
            }
        }

        if (list.size() == 0)
            list.add(target);

        needHitBot = list.get(random.nextInt(list.size()));


        boolean superkb = (!kbBlock.getValue() || mc.thePlayer.hurtResistantTime > 0);
        if (mc.thePlayer.getFoodStats().getFoodLevel() > 6 && morekb.getValue() && superkb) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }
        //Critical
        Criticals.doCrit();
        EventManager.call(new EventAttack(target));

        if ((glich.getValue())) {
            int beforeHeldItem = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = 8));
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = beforeHeldItem));
        }

        //attack
        attackEntity();


        if (mc.playerController.getCurrentGameType() != WorldSettings.GameType.SPECTATOR && !ModManager.getModule(KeepSprint.class).isEnabled())
            mc.thePlayer.attackTargetEntityWithCurrentItem(target);


        if (mc.thePlayer.getFoodStats().getFoodLevel() > 6 && morekb.getValue() && superkb) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        }


        AutoSword.publicItemTimer.reset();
        needHitBot = null;

        if (!attacked.contains(target) && target instanceof EntityPlayer) {
            attacked.add(target);
        }
    }


    private void attackEntity() {
        //Normal Single && Switch
        mc.thePlayer.swingItem();

        if (interact.getValue())
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(aacbot.getValueState() ? needHitBot == null ? target : needHitBot : target, C02PacketUseEntity.Action.INTERACT));

        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(aacbot.getValueState() ? needHitBot == null ? target : needHitBot : target, C02PacketUseEntity.Action.ATTACK)); // 攻击


        //Multi Aura
        if (mult.getValue()) {
            targets.stream().filter(entityLivingBase -> entityLivingBase != target && entityLivingBase.hurtTime < 7)
                    .forEach(target -> {
                        mc.thePlayer.swingItem();
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(aacbot.getValueState() ? needHitBot == null ? target : needHitBot : target, C02PacketUseEntity.Action.ATTACK)); // 攻击
                    });
        }

    }

    private void doBlock(boolean setItemUseInCount) {
        if (setItemUseInCount)
            ((IEntityPlayer) mc.thePlayer).setItemInUseCount(mc.thePlayer.getHeldItem().getMaxItemUseDuration());

        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));

        isBlocking = true;
    }

    private void unBlock(boolean setItemUseInCount) {
        if (setItemUseInCount)
            ((IEntityPlayer) mc.thePlayer).setItemInUseCount(0);

        double blockvalue = -1;

        if (!PlayerUtil.isMoving2() && dynamic.getValue())
            blockvalue = ThreadLocalRandom.current().nextDouble(-1.0, -0.2);

        if (!blockMode.isCurrentMode("Always")) {

            if (!blockMode.isCurrentMode("Exploit"))
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(blockvalue, blockvalue, blockvalue), EnumFacing.DOWN));

            isBlocking = false;
        }
    }

    @Override
    public void onEnable() {
        attacked = new ArrayList<>();
        lastRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (isBlocking) { // 格挡
            unBlock(true);
        }
        targets.clear();
        target = null; // 清空目标 (AutoBlock动画修复)
        cps = 0;
        super.onDisable();
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S30PacketWindowItems && isBlocking && blockMode.isCurrentMode("Exploit")) {
            e.setCancelled(true);
        }
    }

    private List<EntityLivingBase> getTargets() {
        Stream<EntityLivingBase> stream = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity)
                .filter(KillAura::isValidEntity);

        if (priority.isCurrentMode("Armor")) {
            stream = stream.sorted(Comparator.comparingInt(o -> ((o instanceof EntityPlayer ? ((EntityPlayer) o).inventory.getTotalArmorValue() : (int) o.getHealth()))));
        } else {
            if (priority.isCurrentMode("Range")) {
                stream = stream.sorted((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
            } else {
                if (priority.isCurrentMode("Fov")) {
                    stream = stream.sorted(Comparator.comparingDouble(o -> getDistanceBetweenAngles(mc.thePlayer.rotationPitch, AimUtil.getRotations(o)[0])));
                } else if (priority.isCurrentMode("Angle")) {
                    stream = stream.sorted((o1, o2) -> {
                        float[] rot1 = AimUtil.getRotations(o1);
                        float[] rot2 = AimUtil.getRotations(o2);
                        return (int) (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0]));
                    });
                } else if (priority.isCurrentMode("Health")) {
                    stream = stream.sorted((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
                } else if (priority.isCurrentMode("Hurt Time")) {
                    stream = stream.sorted(Comparator.comparingInt(o -> (20 - o.hurtResistantTime)));
                }

            }
        }

        List<EntityLivingBase> list;

        if (witherPriority.getValue()) {
            List<EntityLivingBase> sortedList = stream.collect(Collectors.toList());
            list = new ArrayList<>();
            list.addAll(sortedList.stream().filter((entity) -> entity instanceof EntityWither).collect(Collectors.toList()));
            list.addAll(sortedList.stream().filter((entity) -> !(entity instanceof EntityWither)).collect(Collectors.toList()));
        } else {
            list = stream.collect(Collectors.toList());
        }


        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
            Collections.reverse(list);

        return list.subList(0, Math.min(list.size(), switchsize.getValue().intValue()));
    }

    private void getTarget() {
        int maxSize = switchsize.getValueState().intValue(); // 最大实体数量

        if (maxSize > 1) {
            if (mult.getValue())
                setDisplayName("Multi");
            else
                setDisplayName("Switch");
        } else {
            setDisplayName("Single");
        }

        try {
            targets = this.getTargets();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static class AimUtil {
        private static float getAngleDifference(float a, float b) {
            return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
        }


        public static double getRotationDifference(Rotation a, Rotation b) {
            return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), (a.getPitch() - b.getPitch()));
        }

        public static double getRotationDifference(Rotation rotation) {
            return getRotationDifference(rotation, serverRotation);
        }


        public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean predict) {
            VecRotation vecRotation = null;

            double i = 0.1;
            switch (resolvers.getValue().intValue()) {
                case 1:
                    i = 0.25;
                    break; //
                case 2:
                    i = 0.2;
                    break; //
                case 3:
                    i = 0.15;
                    break; //
                case 4:
                    i = 0.125;
                    break; //
                case 5:
                    break; //
                case 6:
                    i = 0.08;
                    break; //
                case 7:
                    i = 0.075;
                    break; //
                case 8:
                    i = 0.06;
                    break; //
                case 9:
                    i = 0.05;
                    break; //
                case 10:
                    i = 0.04;
                    break;
                case 11:
                    i = 0.03;
                    break;
                case 12:
                    i = 0.02;
            }

            for (double xSearch = 0.0D; xSearch < 1.0D; xSearch += i) {
                for (double ySearch = 0.0D; ySearch < 1.0D; ySearch += i) {
                    for (double zSearch = 0.0D; zSearch < 1.0D; zSearch += i) {
                        final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch,
                                bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                        final Rotation rotation = toRotation(vec3, predict);

                        final VecRotation currentVec = new VecRotation(vec3, rotation);

                        if (vecRotation == null || (getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
                            vecRotation = currentVec;
                    }
                }
            }

            return vecRotation;
        }

        public static Rotation toRotation(final Vec3 vec, final boolean predict) {
            final Vec3 eyesPos = new Vec3(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY +
                    Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);

            if (predict)
                eyesPos.addVector(Minecraft.getMinecraft().thePlayer.motionX, Minecraft.getMinecraft().thePlayer.motionY, Minecraft.getMinecraft().thePlayer.motionZ);

            final double diffX = vec.xCoord - eyesPos.xCoord;
            final double diffY = vec.yCoord - eyesPos.yCoord;
            final double diffZ = vec.zCoord - eyesPos.zCoord;

            return new Rotation(MathHelper.wrapAngleTo180_float(
                    (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
            ), MathHelper.wrapAngleTo180_float(
                    (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
            ));
        }


        public static Vec3 getLocation(AxisAlignedBB bb) {
            double value = Math.random();
            boolean reverse = random.nextInt(100) < KillAura.reverse.getValueState().intValue();
            boolean mistake = random.nextInt(100) < KillAura.outpoint.getValueState().intValue();

            Vec3 resolve = null;

            if (resolverY.getValue() || resolverXZ.getValue())
                resolve = searchCenter(bb, true).getVec3();

            double minX = Math.min(yawxmin.getValue(), yawxmax.getValue());
            double maxX = Math.max(yawxmin.getValue(), yawxmax.getValue());

            double minZ = Math.min(yawzmin.getValue(), yawzmax.getValue());
            double maxZ = Math.max(yawzmin.getValue(), yawzmax.getValue());

            double x = (xRandom.getValue() ? value : 0.5 + (getRandomDoubleInRange(minX, maxX) / 2));
            double z = (zRandom.getValue() ? value : 0.5 + (getRandomDoubleInRange(minZ, maxZ) / 2));

            double pitch = (pitchRandom.getValue() ? value : 0.5 + (KillAura.pitchRand.getValue() / 2));


            return new Vec3(resolverXZ.getValue() ? Objects.requireNonNull(resolve).xCoord : (bb.minX + (bb.maxX - bb.minX) * (reverse ? 1.0 - x : x)) * (mistake ? 1 + value * 0.1 : 1),
                    resolverY.getValue() ? Objects.requireNonNull(resolve).yCoord : bb.minY + (bb.maxY - bb.minY) * pitch, resolverXZ.getValue() ? Objects.requireNonNull(resolve).zCoord : bb.minZ + (bb.maxZ - bb.minZ) * (reverse ? 1.0 - z : z) * (mistake ? 1 + value * 0.1 : 1));
        }

        public static boolean isVisibleFOV(final Entity e, final float fov) {
            return ((Math.abs(AimUtil.getRotations(e)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(AimUtil.getRotations(e)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0f) : (Math.abs(AimUtil.getRotations(e)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0f)) <= fov;
        }


        float updateRotation(float curRot, float destination, float speed) {
            float f = MathHelper.wrapAngleTo180_float(destination - curRot);
            if (f > speed) {
                f = speed;
            }
            if (f < -speed) {
                f = -speed;
            }
            return curRot + f;
        }

        //Skidded from Minecraft
        public static float[] getRotations(final Entity entity) {
            if (entity == null) {
                return null;
            }
            final double diffX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
            final double diffZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
            double diffY;
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase elb = (EntityLivingBase) entity;
                diffY = elb.posY + (elb.getEyeHeight()) - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
            } else {
                diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
            }
            final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
            final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
            final float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
            return new float[]{yaw, pitch};
        }

        public static float[] NormalFix(float[] rot) {
            float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 1.2F;
            return new float[]{rot[0] - (rot[0] % f1), rot[1] - (rot[1] % f1)};
        }

        public static float[] PrefectFix(float[] rot) {
            float yaw = rot[0];
            float pitch = rot[1];
            float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float pos = f * f * f * 8.0F;
            yaw += pos * 0.15F;
            pitch -= pos * 0.15F;
            return new float[]{yaw, pitch};
        }

    }

    //Render Part

    @EventTarget
    public void targetHud(EventRender2D event) {
        if (targetHUD.getValueState()) {
            ScaledResolution sr = new ScaledResolution(mc);
            if (this.hudMode.isCurrentMode("Simple")) {
                if (target != null) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    HFontRenderer font = Hanabi.INSTANCE.fontManager.wqy18;
                    font.drawStringWithShadow(target.getName(), sr.getScaledWidth() / 2f - (font.getStringWidth(target.getName().replaceAll("\247.", "")) / 2f), sr.getScaledHeight() / 2f - 33, 0xffffffff);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));

                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glDepthMask(false);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    GlStateManager.color(1, 1, 1);
                    int i = 0;
                    while (i < target.getMaxHealth() / 2.0f) {
                        mc.ingameGUI.drawTexturedModalRect(
                                (sr.getScaledWidth() / 2) - target.getMaxHealth() / 2.0f * 9.5f / 2.0f + (i * 10),
                                (sr.getScaledHeight() / 2 - 20), 16, 0, 9, 9);
                        ++i;
                    }

                    i = 0;
                    while (i < target.getHealth() / 2.0f) {
                        mc.ingameGUI.drawTexturedModalRect(
                                (sr.getScaledWidth() / 2) - target.getMaxHealth() / 2.0f * 9.5f / 2.0f + (i * 10),
                                (sr.getScaledHeight() / 2 - 20), 52, 0, 9, 9);
                        ++i;
                    }

                    GL11.glDepthMask(true);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                    GlStateManager.disableBlend();
                    GlStateManager.color(1, 1, 1);
                    RenderHelper.disableStandardItemLighting();
                }
            }

            if (hudMode.isCurrentMode("Fancy")) {
                HFontRenderer font = Hanabi.INSTANCE.fontManager.wqy18;
                HFontRenderer font1 = Hanabi.INSTANCE.fontManager.wqy16;
                if (target != null) {
                    int width = (sr.getScaledWidth() / 2) + 100;
                    int height = sr.getScaledHeight() / 2;

                    EntityLivingBase player = target;
                    if (ClickGUIModule.theme.isCurrentMode("Light")) {
                        Gui.drawRect(width - 70, height + 30, width + 80, height + 105, new Color(255, 255, 255, 100).getRGB());
                    } else {
                        Gui.drawRect(width - 70, height + 30, width + 80, height + 105, new Color(0, 0, 0, 140).getRGB());
                    }

                    font.drawString(player.getName() + "             " + (Criticals.isReadyToCritical ? "Critical " : " "), width - 65, height + 35, 0xFFFFFF);
                    font1.drawString(player.onGround ? "On Ground" : "No Ground", width - 65, height + 50, 0xFFFFFF);
                    font1.drawString("HP: " + player.getHealth(), width - 65 + font1.getStringWidth("off Ground") + 13, height + 50, 0xFFFFFF);
                    font1.drawString("Distance: " + mc.thePlayer.getDistanceToEntity(player), width - 65, height + 60, -1);
                    font1.drawString("FDistance: " + player.fallDistance, width - 65, height + 70, -1);
                    font1.drawString("HurtTime: " + player.hurtTime, width - 5, height + 70, -1);

                    font.drawString(player.getHealth() > mc.thePlayer.getHealth() ? "Lower Health" : "Higher Health", width - 65, height + 80, player.getHealth() > mc.thePlayer.getHealth() ? Color.RED.getRGB() : Color.GREEN.brighter().getRGB());
                    GL11.glPushMatrix();
                    GL11.glColor4f(1, 1, 1, 1);
                    GlStateManager.scale(1.0f, 1.0f, 1.0f);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(player.getHeldItem(), width + 50, height + 80);
                    GL11.glPopMatrix();

                    float health = player.getHealth();
                    float healthPercentage = (health / player.getMaxHealth());
                    float targetHealthPercentage = 0;
                    if (healthPercentage != lastHealth) {
                        float diff = healthPercentage - this.lastHealth;
                        targetHealthPercentage = this.lastHealth;
                        this.lastHealth += diff / 8;
                    }
                    Color healthcolor = Color.WHITE;
                    if (healthPercentage * 100 > 75) {
                        healthcolor = Color.GREEN;
                    } else if (healthPercentage * 100 > 50 && healthPercentage * 100 < 75) {
                        healthcolor = Color.YELLOW;
                    } else if (healthPercentage * 100 < 50 && healthPercentage * 100 > 25) {
                        healthcolor = Color.ORANGE;
                    } else if (healthPercentage * 100 < 25) {
                        healthcolor = Color.RED;
                    }
                    Gui.drawRect(width - 70, height + 104, (int) (width - 70 + (149 * targetHealthPercentage)), height + 106, healthcolor.getRGB());
                    Gui.drawRect(width - 70, height + 104, (int) (width - 70 + (149 * healthPercentage)), height + 106, Color.GREEN.getRGB());
                    GL11.glColor4f(1, 1, 1, 1);
                    GuiInventory.drawEntityOnScreen(width + 60, height + 75, 20, Mouse.getX(), Mouse.getY(), player);
                }
            }

            if (hudMode.isCurrentMode("Flat")) {
                //Distance TH code by Mymylesaws
                int blackcolor = new Color(0, 0, 0, 180).getRGB();
                int blackcolor2 = new Color(200, 200, 200, 160).getRGB();
                ScaledResolution sr2 = new ScaledResolution(mc);
                float scaledWidth = sr2.getScaledWidth();
                float scaledHeight = sr2.getScaledHeight();
                HFontRenderer font1 = Hanabi.INSTANCE.fontManager.wqy16;

                nulltarget = target == null;

                float x = scaledWidth / 2.0f - 50;
                float y = scaledHeight / 2.0f + 32;
                float health;
                double hpPercentage;
                Color hurt;
                int healthColor;
                String healthStr;
                if (nulltarget) {
                    health = 0;
                    hpPercentage = health / 20;
                    hurt = Color.getHSBColor(300f / 360f, ((float) 0 / 10f) * 0.37f, 1f);
                    healthStr = String.valueOf((float) 0 / 2.0f);
                    healthColor = getHealthColor(0, 20).getRGB();
                } else {
                    health = target.getHealth();
                    hpPercentage = health / target.getMaxHealth();
                    hurt = Color.getHSBColor(310f / 360f, ((float) target.hurtTime / 10f), 1f);
                    healthStr = String.valueOf((float) (int) (target.getHealth()) / 2.0f);
                    healthColor = getHealthColor(target.getHealth(), target.getMaxHealth()).getRGB();
                }
                hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0);
                double hpWidth = 140.0 * hpPercentage;

                if (nulltarget) {
                    this.healthBarWidth2 = RenderUtil.getAnimationStateSmooth(0, this.healthBarWidth2, 6f / Minecraft.getDebugFPS());
                    this.healthBarWidth = RenderUtil.getAnimationStateSmooth(0, this.healthBarWidth, 14f / Minecraft.getDebugFPS());

                    this.hudHeight = RenderUtil.getAnimationStateSmooth(0.0, this.hudHeight, 8f / Minecraft.getDebugFPS());
                } else {
                    this.healthBarWidth2 = AnimationUtil.moveUD((float) this.healthBarWidth2, (float) hpWidth, 6f / Minecraft.getDebugFPS(), 3f / Minecraft.getDebugFPS());
                    this.healthBarWidth = RenderUtil.getAnimationStateSmooth(hpWidth, this.healthBarWidth, 14f / Minecraft.getDebugFPS());

                    this.hudHeight = RenderUtil.getAnimationStateSmooth(40.0, this.hudHeight, 8f / Minecraft.getDebugFPS());
                }

                if (hudHeight == 0) {
                    this.healthBarWidth2 = 140;
                    this.healthBarWidth = 140;
                }

                GL11.glEnable(3089);
                RenderUtil.prepareScissorBox(x, (float) ((double) y + 40 - hudHeight), x + 140.0f, (float) ((double) y + 40));
                RenderUtil.drawRect(x, y, x + 140.0f, y + 40.0f, blackcolor);
                RenderUtil.drawRect(x, y + 37.0f, (x) + 140, y + 40f, new Color(0, 0, 0, 49).getRGB());

                RenderUtil.drawRect(x, y + 37.0f, (float) (x + this.healthBarWidth2), y + 40.0f, new Color(255, 0, 213, 220).getRGB());
                RenderUtil.drawGradientSideways(x, y + 37.0f, (x + this.healthBarWidth), y + 40.0f, new Color(0, 81, 179).getRGB(), healthColor);

                font1.drawStringWithShadow(healthStr, x + 40.0f + 85.0f - (float) font1.getStringWidth(healthStr) / 2.0f + mc.fontRendererObj.getStringWidth("\u2764") / 1.9f, y + 26.0f, blackcolor2);
                mc.fontRendererObj.drawStringWithShadow("\u2764", x + 40.0f + 85.0f - (float) font1.getStringWidth(healthStr) / 2.0f - mc.fontRendererObj.getStringWidth("\u2764") / 1.9f, y + 26.5f, hurt.getRGB());

                HFontRenderer font2 = Hanabi.INSTANCE.fontManager.usans14;
                if (nulltarget) {
                    font2.drawStringWithShadow("XYZ:" + 0 + " " + 0 + " " + 0 + " | " + "Hurt: " + (false), x + 37f, y + 15f, Colors.WHITE.c);
                    font1.drawStringWithShadow("(No target)", x + 36.0f, y + 5.0f, Colors.WHITE.c);
                } else {
                    font2.drawStringWithShadow("XYZ:" + (int) target.posX + " " + (int) target.posY + " " + (int) target.posZ + " | " + "Hurt: " + (target.hurtTime > 0), x + 37f, y + 15f, Colors.WHITE.c);

                    if ((target instanceof EntityPlayer)) {
                        font2.drawStringWithShadow("Block:" + " " + (((EntityPlayer) target).isBlocking() ? "True" : "False"), x + 37f, y + 25f, Colors.WHITE.c);
                    }

                    font1.drawStringWithShadow(target.getName(), x + 36f, y + 4.0f, Colors.WHITE.c);

                    if ((target instanceof EntityPlayer)) {
                        GlStateManager.resetColor();
                        mc.getTextureManager().bindTexture(((AbstractClientPlayer) target).getLocationSkin());

                        GlStateManager.color(1, 1, 1);
                        Gui.drawScaledCustomSizeModalRect((int) x + 3, (int) y + 3, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
                    }
                }
                GL11.glDisable(3089);
            }

        }

    }

    @EventTarget
    public void onRender(EventRender render) { // Copy
        if (target != null && attacktimer.isDelayComplete(randomClickDelay(Math.min(minCps.getValue(), maxCps.getValue()), Math.max(minCps.getValue(), maxCps.getValue())))) {
            cps++;
            attacktimer.reset();
        }

        if (target == null || !esp.getValueState()) {
            return;
        }

        if (EspMode.isCurrentMode("Box")) {
            for (EntityLivingBase entity : targets) {
                renderBox(entity);
            }
        }

        if (EspMode.isCurrentMode("Circle")) {
            for (int i = 0; i < 5; i++) {
                drawCircle(target, render.getPartialTicks(), 0.8, delay / 100);
            }
        }


        if (EspMode.isCurrentMode("New")) {
            EntityLivingBase entity = target;
            drawESP(entity, entity.hurtTime >= 1 ? (new Color(255, 0, 0, 160).getRGB()) : (new Color(47, 116, 253, 255).getRGB()), render);
        }

        if (EspMode.isCurrentMode("Cylinder")) {
            EntityLivingBase entity = target;

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();

            if (entity.hurtTime > 0)
                RenderUtil.drawWolframEntityESP(entity, (new Color(255, 102, 113)).getRGB(), x, y, z);
            else
                RenderUtil.drawWolframEntityESP(entity, boxColor.getColor(), x, y, z);
        }

        if (EspMode.isCurrentMode("ExeterCross")) {
            EntityLivingBase entity = target;

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) Wrapper.getTimer().renderPartialTicks
                    - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();

            if (entity.hurtTime > 0)
                RenderUtil.drawExeterCrossESP(entity, (new Color(255, 102, 113)).getRGB(), x, y, z);
            else
                RenderUtil.drawExeterCrossESP(entity, boxColor.getColor(), x, y, z);
        }


        if (delay > 200) {
            step = false;
        }
        if (delay < 0) {
            step = true;
        }
        if (step) {
            delay += 3;
        } else {
            delay -= 3;
        }
    }

    public void renderBox(EntityLivingBase entity) {
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

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        RenderUtil.color(entity.hurtTime > 1 ? RenderUtil.reAlpha(new Color(0.8f, 0.0f, 0.0f).getRGB(), 0.2f) : RenderUtil.reAlpha(boxColor.getColor(), 0.15f));
        RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(1);
        GL11.glColor4f(1f,
                1f, 1f, 0);
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }


    private void drawCircle(Entity entity, float partialTicks, double rad, double height) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(2.0f);
        glBegin(GL_LINE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;

        final double pix2 = Math.PI * 2.0D;

        //GlStateManager.color(0, 0, 0, RenderUtil.reAlpha(Color.WHITE.getRGB(),  0.8f));

        for (int i = 0; i <= 90; ++i) {
            GlStateManager.color(PaletteUtil.fade(Color.WHITE, 30, 14).getRed(), PaletteUtil.fade(Color.WHITE, 30, 14).getGreen(), PaletteUtil.fade(Color.WHITE, 30, 14).getBlue(), 0.8f);
            glVertex3d(x + rad * Math.cos(i * pix2 / 45), y + height, z + rad * Math.sin(i * pix2 / 45));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }


    public static void drawESP(EntityLivingBase entity, int color, EventRender e) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) e.getPartialTicks()
                - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) e.getPartialTicks()
                - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) e.getPartialTicks()
                - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        float radius = 0.2f;
        int side = 6;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 2, z);
        GL11.glRotatef(-entity.width, 0.0f, 1.0f, 0.0f);
        glColor(new Color(Math.max(new Color(color).getRed() - 75, 0), Math.max(new Color(color).getGreen() - 75, 0),
                Math.max(new Color(color).getBlue() - 75, 0), new Color(color).getAlpha()).getRGB());
        enableSmoothLine(1.0f);
        Cylinder c = new Cylinder();
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);

        c.setDrawStyle(100012);
        c.draw(0, radius, 0.3f, side, 1);
        glColor(color);
        c.setDrawStyle(100012);
        GL11.glTranslated(0, 0, 0.3);
        c.draw(radius, 0, 0.3f, side, 1);

        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

        disableSmoothLine();
        GL11.glPopMatrix();
    }

    public static void glColor(int hex) {
        float alpha = (float) (hex >> 24 & 255) / 255.0f;
        float red = (float) (hex >> 16 & 255) / 255.0f;
        float green = (float) (hex >> 8 & 255) / 255.0f;
        float blue = (float) (hex & 255) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha == 0.0f ? 1.0f : alpha);
    }

    public static void enableSmoothLine(float width) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }

    public static void disableSmoothLine() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

}




