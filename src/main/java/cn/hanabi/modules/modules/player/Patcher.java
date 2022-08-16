package cn.hanabi.modules.modules.player;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.events.EventTick;
import cn.hanabi.events.EventWorldChange;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.random.Random;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;


@ObfuscationClass
public class Patcher extends Mod {
    private final Value mode = new Value("Patcher", "Mode", 0)
            .LoadValue(new String[]{"Hypixel", "AACv4LessFlag"});

    private final Value<Boolean> pong = new Value<>("Patcher", "Pong", true);

    byte[] uuid = UUID.randomUUID().toString().getBytes();
    int count;
    private double x, y, z;
    float cacheYaw, cachePitch;

    private final Queue<TimestampedPacket> queue = new ConcurrentLinkedDeque<>();
    private final LinkedList<Packet<?>> list = new LinkedList<>();

    private int bypassValue = 0;
    private long lastTransaction = 0L;

    int biqiling;

    private int lastUid;
    private boolean checkReset;
    private boolean active;


    private boolean collect = false;
    private int serverPosPacket = 0;

    private final TimeHelper generation = new TimeHelper();
    private final TimeHelper tick = new TimeHelper();
    private final TimeHelper collecttimer = new TimeHelper();


    private final TimeHelper choke = new TimeHelper();

    private final TimeHelper giga = new TimeHelper();

    public Patcher() {
        super("Patcher", Category.PLAYER);
    }


    @EventTarget
    public void onChangeWorld(EventWorldChange e) {
    }

    @EventTarget
    public void onPre(EventTick e) {

    }

