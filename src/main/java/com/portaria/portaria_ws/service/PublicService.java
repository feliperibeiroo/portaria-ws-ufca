package com.portaria.portaria_ws.service;

import com.portaria.portaria_ws.entity.CondominioEntity;
import com.portaria.portaria_ws.dto.response.CondominioResponse;
import com.portaria.portaria_ws.dto.response.EmpresaResponse;
import com.portaria.portaria_ws.entity.EmpresaEntity;
import com.portaria.portaria_ws.repository.CondominioRepository;
import com.portaria.portaria_ws.repository.EmpresaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublicService {

    @Autowired
    CondominioRepository condominioRepository;

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    ModelMapper modelMapper;

    public ResponseEntity<List<CondominioResponse>> getCondominios() {
        List<CondominioEntity> condominios = condominioRepository.findAll();
        return ResponseEntity.ok(condominios.stream()
            .map(condominio -> modelMapper.map(condominio, CondominioResponse.class))
            .toList());
    }

    public ResponseEntity<CondominioResponse> getCondominioById(Long idCondominio) {
        Optional<CondominioEntity> condominio = condominioRepository.findById(idCondominio);
        if (condominio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapper.map(condominio.get(), CondominioResponse.class));
    }

    public ResponseEntity<List<EmpresaResponse>> getEmpresasByCondominio(Long idCondominio) {
        List<EmpresaEntity> empresas = empresaRepository.getEmpresasByIdCondominio(idCondominio);
        return ResponseEntity.ok(empresas.stream()
            .map(emp -> {
                EmpresaResponse empresaResponse = modelMapper.map(emp, EmpresaResponse.class);
                empresaResponse.setCondominio(
                        modelMapper.map(condominioRepository.getReferenceById(
                                emp.getIdCondominio()),
                                CondominioResponse.class
                        ));
                return empresaResponse;
            })
            .toList());
    }

    public ResponseEntity<EmpresaResponse> getEmpresaById(Long idEmpresa) {
        Optional<EmpresaEntity> empresa = empresaRepository.findById(idEmpresa);
        if (empresa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        EmpresaResponse empresaResponse = modelMapper.map(empresa.get(), EmpresaResponse.class);
        empresaResponse.setCondominio(modelMapper.map(condominioRepository.getReferenceById(
                empresa.get().getIdCondominio()),
                CondominioResponse.class)
        );
        return ResponseEntity.ok(empresaResponse);
    }
}
