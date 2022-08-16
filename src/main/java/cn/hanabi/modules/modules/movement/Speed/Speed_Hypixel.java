package cn.hanabi.modules.modules.movement.Speed;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.*;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.combat.KillAura;
import cn.hanabi.modules.modules.combat.TargetStrafe;
import cn.hanabi.utils.MathUtils;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


@ObfuscationClass
public class Speed_Hypixel {

    public static Value<String[]> Friction = new Value<String[]>("Speed", "Friction Mode", 0)
            .LoadValue(new String[]{"Normal", "Fix"});

    public static Value<String[]> boostMode = new Value<String[]>("Speed", "Boost Mode", 0)
            .LoadValue(new String[]{"Normal", "Boost"});

    public static Value<String[]> motion = new Value<String[]>("Speed", "Motion", 0)
            .LoadValue(new String[]{"Normal", "New", "LowHop"});

    public static Value<Double> damageBoost = new Value<>("Speed", "DMG Boost Value", 0.39, 0.01, 1.0, 0.01);
    public static Value<Double> speedchange = new Value<>("Speed", "Bhop Speed", 1.0, 0.8, 1.0, 0.01);
    public static Value<Double> glideValue = new Value<>("Speed", "Glide Stage", 0.1, 0.0, 0.4, 0.01);

    public static TimeHelper ticks = new TimeHelper();

    public Value<Boolean> boost = new Value<>("Speed", "DMG Boost", true);
    public Value<Boolean> tp = new Value<Boolean>("Speed", "Tp-Motion", true);

    public Value<Boolean> groundspoof = new Value<Boolean>("Speed", "GroundSpoof", true);

    Minecraft mc = Minecraft.getMinecraft();
    double y;
    private double speed;
    private int stage;


    private double lastDist;

    public static double defaultSpeed() {
        double baseSpeed = 0.2887D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }

