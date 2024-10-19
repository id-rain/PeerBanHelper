package com.ghostchu.peerbanhelper.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@DatabaseTable(tableName = "kademlia_peers")
public final class KademliaPeerRecordEntity {
    @DatabaseField(id = true, index = true)
    private BigInteger nodeId;
    @DatabaseField(index = true)
    private String host;
    @DatabaseField
    private Integer port;
    @DatabaseField
    private Timestamp lastSeenAt;
}