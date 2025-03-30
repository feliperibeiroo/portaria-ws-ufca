package com.portaria.portaria_ws.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitaVisitanteKey implements Serializable {

    private Long idVisita;
    private Long idVisitante;

    // equals e hashCode para chave composta
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitaVisitanteKey that = (VisitaVisitanteKey) o;
        return Objects.equals(idVisita, that.idVisita) &&
                Objects.equals(idVisitante, that.idVisitante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVisita, idVisitante);
    }

}
