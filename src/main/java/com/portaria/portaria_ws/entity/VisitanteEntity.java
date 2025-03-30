package com.portaria.portaria_ws.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "visitantes", schema = "apl_catraca")
public class VisitanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visitantes_id_seq")
    @SequenceGenerator(
            name = "visitantes_id_seq",
            schema = "apl_catraca",
            sequenceName = "visitantes_id_seq",
            initialValue = 1,
            allocationSize = 1)
    private Long id;
    private String cpf;
    private String nomeCompleto;

}
