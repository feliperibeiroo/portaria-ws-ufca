package com.portaria.portaria_ws.repository;

import com.portaria.portaria_ws.entity.CondominioEntity;
import com.portaria.portaria_ws.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Long> {

    List<EmpresaEntity> getEmpresasByIdCondominio(Long idCondominio);
}
