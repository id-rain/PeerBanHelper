package com.ghostchu.peerbanhelper.decentralized.libp2p.protocol;

import io.libp2p.core.PeerId;
import io.libp2p.core.Stream;
import io.libp2p.core.multiformats.Multiaddr;
import io.libp2p.protocol.ProtocolMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.concurrent.CompletableFuture;

public class PBHDecentralizedResponder implements PBHDecentralizedP2PController, ProtocolMessageHandler<ByteBuf> {
    private final PBHDecentralizedCallback callback;
    private Stream stream;
    private final CompletableFuture<Void> ready;

    public PBHDecentralizedResponder(PBHDecentralizedCallback callback, CompletableFuture<Void> ready) {
        this.callback = callback;
        this.ready = ready;
    }

    @Override
    public void fireMessage(Stream stream, Object o) {
        onMessage(stream, (ByteBuf) o);
    }

    @Override
    public void onActivated(Stream stream) {
        this.stream = stream;
        ready.complete(null);
    }

    @Override
    public void onClosed(Stream stream) {

    }

    @Override
    public void onException(Throwable throwable) {

    }

    @Override
    public void onMessage(Stream stream, ByteBuf message) {
        PeerId peerId = stream.remotePeerId();
        Multiaddr multiaddr = new Multiaddr(stream.getConnection().remoteAddress(), peerId);
        callback.onMessage(peerId, multiaddr, message.array());
    }

    @Override
    public void send(byte[] message) {
        stream.writeAndFlush(Unpooled.wrappedBuffer(message));
    }
}
