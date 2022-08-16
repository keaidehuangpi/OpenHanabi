package cn.hanabi.modules.modules.combat;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventJump;
import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.injection.interfaces.IS12PacketEntityVelocity;
import cn.hanabi.injection.interfaces.IS27PacketExplosion;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;


@ObfuscationClass
public class Velocity extends Mod {
    public final static Value<String> modes = new Value<String>("Velocity", "Mode", 0)
            .LoadValue(new String[]{"Cancel", "Packet", "AAC", "AAC4.4.0", "AAC4", "Intave", "RedeSky", "RedeSkyHVH", "RedeSkyPacket", "AAC5"});

    public Value<Double> x = new Value<>("Velocity", "Vertical", 0.0d, -100.0d, 100d, 1.0);
    public Value<Double> y = new Value<>("Velocity", "Horizontal", 0.0d, 0.0d, 100d, 1.0);
    public Value<Boolean> detect = new Value<>("Velocity", "Check KB", true);

    private final TimeHelper timer = new TimeHelper(), calcTimer = new TimeHelper(), knockBackTimer = new TimeHelper();
    boolean canVelo = false, wtfBoolean = true;


    public Velocity() {
        super("Velocity", Category.COMBAT);
    }

    @EventTarget
    private void onPacket(EventPacket e) {

        if (detect.getValue()) {
            if (e.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                if (packet.getEntityID() != mc.thePlayer.getEntityId()) return;
                if (knockBackTimer.isDelayComplete(250) && mc.thePlayer.ticksExisted > 60) {
                    if (wtfBoolean && mc.thePlayer.hurtResistantTime == 0 && mc.thePlayer.velocityChanged) {
                        ClientUtil.sendClientMessage("You may have been KB checked!", Notification.Type.WARNING);
                        wtfBoolean = false;
                        mc.thePlayer.addVelocity(packet.getMotionX(), packet.getMotionY(), packet.getMotionZ());
                    } else {
                        wtfBoolean = false;
                    }
                } else if (wtfBoolean && mc.thePlayer.hurtResistantTime > 0) {
                    wtfBoolean = false;
                }
                e.setCancelled(true);
            }
        }


        if (modes.isCurrentMode("Cancel")) {
            this.setDisplayName("Cancel");
            if (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {
                e.setCancelled(true);
            }
        } else if (modes.isCurrentMode("Packet")) {
            this.setDisplayName("Packet");
            if (e.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                    ((IS12PacketEntityVelocity) packet).setX((int) (packet.getMotionX() * x.getValue() / 100.0));
                    ((IS12PacketEntityVelocity) packet).setY((int) (packet.getMotionY() * y.getValue() / 100.0));
                    ((IS12PacketEntityVelocity) packet).setZ((int) (packet.getMotionZ() * x.getValue() / 100.0));
                }
            }

            if (e.getPacket() instanceof S27PacketExplosion) {
                S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
                ((IS27PacketExplosion) packet).setX(packet.func_149149_c() * x.getValue().floatValue() / 100.0f);
                ((IS27PacketExplosion) packet).setY(packet.func_149144_d() * y.getValue().floatValue() / 100.0f);
                ((IS27PacketExplosion) packet).setZ(packet.func_149147_e() * x.getValue().floatValue() / 100.0f);
            }
        }

        if (modes.isCurrentMode("RedeSky")) {
            // can i silent apply that flag?
//            if(blockAFlag&&packet instanceof S08PacketPlayerPosLook){
//                e.setCancelled(true);
//                blockAFlag=false;
//            }
            S12PacketEntityVelocity veloPacket = null;
            if (e.getPacket() instanceof S12PacketEntityVelocity) {
                if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                    veloPacket = (S12PacketEntityVelocity) e.getPacket();
                }
            } else if (e.getPacket() instanceof S27PacketExplosion) {
                // convert it to normal velocity packet
                S27PacketExplosion explPacket = (S27PacketExplosion) e.getPacket();
                if (!(explPacket.func_149149_c() == 0 && explPacket.func_149144_d() == 0 && explPacket.func_149147_e() == 0)) {
                    veloPacket = new S12PacketEntityVelocity(mc.thePlayer.getEntityId(), explPacket.func_149149_c()
                            , explPacket.func_149144_d(), explPacket.func_149147_e());
                }
            }
            if (veloPacket == null)
                return;

            boolean near = false;
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (!entity.equals(mc.thePlayer) && entity.getDistanceToEntity(mc.thePlayer) < 10) {
                    near = true;
                    break;
                }
            }
            if (!near)
                return;

