package com.portaria.portaria_ws.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "visitas", schema = "apl_catraca")
@ToString(exclude = { "acessos" })
public class VisitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visitas_id_seq")
    @SequenceGenerator(
            name = "visitas_id_seq",
            schema = "apl_catraca",
            sequenceName = "visitas_id_seq",
            initialValue = 1,
            allocationSize = 1)
    private Long id;
    private Long idEmpresa;
    private Timestamp horSolicitacao;
    private Timestamp horInicio;
    private Timestamp horFinal;
    private String motivo;
    private String anfitriao;
    private String idAtendente;
    private Boolean autorizada;
    private Timestamp horAtendimento;

    @OneToMany(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitaVisitanteEntity> acessos;

}
