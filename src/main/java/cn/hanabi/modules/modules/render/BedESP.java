package cn.hanabi.modules.modules.render;

import cn.hanabi.events.EventRender;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.NukerUtil;
import cn.hanabi.utils.RenderUtil;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.util.BlockPos;


public class BedESP extends Mod {
    public BedESP() {
        super("BedESP", Category.RENDER);
        // TODO Auto-generated constructor stub
    }

    @EventTarget
    public void onRender(EventRender e) {
        for (BlockPos pos : NukerUtil.list) {
            RenderUtil.drawSolidBlockESP(pos.getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX(),
                    pos.getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY(),
                    pos.getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), 1f, 1f, 1f, 0.2f);
        }
    }
}
