package me.yarukon.hud.window.impl;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Wrapper;
import cn.hanabi.modules.modules.world.Teams;
import cn.hanabi.utils.Colors;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.value.Value;
import me.yarukon.YRenderUtil;
import me.yarukon.hud.window.HudWindow;
import net.minecraft.entity.player.EntityPlayer;


@ObfuscationClass

public class WindowRadar extends HudWindow {

    public Value<Double> scale = new Value<>("HudWindow", "Radar Scale", 2d, 0d, 20d, 0.1d);

    public WindowRadar() {
        super("Radar", 5, 25, 100, 100, "Radar", "", 12, 0, 1f, true, 100, 100);
    }

    @Override
    public void draw() {
        super.draw();

        float xOffset = x;
        float yOffset = y + draggableHeight;
        float playerOffsetX = (float) mc.thePlayer.posX;
        float playerOffSetZ = (float) mc.thePlayer.posZ;

        YRenderUtil.drawRectNormal(xOffset + ((width / 2f) - 0.5f), yOffset + 3.5f, xOffset + (width / 2f) + 0.5f, (yOffset + height) - 3.5f, 0x50ffffff);
        YRenderUtil.drawRectNormal(xOffset + 3.5f, yOffset + ((height / 2f) - 0.5f), (xOffset + width) - 3.5f, yOffset + (height / 2) + 0.5f, 0x50ffffff);

        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityPlayer) {
                EntityPlayer ent = (EntityPlayer) o;
                if (ent.isEntityAlive() && ent != mc.thePlayer && !ent.isInvisible() && !ent.isInvisibleToPlayer(mc.thePlayer)) {
                    float pTicks = Wrapper.getTimer().renderPartialTicks;
                    float posX = (float) ((ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks - (double) playerOffsetX) * (scale.getValueState().floatValue()));
                    float posZ = (float) ((ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks - (double) playerOffSetZ) * (scale.getValueState().floatValue()));
                    int color = Teams.isOnSameTeam(ent) ? Colors.GREEN.c : Colors.RED.c;

                    float cos = (float) Math.cos((double) mc.thePlayer.rotationYaw * 0.017453292519943295D);
                    float sin = (float) Math.sin((double) mc.thePlayer.rotationYaw * 0.017453292519943295D);
                    float rotY = -(posZ * cos - posX * sin);
                    float rotX = -(posX * cos + posZ * sin);

                    if (rotY > (height / 2 - 5f)) {
                        rotY = (height / 2) - 5f;
                    } else if (rotY < -(height / 2) + 5f) {
                        rotY = -(height / 2) + 5f;
                    }

                    if (rotX > (width / 2) - 5.0F) {
                        rotX = (width / 2 - 5);
                    } else if (rotX < (-(width / 2 - 5))) {
                        rotX = -((width / 2) - 5.0F);
                    }

                    RenderUtil.circle((xOffset + (width / 2) + rotX), (yOffset + (height / 2) + rotY), 1, color);
                }
            }
        }
    }
}
