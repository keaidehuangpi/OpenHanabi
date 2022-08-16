package cn.hanabi.injection.mixins;

import cn.hanabi.injection.interfaces.IS32PacketConfirmTransaction;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(S32PacketConfirmTransaction.class)
public class MixinS32PacketConfirmTransaction implements IS32PacketConfirmTransaction {

    @Shadow
    private int windowId;
    @Shadow
    private short actionNumber;
    @Shadow
    private boolean field_148893_c;

    @Override
    public void setwindowId(int b) {
        windowId = b;
    }

    @Override
    public int getwindowID() {
        return windowId;
    }

    @Override
    public short getUid() {
        return actionNumber;
    }

    @Override
    public void setUid(short b) {
        actionNumber = b;
    }

    @Override
    public boolean getAccepted() {
        return field_148893_c;
    }

    @Override
    public void setAccepted(boolean b) {
        field_148893_c = b;
    }

}
