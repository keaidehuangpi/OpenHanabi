package cn.hanabi.injection.mixins;

import cn.hanabi.injection.interfaces.IS19PacketEntityStatus;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(S19PacketEntityStatus.class)
public class MixinS19PacketEntityStatus implements IS19PacketEntityStatus {

    @Shadow
    private int entityId;

    @Shadow
    private byte logicOpcode;

    @Override
    public void getEntityId(int b) {
        entityId = b;
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void getLogicOpcode(byte b) {
        logicOpcode = b;
    }

    @Override
    public byte getLogicOpcode() {
        return logicOpcode;
    }


}
