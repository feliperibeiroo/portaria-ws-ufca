package com.portaria.portaria_ws.service;

import com.portaria.portaria_ws.dto.request.VisitaCreateRequest;
import com.portaria.portaria_ws.dto.response.*;
import com.portaria.portaria_ws.entity.*;
import com.portaria.portaria_ws.feign.clients.AppCatracaClient;
import com.portaria.portaria_ws.models.enums.TicketType;
import com.portaria.portaria_ws.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class VisitanteService {

    @Autowired
    VisitaRepository visitaRepository;

    @Autowired
    VisitanteRepository visitanteRepository;

    @Autowired
    AppCatracaClient appCatracaClient;

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    VisitaVisitanteRepository visitaVisitanteRepository;

    @Autowired  
    ModelMapper modelMapper;

    @Value("${app-catraca.application-id}")
    String applicationId;

    @Value("${app-catraca.application-version}")
    String applicationVersion;

    @Transactional
    public ResponseEntity<Void> createVisita(VisitaCreateRequest visitaCreateRequest) {

        VisitaEntity visitaEntity;
        visitaEntity = VisitaEntity.builder()
                .anfitriao(visitaCreateRequest.getAnfitriao())
                .motivo(visitaCreateRequest.getMotivo())
                .horSolicitacao(new Timestamp(System.currentTimeMillis()))
                .idEmpresa(visitaCreateRequest.getIdEmpresa())
                .build();

        // Salvar visita
        visitaEntity = visitaRepository.save(visitaEntity);

        VisitaEntity finalVisitaEntity = visitaEntity;

        if (visitaCreateRequest.getAcessos().size()==0)
            throw new RuntimeException("Nenhum acesso foi solicitado");

        // Salvar visitantes
        List<VisitaVisitanteEntity> visitaVisitanteEntities = visitaCreateRequest.getAcessos().stream().map(i -> {

            // Verificar se já existe visita ativa para o visitante na empresa
            // (um visitante não pode ter duas visitas ativas na mesma empresa)
            VisitaEntity visita = visitaRepository.getActiveVisitaByCpfAndIdEmpresa(
                    i.getCpf(), visitaCreateRequest.getIdEmpresa()
            );

            if (visita!=null) {
                throw new RuntimeException("Já existe uma visita não finalizada para o usuário nesta empresa");
            }

            // Verificar se já existe um visitante no banco com o mesmo CPF:
            // - Caso exista, obter visitante do banco e mapear a modificações do visitante
            // - Caso não exista, salvar novo visitante no banco e obter a entidade salva.
            VisitanteEntity visitante = visitanteRepository.getVisitanteByCpf(i.getCpf());
            if (visitante==null) {
                visitante = visitanteRepository.save(VisitanteEntity.builder()
                        .cpf(i.getCpf())
                        .nomeCompleto(i.getNome())
                        .build());
            } else {
                modelMapper.map(i, visitante);
            }

            return VisitaVisitanteEntity.builder()
                    .id(new VisitaVisitanteKey(finalVisitaEntity.getId(), visitante.getId()))
                    .visitante(visitante)
                    .visita(finalVisitaEntity)
                    .isAcompanhante(i.getIsAcompanhante())
                    .build();

        }).toList();

        visitaVisitanteRepository.saveAll(visitaVisitanteEntities);

        return ResponseEntity.ok().build();

    }

    @Transactional
    public ResponseEntity<String> aprovarReprovarVisita(Long idEmpresa, String idAtendente, Long idVisita, Timestamp horFinal, Boolean aprovado) {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Obter visita pelo idVisita
        VisitaEntity visitaEntity = visitaRepository.getVisitaByIdAndIdEmpresa(idVisita, idEmpresa);
        if (visitaEntity==null) {
            return ResponseEntity.notFound().build();
        }

        if (horFinal.getTime()<now.getTime()) {
            return ResponseEntity.status(500).body("Hora final não pode ser menor que a hora atual");
        }

        if (visitaEntity.getAutorizada()!=null) {
            return ResponseEntity.status(500).body("Visita já foi autorizada/reprovada");
        }

        List<VisitaVisitanteEntity> acessos = visitaEntity.getAcessos();

        if (aprovado) {
            for (int i=0; i<acessos.size(); i++) {

                ResponseEntity<TicketResponse> ticketResponse = appCatracaClient.createNewTicket(
                    idEmpresa,
                    acessos.get(i).getVisitante().getCpf(),
                    horFinal,
                    applicationId,
                    applicationVersion,
                    TicketType.FIXO
                );

                if (ticketResponse.getStatusCode().is2xxSuccessful()) {
                    acessos.get(i).setQrcode(ticketResponse.getBody().getToken());
                } else {
                    throw new RuntimeException("Falha ao realizar chamada REST");
                }

            }
        }

        visitaEntity.setAcessos(acessos);

        // Autorizar a visita
        visitaEntity.setAutorizada(aprovado);

        // Salvar horário do atendimento
        visitaEntity.setHorAtendimento(now);

        // Salvar horário do início da visita
        visitaEntity.setHorInicio(now);

        // Salvar horário do final da visita
        visitaEntity.setHorFinal(aprovado ? horFinal : now);

        // Salvar o ID do atendente
        visitaEntity.setIdAtendente(idAtendente);

        visitaRepository.save(visitaEntity);
        return ResponseEntity.ok().build();

    }

    @Transactional
    public ResponseEntity<String> encerrarVisita(Long idEmpresa, Long idVisita) {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Obter visita pelo idVisita
        VisitaEntity visitaEntity = visitaRepository.getVisitaByIdAndIdEmpresa(idVisita, idEmpresa);
        if (visitaEntity==null) {
            return ResponseEntity.notFound().build();
        }

        // Se visita não foi autorizada ou já estiver expirada, retornar ACCEPTED
        if (!visitaEntity.getAutorizada() || visitaEntity.getHorFinal().getTime()<now.getTime()) {
            return ResponseEntity.status(500).body("Visita já foi reprovada ou já encerrou");
        }

        // Setar now para a visita
        visitaEntity.setHorFinal(now);

        List<VisitaVisitanteEntity> acessos = visitaEntity.getAcessos();

        for (int i=0; i<acessos.size(); i++) {
            ResponseEntity<Void> response = appCatracaClient.updateDuracaoTicket(acessos.get(i).getQrcode(), now);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Falha ao realizar chamada REST");
            }
        }

        visitaEntity.setAcessos(acessos);

        visitaRepository.save(visitaEntity);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<String> prolongarVisita(Long idEmpresa, Long idVisita, Timestamp horFinal) {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Obter visita pelo idVisita
        VisitaEntity visitaEntity = visitaRepository.getVisitaByIdAndIdEmpresa(idVisita, idEmpresa);
        if (visitaEntity==null) {
            return ResponseEntity.notFound().build();
        }

        if (horFinal.getTime()<now.getTime()) {
            return ResponseEntity.status(500).body("Hora final não pode ser menor que a hora atual");
        }

        // Se visita não foi autorizada ou tiver expirada há mais de 24h, retornar ACCEPTED.
        if (!visitaEntity.getAutorizada() || visitaEntity.getHorFinal().getTime()<(now.getTime()-24*60*60*1000)) {
            return ResponseEntity.status(500).body("Visita já foi reprovada ou expirou há mais de 24h");
        }

        // Setar novo horFinal
        visitaEntity.setHorFinal(horFinal);

        List<VisitaVisitanteEntity> acessos = visitaEntity.getAcessos();

        for (int i=0; i<acessos.size(); i++) {
            ResponseEntity<Void> response = appCatracaClient.updateDuracaoTicket(acessos.get(i).getQrcode(), horFinal);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Falha ao realizar chamada REST");
            }
        }

        visitaEntity.setAcessos(acessos);

        visitaRepository.save(visitaEntity);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<VisitanteResponse> getVisitanteByUsername(String cpf) {
        VisitanteEntity visitanteEntity = visitanteRepository.getVisitanteByCpf(cpf);

        if (visitanteEntity!=null)
            return ResponseEntity.ok(modelMapper.map(visitanteEntity, VisitanteResponse.class));

        return ResponseEntity.notFound().build();

    }

    public PageImpl<VisitaResponse> getVisitasByEmpresa(Long idEmpresa, Long first, Long max) {

        PageRequest pageable = PageRequest.of(
                Integer.parseInt(String.valueOf(first/max)),
                Integer.parseInt(String.valueOf(max)));

        // Obter entidades de visita
        List<VisitaEntity> visitas = visitanteRepository.getVisitasByIdEmpresa(idEmpresa, first, max);

        // Mapear para visitaResponse
        List<VisitaResponse> visitaResponses = visitas.stream().map(i ->
                modelMapper.map(i, VisitaResponse.class))
                .toList();

        // Mapear acessos
        visitaResponses.forEach(v -> {

            List<VisitaVisitanteEntity> visitaVisitanteEntities = visitaVisitanteRepository.getVisitaVisitantesByIdVisita(v.getId());

            // Mapear acessos
            v.setAcessos(visitaVisitanteEntities.stream()
                    .map(i -> AccessResponse.builder()
                            .cpf(i.getVisitante().getCpf())
                            .nome(i.getVisitante().getNomeCompleto())
                            .isAcompanhante(i.getIsAcompanhante())
                            .qrCode(i.getQrcode())
                            .build())
                    .toList());
        });

        // Obter o count total
        Long totalVisitas = visitanteRepository.getVisitasByIdEmpresaCount(idEmpresa);

        return new PageImpl<VisitaResponse>(visitaResponses, pageable, totalVisitas);

    }

    public ResponseEntity<VisitaResponse> getVisitaAtual(String cpf, Long idEmpresa) {

        // Obter visita ativa do usuário na empresa
        VisitaEntity visitaEntity = visitanteRepository.getVisitaAtualByCpfAndIdEmpresa(cpf, idEmpresa);

        if (visitaEntity==null) {
            return ResponseEntity.notFound().build();
        }

        // Mapear valores
        VisitaResponse visitaResponse = modelMapper.map(visitaEntity, VisitaResponse.class);

        List<VisitaVisitanteEntity> visitaVisitanteEntities = visitaVisitanteRepository.getVisitaVisitantesByIdVisita(visitaEntity.getId());

        // Mapear acessos
        visitaResponse.setAcessos(visitaVisitanteEntities.stream()
                .map(i -> AccessResponse.builder()
                        .cpf(i.getVisitante().getCpf())
                        .nome(i.getVisitante().getNomeCompleto())
                        .isAcompanhante(i.getIsAcompanhante())
                        .qrCode(i.getQrcode())
                        .build())
                .toList());

        return ResponseEntity.ok(visitaResponse);

    }

    public ResponseEntity<VisitaResponse> getVisitaAtual(String cpf) {
        // Obter visita ativa do usuário na empresa
        VisitaEntity visitaEntity = visitanteRepository.getVisitaAtualByCpf(cpf);

        if (visitaEntity==null) {
            return ResponseEntity.notFound().build();
        }

        // Mapear valores
        VisitaResponse visitaResponse = modelMapper.map(visitaEntity, VisitaResponse.class);

        List<VisitaVisitanteEntity> visitaVisitanteEntities = visitaVisitanteRepository.getVisitaVisitantesByIdVisita(visitaEntity.getId());

        // Mapear acessos
        visitaResponse.setAcessos(visitaVisitanteEntities.stream()
            .map(i -> AccessResponse.builder()
                .cpf(i.getVisitante().getCpf())
                .nome(i.getVisitante().getNomeCompleto())
                .isAcompanhante(i.getIsAcompanhante())
                .qrCode(i.getQrcode())
                .build())
            .toList());

        return ResponseEntity.ok(visitaResponse);
    }

    public ResponseEntity<List<ActivityFeignResponse>> getHistoricoVisitanteByVisita(Long idVisita, Long idVisitante, Long limit) {

        VisitaVisitanteEntity visitaVisitanteEntity =
            visitaVisitanteRepository.getVisitaVisitantesByIdVisitaAndIdVisitante(idVisita, idVisitante);

        if (visitaVisitanteEntity==null || visitaVisitanteEntity.getQrcode()==null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<List<ActivityFeignResponse>> response =
            appCatracaClient.getTicketActivity(visitaVisitanteEntity.getQrcode(), limit);

        if (!response.getStatusCode().is2xxSuccessful())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(response.getBody());

    }

    public ResponseEntity<VisitaResponse> getVisitaById(Long id) {

        // Obter visita ativa do usuário na empresa
        Optional<VisitaEntity> visitaEntityOpt = visitaRepository.findById(id);

        if (visitaEntityOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        VisitaEntity visitaEntity = visitaEntityOpt.get();

        // Mapear valores
        VisitaResponse visitaResponse = modelMapper.map(visitaEntity, VisitaResponse.class);

        List<VisitaVisitanteEntity> visitaVisitanteEntities = visitaVisitanteRepository.getVisitaVisitantesByIdVisita(visitaEntity.getId());

        // Mapear acessos
        visitaResponse.setAcessos(visitaVisitanteEntities.stream()
                .map(i -> AccessResponse.builder()
                        .cpf(i.getVisitante().getCpf())
                        .nome(i.getVisitante().getNomeCompleto())
                        .isAcompanhante(i.getIsAcompanhante())
                        .qrCode(i.getQrcode())
                        .build())
                .toList());

        return ResponseEntity.ok(visitaResponse);

    }
}