    public static void setMotion(EventMove em, double speed) {
        Minecraft mc = Minecraft.getMinecraft();
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0 && strafe == 0) {
            em.setX(0);
            em.setZ(0);
        } else {
            if (forward != 0) {
                if (strafe > 0) {
                    yaw += (float) (forward > 0 ? -45 : 45);
                } else if (strafe < 0) {
                    yaw += (float) (forward > 0 ? 45 : -45);
                }
                strafe = 0;
                if (forward > 0) {
                    forward = 1;
                } else if (forward < 0) {
                    forward = -1;
                }
            }
            em.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90)));
            em.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90)));
        }
    }

    public static double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static boolean isOnIce() {
        final Block blockUnder = Minecraft.getMinecraft().theWorld.getBlockState(
                        new BlockPos(Minecraft.getMinecraft().thePlayer.posX, StrictMath.floor(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY) - 1.0, Minecraft.getMinecraft().thePlayer.posZ))
                .getBlock();
        return blockUnder instanceof BlockIce || blockUnder instanceof BlockPackedIce;
    }

    public void onStep(EventStep event) {

    }

    public void onLoop(EventLoop event) {

    }

    public void onPre(EventPreMotion e) {
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);

        if (motion.isCurrentMode("OnGround")) {
            mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
        }

        if (MoveUtils.isMoving())
            e.setYaw((float) (Math.toDegrees(Math.atan2(zDist, xDist)) - 90.0));

        if (MoveUtils.isMoving() && !e.isOnGround() && mc.thePlayer.motionY > 0.06 && groundspoof.getValue())
            e.setOnGround(true);
    }

    public void onPost(EventPostMotion e) {
    }

    public void onJump(EventJump e) {
        if (motion.isCurrentMode("OnGround"))
            e.setCancelled(true);
    }


    public void onPacket(EventPacket e) {
    }

    public void onMove(EventMove event) {
        double rounded = MathUtils.round(mc.thePlayer.posY - (double) ((int) mc.thePlayer.posY), 3.0D);
        final KillAura killAura = ModManager.getModule(KillAura.class);
        final TargetStrafe targetStrafe = ModManager.getModule(TargetStrafe.class);


        //low hop moment
        if (motion.isCurrentMode("LowHop") || motion.isCurrentMode("OnGround")) {
            if (rounded == MathUtils.round(0.4D, 3.0D)) {
                event.y = mc.thePlayer.motionY = 0.31D;
            } else if (rounded == MathUtils.round(0.71D, 3.0D)) {
                event.y = mc.thePlayer.motionY = 0.04D;
            } else if (rounded == MathUtils.round(0.75D, 3.0D)) {
                event.y = mc.thePlayer.motionY = -0.2D;
            } else if (rounded == MathUtils.round(0.55D, 3.0D)) {
                event.y = mc.thePlayer.motionY = -0.14D;
            } else if (rounded == MathUtils.round(0.41D, 3.0D)) {
                event.y = mc.thePlayer.motionY = -0.2D;
            }
        }

        if (stage > 0) {
            if (stage == 1 && mc.thePlayer.onGround && MoveUtils.isMoving())
                stage += 1;

            if (stage == 2 && mc.thePlayer.onGround && MoveUtils.isMoving()) {

                if (tp.getValue())
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.001 * Math.random(), mc.thePlayer.posZ);

                y = ThreadLocalRandom.current().nextDouble(0.39999998688698, 0.4000199999);

                event.setY(mc.thePlayer.motionY = y + (PlayerUtil.getJumpEffect() * 0.1));
            } else if (stage >= 3) {
                if (motion.isCurrentMode("New")) {
                    if (stage == 6)
                        mc.thePlayer.motionY = -(y - 0.310999999999998D);
                }
                if (mc.thePlayer.isCollidedVertically) {
                    speed = getBaseSpeed();
                    lastDist = 0.0;
                    stage = 0;
                }
            }
        } else {
            stage = 0;
        }

        if (event.y < 0)
            event.y *= 1 - glideValue.getValue();

        if (boostMode.isCurrentMode("Boost"))
            onHypixelSpeed();
        else
            getHypixelBest();

        ++stage;

        double add = mc.thePlayer.isBurning() ? 0 : mc.thePlayer.hurtResistantTime < 8 ? mc.thePlayer.hurtResistantTime * damageBoost.getValue() * 0.008 : mc.thePlayer.hurtResistantTime * damageBoost.getValue() * .01;
        speed *= 1 + (boost.getValue() ? add : 0);


        if (MoveUtils.isMoving()) {
            if (targetStrafe.isStrafing(event, killAura.target, speed))
                setMotion(event, speed);
        } else {
            setMotion(event, 0.0);
            stage = 0;
        }
    }

    public boolean isMoving2() {
        return ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F));
    }

    private boolean canSpeed() {
        Block blockBelow = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)).getBlock();
        return mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isPressed() && blockBelow != Blocks.stone_stairs && blockBelow != Blocks.oak_stairs && blockBelow != Blocks.sandstone_stairs && blockBelow != Blocks.nether_brick_stairs && blockBelow != Blocks.spruce_stairs && blockBelow != Blocks.stone_brick_stairs && blockBelow != Blocks.birch_stairs && blockBelow != Blocks.jungle_stairs && blockBelow != Blocks.acacia_stairs && blockBelow != Blocks.brick_stairs && blockBelow != Blocks.dark_oak_stairs && blockBelow != Blocks.quartz_stairs && blockBelow != Blocks.red_sandstone_stairs && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0D, mc.thePlayer.posZ)).getBlock() == Blocks.air;
    }

    public boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public void onHypixelSpeed() {
        speed = getBaseSpeed();

        if (stage < 1) {
            ++stage;
            lastDist = 0.0;
        }
        float diff;

        int amplifier = -1;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
        }

        if (stage == 2 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)
                && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
            speed *= 1.8125 + (0.041D + ThreadLocalRandom.current().nextDouble(0.005) / 10) * (amplifier + 1);
            speed *= speedchange.getValue();

        } else if (stage == 3) {
            diff = (float) (0.72
                    * (lastDist - getBaseSpeed() * (isOnIce() ? 1.1 : 1.0)));
            speed = lastDist - diff;
        } else {
            if ((mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                    mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0
                    || mc.thePlayer.isCollidedVertically) && stage > 0) {
                stage = ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) ? 1 : 0);
            }

            if (Friction.isCurrentMode("Normal")) {
                speed = lastDist - lastDist / (isOnIce() ? 91.0 : 99.00000014353486);
            } else if (Friction.isCurrentMode("Fix")) {
                speed = MoveUtils.calculateFriction(speed, lastDist, getBaseSpeed());
            }

        }

        speed = Math.max(speed, getBaseSpeed());
    }

    public void onPullback(EventPullback event) {
    }

    public void onEnable() {
        lastDist = 0;
        speed = defaultSpeed();
        stage = 2;
    }

    public void onDisable() {
        mc.thePlayer.stepHeight = 0.5f;
    }

    private double getBaseSpeed() {
        final EntityPlayerSP player = mc.thePlayer;
        double base = 0.2895;
        final PotionEffect moveSpeed = player.getActivePotionEffect(Potion.moveSpeed);
        final PotionEffect moveSlowness = player.getActivePotionEffect(Potion.moveSlowdown);
        if (moveSpeed != null)
            base *= 1.0 + 0.19 * (moveSpeed.getAmplifier() + 1);

        if (moveSlowness != null)
            base *= 1.0 - 0.13 * (moveSlowness.getAmplifier() + 1);

        if (player.isInWater()) {
            base *= 0.5203619984250619;
            final int depthStriderLevel = EnchantmentHelper.getDepthStriderModifier(mc.thePlayer);
            if (depthStriderLevel > 0) {
                double[] DEPTH_STRIDER_VALUES = new double[]{1.0, 1.4304347400741908, 1.7347825295420374,
                        1.9217391028296074};
                base *= DEPTH_STRIDER_VALUES[depthStriderLevel];
            }
        } else if (player.isInLava()) {
            base *= 0.5203619984250619;
        }
        return base;
    }


    private void getHypixelBest() {
        EntityPlayer player = mc.thePlayer;

        if (player == null) return;

        switch (stage) {
            case 1: {
                break;
            }
            case 2: {
                if (player.onGround && MoveUtils.isMoving()){
                    mc.thePlayer.motionY += 0.005;
                    speed *= 1.9;
                }
                speed *= speedchange.getValue();
                break;
            }
            case 3: {
                speed += new Random().nextDouble() / 4800;
                double difference = 0.66 * (lastDist - getBaseSpeed());
                speed = lastDist - difference;
                PlayerUtil.debugChat(speed);
                break;
            }
            default: {
                if ((mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                        mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0
                        || mc.thePlayer.isCollidedVertically) && stage > 0) {
                    stage = ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) ? 1 : 0);
                }

                if (Friction.isCurrentMode("Normal")) {
                    speed = lastDist - lastDist / 159;
                } else if (Friction.isCurrentMode("Fix")) {
                    speed = MoveUtils.calculateFriction(speed, lastDist, getBaseSpeed());
                }
                break;
            }
        }


        speed = Math.max(speed - 0.02 * lastDist, getBaseSpeed());
    }


}
