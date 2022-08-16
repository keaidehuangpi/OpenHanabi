package cn.hanabi.modules.modules.world;

import cn.hanabi.events.BBSetEvent;
import cn.hanabi.events.EventJump;
import cn.hanabi.events.EventMove;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.BlockUtils;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.concurrent.ThreadLocalRandom;



public class Jesus extends Mod {
    private static final Value mode = new Value("Jesus", "Mode", 0);
    public static Value<Double> motionY = new Value<>("Jesus", "Motion Y", 0.27d, 0.01d, 0.4d, 0.01d);

    private final TimeHelper timer = new TimeHelper();
    int stage;

    public Jesus() {
        super("Jesus", Category.WORLD);
        mode.LoadValue(new String[]{"Solid", "Motion"});
        //mode.addValue("BHop");

    }

    public static boolean isOnLiquid() {
        if (mc.thePlayer == null)
            return false;
        boolean onLiquid = false;
        final int y = (int) mc.thePlayer.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper
                    .floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && block.getMaterial() != Material.air) {
                    if (!(block instanceof BlockLiquid))
                        return false;

                    if (mc.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
                            .get(BlockLiquid.LEVEL) instanceof Integer) {
                        if ((int) mc.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
                                .get(BlockLiquid.LEVEL) > 1) {
                            return false;
                        }
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    @EventTarget
    public void onBounding(BBSetEvent event) {
        if (mode.isCurrentMode("Solid")) {
            if (event.getBlock() instanceof BlockLiquid && !mc.thePlayer.isInWater()
                    && !mc.thePlayer.isInLava() && this.isPossible(event.pos)) {
                event.setBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).contract(0.0, 2.0E-12, 0.0)
                        .offset(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()));

            }
        }
    }

    @EventTarget
    public void onPacket(EventPreMotion event) {
        if (mode.isCurrentMode("Solid")) {
            if (mc.thePlayer.onGround && !mc.thePlayer.isInWater() && BlockUtils.isOnLiquid()
                    && !mc.thePlayer.isSneaking()) {
                event.y += mc.thePlayer.ticksExisted % 2 == 0 ? 2.0E-12 : 0.0;
            }
        }
    }

    @EventTarget
    public void onJump(EventJump event) {
        if (BlockUtils.isOnLiquid())
            event.setCancelled(true);
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (mode.isCurrentMode("Motion")) {
            if (isOnLiquid() && !this.isInLiquid())
                MoveUtils.setMotion(event ,0.2163D);
        }
    }

