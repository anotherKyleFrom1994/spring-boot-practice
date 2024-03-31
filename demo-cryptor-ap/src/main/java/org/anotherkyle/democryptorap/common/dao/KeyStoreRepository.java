package org.anotherkyle.democryptorap.common.dao;

import org.anotherkyle.democryptorap.common.model.vo.db.KeyStore;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface KeyStoreRepository extends R2dbcRepository<KeyStore, Long> {
    @Query("SELECT nextval('keystore_id_seq')")
    Mono<Long> getNextId();

    @Query(value = "select *\n" +
            "from (select *, rank() over (partition by ks.id order by ks.created desc) as r from keystore ks) \"t1\"\n" +
            "where t1.r <= :rank")
    Flux<KeyStore> findLatestNRecords(int rank);
}