    @EventTarget
    public void onPreUpdate(EventPreMotion e) {
        if (mode.isCurrentMode("Hypixel")) {
            base(null);
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();

        if (mode.isCurrentMode("AACv4LessFlag")) {
            if (packet instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook packetS08 = (S08PacketPlayerPosLook) packet;
                double x = packetS08.getX() - mc.thePlayer.posX;
                double y = packetS08.getY() - mc.thePlayer.posY;
                double z = packetS08.getZ() - mc.thePlayer.posZ;
                double diff = Math.sqrt(x * x + y * y + z * z);
                if (diff <= 8) {
                    event.setCancelled(true);
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packetS08.getX(), packetS08.getY(), packetS08.getZ(), packetS08.getYaw(), packetS08.getPitch(), true));
                }
            }
        } else if (mode.isCurrentMode("HypixelSlime")) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                PlayerCapabilities capabilities = new PlayerCapabilities(); // flags=2
                capabilities.disableDamage = false;
                capabilities.isFlying = true;
                capabilities.allowFlying = false;
                capabilities.isCreativeMode = false;
                mc.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(capabilities));
            }
        } else if (mode.isCurrentMode("Hypixel")) {
            base(event);
        }

    }

    @Override
    public void onEnable() {
        super.onEnable();
        uuid = UUID.randomUUID().toString().getBytes();

        list.clear();
        if (mc.thePlayer.ticksExisted > 1) {
            PlayerUtil.tellPlayer("Login again to disable watchdog.");
        }
    }

    @Override
    public void onDisable() {
    }


    /**
     * @Disabler Check Vaild
     **/

    private void checkUidVaild(EventPacket event) {
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            final C0FPacketConfirmTransaction C0F = (C0FPacketConfirmTransaction) event.getPacket();
            final int windowId = C0F.getWindowId();
            final int uid = C0F.getUid();
            if (windowId == 0 && uid < 0) {
                final int predictedUid = lastUid - 1;
                if (!checkReset) {
                    if (uid == predictedUid) {
                        if (!active) {
                            active = true;
                        }
                    } else {
                        active = false;
                    }
                } else {
                    if (uid != predictedUid) {
                        active = false;
                    }
                    checkReset = false;
                }
                lastUid = uid;
            }
        }
    }


    public void hypixel(EventPacket event) {
        if (mc.isSingleplayer())
            return;

        if (event != null) {
            //Init
            if (event.getPacket() instanceof S01PacketJoinGame) {
                setDisplayName("Hypixel");
                generation.reset();
                tick.reset();
                count = 0;
                lastTransaction = 0L;
                collect = true;

                queue.clear();
                list.clear();
                PlayerUtil.debugChat("Clear");

                giga.reset();
                bypassValue = 2000;
            }

            //Do with S08 stuff --> to keep less detect rate
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();

                if (mc.currentScreen instanceof GuiDownloadTerrain)
                    mc.currentScreen = null;

                if (packet.getYaw() == 0 && packet.getPitch() == 0)
                    event.setCancelled(true);
                else {
                    if (count < 1) {
                        event.setCancelled(true);
                        biqiling = 0;
                        count++;
                    } else {
                        x = packet.getX();
                        y = packet.getY();
                        z = packet.getZ();

                        if (giga.isDelayComplete(2000)) {
                            biqiling = 6;
                            giga.reset();
                        }
                    }
                }
            }


            //keepalive stuff
            if (event.getPacket() instanceof C00PacketKeepAlive) {
                event.setCancelled(true);

                if (pong.getValue()) {
                    queue.add(new TimestampedPacket(event.getPacket(), System.currentTimeMillis()));
                } else {
                    list.add(event.getPacket());
                }
            }
            //ping test bypass
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                if (((C0FPacketConfirmTransaction) event.getPacket()).getUid() < 0 && ((C0FPacketConfirmTransaction) event.getPacket()).getWindowId() == 0) {
                    lastTransaction = System.currentTimeMillis();
                    event.setCancelled(true);

                    if (pong.getValue()) {
                        queue.add(new TimestampedPacket(event.getPacket(), System.currentTimeMillis()));
                    } else {
                        list.add(event.getPacket());
                    }
                }
            }

            //c03 stuff one
            if (event.getPacket() instanceof C03PacketPlayer) {
                final C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();

                //cancel a lot
                if (collect) {
                    if (!packet.isMoving() && !packet.getRotating())
                        event.setCancelled(true);
                }

                if (biqiling > 0) {
                    event.setCancelled(true);
                    biqiling--;
                }

                if (!event.isCancelled()) {
                    if (pong.getValue()) {
                        queue.add(new TimestampedPacket(event.getPacket(), System.currentTimeMillis()));
                        event.setCancelled(true);
                    } else {
                        list.add(event.getPacket());
                        event.setCancelled(true);
                    }
                }
            }

        } else {
            if (mc.thePlayer == null || mc.theWorld == null)
                return;

            if (tick.isDelayComplete(10000) || !generation.isDelayComplete(5000)) {
                collect = true;

                tick.reset();
                choke.reset();
                collecttimer.reset();
            }

            if ((list.isEmpty() && queue.isEmpty()))
                return;

            long collectvalue = collect ? (!generation.isDelayComplete(5000) ? 2000 : 500 + randomInt(50)) : 180;

            if (pong.getValue()) {
                if (Math.abs(lastTransaction - System.currentTimeMillis()) <= 200L && choke.isDelayComplete(750L) && collect) {
                    PlayerUtil.debugChat("Reset");
                    bypassValue = 0;
                    collect = false;
                }

                for (final TimestampedPacket timestampedPacket : queue) {
                    final long timestamp = timestampedPacket.timestamp;
                    if (Math.abs(timestamp - System.currentTimeMillis()) >= bypassValue) {
                        Wrapper.sendPacketNoEvent(timestampedPacket.packet);
                        queue.remove(timestampedPacket);
                    }
                }
            } else {
                if (collecttimer.isDelayComplete(collectvalue)) {
                    while (!list.isEmpty())
                        Wrapper.sendPacketNoEvent(list.poll());

                    collect = false;
                    collecttimer.reset();
                }
            }
        }
    }


    /**
     * @Disabler Ching Ching Disabler
     **/


    public void base(EventPacket event) {
        if (mc.isSingleplayer())
            return;

        if (event != null) {
            if (event.getPacket() instanceof S07PacketRespawn) {
                queue.clear();
                checkReset = true;
            }

            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook serverSidePosition = (S08PacketPlayerPosLook) event.getPacket();

                if (mc.currentScreen instanceof GuiDownloadTerrain)
                    mc.currentScreen = null;

                final float serverPitch = serverSidePosition.getPitch();
                final float serverYaw = serverSidePosition.getYaw();

                if (serverPitch == 0 && serverYaw == 0)
                    event.setCancelled(true);
            }

            //ping test bypass
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                lastTransaction = System.currentTimeMillis();
                checkUidVaild(event);
                if (active){
                    event.setCancelled(true);
                    queue.add(new TimestampedPacket(event.getPacket(), System.currentTimeMillis()));
                }
            }

        } else {
            if (Math.abs(lastTransaction - System.currentTimeMillis()) <= 200L
                    && tick.isDelayComplete(10000)) {
                bypassValue = 300 + Random.nextInt(40, 60);
                Hanabi.INSTANCE.println("Reset");
                tick.reset();
            }

            if (collecttimer.isDelayComplete(bypassValue)) {
                collecttimer.reset();
                // ping spoof
               Hanabi.INSTANCE.println(String.valueOf(queue.size()));

                while (queue.size() > 1)
                    Wrapper.sendPacketNoEvent(queue.poll().packet);
            }

        }
    }

    /**
     * @Disabler Verus Funny Disabler
     **/

 /*

    public void verusmeme(EventPacket event) {
        if (event != null) {
            if (event.getPacket() instanceof S07PacketRespawn) { //init
                expectedTeleport = false; // huh
                basictiming.reset(); // timer reset
                packetQueue.clear(); // packet list reset

                count = 0; //counter reset
            }

            // Ping Spoof

            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                int windowId = ((C0FPacketConfirmTransaction) event.getPacket()).getWindowId();  // Action Uid Check
                short action = ((C0FPacketConfirmTransaction) event.getPacket()).getUid();  // Action Uid Check

                if (action > 0 && action < 100)
                    return;

                // Check the Vaild Detect Packet
                packetQueue.add(new C0FPacketConfirmTransaction(windowId, action, true));
                event.setCancelled(true);
            }

            if (event.getPacket() instanceof C0BPacketEntityAction) {
                event.setCancelled(true);
            }

            if (event.getPacket() instanceof C00PacketKeepAlive) {
                packetQueue.add(event.getPacket());  //cancel some due to cool / -1 is can replace others
                event.setCancelled(true);
            }

            if (event.getPacket() instanceof C03PacketPlayer) {
                if (mc.thePlayer.ticksExisted % 32 == 0) {
                    expectedTeleport = true; // huh
                    Wrapper.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, -0.0125, mc.thePlayer.posZ, false));
                    // legit for flag (ft.monkey verus skid ncp moment) / abusing for (fall distance / phase) detection
                    event.setCancelled(true); // just edit packet is fine but i like do this ;3
                }

                // Remeber for Min_value no Max_value due to monkey detection of verus :3
                //  event.setCancelled(mc.thePlayer.ticksExisted % 3 == 0);
            }

            //to prevent lots of flag xd
            if (event.getPacket() instanceof S08PacketPlayerPosLook && expectedTeleport) {
                S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) event.getPacket();
                expectedTeleport = false;
                Wrapper.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ(), s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), true));
                event.setCancelled(true);
            }
        } else {
            // 288 is fine / 250 --> 400 ms
            if (basictiming.isDelayComplete(899)) {
                basictiming.reset();
                // ping spoof
                while (packetQueue.size() > 20) {
                    Wrapper.sendPacketNoEvent(this.packetQueue.poll());
                }
            }
        }
    }

    */



    private static class TimestampedPacket {
        private final Packet<?> packet;
        private final long timestamp;

        public TimestampedPacket(final Packet<?> packet, final long timestamp) {
            this.packet = packet;
            this.timestamp = timestamp;
        }
    }
}