    @EventTarget
    public void onUpdatePre(EventPreMotion event) {
        if (BlockUtils.isInLiquid() && mc.thePlayer.movementInput.jump) {
            mc.thePlayer.movementInput.jump = false;
            mc.thePlayer.setJumping(false);
        }

        if (mode.isCurrentMode("Motion")) {
            if (!ModManager.getModule("Speed").isEnabled() && !mc.gameSettings.keyBindSneak.isKeyDown()
                    && !mc.gameSettings.keyBindJump.isKeyDown()) {
                if (isOnLiquid() && !this.isInLiquid() && !isInLiquiddol()) {
                    Motion(event);
                    return;
                } else
                    if (this.isInLiquid() && isInLiquiddol()) {
                        flat();
                    }
            }

            this.stage = 0;

            if (mc.thePlayer.stepHeight < 0.02f)
                mc.thePlayer.stepHeight = 0.6f;
        }

        if (mode.isCurrentMode("BHop")) {
            if (BlockUtils.isInLiquid() && timer.isDelayComplete(200) && !mc.gameSettings.keyBindSneak.isKeyDown()
                    && !mc.thePlayer.isInWater()) {
                float forward = mc.thePlayer.movementInput.moveForward;
                float strafe = mc.thePlayer.movementInput.moveStrafe;

                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                if ((forward != 0.0F || strafe != 0.0F)) {

                    // jump
                    mc.thePlayer.motionY = 0.4D;
                    mc.thePlayer.isAirBorne = true;

                    // move
                    float var4 = strafe * strafe + forward * forward;
                    var4 = MathHelper.sqrt_float(var4);

                    if (var4 < 1.0F) {
                        var4 = 1.0F;
                    }

                    var4 = 0.26f / var4;
                    strafe *= var4;
                    forward *= var4;
                    float var5 = MathHelper.sin(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
                    float var6 = MathHelper.cos(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
                    mc.thePlayer.motionX += strafe * var6 - forward * var5;
                    mc.thePlayer.motionZ += forward * var6 + strafe * var5;

                    timer.reset();
                } else {

                    mc.thePlayer.motionY = 0.085D;
                }
            } else
                if (mc.thePlayer.isInWater() && mc.thePlayer.movementInput.jump) {
                    mc.thePlayer.motionY = 0.08;
                }
        }
    }

    private boolean isPossible(BlockPos pos) {
        final int i = mc.theWorld.getBlockState(pos).getBlock()
                .getMetaFromState(mc.theWorld.getBlockState(pos));
        return i < 5 && mc.thePlayer.fallDistance <= 3.0f && !mc.thePlayer.isSneaking();
    }

    public boolean shouldGround(final double n) {
        return n % 1.0 == 0.015625 || n % 1.0 == 0.0625 || n % 0.125 == 0.0;
    }

    @EventTarget
    public void onBlockCollide(BBSetEvent event) {
        int x = event.getPos().getX();
        int y = event.getPos().getY();
        int z = event.getPos().getZ();

        if (mode.isCurrentMode("Motion")) {

            if (!ModManager.getModule("Speed").isEnabled() && !this.isInLiquid()
                && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {

            if (mc.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
                    .get(BlockLiquid.LEVEL) instanceof Integer) {
                if ((int) mc.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
                        .get(BlockLiquid.LEVEL) > 2) {
                    return;
                }
            }

            final Object v2 = mc.theWorld.getBlockState(event.getPos()).getProperties()
                    .get(BlockLiquid.LEVEL);
            if ((!(v2 instanceof Integer) || (int) v2 <= 3)
                    && mc.theWorld.getBlockState(event.getPos()).getBlock() instanceof BlockLiquid
                    && !mc.thePlayer.isSneaking()) {

                event.setCancelled(true);
                event.setBoundingBox(new AxisAlignedBB(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
                        event.getPos().getX() + 0.98, event.getPos().getY() + 1.0, event.getPos().getZ() + 0.98));
            }
            }
        }

    }

    private boolean isInLiquid() {
        if (mc.thePlayer == null) {
            return false;
        }
        int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX);
        while (true) {
            final int n = x;
            if (n >= MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1) {
                return false;
            }
            int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ);
            while (true) {
                final int n2 = z;
                if (n2 >= MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1) {
                    ++x;
                    break;
                }
                final int x2 = x;
                final BlockPos pos = new BlockPos(x2, (int) mc.thePlayer.getEntityBoundingBox().minY, z);
                final Block block = mc.theWorld.getBlockState(pos).getBlock();
                if (block != null && !(block instanceof BlockAir)) {

                    if (block instanceof BlockLiquid
                            && mc.theWorld.getBlockState(pos).getProperties()
                            .get(BlockLiquid.LEVEL) instanceof Integer) {
                        if ((int) mc.theWorld.getBlockState(pos).getProperties()
                                .get(BlockLiquid.LEVEL) > 1) {
                            return false;
                        }
                    }
                    return block instanceof BlockLiquid;
                }
                ++z;
            }
        }
    }

    public void Motion(EventPreMotion em) {
        if (mc.thePlayer.fallDistance != 0.0f) {
            return;
        }


        mc.thePlayer.stepHeight = 0.015625f;

        ++this.stage;
        if (this.stage == 1) {
            em.setY(em.y - ThreadLocalRandom.current().nextDouble(0.015625D - 1.000000001E-4D, 0.015625D));
        }
        if (this.stage == 2) {
            em.setY(em.y + ThreadLocalRandom.current().nextDouble(0.015D - 1.000000001E-4D, 0.015D));
        }
        if (this.stage == 3) {
            em.setY(em.y + ThreadLocalRandom.current().nextDouble(0.02D - 1.000000001E-4D, 0.02D));
        }
        if (this.stage >= 4) {
            em.setY(em.y + 0.015625D);
            this.stage = 0;
        }

        if (this.stage % 2 == 0) {
            em.setY(em.y - 1.0E-13);
        }
        em.setY(em.y + 1.0E-13);


        em.setOnGround(shouldGround(em.y));

    }

    public void flat() {
        mc.thePlayer.motionY = 0.11999998688698;
    }

    public boolean isInLiquiddol() {
        if (mc.thePlayer == null) {
            return false;
        }
        boolean inLiquid = false;
        final int y = (int) (mc.thePlayer.getEntityBoundingBox().minY + 0.02);
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper
                    .floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }

                    if (mc.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
                            .get(BlockLiquid.LEVEL) instanceof Integer) {
                        if ((int) mc.theWorld.getBlockState(new BlockPos(x, y, z)).getProperties()
                                .get(BlockLiquid.LEVEL) > 1) {
                            return false;
                        }
                    }

                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

}