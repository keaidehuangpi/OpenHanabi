package cn.hanabi.modules.modules.movement.Fly;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventPacket;
import cn.hanabi.utils.MoveUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.ArrayList;


@ObfuscationClass
public class Fly_AACv5 {
    Minecraft mc = Minecraft.getMinecraft();

    private boolean blockC03=false;

    public void onPacket(EventPacket event){
        Packet packet = event.getPacket();
        if(blockC03&&packet instanceof C03PacketPlayer){
            cacheList.add((C03PacketPlayer) packet);
            event.setCancelled(true);
            if(cacheList.size()>7) {
                sendC03();
            }
        }
    }

    public void onEnable(){
        blockC03=true;
    }

    public void onDisable(){
        sendC03();
        blockC03=false;
    }

    public void onUpdate(){
        double vanillaSpeed=Fly.timer.getValue(); // idk why called it timer

        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.thePlayer.motionY += vanillaSpeed;
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.thePlayer.motionY -= vanillaSpeed;
        MoveUtils.strafe(vanillaSpeed);
    }

    private final ArrayList<C03PacketPlayer> cacheList=new ArrayList<>();

    private void sendC03(){
        blockC03=false;
        for(C03PacketPlayer packet : cacheList){
            mc.getNetHandler().addToSendQueue(packet);
            if(packet.isMoving()){
                mc.getNetHandler().addToSendQueue((new C03PacketPlayer.C04PacketPlayerPosition(packet.getPositionX(),1e+159,packet.getPositionZ(), true)));
                mc.getNetHandler().addToSendQueue((new C03PacketPlayer.C04PacketPlayerPosition(packet.getPositionX(),packet.getPositionY(),packet.getPositionZ(), true)));
            }
        }
        cacheList.clear();
        blockC03=true;
    }
}
