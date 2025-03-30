package com.portaria.portaria_ws.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visita_visitante", schema = "apl_catraca")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitaVisitanteEntity {

    @EmbeddedId
    VisitaVisitanteKey id;

    @ManyToOne
    @MapsId("idVisita")
    @JoinColumn(name = "id_visita")
    private VisitaEntity visita;

    @ManyToOne
    @MapsId("idVisitante")
    @JoinColumn(name = "id_visitante")
    private VisitanteEntity visitante;

    private Boolean isAcompanhante;
    private String qrcode;

}

