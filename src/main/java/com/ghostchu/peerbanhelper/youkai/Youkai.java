package com.ghostchu.peerbanhelper.youkai;

import com.ghostchu.peerbanhelper.util.ByteUtil;
import dev.foxgirl.torrent.client.*;
import dev.foxgirl.torrent.util.Hash;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class Youkai {
    private final String ip;
    private final int port;
    private final byte[] infoHash;
    private final String peerId;
    private final String clientName;
    private final int bindPort;
    private final YoukaiResult recorder;

    public Youkai(String peerIp, int peerPort, int bindPort, byte[] infoHash, String peerId, String clientName) {
        this.ip = peerIp;
        this.port = peerPort;
        this.bindPort = bindPort;
        this.infoHash = infoHash;
        this.peerId = peerId;
        this.clientName = clientName;
        this.recorder = new YoukaiResult();
    }

    public CompletableFuture<YoukaiResult> test() {
        return CompletableFuture.supplyAsync(() -> {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Identity identity = Identity.generate(peerId, new InetSocketAddress(InetAddress.getLocalHost(), bindPort));
                @Cleanup
                var client = new Client(identity, extensions());
                var peerAddress = new InetSocketAddress(InetAddress.getByName(ip), port);
                @Cleanup
                var peerChannel = AsynchronousSocketChannel.open();
                @Cleanup
                var peer = new Peer(client, peerChannel) {
                    @Override
                    public void onReceive(@NotNull MessageImpl message) throws Exception {
                        super.onReceive(message);
                        try {
                            switch (message.getType()) {
                                case HAVE -> {
                                    int pieceIndex = message.getPayload().getInt();
                                    recorder.getHavePieces().add(pieceIndex);
                                }
                                case CHOKE -> {
                                    recorder.setChoke(true);
                                }
                                case UNCHOKE -> {
                                    recorder.setChoke(false);
                                }
                                case INTERESTED -> {
                                    recorder.setInterested(true);
                                    getProtocol().send(new MessageImpl(MessageType.UNCHOKE));
                                }
                                case NOT_INTERESTED -> {
                                    recorder.setInterested(false);
                                }
                                case BITFIELD -> {
                                    int actualByteCount = message.getPayload().remaining();
                                    recorder.setBitfieldByteCount(actualByteCount);
                                }
                                case HAVE_ALL -> {
                                    recorder.setHaveAll(true);
                                    getProtocol().send(new MessageImpl(MessageType.UNCHOKE));
                                }
                                case HAVE_NONE -> {
                                    recorder.setHaveNone(true);
                                    getProtocol().send(new MessageImpl(MessageType.UNCHOKE));
                                }
                                case EXTENDED -> {
                                    recorder.setExtensionClientVersion(getPeerExtensions().getExtensionClientVersion());
                                    recorder.setExtensionTcpListenPort(getPeerExtensions().getExtensionTcpListenPort());
                                    recorder.setExtensionMaxOutstandingRequests(getPeerExtensions().getExtensionMaxOutstandingRequests());
                                    recorder.setExtensions(getPeerExtensions());
                                }
                                case REQUEST -> {
                                    recorder.setRequest(true);
                                }
                                case PIECE -> {
                                    recorder.setPiece(true);
                                }
                                case SUGGEST_PIECE -> {
                                    recorder.setSuggestPieces(true);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnect(@NotNull Identity identity) {
                        super.onConnect(identity);
                        recorder.setPeerId(identity.getIDString());
                        recorder.setPeerIdHex(ByteUtil.bytesToHex(identity.getID()));
                    }

                    @Override
                    public void onClose(@NotNull Throwable throwable) {
                        super.onClose(throwable);
                        latch.countDown();
                    }
                };
                peer.isReady = true; // force ready
                peerChannel.connect(peerAddress).get(10, TimeUnit.SECONDS);
                peer.establishOutgoing(peerAddress, Hash.of(infoHash)).get();
                Thread.sleep(3000);
                peer.getProtocol().send(new MessageImpl(MessageType.HAVE_NONE));
                Thread.sleep(3000);
                peer.getProtocol().send(new MessageImpl(MessageType.HAVE_ALL));
                Thread.sleep(3000);
                peer.getProtocol().send(new MessageImpl(MessageType.INTERESTED));
                latch.await(15,TimeUnit.SECONDS);
                peer.close();
            } catch (Throwable throwable) {
                recorder.setThrowable(throwable.getMessage());
            }
            return recorder;
        });

    }

    private Extensions extensions() {
        Extensions extensions = new Extensions();
        extensions.setFastPeers(true);
        extensions.setExtensionProtocol(true);
        extensions.setExtensionMessages(Map.of(
                "ut_metadata", 10,
                "lt_donthave", 20
        ));
        extensions.setExtensionClientVersion(clientName);
        extensions.setExtensionMaxOutstandingRequests(100);
        return extensions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YoukaiResult {
        private Boolean choke = true;
        private Boolean interested;
        private Boolean have;
        private Set<Integer> havePieces = new CopyOnWriteArraySet<>();
        private Boolean bitfield;
        private Integer bitfieldByteCount;
        private Boolean request;
        private Boolean piece;
        private Boolean cancel;
        private Boolean port;
        private Boolean haveAll;
        private Boolean haveNone;
        private Boolean suggestPieces;
        private Boolean rejectRequest;
        private Boolean allowFast;
        private Boolean extended;
        private String throwable;
        private String extensionClientVersion;
        private Integer extensionTcpListenPort;
        private Integer extensionMaxOutstandingRequests;
        private String peerId;
        private String peerIdHex;
        private Extensions extensions;
    }

}
