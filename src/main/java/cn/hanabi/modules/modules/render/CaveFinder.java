package cn.hanabi.modules.modules.render;

import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventRenderBlock;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.RenderUtil;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public class CaveFinder extends Mod {
    ArrayList<BlockPos> list = new ArrayList<>();

    public CaveFinder() {
        super("CaveFinder", Category.RENDER);
        // TODO Auto-generated constructor stub
    }

    public void onEnable() {
        mc.renderGlobal.loadRenderers();
        list.clear();
    }

    @EventTarget
    public void onRenderBlock(EventRenderBlock e) {
        BlockPos pos = new BlockPos(e.x, e.y, e.z);
        if (!list.contains(pos) && e.block instanceof BlockLiquid && e.y <= 40) {
            list.add(pos);
        }
    }

    @EventTarget
    public void onRender(EventRender e) {
        for (BlockPos pos : list) {
            IBlockState state = mc.theWorld.getBlockState(pos);
            if (!(state.getBlock() instanceof BlockLiquid)) list.remove(pos);
            RenderUtil.drawSolidBlockESP(pos.getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX(),
                    pos.getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY(),
                    pos.getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), state.getBlock().getMaterial() == Material.lava ? 1 : 0, 0, state.getBlock().getMaterial() == Material.water ? 1 : 0, 0.2f);
        }
    }
}
