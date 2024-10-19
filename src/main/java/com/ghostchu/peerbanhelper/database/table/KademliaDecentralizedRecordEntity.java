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
@DatabaseTable(tableName = "kademlia_decentralized_db")
public final class KademliaDecentralizedRecordEntity {
    @DatabaseField(id = true, index = true)
    private BigInteger key;
    @DatabaseField
    private String value;
    @DatabaseField
    private Timestamp lastPublished;
}