package com.portaria.portaria_ws.repository;

import com.portaria.portaria_ws.entity.CondominioEntity;
import com.portaria.portaria_ws.entity.VisitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CondominioRepository extends JpaRepository<CondominioEntity, Long> {

}
