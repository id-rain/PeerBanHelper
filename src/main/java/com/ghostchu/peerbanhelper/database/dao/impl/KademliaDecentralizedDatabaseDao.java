package com.ghostchu.peerbanhelper.database.dao.impl;

import com.ghostchu.peerbanhelper.database.Database;
import com.ghostchu.peerbanhelper.database.dao.AbstractPBHDao;
import com.ghostchu.peerbanhelper.database.table.KademliaDecentralizedRecordEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.SQLException;

@Component
@Slf4j
public class KademliaDecentralizedDatabaseDao extends AbstractPBHDao<KademliaDecentralizedRecordEntity, BigInteger> {
    public KademliaDecentralizedDatabaseDao(@Autowired Database database) throws SQLException {
        super(database.getDataSource(), KademliaDecentralizedRecordEntity.class);
    }
}