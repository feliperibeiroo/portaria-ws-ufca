package com.portaria.portaria_ws.repository;

import com.portaria.portaria_ws.entity.VisitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitaRepository extends JpaRepository<VisitaEntity, Long> {

    @Query(nativeQuery = true, value = """
            select v.* from apl_catraca.visitas v
            inner join apl_catraca.visita_visitante vv on vv.id_visita = v.id
            inner join apl_catraca.visitantes v2 on vv.id_visitante = v2.id
            where v2.cpf = :username and v.id_empresa = :id_empresa
            and (v.hor_final is null or v.hor_final > now())
    """)
    VisitaEntity getActiveVisitaByCpfAndIdEmpresa(@Param("username") String username, @Param("id_empresa") Long idEmpresa);

    @Query(nativeQuery = true, value = "select * from apl_catraca.visitas where id = :id and id_empresa = :id_empresa")
    VisitaEntity getVisitaByIdAndIdEmpresa(@Param("id") Long id, @Param("id_empresa") Long idEmpresa);
}
