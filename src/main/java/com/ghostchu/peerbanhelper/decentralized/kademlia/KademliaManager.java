package com.ghostchu.peerbanhelper.decentralized.kademlia;

import com.ghostchu.peerbanhelper.Main;
import com.ghostchu.peerbanhelper.database.dao.impl.KademliaDecentralizedDatabaseDao;
import com.ghostchu.peerbanhelper.database.dao.impl.KademliaPeerRecordDao;
import com.ghostchu.peerbanhelper.database.table.KademliaPeerRecordEntity;
import com.ghostchu.peerbanhelper.text.Lang;
import com.ghostchu.peerbanhelper.util.MiscUtil;
import com.ghostchu.peerbanhelper.util.encrypt.RSAUtils;
import com.ghostchu.simplereloadlib.ReloadResult;
import com.ghostchu.simplereloadlib.Reloadable;
import de.cgrotz.kademlia.Configuration;
import de.cgrotz.kademlia.Kademlia;
import de.cgrotz.kademlia.config.UdpListener;
import de.cgrotz.kademlia.node.Key;
import de.cgrotz.kademlia.node.Node;
import de.cgrotz.kademlia.routing.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ghostchu.peerbanhelper.text.TextManager.tlUI;

@Component
@Slf4j
public class KademliaManager implements Reloadable {
    private static final ScheduledExecutorService cronJobs = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
    private final File dataDirectory;
    private final KademliaDecentralizedDatabaseDao kademliaDecentralizedDatabaseDao;
    private final ScheduledExecutorService scheduledThreadPool;
    private final KademliaPeerRecordDao kademliaPeerRecordDao;
    private final KademliaMemoryRepository kademliaMemoryRepository;
    private String privateKey;
    private String publicKey;
    private Kademlia kad;

    public KademliaManager(KademliaDecentralizedDatabaseDao kademliaDecentralizedDatabaseDao, KademliaPeerRecordDao kademliaPeerRecordDao, KademliaMemoryRepository kademliaMemoryRepository) throws Exception {
        this.dataDirectory = new File(Main.getDataDirectory(), "decentralized");
        this.kademliaDecentralizedDatabaseDao = kademliaDecentralizedDatabaseDao;
        this.kademliaPeerRecordDao = kademliaPeerRecordDao;
        this.kademliaMemoryRepository = kademliaMemoryRepository;
        reloadConfig();
        launchKadNode(MiscUtil.randomPort());
        Main.getReloadManager().register(this);
        this.scheduledThreadPool = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
        this.scheduledThreadPool.scheduleWithFixedDelay(this::save, 5, 10, TimeUnit.MINUTES);
    }

    @SneakyThrows
    private void save() {
        List<Node> peers = new ArrayList<>();
        for (Bucket bucket : this.kad.getRoutingTable().getBuckets()) {
            peers.addAll(bucket.getNodes());
        }
        kademliaPeerRecordDao.callBatchTasks(() -> {
            peers.forEach(node -> node.getAdvertisedListeners().forEach(listener -> {
                try {
                    kademliaPeerRecordDao.createOrUpdate(new KademliaPeerRecordEntity(
                            node.getId().getKey(),
                            ((UdpListener) listener).getHost(),
                            ((UdpListener) listener).getPort(),
                            new Timestamp(node.getLastSeen())
                    ));
                } catch (SQLException e) {
                    log.warn("Unable to save/update the kademlia peers into database", e);
                }
            }));
            return null;
        });
    }

    @SneakyThrows
    private void launchKadNode(int port) {
        log.info(tlUI(Lang.KAD_STARTING));
        byte[] id = new byte[Key.ID_LENGTH / 8];
        System.arraycopy(publicKey.getBytes(StandardCharsets.ISO_8859_1), 0, id, 0, id.length);
        log.info(tlUI(Lang.KAD_YOUR_PEERID, new String(id, StandardCharsets.ISO_8859_1), new Key(id).getKey().toString(16)));
        this.kad = new Kademlia(Configuration.builder()
                .listeners(List.of(new UdpListener("0.0.0.0", port)))
                .nodeId(new Key(id))
                .build(), kademliaMemoryRepository);
        cronJobs.scheduleWithFixedDelay(kad::refreshBuckets, 5, 60, TimeUnit.MINUTES);
        cronJobs.scheduleWithFixedDelay(kad::republishKeys, 1, 5, TimeUnit.MINUTES);
        List<KademliaPeerRecordEntity> bootstrapNodes = new ArrayList<>();
        bootstrapNodes.add(new KademliaPeerRecordEntity(BigInteger.ZERO, "pbh-kad-bootstrap.ghostchu-services.top", 11455, new Timestamp(System.currentTimeMillis())));
        bootstrapNodes.addAll(kademliaPeerRecordDao.fetchPeers(15));
        Thread.ofVirtual().start(() -> {
            bootstrapNodes.forEach(peer -> this.kad.bootstrap(Node.builder().advertisedListener(new UdpListener(peer.getHost(), peer.getPort())).build()));
            this.kad.republishKeys();
            log.info(tlUI(Lang.KAD_STARTED_UP, getConnectedNodeCount()));
        });
    }

    public long getConnectedNodeCount() {
        long base = this.kad.getRoutingTable().getBucketStream().mapToLong(bucket -> bucket.getNodes().size()).sum();
        long replacements = this.kad.getRoutingTable().getBucketStream().mapToLong(bucket -> bucket.getReplacementNodes().size()).sum();
        return base + replacements;

    }

    @Override
    public ReloadResult reloadModule() throws Exception {
        reloadConfig();
        return Reloadable.super.reloadModule();
    }

    private void reloadConfig() throws Exception {
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        File privateKeyFile = new File(dataDirectory, "kademlia-identity.key");
        File publicKeyFile = new File(dataDirectory, "kademlia-identity.pub");
        if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
            var keyPair = RSAUtils.genKeyPair();
            Files.writeString(privateKeyFile.toPath(), Base64.getEncoder().encodeToString(((PrivateKey) keyPair.get(RSAUtils.PRIVATE_KEY)).getEncoded()));
            Files.writeString(publicKeyFile.toPath(), Base64.getEncoder().encodeToString(((PublicKey) keyPair.get(RSAUtils.PUBLIC_KEY)).getEncoded()));
        }
        privateKey = Files.readString(privateKeyFile.toPath());
        publicKey = Files.readString(publicKeyFile.toPath());
    }

}
