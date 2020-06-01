DROP KEYSPACE IF EXISTS myRetail;

CREATE KEYSPACE IF NOT EXISTS myRetail WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1};

DROP TABLE IF EXISTS myRetail.product;

CREATE TABLE IF NOT EXISTS myRetail.product (
    id text,
    price decimal,
    gen_date timestamp,
PRIMARY KEY (id)
) WITH bloom_filter_fp_chance = 0.1
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'enabled': 'true', 'tombstone_compaction_interval': '3600', 'tombstone_threshold': '0.01', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';
