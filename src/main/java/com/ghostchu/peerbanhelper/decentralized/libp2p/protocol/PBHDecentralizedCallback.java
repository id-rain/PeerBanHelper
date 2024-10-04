package com.ghostchu.peerbanhelper.decentralized.libp2p.protocol;

import io.libp2p.core.PeerId;
import io.libp2p.core.multiformats.Multiaddr;

public interface PBHDecentralizedCallback {
    void onChatMessage(PeerId peerId, Multiaddr peerAddr, String message);
}
