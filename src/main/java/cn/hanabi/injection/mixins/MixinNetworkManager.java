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
import cn.hanabi.events.EventPacket;
import cn.hanabi.injection.interfaces.INetworkManager;
import cn.hanabi.utils.PacketHelper;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import com.google.common.collect.Queues;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements INetworkManager {

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    @Shadow
    private Channel channel;

    @Final
    @Shadow
    private final Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();


    @Inject(method = "channelRead0*", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void packetReceived(ChannelHandlerContext p_channelRead0_1_, Packet<? extends net.minecraft.network.INetHandler> packet, CallbackInfo ci) {
        EventPacket event = new EventPacket(EventType.RECIEVE, packet);
        EventManager.call(event);
        PacketHelper.onPacketReceive(packet);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void sendPacket(Packet<? extends net.minecraft.network.INetHandler> packetIn, CallbackInfo ci) {
        try {
            if (packetIn instanceof C00Handshake) {
                C00Handshake handshakePacket = (C00Handshake)packetIn;
                Class clazz = handshakePacket.getClass();
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getType() == String.class) {
                        field.setAccessible(true);
                        String targetIP = field.get(handshakePacket).toString();
                        if (Hanabi.INSTANCE.hypixelBypass) {
                            Hanabi.INSTANCE.println("Redirect to Hypixel");
                            field.set(handshakePacket, "mc.hypixel.net");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        EventPacket event = new EventPacket(EventType.SEND, packetIn);
        EventManager.call(event);

        if (event.isCancelled()) ci.cancel();
    }


    @Inject(method = "closeChannel(Lnet/minecraft/util/IChatComponent;)V", at = @At("RETURN"))
    private void onClose(IChatComponent chatComponent, CallbackInfo ci) {
       Logger.getLogger("Closed");
    }

    @Shadow
    public abstract boolean isChannelOpen();


    @Override
    public void sendPacketNoEvent(Packet packet) {
        if (channel != null && channel.isOpen()) {
            flushOutboundQueue();
            dispatchPacket(packet, null);
        } else {
            outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener[]) null));
        }
    }



    @Shadow
    protected abstract void dispatchPacket(Packet a, GenericFutureListener[] a2);

    @Shadow
    protected abstract void flushOutboundQueue();


}

class InboundHandlerTuplePacketListener
{

    @SafeVarargs
    public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener <? extends Future <? super Void >> ... inFutureListeners)
    {
    }
}
