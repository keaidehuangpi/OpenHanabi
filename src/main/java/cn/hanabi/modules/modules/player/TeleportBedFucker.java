package cn.hanabi.modules.modules.player;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.NukerUtil;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.pathfinder.PathUtils;
import cn.hanabi.utils.pathfinder.Vec3;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockBed;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;


@ObfuscationClass
public class TeleportBedFucker extends Mod {
    public BlockPos playerBed;
    public BlockPos fuckingBed;
    public ArrayList<BlockPos> posList;
    public Value<Double> delay = new Value<>("TP2Bed", "Delay", 600d, 200d, 3000d, 100d);
    TimeHelper timer = new TimeHelper();
    private ArrayList<Vec3> path = new ArrayList<>();

    public TeleportBedFucker() {
        super("TP2Bed", Category.PLAYER);
        // TODO Auto-generated constructor stub
    }

    public void onEnable() {
        try {
            posList = new ArrayList<>(NukerUtil.list);
            posList.sort((o1, o2) -> {
                double distance1 = getDistanceToBlock(o1);
                double distance2 = getDistanceToBlock(o2);
                return (int) (distance1 - distance2);
            });

            if (posList.size() < 3) {
                this.set(false);
            }

            ArrayList<BlockPos> posListFor = new ArrayList<>(posList);
            int index = 1;
            for (BlockPos kid : posListFor) {
                index++;
                if (index % 2 == 1) {
                    posList.remove(kid);
                }
            }

            playerBed = posList.get(0);
            posList.remove(0);
            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && MoveUtils.isOnGround(0.01)) {
                for (int i = 0; i < 49; i++) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX, mc.thePlayer.posY + 0.06249D, mc.thePlayer.posZ, false));
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                }
                mc.thePlayer.onGround = false;
                mc.thePlayer.jumpMovementFactor = 0;
            }
            fuckingBed = posList.get(0);
        } catch (Throwable e) {
            this.set(false);
        }
    }

    @EventTarget
    public void onRender(EventRender e) {
    }

    @Override
    protected void onDisable() {
        Wrapper.canSendMotionPacket = true;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        for (BlockPos pos : posList)
            if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockBed)) {
                PlayerUtil.tellPlayer("Destory!" + pos);
                posList.remove(pos);
                posList.sort((o1, o2) -> {
                    double distance1 = getDistanceToBlock(o1);
                    double distance2 = getDistanceToBlock(o2);
                    return (int) (distance1 - distance2);
                });
                fuckingBed = posList.get(0);
            }
        if (mc.thePlayer.getDistance(fuckingBed.getX(), fuckingBed.getY(), fuckingBed.getZ()) < 4) {
            Wrapper.canSendMotionPacket = true;
            PlayerUtil.tellPlayer("Teleported! :3");
            this.set(false);
        }

        if (timer.isDelayComplete(delay.getValueState())) {
            Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            Vec3 to = new Vec3(fuckingBed.getX() + 1, fuckingBed.getY(), fuckingBed.getZ() + 1);

            path = PathUtils.computePath(topFrom, to);

            if (mc.thePlayer.getDistance(fuckingBed.getX(), fuckingBed.getY(), fuckingBed.getZ()) > 4) {
                PlayerUtil.tellPlayer("Trying to teleport...");
                Wrapper.canSendMotionPacket = false;
                for (Vec3 pathElm : path) {
                    mc.thePlayer.sendQueue
                            .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(),
                                    pathElm.getZ(), true));
                }
            }

            timer.reset();
        }
        if (posList.size() == 0) {
            this.set(false);
        }
    }

    public double getDistanceToBlock(BlockPos pos) {
        return mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ());
    }
}