            if (veloPacket.getMotionX() == 0 && veloPacket.getMotionZ() == 0)
                return;

            e.setCancelled(true);

            if (mc.thePlayer.onGround) {
                // oh aac checks this
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.03, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
            }
        } else if (modes.isCurrentMode("RedeSkyHVH")) {
            if (e.getPacket() instanceof S12PacketEntityVelocity) {
                if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                    Wrapper.getTimer().timerSpeed = 0.3f;
                    canVelo = true;
                    timer.reset();
                }
            }
        } else if (modes.isCurrentMode("RedeSkyPacket")) {
            if (e.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                if (packet.getMotionY() <= 0 || packet.getEntityID() != mc.thePlayer.getEntityId())
                    return;

                EntityLivingBase target = null;
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityLivingBase && !entity.equals(mc.thePlayer)) {
                        target = (EntityLivingBase) entity;
                        break;
                    }
                }

                if (target == null)
                    return;

                mc.thePlayer.motionX = 0.0;
                mc.thePlayer.motionZ = 0.0;
                mc.thePlayer.motionY = (packet.getMotionY() / 8000f) * 1.0;
                e.setCancelled(true);

                if (timer.hasReached(500)) {
                    int count = 20;
                    if (!timer.hasReached(800)) {
                        count = 5;
                    } else if (!timer.hasReached(1200)) {
                        count = 8;
                    }
                    for (int i = 0; i < count; i++) {
                        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    }
                    calcTimer.reset();
                }
            }
        }
    }

    @EventTarget
    public void onPre(EventPreMotion e) {
        this.setDisplayName(modes.getModeAt(modes.getCurrentMode()));

        if (modes.isCurrentMode("AAC4.4.0")) {
            this.setDisplayName("AAC4.4.0");
            if (!mc.thePlayer.onGround) {
                if (mc.thePlayer.hurtResistantTime > 0) {
                    mc.thePlayer.motionX *= 0.6;
                    mc.thePlayer.motionZ *= 0.6;
                }
            }
        } else if (modes.isCurrentMode("AAC4")) {
            this.setDisplayName("AAC4");
            if (mc.thePlayer.hurtTime == 9) {
                mc.thePlayer.motionX *= 0.5D;
                mc.thePlayer.motionZ *= 0.5D;
            }

            if (mc.thePlayer.hurtTime == 8) {
                mc.thePlayer.motionX *= 0.4D;
                mc.thePlayer.motionZ *= 0.4D;
            }

            if (mc.thePlayer.hurtTime == 7) {
                mc.thePlayer.motionX *= 0.7D;
                mc.thePlayer.motionZ *= 0.7D;
            }

            if (mc.thePlayer.hurtTime == 6) {
                mc.thePlayer.motionX *= 0.3D;
                mc.thePlayer.motionZ *= 0.3D;
            }

            if (mc.thePlayer.hurtTime == 5) {
                mc.thePlayer.motionX *= 0.1D;
                mc.thePlayer.motionZ *= 0.1D;
            }

        } else if (modes.isCurrentMode("Intave")) {
            this.setDisplayName("Intave");
            if (mc.thePlayer.hurtTime > 1 && mc.thePlayer.hurtTime < 10) {
                mc.thePlayer.motionX *= 0.75;
                mc.thePlayer.motionZ *= 0.75;

                if (mc.thePlayer.hurtTime < 4) {
                    if (mc.thePlayer.motionY > 0) {
                        mc.thePlayer.motionY *= 0.9;
                    } else {
                        mc.thePlayer.motionY *= 1.1;
                    }
                }
            }
        } else if (modes.isCurrentMode("AAC")) {
            this.setDisplayName("AAC");
            if (mc.thePlayer.hurtTime != 0) {
                mc.thePlayer.onGround = true;
            }
        } else if (modes.isCurrentMode("RedeSkyHVH")) {
            if (timer.isDelayComplete(100) && canVelo) {
                canVelo = false;
                Wrapper.getTimer().timerSpeed = 1f;
                mc.thePlayer.motionX *= 0.6;
                mc.thePlayer.motionZ *= 0.6;
            }
        } else if (modes.isCurrentMode("AAC5")) {
            if (mc.thePlayer.hurtTime > 8) {
                mc.thePlayer.motionX *= 0.6;
                mc.thePlayer.motionZ *= 0.6;
            }
        }
    }

    @EventTarget
    public void onJump(EventJump event) {
        boolean jump = true;
    }

}
