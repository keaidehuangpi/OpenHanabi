package cn.hanabi.modules.modules.player;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventPostMotion;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.injection.interfaces.IC03PacketPlayer;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.BlockUtils;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;


@ObfuscationClass
public class NoFall extends Mod {
    public static Value<Double> falls = new Value<>("NoFall", "Fall Distance", 2.5d, 1.5d, 4.0d, 0.01d);
    public static Value<Double> caneled = new Value<>("NoFall", "Canceled Distance", 1.0d, 0.0d, 3.0d, 0.1d);
    public static Value<Double> spoof = new Value<>("NoFall", "Spoof Location", 0.0325d, 0.01d, 0.05d, 0.0025d);

    private final float b = 1.0F;
    private final boolean c = true;
    private final Value mode = new Value("NoFall", "Mode", 0)
            .LoadValue(new String[]{"Hypixel", "Mineplex", "AAC", "OnGround", "NoGround", "Verus" , "Edit"});
    private final Value hypixelMode = new Value("NoFall", "Hypixel Mode", 0)
            .LoadValue(new String[]{"Spoof", "Packet", "Edit", "Async"});
    private final Value aacMode = new Value("NoFall", "AAC Mode", 0)
            .LoadValue(new String[]{"AACv4", "None"/*,"RedeSky"天空网络的ground检测炸了，直接spoofground就能绕*/});
    private final ArrayList<C03PacketPlayer> aac4Packets = new ArrayList<>();
    public Value<Boolean> tryspoof = new Value<>("NoFall", "More Packet", true);
    public Value<Boolean> reset = new Value<>("NoFall", "F-Distance Reset", true);
    public Value<Boolean> cancel = new Value<>("NoFall", "Cancel Packet", true);

    public double fallDist = 0;
    public TimeHelper timer = new TimeHelper();
    double lastFall;
    int times;
    boolean showed;
    double fall;
    double fallDist1;
    private int hypixel1;
    private int d;
    private int state;
    private double actualFallDistance;
    private boolean damage;
    private BlockPos startPos;
    private boolean aac4Fakelag = false;
    private boolean aac4PacketModify = false;

    private boolean needSpoof = false;

