package cn.hanabi.injection.mixins;

import cn.hanabi.injection.interfaces.IItemTools;
import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemTool.class)
public class MixinItemTools implements IItemTools {
    @Shadow
    protected float efficiencyOnProperMaterial = 4.0F;
    @Shadow
    private float damageVsEntity;


    @Override
    public float getEfficiencyOnProperMaterial() {
        return efficiencyOnProperMaterial;
    }

    @Override
    public float getdamageVsEntity() {
        return damageVsEntity;
    }
}
