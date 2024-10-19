package com.ghostchu.peerbanhelper.database.dao.impl;

import com.ghostchu.peerbanhelper.database.Database;
import com.ghostchu.peerbanhelper.database.dao.AbstractPBHDao;
import com.ghostchu.peerbanhelper.database.table.KademliaPeerRecordEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class KademliaPeerRecordDao extends AbstractPBHDao<KademliaPeerRecordEntity, String> {
    public KademliaPeerRecordDao(@Autowired Database database) throws SQLException {
        super(database.getDataSource(), KademliaPeerRecordEntity.class);
    }

    public List<KademliaPeerRecordEntity> fetchPeers(long size) {
        try {
            return queryBuilder().limit(size).orderBy("lastSeenAt", false).query();
        } catch (SQLException e) {
            log.warn("Unable fetch peers from database.", e);
            return Collections.emptyList();
        }
    }
//
//    public void save(List<ExternalNode<BigInteger, NettyConnectionInfo>> routingTable) throws SQLException {
//        callBatchTasks(() -> {
//            routingTable.forEach(node -> {
//                try {
//                    createOrUpdate(new KademliaPeerRecordEntity(node.getId(), node.getConnectionInfo().getHost(), node.getConnectionInfo().getPort(), new Timestamp(System.currentTimeMillis())));
//                } catch (SQLException e) {
//                    log.warn("Unable to save Kademlia peer records");
//                }
//            });
//            return null;
//        });
//    }
}