package com.ghostchu.peerbanhelper.decentralized.kademlia;


import com.ghostchu.peerbanhelper.database.dao.impl.KademliaDecentralizedDatabaseDao;
import com.ghostchu.peerbanhelper.database.table.KademliaDecentralizedRecordEntity;
import de.cgrotz.kademlia.node.Key;
import de.cgrotz.kademlia.storage.LocalStorage;
import de.cgrotz.kademlia.storage.Value;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KademliaMemoryRepository implements LocalStorage {
    @Autowired
    private KademliaDecentralizedDatabaseDao dao;

    @SneakyThrows
    @Override
    public void put(Key key, Value value) {
        dao.createOrUpdate(new KademliaDecentralizedRecordEntity(
                key.getKey(), value.getContent(), new Timestamp(value.getLastPublished())
        ));
    }

    @SneakyThrows
    @Override
    public Value get(Key key) {
        var data = dao.queryForId(key.getKey());
        if (data == null) return null;
        return Value.builder().content(data.getValue()).lastPublished(data.getLastPublished().getTime()).build();
    }

    @SneakyThrows
    @Override
    public boolean contains(Key key) {
        return dao.idExists(key.getKey());
    }

    @SneakyThrows
    @Override
    public List<Key> getKeysBeforeTimestamp(long l) {
        var list = dao.queryBuilder().where().lt("lastPublished", l).query();
        return list.stream().map(e -> new Key(e.getKey())).collect(Collectors.toList());
    }
}