    public NoFall() {
        super("NoFall", Category.PLAYER);
    }

    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0.0D) {
            return false;
        } else {
            int off = 0;

            while (true) {
                if (off >= (int) mc.thePlayer.posY + 2) {
                    return false;
                }

                AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0D, -off, 0.0D);
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }

                off += 2;
            }
        }
    }

    public static int getJumpEffect() {
        return mc.thePlayer.isPotionActive(Potion.jump)
                ? mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1
                : 0;
    }

    private BlockPos getGroundPos(BlockPos startPos) {
        for (int i = 0; i < startPos.getY(); i++) {
            final BlockPos newPos = startPos.down(i);
            if (BlockUtils.getBlock(newPos) != Blocks.air) {
                return newPos;
            }
        }
        return null;
    }

    @EventTarget
    public void onUpdate(EventPreMotion event) {
        this.setDisplayName(mode.getModeAt(mode.getCurrentMode()));

        if (mode.isCurrentMode("NoGround")){
            if (MoveUtils.isOnGround(0.001)){
                event.setY(event.getY() + 0.0001);
            }
        }

        //

        if (mc.thePlayer.isSpectator() || mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.disableDamage || mc.thePlayer.onGround)
            return;

        if (tryspoof.getValue() && mc.thePlayer.fallDistance > (falls.getValue() - 1) && mc.thePlayer.fallDistance % spoof.getValue() == 0)
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));


            if (mode.isCurrentMode("Hypixel")) {
            if (hypixelMode.isCurrentMode("Spoof")) {
                if ((mc.thePlayer.fallDistance > falls.getValue()) && isBlockUnder()) {
                    if (reset.getValue()) {
                        sync();
                        mc.thePlayer.fallDistance *= .1;
                    } else
                        sync();
                }
            } else if (hypixelMode.isCurrentMode("Packet")) {
                if (fallDist > mc.thePlayer.fallDistance) fallDist = 0f;
                if (mc.thePlayer.motionY < 0 && mc.thePlayer.fallDistance > falls.getValue() && isBlockUnder() && !mc.thePlayer.capabilities.allowFlying) {
                    double motionY = mc.thePlayer.motionY;
                    double fallingDist = mc.thePlayer.fallDistance - fallDist;
                    double realDist = fallingDist + -((motionY - 0.08) * 0.9800000190734863);
                    if (realDist >= 3) {
                        if (reset.getValue()) {
                            for (int i = 0; i < (int) mc.thePlayer.fallDistance; i++)
                                sync();
                            mc.thePlayer.fallDistance *= .1;
                        } else
                            sync();

                        fallDist = mc.thePlayer.fallDistance;
                    }
                }
            } else if (hypixelMode.isCurrentMode("ASync")) {
                if ((mc.thePlayer.fallDistance > falls.getValue()) && isBlockUnder()) {
                    sync();
                }
            }

        } else if (mode.isCurrentMode("Mineplex")) {
            setDisplayName("Mineplex");
            if (mc.thePlayer.fallDistance > 2.5F) {
                (mc.getNetHandler().getNetworkManager()).sendPacket(new C03PacketPlayer(true));
                mc.thePlayer.fallDistance = 0.5F;
            }
        } else if (mode.isCurrentMode("AAC") && aacMode.isCurrentMode("AACv4")) {
            if (!inVoid()) {
                if (aac4Fakelag) {
                    aac4Fakelag = false;
                    if (aac4Packets.size() > 0) {
                        for (C03PacketPlayer packet : aac4Packets) {
                            mc.thePlayer.sendQueue.addToSendQueue(packet);
                        }
                        aac4Packets.clear();
                    }
                }
                return;
            }
            if (mc.thePlayer.onGround && aac4Fakelag) {
                aac4Fakelag = false;
                if (aac4Packets.size() > 0) {
                    for (C03PacketPlayer packet : aac4Packets) {
                        mc.thePlayer.sendQueue.addToSendQueue(packet);
                    }
                    aac4Packets.clear();
                }
                return;
            }
            if (mc.thePlayer.fallDistance > 3 && aac4Fakelag) {
                aac4PacketModify = true;
                mc.thePlayer.fallDistance = 0;
            }
            if (inAir(4.0, 1.0)) {
                return;
            }
            if (!aac4Fakelag) {
                aac4Fakelag = true;
            }
        } else if (mode.isCurrentMode("Verus")) { // I HATE SHITCODE
            if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
                mc.thePlayer.motionY = 0;
                mc.thePlayer.fallDistance = 0.0f;
                mc.thePlayer.motionX *= 0.6;
                mc.thePlayer.motionZ *= 0.6;
                needSpoof = true;
            }
        }
    }


    @EventTarget
    public void onPost(EventPostMotion e) {
        if (mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.disableDamage
                || mc.thePlayer.motionY >= 0.0d)
            return;


        if (mode.isCurrentMode("Hypixel")) {
            if (hypixelMode.isCurrentMode("Async")) {
                if ((mc.thePlayer.fallDistance > falls.getValue()) && isBlockUnder()) {
                    if (reset.getValue()) {
                        for (int i = 0; i < (int) mc.thePlayer.fallDistance; i++)
                            sync();
                        mc.thePlayer.fallDistance *= .1;
                    } else
                        sync();

                }
            }
        }
    }

    private void sync() {
        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
    }

    private boolean inVoid() {
        if (mc.thePlayer.posY < 0) {
            return false;
        }
        for (int off = 0; off < mc.thePlayer.posY + 2; off += 2) {
            AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.posX, off, mc.thePlayer.posZ);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean inAir(final double height, final double plus) {
        if (mc.thePlayer.posY < 0)
            return false;
        for (int off = 0; off < height; off += plus) {
            AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.posX, mc.thePlayer.posY - off, mc.thePlayer.posZ);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        fallDist = mc.thePlayer.fallDistance;
        aac4Fakelag = false;
        aac4PacketModify = false;
        aac4Packets.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (mode.isCurrentMode("NoGround")) {
            IC03PacketPlayer packet = (IC03PacketPlayer) e.getPacket();
            packet.setOnGround(false);
        } else if (mode.isCurrentMode("OnGround")) {
            IC03PacketPlayer packet = (IC03PacketPlayer) e.getPacket();
            if (mc.thePlayer.fallDistance > 2.0)
                packet.setOnGround(true);
        } else if (mode.isCurrentMode("AAC") && aacMode.isCurrentMode("AACv4") && aac4Fakelag) {
            e.setCancelled(true);
            if (aac4PacketModify) {
                ((IC03PacketPlayer) e.getPacket()).setOnGround(true);
                aac4PacketModify = false;
            }
            aac4Packets.add((C03PacketPlayer) e.getPacket());
        } else if (mode.isCurrentMode("Hypixel")) {
            C03PacketPlayer look = (C03PacketPlayer) e.getPacket();
            if (cancel.getValue())
                if (look.getRotating() && look.isMoving())
                    if ((mc.thePlayer.fallDistance > falls.getValue() - caneled.getValue()) && isBlockUnder()) {
                        Wrapper.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition
                                (look.getPositionX(), look.getPositionY(), look.getPositionZ(), look.isOnGround()));
                        e.setCancelled(true);
                    }


            if (hypixelMode.isCurrentMode("Edit")) {
                final C03PacketPlayer C03 = (C03PacketPlayer) e.getPacket();

                final double[] packetPosition = {
                        C03.getPositionX(),
                        C03.getPositionY(),
                        C03.getPositionZ(),
                };

                final double[] myPosition = {
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                };

                final boolean same = packetPosition[0] == myPosition[0] && packetPosition[1] == myPosition[1] && packetPosition[2] == myPosition[2];

                if (!mc.thePlayer.isSpectator() && !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.capabilities.disableDamage && !mc.thePlayer.onGround && mc.thePlayer.fallDistance >= 2.5F && same && !C03.isOnGround()) {
                    ((IC03PacketPlayer) C03).setOnGround(true);
                }
            }
        } else if (mode.isCurrentMode("Verus")) {
            if (needSpoof && !MoveUtils.isOverVoid()) {
                ((IC03PacketPlayer) e.getPacket()).setOnGround(true);
                needSpoof = false;
            }
        } else if (mode.isCurrentMode("Edit")) {
            if (mc.thePlayer.fallDistance > 2){
                if (mc.thePlayer.fallDistance % 3 ==0)
                    ((IC03PacketPlayer) e.getPacket()).setOnGround(true);
            }
        }
    }
}
