package com.ghostchu.peerbanhelper.decentralized.ipfs;

import com.ghostchu.peerbanhelper.Main;
import io.ipfs.cid.Cid;
import io.ipfs.multiaddr.MultiAddress;
import io.libp2p.core.PeerId;
import io.libp2p.core.crypto.PrivKey;
import org.peergos.*;
import org.peergos.blockstore.FileBlockstore;
import org.peergos.config.IdentitySection;
import org.peergos.protocol.dht.RamRecordStore;
import org.peergos.protocol.http.HttpProtocol;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class IPFS {
    private static File dataDirectory = new File(Main.getDataDirectory(), "ipfs-blockstore");

    static {
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
    }

    public void init() {
        List<MultiAddress> swarmAddresses = List.of(
                new MultiAddress("/ip6/::/tcp/4001")
                , new MultiAddress("/ip4/0.0.0.0/tcp/4001"));

        List<MultiAddress> bootstrapAddresses = List.of(
                new MultiAddress("/dnsaddr/bootstrap.libp2p.io/p2p/QmQCU2EcMqAqQPR2i9bChDtGNJchTbq5TbXJJ16u19uLTa"),
                new MultiAddress("/dnsaddr/bootstrap.libp2p.io/p2p/QmbLHAnMoJPWSCR5Zhtx6BHJX9KiKNN6tpvbUcqanj75Nb"),
                new MultiAddress("/dnsaddr/bootstrap.libp2p.io/p2p/QmcZf59bWwK5XFi76CZX8cbJ4BhTzzA3gU1ZjYZcYW3dwt"),
                new MultiAddress("/ip4/104.131.131.82/tcp/4001/p2p/QmaCpDMGvV2BGHeYERUEnRQAwe3N8SzbUtfsmvsqQLuvuJ"),
                new MultiAddress("/ip4/25.196.147.100/tcp/4001/p2p/QmaMqSwWShsPg2RbredZtoneFjXhim7AQkqbLxib45Lx4S")
        );
        BlockRequestAuthoriser authoriser = (cid, peerid, auth) -> CompletableFuture.completedFuture(true);
        HostBuilder builder = new HostBuilder().generateIdentity();
        PrivKey privKey = builder.getPrivateKey();
        PeerId peerId = builder.getPeerId();
        IdentitySection identity = new IdentitySection(privKey.bytes(), peerId);
        boolean provideBlocks = true;
        SocketAddress httpTarget = new InetSocketAddress("localhost", 10000);
        Optional<HttpProtocol.HttpRequestProcessor> httpProxyTarget =
                Optional.of((s, req, h) -> HttpProtocol.proxyRequest(req, httpTarget, h));
        EmbeddedIpfs ipfs = EmbeddedIpfs.build(new RamRecordStore(),
                new FileBlockstore(dataDirectory.toPath()),
                provideBlocks,
                swarmAddresses,
                bootstrapAddresses,
                identity,
                authoriser,
                httpProxyTarget
        );
        ipfs.start();
        List<Want> wants = List.of(new Want(Cid.decode("zdpuAwfJrGYtiGFDcSV3rDpaUrqCtQZRxMjdC6Eq9PNqLqTGg")));
        Set<PeerId> retrieveFrom = Set.of(PeerId.fromBase58("QmVdFZgHnEgcedCS2G2ZNiEN59LuVrnRm7z3yXtEBv2XiF"));
        boolean addToLocal = true;
        List<HashedBlock> blocks = ipfs.getBlocks(wants, retrieveFrom, addToLocal);
        byte[] data = blocks.get(0).block;
    }
}
