package com.portaria.portaria_ws.feign.clients;

import com.portaria.portaria_ws.dto.response.ActivityFeignResponse;
import com.portaria.portaria_ws.dto.response.TicketResponse;
import com.portaria.portaria_ws.feign.CatracaWsOAuthFeignInterceptor;
import com.portaria.portaria_ws.models.enums.TicketType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;

@FeignClient(
    name = "app-catraca-client",
    url = "${app-catraca.url}",
    configuration = CatracaWsOAuthFeignInterceptor.class)
public interface AppCatracaClient {

    @GetMapping("empresa/token/{qr_code}")
    ResponseEntity<TicketResponse> getTicketByQrCode(@PathVariable("qr_code") String qrCode);

    @GetMapping("empresa/token/app-ticket")
    ResponseEntity<TicketResponse> createNewTicket(
        @RequestParam("empresa_id") Long empresaId,
        @RequestParam("user_id") String userId,
        @RequestParam("hor_final") Timestamp horFinal,
        @RequestParam("application_id") String applicationId,
        @RequestParam("application_version")  String applicationVersion,
        @RequestParam("ticket_type") TicketType ticketType
    );

    @PutMapping("empresa/token/update-ticket")
    ResponseEntity<Void> updateDuracaoTicket(
        @RequestParam("token") String token,
        @RequestParam("hor_final") Timestamp horFinal
    );

    @GetMapping("empresa/token/app-ticket/activity")
    ResponseEntity<List<ActivityFeignResponse>> getTicketActivity(
        @RequestParam("qrcode") String qrcode,
        @RequestParam("limit") Long limit
    );

}
