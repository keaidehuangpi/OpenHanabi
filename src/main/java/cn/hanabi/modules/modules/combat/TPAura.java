package cn.hanabi.modules.modules.combat;

import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.pathfinder.PathUtils;
import cn.hanabi.utils.pathfinder.Vec3;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;

import java.util.ArrayList;
import java.util.List;


public class TPAura extends Mod {
    public final Value<Double> cps = new Value<>("TPAura", "CPS", 2.0, 1.0, 20.0, 1.0);
    public final Value<Double> range = new Value<>("TPAura", "Range", 30.0, 10.0, 100.0, 1.0);
    public final Value<Double> targets = new Value<>("TPAura", "Targets", 1.0, 1.0, 10.0, 1.0);
    public final Value<Boolean> swingValue = new Value<>("TPAura", "Swing", true);
    public final Value<Boolean> renderPathValue = new Value<>("TPAura", "Render Path", true);

    private final TimeHelper timer=new TimeHelper();
    private final ArrayList<Vec3> path=new ArrayList<>();
    private Thread thread=new Thread(()->{/*do nothing*/});

    public TPAura() {
        super("TPAura", Category.COMBAT);
    }

    @Override
    public void onEnable(){
        path.clear();
        timer.reset();
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {
        if(!timer.isDelayComplete(KillAura.randomClickDelay(cps.getValue().intValue(),cps.getValue().intValue()+1))||thread.isAlive())
            return;

        thread=new Thread(() -> {
            int target=0;
            path.clear();
            for(Entity entity : mc.theWorld.loadedEntityList){
                if(KillAura.isValidEntityType(entity)&&mc.thePlayer.getDistanceToEntity(entity)<range.getValue()){
                    target++;

                    doTPHit((EntityLivingBase) entity);

                    if(target>=targets.getValue())
                        break;
                }
            }
        });
        thread.start();

        timer.reset();
    }

    private void doTPHit(EntityLivingBase entity){
        List<Vec3> tpPath=PathUtils.computePath(mc.thePlayer,entity);

        tpPath.forEach((vec3) -> {
            path.add(vec3);
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),vec3.getY(),vec3.getZ(),true));
        });

        // attack
        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        if(swingValue.getValue()){
            mc.thePlayer.swingItem();
        }else{
            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }

        tpPath=Lists.reverse(tpPath);


        tpPath.forEach((vec3) -> mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),vec3.getY(),vec3.getZ(),true)));
    }

    @EventTarget
    public void onRender(EventRender event) {

    }
}
