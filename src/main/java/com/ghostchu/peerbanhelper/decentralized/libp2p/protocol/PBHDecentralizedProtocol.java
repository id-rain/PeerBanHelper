package com.ghostchu.peerbanhelper.decentralized.libp2p.protocol;

import io.libp2p.core.P2PChannel;
import io.libp2p.core.Stream;
import io.libp2p.protocol.ProtocolHandler;
import io.libp2p.protocol.ProtocolMessageHandler;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.CompletableFuture;

public class PBHDecentralizedProtocol extends ProtocolHandler<PBHDecentralizedP2PController> {
    private static final String announce = "/p2p/peerbanhelper-decentralized/0.0.0";
    private final PBHDecentralizedCallback chatCallback;
    private final boolean useLimit;

    public PBHDecentralizedProtocol(PBHDecentralizedCallback chatCallback) {
        super(-1, -1);
        this.useLimit = false;
        this.chatCallback = chatCallback;
    }

    public PBHDecentralizedProtocol(long initiatorTrafficLimit, long responderTrafficLimit, PBHDecentralizedCallback chatCallback) {
        super(initiatorTrafficLimit, responderTrafficLimit);
        this.useLimit = true;
        this.chatCallback = chatCallback;
    }


    public String getAnnounce() {
        return announce;
    }

    @Override
    public CompletableFuture<PBHDecentralizedP2PController> initChannel(P2PChannel ch) {
        if (useLimit) {
            return super.initChannel(ch);
        } else {
            Stream stream = (Stream) ch;
            initProtocolStream(stream);

            if (stream.isInitiator()) {
                return onStartInitiator(stream);
            } else {
                return onStartResponder(stream);
            }
        }
    }

    @Override
    protected CompletableFuture<PBHDecentralizedP2PController> onStartInitiator(Stream stream) {
        return onStart(stream);
    }

    @Override
    protected CompletableFuture<PBHDecentralizedP2PController> onStartResponder(Stream stream) {
        return onStart(stream);
    }

    private CompletableFuture<PBHDecentralizedP2PController> onStart(Stream stream) {
        CompletableFuture<Void> ready = new CompletableFuture<>();
        PBHDecentralizedResponder chatResponder = new PBHDecentralizedResponder(chatCallback, ready);
        stream.pushHandler(chatResponder);
        return ready.thenApply(unused -> chatResponder);
    }

    public static class PBHDecentralizedResponder implements PBHDecentralizedP2PController, ProtocolMessageHandler<ByteBuf> {
        private Stream stream;
        private final CompletableFuture<Void> ready;

        public ChatResponder(ChatCallback chatCallback, CompletableFuture<Void> ready) {
            this.chatCallback = chatCallback;
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
            String msgStr = message.toString(Charset.defaultCharset());
            PeerId peerId = stream.remotePeerId();
            Multiaddr multiaddr = new Multiaddr(stream.getConnection().remoteAddress(), peerId);
            chatCallback.onChatMessage(peerId, multiaddr, msgStr);
        }

        @Override
        public void send(String message) {
            byte[] data = message.getBytes(Charset.defaultCharset());
            stream.writeAndFlush(Unpooled.wrappedBuffer(data));
        }
    }
}
