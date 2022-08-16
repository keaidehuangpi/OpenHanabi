package cn.hanabi.modules.modules.player;

import cn.hanabi.events.EventPreMotion;
import cn.hanabi.injection.interfaces.IPlayerControllerMP;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.CombatUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;
import java.util.Iterator;



public class Nuker extends Mod {
    private final TimeHelper timer2 = new TimeHelper();
    private final TimeHelper timer = new TimeHelper();
    private final Value mode = new Value("Nuker", "Mode", 0);
    private final Value<Double> reach = new Value<>("Nuker", "Reach", 6.0D, 1.0D, 6.0D, 0.1D);
    private final Value<Double> delay = new Value<>("Nuker", "Delay", 120.0D, 0.0D, 1000.0D, 10.0D);
    private final Value<Boolean> instant = new Value("Nuker", "Instant", false);
    ArrayList positions = null;

    public Nuker() {
        super("Nuker", Category.PLAYER);
        mode.LoadValue(new String[]{"Bed", "Egg", "Cake"});
    }

    @EventTarget
    public void onPre(EventPreMotion event) {
        Iterator positions = BlockPos
                .getAllInBox(
                        mc.thePlayer.getPosition().subtract(
                                new Vec3i(reach.getValueState(), reach.getValueState(), reach.getValueState())),
                        mc.thePlayer.getPosition()
                                .add(new Vec3i(reach.getValueState(), reach.getValueState(), reach.getValueState())))
                .iterator();
        BlockPos bedPos = null;

        while ((bedPos = (BlockPos) positions.next()) != null
                && (!(mc.theWorld.getBlockState(bedPos).getBlock() instanceof BlockBed) || !mode.isCurrentMode("Bed"))
                && (!(mc.theWorld.getBlockState(bedPos).getBlock() instanceof BlockDragonEgg)
                || !mode.isCurrentMode("Egg"))
                && (!(mc.theWorld.getBlockState(bedPos).getBlock() instanceof BlockCake)
                || !mode.isCurrentMode("Cake"))) {
        }

        if (bedPos != null) {
            float[] rot = CombatUtil.getRotationsNeededBlock(bedPos.getX(), bedPos.getY(), bedPos.getZ());
            event.yaw = rot[0];
            event.pitch = rot[1];

            if (timer.isDelayComplete(delay.getValueState())) {

                if (instant.getValueState()) {
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                            Action.START_DESTROY_BLOCK, bedPos, EnumFacing.DOWN));

                    mc.thePlayer.swingItem();

                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                            Action.STOP_DESTROY_BLOCK, bedPos, EnumFacing.DOWN));
                } else {
                    if (((IPlayerControllerMP) mc.playerController).getBlockDELAY() > 1) {
                        ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(1);
                    }
                    mc.thePlayer.swingItem();
                    EnumFacing direction = getClosestEnum(bedPos);
                    if (direction != null) {
                        mc.playerController.onPlayerDamageBlock(bedPos, direction);
                    }
                }

                mc.thePlayer.sendQueue.addToSendQueue(
                        new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, bedPos, EnumFacing.DOWN));
                mc.thePlayer.sendQueue
                        .addToSendQueue(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, bedPos, EnumFacing.DOWN));
                mc.thePlayer.sendQueue.addToSendQueue(
                        new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, bedPos, EnumFacing.DOWN));
                mc.thePlayer.swingItem();

                timer.reset();
            }

        }
    }

    private EnumFacing getClosestEnum(BlockPos pos) {
        EnumFacing closestEnum = EnumFacing.UP;
        float rotations = MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[0]);
        if (rotations >= 45 && rotations <= 135) {
            closestEnum = EnumFacing.EAST;
        } else if ((rotations >= 135 && rotations <= 180) || (rotations <= -135 && rotations >= -180)) {
            closestEnum = EnumFacing.SOUTH;
        } else if (rotations <= -45 && rotations >= -135) {
            closestEnum = EnumFacing.WEST;
        } else if ((rotations >= -45 && rotations <= 0) || (rotations <= 45 && rotations >= 0)) {
            closestEnum = EnumFacing.NORTH;
        }
        if (MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) > 75
                || MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) < -75) {
            closestEnum = EnumFacing.UP;
        }
        return closestEnum;
    }

    public float[] getRotations(BlockPos block, EnumFacing face) {
        double x = block.getX() + 0.5 - mc.thePlayer.posX;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - (block.getY() + 0.5);
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
        if (yaw < 0.0F) {
            yaw += 360f;
        }
        return new float[]{yaw, pitch};
    }
}
