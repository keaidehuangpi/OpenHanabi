package cn.hanabi.modules.modules.render;

import cn.hanabi.events.EventRender;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import me.yarukon.palette.ColorValue;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class RacistHat extends Mod {

    public Value<Boolean> renderInFirstPerson = new Value<>("BambooHat", "ShowInFirstPerson", false);
    public Value<Double> side = new Value<>("BambooHat", "Side", 45.0d, 30.0d, 50.0d, 1.0d);
    public Value<Double> stack = new Value<>("BambooHat", "Stacks", 50.0d, 45.0d, 200.0d, 5.0d);

    public ColorValue hatColor = new ColorValue("BambooHat Color", 0.5f, 1f, 1f, 1f, false, false, 10f);

    public RacistHat() {
        super("BambooHat", Category.RENDER);
    }

    @EventTarget
    public void onRender3D(EventRender evt) {
        if (mc.gameSettings.thirdPersonView == 0 && !renderInFirstPerson.getValueState()) {
            return;
        }

        this.drawDoli(mc.thePlayer, evt);
    }

    private void drawDoli(EntityLivingBase entity, EventRender evt) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) evt.getPartialTicks() - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) evt.getPartialTicks() - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) evt.getPartialTicks() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        int side = this.side.getValueState().intValue();
        int stack = this.stack.getValueState().intValue();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + (mc.thePlayer.isSneaking() ? 2.0 : 2.2), z);

        GL11.glRotatef(-entity.width, 0.0f, 1.0f, 0.0f);

        RenderUtil.color(RenderUtil.reAlpha(hatColor.getColor(), 0.4f));

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(1.0f);

        Cylinder c = new Cylinder();
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.0f, 0.8f, 0.4f, side, stack);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glPopMatrix();
    }
}
