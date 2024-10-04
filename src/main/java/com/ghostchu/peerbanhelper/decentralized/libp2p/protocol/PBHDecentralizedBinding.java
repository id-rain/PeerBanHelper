package com.ghostchu.peerbanhelper.decentralized.libp2p.protocol;

import io.libp2p.core.multistream.StrictProtocolBinding;

public class PBHDecentralizedBinding extends StrictProtocolBinding<PBHDecentralizedP2PController> {
    public PBHDecentralizedBinding(PBHDecentralizedProtocol protocol) {
        super(protocol.getAnnounce(), protocol);
    }
}
