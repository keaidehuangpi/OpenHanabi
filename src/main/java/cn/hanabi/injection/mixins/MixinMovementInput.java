package cn.hanabi.injection.mixins;

import net.minecraft.util.MovementInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MovementInput.class)
public class MixinMovementInput{

    @Shadow
    public float moveStrafe;
    /** The speed at which the player is moving forward. Negative numbers will move backwards. */
    @Shadow
    public float moveForward;
    @Shadow
    public boolean jump;
    @Shadow
    public boolean sneak;



}

