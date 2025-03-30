package com.portaria.portaria_ws.repository;

import com.portaria.portaria_ws.dto.response.AccessResponse;
import com.portaria.portaria_ws.entity.VisitaEntity;
import com.portaria.portaria_ws.entity.VisitanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitanteRepository extends JpaRepository<VisitanteEntity, Long> {

    VisitanteEntity getVisitanteByCpf(String cpf);

    @Query(nativeQuery = true, value = """
            select * from apl_catraca.visitas
            where id_empresa = :idEmpresa
            order by id desc
            offset :first limit :max
    """)
    List<VisitaEntity> getVisitasByIdEmpresa(Long idEmpresa, Long first, Long max);

    @Query(nativeQuery = true, value = "select count(*) from apl_catraca.visitas where id_empresa = :idEmpresa")
    Long getVisitasByIdEmpresaCount(Long idEmpresa);

    @Query(nativeQuery = true, value = """
            select vv.qrcode qr_code, v2.cpf, v2.nome_completo nome, vv.is_acompanhante from apl_catraca.visitas v
            inner join apl_catraca.visita_visitante vv on v.id = vv.id_visita
            inner join apl_catraca.visitantes v2 on v2.id = vv.id_visitante
            where v2.cpf = :cpf and v.id = :id_visita
    """)
    AccessResponse getAccessResponseByCpfAndIdVisita(@Param("cpf") String cpf, @Param("id_visita") Long idVisita);

    @Query(nativeQuery = true, value = """
        select v.* from apl_catraca.visitas v
        inner join apl_catraca.visita_visitante vv on v.id = vv.id_visita
        inner join apl_catraca.visitantes v2 on v2.id = vv.id_visitante
        where v2.cpf = :cpf and v.id_empresa = :id_empresa and (v.hor_final is null or v.hor_final > now())
        order by v.hor_inicio desc limit 1
    """)
    VisitaEntity getVisitaAtualByCpfAndIdEmpresa(@Param("cpf") String cpf, @Param("id_empresa") Long idEmpresa);

    @Query(nativeQuery = true, value = """
        select v.* from apl_catraca.visitas v
        inner join apl_catraca.visita_visitante vv on v.id = vv.id_visita
        inner join apl_catraca.visitantes v2 on v2.id = vv.id_visitante
        where v2.cpf = :cpf and (v.hor_final is null or v.hor_final > now())
        order by v.hor_inicio desc limit 1
    """)
    VisitaEntity getVisitaAtualByCpf(@Param("cpf") String cpf);
}
