/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.hanabi.injection.mixins;

import cn.hanabi.Hanabi;
import cn.hanabi.modules.ModManager;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiChat extends MixinGuiScreen {
    @Shadow
    private boolean waitingOnAutocomplete;

    @Shadow
    public abstract void onAutocompleteResponse(String[] p_146406_1_);

    @Inject(method = "sendAutocompleteRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void autocomplete(String cmd, String p_146405_2_, @NotNull CallbackInfo ci) {
        if (cmd.startsWith(".") && !ModManager.getModule("NoCommand").isEnabled()) {
            String[] ls = Hanabi.INSTANCE.commandManager.autoComplete(cmd).toArray(new String[0]);

           if (ls.length == 0 || cmd.toLowerCase().endsWith(ls[ls.length - 1].toLowerCase())) {
                return;
            }
            waitingOnAutocomplete = true;
            onAutocompleteResponse(ls);
            ci.cancel();
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    public void mouse(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        Hanabi.INSTANCE.hudWindowMgr.mouseCoordinateUpdate(mouseX, mouseY);
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    public void updateScr(CallbackInfo info) {
        Hanabi.INSTANCE.hudWindowMgr.updateScreen();
    }

    @Inject(method = "handleMouseInput", at = @At("RETURN"))
    public void handleMouseInput(CallbackInfo info) {
        Hanabi.INSTANCE.hudWindowMgr.handleMouseInput(this.width, this.height);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo info) {
        Hanabi.INSTANCE.hudWindowMgr.mouseClick(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        Hanabi.INSTANCE.hudWindowMgr.mouseRelease(mouseX, mouseY, state);
        super.mouseReleased(mouseX, mouseY, state);
    }
}
