package cn.hanabi.modules.modules.player;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.events.EventRender;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.WorldUtil;
import cn.hanabi.utils.rotation.RotationUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemSword;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ObfuscationClass
public class AimBot extends Mod {

    public static Value<Boolean> players = new Value<>("AimBot", "Players", false);
    public static Value<Boolean> headshot = new Value<>("AimBot", "Only Head", false);
    public static Value<Double> range = new Value<>("AimBot", "Range", 100.0, 1.0, 500.0, 5.0);
    public static Value<Double> deviation = new Value<>("AimBot", "Pre-Attack", 1.5, 0.0, 10.0, 0.1);

    private Vec3 aimed;


    public AimBot() {
        super("AimBot", Category.PLAYER);
    }

    @EventTarget(Priority.LOW)
    private void onUpdatePre(EventPreMotion event) {
        List<EntityLivingBase> list;

        final List<EntityLivingBase> targets = WorldUtil.getLivingEntities().stream()
                .filter(this::isValid)
                .sorted(Comparator.comparing(e -> mc.thePlayer.getDistanceToEntity(e)))
                .collect(Collectors.toList());

        list = new ArrayList<>();
        list.addAll(targets.stream().filter((entity) -> entity instanceof EntityGiantZombie || entity instanceof EntityWither).collect(Collectors.toList()));
        list.addAll(targets.stream().filter((entity) -> !(entity instanceof EntityGiantZombie || entity instanceof EntityWither)).collect(Collectors.toList()));


        if (list.size() <= 0)
            return;

        this.aimed = this.getFixedLocation(list.get(0), deviation.getValue().floatValue(), headshot.getValue());

        final float[] rotations = RotationUtil.getRotationToLocation(this.aimed);

        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);

        mc.thePlayer.rotationYawHead = rotations[0];

        if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemBook))
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
    }

    @EventTarget
    private void onRender3D(final EventRender event) {
        if (this.aimed == null)
            return;

        double posX = this.aimed.xCoord - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double posY = this.aimed.yCoord - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double posZ = this.aimed.zCoord - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();

        RenderUtil.drawBlockESP(posX - 0.5, posY - 0.5, posZ - 0.5, new Color(255, 0, 0, 100).getRGB(), new Color(0xFFE900).getRGB(), 0.4f, 0.1f);
    }

    private Vec3 getFixedLocation(final EntityLivingBase entity, final float velocity, final boolean head) {
        double x = entity.posX + ((entity.posX - entity.lastTickPosX) * velocity);
        double y = entity.posY + ((entity.posY - entity.lastTickPosY) * (velocity * 0.3)) + (head ? entity.getEyeHeight() : 1.0);
        double z = entity.posZ + ((entity.posZ - entity.lastTickPosZ) * velocity);

        return new Vec3(x, y, z);
    }

    private boolean isValid(final EntityLivingBase entity) {
        if (!(entity instanceof EntityZombie || entity instanceof EntitySilverfish || entity instanceof EntityWither || entity instanceof EntityGhast || entity instanceof EntitySpider || entity instanceof EntityGiantZombie || entity instanceof EntitySkeleton || entity instanceof EntityGolem || entity instanceof EntityEndermite || entity instanceof EntityWitch || entity instanceof EntityBlaze || entity instanceof EntitySlime || entity instanceof EntityCreeper || entity instanceof EntityWolf || entity instanceof EntityPlayer && players.getValue()))
            return false;

        if (entity.isDead || entity.getHealth() <= 0)
            return false;

        if (mc.thePlayer.getDistanceToEntity(entity) > range.getValue())
            return false;

        return PlayerUtil.canEntityBeSeenFixed(entity);
    }

    

}
