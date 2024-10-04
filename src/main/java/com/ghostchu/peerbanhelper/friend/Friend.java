package com.ghostchu.peerbanhelper.friend;

import com.ghostchu.peerbanhelper.Main;
import com.ghostchu.peerbanhelper.decentralized.ipfs.IPFS;
import com.ghostchu.peerbanhelper.util.json.JsonUtil;
import io.ipfs.multihash.Multihash;
import io.libp2p.core.PeerId;
import io.netty.handler.codec.http.*;
import lombok.Getter;
import org.peergos.EmbeddedIpfs;
import org.peergos.net.ConnectionException;
import org.peergos.protocol.http.HttpProtocol;

import java.util.Objects;

public class Friend {
    @Getter
    private final String peerId;
    private final transient IPFS ipfs;
    @Getter
    private long lastAttemptConnectTime;
    @Getter
    private long lastCommunicationTime;
    @Getter
    private String lastRecordedPBHVersion;
    @Getter
    private String lastRecordedConnectionStatus;
    private transient HttpProtocol.HttpController controller;

    public Friend(IPFS ipfs, String peerId, long lastAttemptConnectTime, long lastCommunicationTime, String lastRecordedPBHVersion) {
        this.ipfs = ipfs;
        this.peerId = peerId;
        this.lastAttemptConnectTime = lastAttemptConnectTime;
        this.lastCommunicationTime = lastCommunicationTime;
        this.lastRecordedPBHVersion = lastRecordedPBHVersion;
    }

    public HttpProtocol.HttpController connectAndGetController() {
        if (!connect()) {
            return null;
        }
        return controller;
    }

    public boolean connect() {
        if (controller != null) {
            FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/heartbeat");
            httpRequest.headers().add("User-Agent", Main.getUserAgent());
            if (controller.send(httpRequest).join().status() == HttpResponseStatus.NO_CONTENT) {
                lastCommunicationTime = System.currentTimeMillis();
                lastRecordedConnectionStatus = "OK";
                return true;
            }
        }
        lastAttemptConnectTime = System.currentTimeMillis();
        var ipfs = this.ipfs.getIpfs();
        if (ipfs == null) {
            lastRecordedConnectionStatus = "IPFS component not ready, try again later.";
            return false;
        }
        try {
            var addrs = EmbeddedIpfs.getAddresses(ipfs.node, ipfs.dht, Multihash.fromBase58(peerId));
            HttpProtocol.HttpController proxier = ipfs.p2pHttp.get().dial(ipfs.node, PeerId.fromBase58(peerId), addrs).getController().join();
            FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/manifest");
            var response = proxier.send(httpRequest).join();
            if (response.status() == HttpResponseStatus.OK) {
                lastCommunicationTime = System.currentTimeMillis();
                var manifest = JsonUtil.standard().fromJson(response.content().toString(), Manifest.class);
                lastRecordedPBHVersion = manifest.version();
                lastRecordedConnectionStatus = "OK";
                this.controller = proxier;
                return true;
            } else {
                lastRecordedConnectionStatus = "Connected, but no excepted response received.";
                return false;
            }
        } catch (ConnectionException e) {
            lastRecordedConnectionStatus = e.getMessage();
            return false;
        }
    }

    public record Manifest(
            String version
    ) {

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Friend friend = (Friend) object;
        return Objects.equals(peerId, friend.peerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(peerId);
    }
}
