package cn.hanabi.modules.modules.world;

import cn.hanabi.Wrapper;
import cn.hanabi.events.*;
import cn.hanabi.injection.interfaces.IKeyBinding;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.BlockUtils;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Scaffold extends Mod {

    //MODE
    private final Value<String> towerMode = new Value<String>("Scaffold", "TowerMode", 0)
            .LoadValue(new String[]{"None", "NCP", "AACv4"});

    //BUILD
    private final Value<Double> delay = new Value<>("Scaffold", "Place Delay", 0d, 0d, 500d, 10d);
    private final Value<Boolean> diagonal = new Value<>("Scaffold", "Diagonal", true);
    private final Value<Boolean> silent = new Value<>("Scaffold", "Slient", true);
    private final Value<Boolean> noSwing = new Value<>("Scaffold", "No Swing", true);


    //MOVEMENT
    private final Value<Boolean> safeWalk = new Value<>("Scaffold", "Safe Walk", true);
    private final Value<Boolean> onlyGround = new Value<>("Scaffold", "Only Ground", true);
    private final Value<Boolean> sprint = new Value<>("Scaffold", "Sprint", true);
    private final Value<Boolean> sneak = new Value<>("Scaffold", "Sneak", true);
    private final Value<Double> speedlimit = new Value<>("Scaffold", "Move Motify", 1.0, 0.6, 1.2, 0.1);

    //RAYCAST
    private final Value<Boolean> rayCast = new Value<>("Scaffold", "Ray Cast", true);

    //RENDER
    private final Value<Boolean> render = new Value<>("Scaffold", "ESP", true);

    //OTHER
    private final Value<Double> sneakAfter = new Value<>("Scaffold", "Sneak Tick", 1d, 1d, 10d, 1d);
    private final Value<Boolean> moveTower = new Value<>("Scaffold", "Move Tower", true);
    private final Value<Boolean> hypixel = new Value<>("Scaffold", "Hypixel", true);
    private final Value<Double> timer = new Value<>("Scaffold", "Timer Speed", 1.0, 0.1, 5.0, 0.01);

    //ROTATE
    private final Value<Double> turnspeed = new Value<>("Scaffold", "Rotation Speed", 6.0, 1.0, 6.0, 0.5);
    float curYaw, curPitch;
    Vec3i rotate = null;
    //Sneak
    int sneakCount;

    //Slot
    int slot;


    //Facing
    EnumFacing enumFacing;


    //Timer
    TimeHelper timeHelper;


    //Tower
    boolean istower;
    double jumpGround = 0.0;

    //OUT
    public static ItemStack items;

    //BlackList
    List<Block> blackList;

    public Scaffold() {
        super("Scaffold", Category.WORLD);
        timeHelper = new TimeHelper();
        blackList = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava,
                Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane,
                Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice,
                Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest,
                Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt,
                Blocks.gold_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore,
                Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
                Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
                Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook,
                Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom,
                Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.gravel,
                Blocks.ender_chest,
                Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web,
                Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence, Blocks.redstone_torch);
    }

    public float[] faceBlock(BlockPos pos, float yTranslation, float currentYaw, float currentPitch, float speed) {
        double x = (pos.getX() + 0.5F) - mc.thePlayer.posX - mc.thePlayer.motionX;
        double y = (pos.getY() - yTranslation) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double z = (pos.getZ() + 0.5F) - mc.thePlayer.posZ - mc.thePlayer.motionZ;

        double calculate = MathHelper.sqrt_double(x * x + z * z);
        float calcYaw = (float) (MathHelper.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float calcPitch = (float) -(MathHelper.atan2(y, calculate) * 180.0D / Math.PI);

        float yaw = updateRotation(currentYaw, calcYaw, speed);
        float pitch = updateRotation(currentPitch, calcPitch, speed);

        final float sense = mc.gameSettings.mouseSensitivity * 0.8f + 0.2f;
        final float fix = (float) (Math.pow(sense, 3.0) * 1.5);
        yaw -= yaw % fix;
        pitch -= pitch % fix;

        return new float[]{yaw, pitch >= 90 ? 90 : pitch <= -90 ? -90 : pitch};
    }

    private float[] getRotation(Vec3i vec3, float currentYaw, float currentPitch, float speed) {
        double xdiff = vec3.getX() - mc.thePlayer.posX;
        double zdiff = vec3.getZ() - mc.thePlayer.posZ;
        double y = vec3.getY();
        double posy = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - y;
        double lastdis = MathHelper.sqrt_double(xdiff * xdiff + zdiff * zdiff);
        float calcYaw = (float) (Math.atan2(zdiff, xdiff) * 180.0 / Math.PI) - 90.0f;
        float calcPitch = (float) (Math.atan2(posy, lastdis) * 180.0 / Math.PI);
        if (Float.compare(calcYaw, 0.0f) < 0)
            calcPitch += 360.0f;
        //TODO: Besserer Mouse Sensi Fix da er auf Verus Kickt

        float yaw = updateRotation(currentYaw, calcYaw, speed);
        float pitch = updateRotation(currentPitch, calcPitch, speed);

        return new float[]{yaw, pitch >= 90 ? 90 : pitch <= -90 ? -90 : pitch};
    }


    public static Vec3i translate(BlockPos blockPos, EnumFacing enumFacing) {
        double x = blockPos.getX();
        double y = blockPos.getY();
        double z = blockPos.getZ();
        double r1 = ThreadLocalRandom.current().nextDouble(0.3, 0.5);
        double r2 = ThreadLocalRandom.current().nextDouble(0.9, 1.0);
        if (enumFacing.equals(EnumFacing.UP)) {
            x += r1;
            z += r1;
            y += 1.0;
        } else if (enumFacing.equals(EnumFacing.DOWN)) {
            x += r1;
            z += r1;
        } else if (enumFacing.equals(EnumFacing.WEST)) {
            y += r2;
            z += r1;
        } else if (enumFacing.equals(EnumFacing.EAST)) {
            y += r2;
            z += r1;
            x += 1.0;
        } else if (enumFacing.equals(EnumFacing.SOUTH)) {
            y += r2;
            x += r1;
            z += 1.0;
        } else if (enumFacing.equals(EnumFacing.NORTH)) {
            y += r2;
            x += r1;
        }
        return new Vec3i(x, y, z);
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


    @EventTarget
    private void onSafe(EventSafeWalk e) {
        if (safeWalk.getValue())
            e.setSafe(mc.thePlayer.onGround || !onlyGround.getValue());
    }

    @EventTarget
    private void onPre(EventPreMotion e) {
        if (rotate != null) {
//            e.setYaw(mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = curYaw);
//            e.setPitch(curPitch);
        }
    }

    @EventTarget
    private void onPacket(EventPacket e) {
        if (e.getPacket() instanceof C09PacketHeldItemChange) {
            final C09PacketHeldItemChange C09 = (C09PacketHeldItemChange) e.getPacket();
            if (slot != C09.getSlotId())
                slot = C09.getSlotId();
        }
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (mc.thePlayer.onGround) {
            event.setX(mc.thePlayer.motionX *= speedlimit.getValue());
            event.setZ(mc.thePlayer.motionZ *= speedlimit.getValue());
        }
    }


    @EventTarget
    private void onUpdate(EventPostMotion e) {
        BlockPos blockPos = getBlockPosToPlaceOn(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));

        if (blockPos != null) rotate = translate(blockPos, enumFacing);

        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slot);

        Wrapper.getTimer().timerSpeed = (float) (timer.getValue().floatValue() + (Math.random() / 100));

        if (silent.getValue() && !(itemStack != null && (itemStack.getItem() instanceof ItemBlock))) {
            if (slot != getBlockSlot()) {
                if (getBlockSlot() == -1) return;
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(getBlockSlot()));
            }
        }

        if (blockPos != null && itemStack != null && itemStack.getItem() instanceof ItemBlock) {
            items = itemStack;
            mc.thePlayer.setSprinting(sprint.getValue());
            ((IKeyBinding) mc.gameSettings.keyBindSprint).setPress(sprint.getValue());

            if (sneak.getValue() && sneakCount >= sneakAfter.getValue())
                ((IKeyBinding) mc.gameSettings.keyBindSneak).setPress(true);
            else if (sneakCount < sneakAfter.getValue())
                ((IKeyBinding) mc.gameSettings.keyBindSneak).setPress(false);

            float[] rotation = hypixel.getValue() ? getRotation(rotate, curYaw, curPitch, turnspeed.getValue().floatValue() * 30)
                    : faceBlock(blockPos, (float) (mc.theWorld.getBlockState(blockPos).getBlock().getBlockBoundsMaxY() - mc.theWorld.getBlockState(blockPos).getBlock().getBlockBoundsMinY()) + 0.5F, curYaw, curPitch, turnspeed.getValue().floatValue() * 30);

            curYaw = rotation[0];
            curPitch = rotation[1];
//            mc.thePlayer.rotationYaw = rotation[0];
//            mc.thePlayer.rotationPitch = rotation[1];

            MovingObjectPosition ray = PlayerUtil.rayCastedBlock(curYaw, curPitch);


            if (timeHelper.isDelayComplete(delay.getValue().longValue()) && (ray != null && ray.getBlockPos().equals(blockPos) || !rayCast.getValue())) {
                Vec3 hitVec = hypixel.getValue() ? new Vec3(rotate.getX(), rotate.getY(), rotate.getZ()) : ray != null ? ray.hitVec : new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, blockPos, enumFacing, hitVec)) {
                    sneakCount++;
                    if (sneakCount > sneakAfter.getValue())
                        sneakCount = 0;

                    if (!noSwing.getValue())
                        mc.thePlayer.swingItem();
                    else
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());

                    timeHelper.reset();
                }
            } else {
                if (sneak.getValue())
                    ((IKeyBinding) mc.gameSettings.keyBindSneak).setPress(false);

            }


            // tower
            if (MoveUtils.getJumpEffect() == 0) {
                if (mc.thePlayer.movementInput.jump) { // if Scaffolded to UP
                    if (MoveUtils.isOnGround(0.15) && (moveTower.getValue() || !PlayerUtil.MovementInput())) {
                        if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            Wrapper.getTimer().timerSpeed = 1f;
                            // different tower mode
                            istower = true;
                            switch (towerMode.getModeAt(towerMode.getCurrentMode())) {
                                case "NCP": {
                                    mc.thePlayer.motionX *= 0.8;
                                    mc.thePlayer.motionZ *= 0.8;
                                    mc.thePlayer.motionY = 0.41999976;
                                    break;
                                }
                                case "AACv4": {
                                    if (mc.thePlayer.onGround) {
                                        fakeJump();
                                        jumpGround = mc.thePlayer.posY;
                                        mc.thePlayer.motionY = 0.42;
                                    }
                                    if (mc.thePlayer.posY > (jumpGround + 0.76)) {
                                        fakeJump();
                                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                                        mc.thePlayer.motionY = 0.42;
                                        jumpGround = mc.thePlayer.posY;
                                    }
                                    Wrapper.getTimer().timerSpeed = 0.7F;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        sneakCount = 0;
        curYaw = mc.thePlayer.rotationYaw;
        curPitch = mc.thePlayer.rotationPitch;
        slot = mc.thePlayer.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        if (silent.getValue() && slot != mc.thePlayer.inventory.currentItem)
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((slot = mc.thePlayer.inventory.currentItem)));

        if (Wrapper.getTimer().timerSpeed != 1)
            Wrapper.getTimer().timerSpeed = 1.0f;
    }

    private BlockPos getBlockPosToPlaceOn(BlockPos pos) {
        BlockPos blockPos1 = pos.add(-1, 0, 0);
        BlockPos blockPos2 = pos.add(1, 0, 0);
        BlockPos blockPos3 = pos.add(0, 0, -1);
        BlockPos blockPos4 = pos.add(0, 0, 1);
        float down = 0;
        if (mc.theWorld.getBlockState(pos.add(0, -1 - down, 0)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.UP;
            return (pos.add(0, -1, 0));
        } else if (mc.theWorld.getBlockState(pos.add(-1, 0 - down, 0)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.EAST;
            return (pos.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(pos.add(1, 0 - down, 0)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.WEST;
            return (pos.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(pos.add(0, 0 - down, -1)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.SOUTH;
            return (pos.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(pos.add(0, 0 - down, 1)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.NORTH;
            return (pos.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.UP;
            return (blockPos1.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos1.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos1.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos1.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos1.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.UP;
            return (blockPos2.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos2.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos2.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos2.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos2.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.UP;
            return (blockPos3.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos3.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos3.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos3.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos3.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.UP;
            return (blockPos4.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos4.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos4.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos4.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getValue()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos4.add(0, 0 - down, 1));
        }
        return null;
    }

    @EventTarget
    public void on3D(EventRender event) {
        if (render.getValueState()) {
            esp(mc.thePlayer, event.getPartialTicks(), 0.5);
            esp(mc.thePlayer, event.getPartialTicks(), 0.4);
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
            float speed = 5000f;
            float baseHue = System.currentTimeMillis() % (int) speed;
            baseHue /= speed;
            for (int i = 0; i <= 90; ++i) {
                float max = ((float) i + (float) (il * 8)) / points;
                float hue = max + baseHue;
                while (hue > 1) {
                    hue -= 1;
                }
                final double pix2 = Math.PI * 2.0D;
                for (int i2 = 0; i2 <= 6; ++i2) {
                    if (istower)
                        GlStateManager.color(255, 255, 255, 1f);
                    else
                        GlStateManager.color(255, 255, 255, 0.4f);

                    GL11.glVertex3d(x + rad * Math.cos(i2 * pix2 / 6.0), y, z + rad * Math.sin(i2 * pix2 / 6.0));
                }
                for (int i2 = 0; i2 <= 6; ++i2) {
                    if (istower)
                        GlStateManager.color(0, 0, 0, 1f);
                    else
                        GlStateManager.color(0, 0, 0, 0.4f);

                    GL11.glVertex3d(x + rad * Math.cos(i2 * pix2 / 6.0) * 1.01, y, z + rad * Math.sin(i2 * pix2 / 6.0) * 1.01);
                }

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

    private void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }

    private int getBlockSlot() {

        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];

            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || itemStack.stackSize < 1)
                continue;

            final ItemBlock block = (ItemBlock) itemStack.getItem();

            if (blackList.contains(block.getBlock()))
                continue;

            slot = i;
            break;
        }

        return slot;
    }
}
