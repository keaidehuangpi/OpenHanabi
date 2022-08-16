package cn.hanabi.injection.mixins;

import cn.hanabi.events.BBSetEvent;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.ghost.Reach;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Shadow
    @Final
    protected BlockState blockState;
    @Shadow
    protected double minX;
    @Shadow
    protected double minY;
    @Shadow
    protected double minZ;
    @Shadow
    protected double maxX;
    @Shadow
    protected double maxY;
    @Shadow
    protected double maxZ;
    @Final
    @Shadow
    protected Material blockMaterial;
    Minecraft mc = Minecraft.getMinecraft();
    int blockID = 0;
    private Block BLOCK;

    @Shadow
    public abstract void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

    @Shadow
    public abstract boolean isFullCube();

    @Shadow
    public abstract boolean isBlockNormalCube();

    @Shadow
    public abstract AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state);

    /**
     * @author Mojang
     */
    @Overwrite
    public boolean isCollidable() {
        Reach reach = ModManager.getModule(Reach.class);
        return !(reach.isEnabled() && Reach.throughWall.getValue());
    }

    /**
     * @author Mojang
     */

    @Overwrite
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(worldIn, pos, state);
        BBSetEvent blockBBEvent = new BBSetEvent(blockState.getBlock(), pos,  axisalignedbb);
        EventManager.call(blockBBEvent);
        axisalignedbb = blockBBEvent.getBoundingBox();
        if(axisalignedbb != null && mask.intersectsWith(axisalignedbb))
            list.add(axisalignedbb);
    }


}