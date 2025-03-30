package com.portaria.portaria_ws.repository;

import com.portaria.portaria_ws.entity.VisitaVisitanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitaVisitanteRepository extends JpaRepository<VisitaVisitanteEntity, Long> {

    @Query(nativeQuery = true, value = "select * from apl_catraca.visita_visitante vv where id_visita = :id_visita")
    List<VisitaVisitanteEntity> getVisitaVisitantesByIdVisita(@Param("id_visita") Long idVisita);

    @Query(nativeQuery = true, value = """
        select * from apl_catraca.visita_visitante vv where id_visita = :id_visita
        and vv.id_visitante = :id_visitante
    """)
    VisitaVisitanteEntity getVisitaVisitantesByIdVisitaAndIdVisitante(
        @Param("id_visita") Long idVisita,
        @Param("id_visitante") Long idVisitante
    );

}
