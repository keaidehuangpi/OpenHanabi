package cn.hanabi.modules.modules.world;

import cn.hanabi.Wrapper;
import cn.hanabi.events.DamageBlockEvent;
import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.injection.interfaces.IPlayerControllerMP;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;



public class SpeedMine extends Mod {
    public static Value<Double> speed = new Value<>("SpeedMine", "Speed", 0.8, 0.3, 1.0, 0.1);
    public static Value<Double> Pspeed = new Value<>("SpeedMine", "PacketSpeed", 1.6, 1.0, 3.0, 0.1);
    //  public static Value<Double> timer = new Value<Double>("SpeedMine", "Timer Boost", 1.0, 1.0, 2.0, 0.1);

    public BlockPos blockPos;
    public EnumFacing facing;
    public C07PacketPlayerDigging curPacket;
    public Value<Boolean> pot = new Value<>("SpeedMine", "Pot Boost", false);
    public Value<Boolean> c03 = new Value<>("SpeedMine", "Extra PP", false);

    public boolean packet = true;
    public boolean damage = true;
    private boolean bzs = false;
    private float bzx = 0.0f;

    public SpeedMine() {
        super("SpeedMine", Category.WORLD);
    }

    @EventTarget
    public void onDamageBlock(EventPacket event) {
        if (event.packet instanceof C07PacketPlayerDigging && event.getPacket() != curPacket && !mc.playerController.extendedReach() && mc.playerController != null) {
            C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging) event.packet;
            if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                this.bzs = true;
                this.blockPos = c07PacketPlayerDigging.getPosition();
                this.facing = c07PacketPlayerDigging.getFacing();
                this.bzx = 0.0f;
                Wrapper.getTimer().timerSpeed = 1.0f;
            } else
                if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                    this.bzs = false;
                    this.blockPos = null;
                    this.facing = null;
                    Wrapper.getTimer().timerSpeed = 1.0f;

                }
        }
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = mc.theWorld.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(mc.theWorld, pos) != -1;
    }

    @EventTarget
    public void onBreak(DamageBlockEvent event) {
        if (canBreak(event.getPos())) {

            if (pot.getValueState()) {
                mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 100, 1));
            }
        }

    }


    @EventTarget
    public void onUpdate(EventUpdate event) {
        IPlayerControllerMP controller = (IPlayerControllerMP) mc.playerController;


        if (mc.playerController.extendedReach()) {
            controller.setBlockHitDelay(0);
            if (controller.getCurBlockDamageMP() >= (1 - speed.getValueState())) {
                controller.setCurBlockDamageMP(1F);
            }
        } else
            if (this.bzs) {
                Block block = mc.theWorld.getBlockState(this.blockPos).getBlock();
                int n = Block.getIdFromBlock(block);
                this.bzx += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.blockPos) * Pspeed.getValueState().floatValue();

                if (this.bzx >= 1.0f) {
                    mc.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);

                    C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing);
                    curPacket = packet;
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(packet);

                    this.bzx = 0.0f;
                    this.bzs = false;

                }

            }
    }
}
