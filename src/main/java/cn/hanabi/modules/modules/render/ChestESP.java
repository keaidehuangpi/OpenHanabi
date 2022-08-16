package cn.hanabi.modules.modules.render;

import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventRenderBlock;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.RenderUtil;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;


public class ChestESP extends Mod {
    ArrayList<BlockPos> list = new ArrayList<>();

    public ChestESP() {
        super("ChestESP", Category.RENDER);
        // TODO Auto-generated constructor stub
    }

    public void onEnable() {
        mc.renderGlobal.loadRenderers();
        list.clear();
    }

    @EventTarget
    public void onRenderBlock(EventRenderBlock e) {
        BlockPos pos = new BlockPos(e.x, e.y, e.z);
        if (!list.contains(pos) && (e.block instanceof BlockChest || e.block instanceof BlockEnderChest)) {
            list.add(pos);
        }
    }

    @EventTarget
    public void onRender(EventRender e) {
        for (BlockPos pos : list) {
            if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockChest) && !(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockEnderChest))
                list.remove(pos);
            RenderUtil.drawSolidBlockESP(pos.getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX(),
                    pos.getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY(),
                    pos.getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), 1f, 1f, 1f, 0.2f);
        }
    }
}
