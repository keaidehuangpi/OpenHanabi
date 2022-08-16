package cn.hanabi.injection.mixins;

import cn.hanabi.Client;
import cn.hanabi.command.commands.SkinChangeCommand;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.render.ClickGUIModule;
import me.yarukon.Yarukon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AbstractClientPlayer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {


    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        ResourceLocation location;
        if (Client.onDebug) {
            if (ModManager.getModule(ClickGUIModule.class).theme.isCurrentMode("Dark"))
                location = new ResourceLocation("Client/capes/darkhanabi.png");
            else location = new ResourceLocation("Client/capes/lighthanabi.png");
        } else location = new ResourceLocation("Client/capes/darkhanabi.png");
        callbackInfoReturnable.setReturnValue(getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID()) ? location : null);
    }


    @Inject(method = "getLocationSkin()Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (!Objects.equals(getGameProfile().getName(), Minecraft.getMinecraft().thePlayer.getGameProfile().getName()) || SkinChangeCommand.targetSkin.isEmpty())
            return;

        callbackInfoReturnable.setReturnValue(Yarukon.INSTANCE.getSkin(SkinChangeCommand.targetSkin));
    }


    @Inject(method = "getSkinType", at = @At("HEAD"), cancellable = true)
    private void getSkinType(CallbackInfoReturnable<String> callbackInfoReturnable) {
        if (!Objects.equals(getGameProfile().getName(), Minecraft.getMinecraft().thePlayer.getGameProfile().getName()) || SkinChangeCommand.targetSkin.isEmpty())
            return;

        callbackInfoReturnable.setReturnValue(SkinChangeCommand.slim ? "slim" : "default");
    }

}
