package cn.hanabi.modules.modules.render;


import cn.hanabi.Wrapper;
import cn.hanabi.events.EventRender;
import cn.hanabi.events.EventRenderBlock;
import cn.hanabi.injection.interfaces.IEntityRenderer;
import cn.hanabi.injection.interfaces.IRenderManager;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class OreTarget extends Mod {

    public static final List<BlockPos> toRender = new CopyOnWriteArrayList<>();
    public static List<BlockPos> toRenderPacket = new CopyOnWriteArrayList<>();

    private final Minecraft mc = Minecraft.getMinecraft();
    private final TimeHelper refresh = new TimeHelper();
    public Value<Boolean> dia = new Value<>("OreTarget", "Diamond", true);
    public Value<Boolean> gold = new Value<>("OreTarget", "Gold", true);
    public Value<Boolean> iron = new Value<>("OreTarget", "Iron", true);
    public Value<Boolean> lapis = new Value<>("OreTarget", "Lapis", true);
    public Value<Boolean> emerald = new Value<>("OreTarget", "Emerald", true);
    public Value<Boolean> coal = new Value<>("OreTarget", "Coal", true);
    public Value<Boolean> redstone = new Value<>("OreTarget", "Redstone", true);

    public Value<Boolean> tracers = new Value<>("OreTarget", "Tracers", true);
    public final Value<Double> interactiveDelay = new Value<>("OreTarget", "Interactive Delay", 1000d, 1000d, 50000d, 100d);
    public final Value<Double> blockDis = new Value<>("OreTarget", "Max Distance", 3d, 3d, 9d, 1d);
    public Value<Boolean> packet = new Value<>("OreTarget", "Packet", true);

    public Value<Boolean> bypass = new Value<>("OreTarget", "Touching Air Or Liquid", true);
    public Value<Double> depth = new Value<>("OreTarget", "TestDepth", 2d, 1d, 5d, 1d);
    public Value<Boolean> radiusOn = new Value<>("OreTarget", "Distance Limit Enabled", true);
    public Value<Double> radius = new Value<>("OreTarget", "Distance Limit", 10d, 5d, 100d, 5d);
    public Value<Boolean> limitEnabled = new Value<>("OreTarget", "Render Limit Enabled", true);
    public Value<Double> limit = new Value<>("OreTarget", "Render Limit", 10d, 5d, 100d, 5d);
    public Value<Double> refresh_timer = new Value<>("OreTarget", "Refresh List", 500d, 0d, 5000d, 100d);
    public Value<Double> alpha = new Value<>("OreTarget", "Alpha", 0.25d, 0d, 1d, 0.05d);
    public Value<Double> width = new Value<>("OreTarget", "Line Width", 2.5d, 1d, 10d, 0.5d);


    TimeHelper timerUtils = new TimeHelper();


    public OreTarget() {
        super("OreTarget", Category.RENDER);
    }

    public static void drawOutlinedBlockESP(double x, double y, double z, float red, float green, float blue,
                                            float alpha, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(red, green, blue, alpha);
        drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    private Color getColorByBlock(final Block block) {
        if (block == Blocks.coal_ore) {
            return new Color(65, 65, 65);
        }

        if (block == Blocks.redstone_ore || block == Blocks.lit_redstone_ore) {
            return new Color(255, 65, 67);
        }

        if (block == Blocks.iron_ore) {
            return new Color(255, 185, 115);
        }

        if (block == Blocks.gold_ore) {
            return new Color(255, 254, 0);
        }

        if (block == Blocks.diamond_ore) {
            return new Color(0, 232, 255);
        }

        if (block == Blocks.emerald_ore) {
            return new Color(1, 255, 0);
        }

        if (block == Blocks.lapis_ore) {
            return new Color(4, 0, 255);
        }

        return null;
    }

    public void onEnable() {
        toRender.clear();
        refresh.reset();
        mc.renderGlobal.loadRenderers();

    }

    public void onDisable() {
        toRender.clear();
        refresh.reset();
        mc.renderGlobal.loadRenderers();
    }

    @EventTarget
    public void onTick(EventRender e) {
        if (refresh.isDelayComplete(refresh_timer.getValueState().floatValue())) {
            new Thread(() -> {
                ArrayList<BlockPos> cache=new ArrayList<>();
                for(BlockPos pos:toRender){
                    if(test(pos))
                        cache.add(pos);
                }
                toRender.clear();
                toRender.addAll(cache);
            }).start();
            refresh.reset();
        }
    }

    @EventTarget
    public void onRenderBlock(EventRenderBlock event) {
        BlockPos pos = new BlockPos(event.x, event.y, event.z);
        if (!toRender.contains(pos)) {
            // 这个总感觉多线程了会出问题
            if (test(pos)) {
                if (!(toRender.size() > limit.getValueState()) || !limitEnabled.getValueState()) {
                    toRender.add(pos);
                }
            }
        }
    }

    public boolean isTarget(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if (Blocks.diamond_ore.equals(block)) {
            return dia.getValueState();
        } else
            if (Blocks.lapis_ore.equals(block)) {
                return lapis.getValueState();
            } else
                if (Blocks.iron_ore.equals(block)) {
                    return iron.getValueState();
                } else
                    if (Blocks.gold_ore.equals(block)) {
                        return gold.getValueState();
                    } else
                        if (Blocks.coal_ore.equals(block)) {
                            return coal.getValueState();
                        } else
                            if (Blocks.emerald_ore.equals(block)) {
                                return emerald.getValueState();
                            } else
                                if (Blocks.redstone_ore.equals(block) || Blocks.lit_redstone_ore.equals(block)) {
                                    return redstone.getValueState();
                                }
        return false;
    }

    private Boolean oreTest(BlockPos origPos, Double depth) {
        Collection<BlockPos> posesNew = new ArrayList<>();
        Collection<BlockPos> posesLast = new ArrayList<>(Collections.singletonList(origPos));
        Collection<BlockPos> finalList = new ArrayList<>();
        for (int i = 0; i < depth; i++) {
            for (BlockPos blockPos : posesLast) {
                posesNew.add(blockPos.up());
                posesNew.add(blockPos.down());
                posesNew.add(blockPos.north());
                posesNew.add(blockPos.south());
                posesNew.add(blockPos.west());
                posesNew.add(blockPos.east());
            }
            for (BlockPos pos : posesNew) {
                if (posesLast.contains(pos)) {
                    posesNew.remove(pos);
                }
            }
            posesLast = posesNew;
            finalList.addAll(posesNew);
            posesNew = new ArrayList<>();
        }

        List<Block> legitBlocks = Arrays.asList(Blocks.water, Blocks.lava, Blocks.flowing_lava, Blocks.air,
                Blocks.flowing_water, Blocks.fire);

        return finalList.stream()
                .anyMatch(blockPos -> legitBlocks.contains(mc.theWorld.getBlockState(blockPos).getBlock()));
    }

    public void asyncTest(BlockPos pos) {
        new Thread(() -> {
            if (test(pos)) {
                synchronized (toRender) {
                    toRender.add(pos);
                }
            }
        }).start();
    }

    public boolean test(BlockPos pos1) {
        if (!isTarget(pos1)) {
            return false;
        }
        if (bypass.getValueState()) {
            if (!oreTest(pos1, depth.getValueState())) {
                return false;
            }
        }
        if (radiusOn.getValueState()) {
            return !(mc.thePlayer.getDistance(pos1.getX(), pos1.getY(), pos1.getZ()) >= radius.getValueState());
        }
        return true;
    }
    
    @EventTarget
    public void onRender(EventRender event) {
        for (BlockPos blockPos : OreTarget.toRender) {
            renderBlock(blockPos);
        }
        for (BlockPos blockPos : toRenderPacket){
            renderBlock(blockPos);
        }
    }

    // 这个没用，没法主动更新方块状态
//    @EventTarget
//    public void onUpdate(EventUpdate event){
//        for (BlockPos blockPos : OreTarget.toRender) {
//            mc.renderGlobal.markBlockForUpdate(blockPos);
//        }
//    }

    private void renderBlock(BlockPos pos) {
        double x = (double) pos.getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double y = (double) pos.getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double z = (double) pos.getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        Color color = this.getColorByBlock(this.mc.theWorld.getBlockState(pos).getBlock());
        boolean old = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        ((IEntityRenderer)mc.entityRenderer).setupCameraTransform(Wrapper.getTimer().renderPartialTicks, 2);
        mc.gameSettings.viewBobbing = old;
        if (tracers.getValue())
            RenderUtil.drawLine(color.getRGB(), x, y, z);

        RenderUtil.drawBlockESP(x, y, z, new Color(255, 255, 255, 0).getRGB(), color.getRGB(),
                alpha.getValue().floatValue(), width.getValueState().floatValue());
    }


    @EventTarget
    public void onMoveBlock(EventRender update) {
        if (packet.getValueState()) {
            int size = blockDis.getValueState().intValue();
            if (timerUtils.isDelayComplete(interactiveDelay.getValueState().longValue())) {
                //TODO PacketSend
                toRenderPacket.clear();
                packet(size);
                timerUtils.reset();
            }
        }
    }
    int interlocks = 0;

    public void packet(int size) {
        for (int x = -size; x < size; x ++) {
            for (int y = -size; y < size; y ++) {
                for (int z = -size; z < size; z ++) {

                    if (interlocks >= size - 1) {
                        interlocks = 0;
                    }

                    int BlockX = (int) (mc.thePlayer.posX + x);
                    int BlockY = (int) (mc.thePlayer.posY + z);
                    int BlockZ = (int) (mc.thePlayer.posZ + z);

                    BlockPos blockPos = new BlockPos(BlockX, BlockY, BlockZ);

                    List<Block> legitBlocks = Arrays.asList(Blocks.water, Blocks.lava, Blocks.flowing_lava, Blocks.air,
                            Blocks.flowing_water, Blocks.fire);

                    if (mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getX()) < interlocks || mc.theWorld.getBlockState(blockPos).getBlock() == legitBlocks)
                        continue;
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.UP));

                    interlocks += 1;
                    if (isTarget(blockPos)) {
                        toRenderPacket.add(blockPos);
                    }
                }
            }
        }
    }
}