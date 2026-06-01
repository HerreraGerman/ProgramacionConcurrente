package com.tp2.atencioncliente.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tp2.atencioncliente.service.ResolucionCompleja;
import com.tp2.atencioncliente.service.SistemaTickets;
import com.tp2.atencioncliente.service.SoporteLiveChat;

@RestController
public class SoporteController {

    // Inyectamos los servicios que el controlador va a utilizar
    private final SoporteLiveChat liveChat;
    private final SistemaTickets sistemaTickets;
    private final ResolucionCompleja resolucionCompleja;

    // Añadimos resolucionCompleja al constructor para que Spring la inyecte de forma automática
    public SoporteController(SoporteLiveChat liveChat, SistemaTickets sistemaTickets, ResolucionCompleja resolucionCompleja) {
        this.liveChat = liveChat;
        this.sistemaTickets = sistemaTickets;
        this.resolucionCompleja = resolucionCompleja;
    }

    // Endpoints para interactuar con los servicios
    @GetMapping("/chat")
    public String solicitarChat(@RequestParam int id) {
        return liveChat.nuevoClienteWeb(id);
    }

    @GetMapping("/ticket")
    public String enviarTicket(@RequestParam String mensaje) {
        return sistemaTickets.recibirFormularioWeb(mensaje);
    }

    @GetMapping("/expertos")
    public String[] verMesaDeExpertos() {
        return resolucionCompleja.obtenerEstadosDeAgentes();
    }

    @GetMapping("/estado-chat")
    public Map<String, Object> verEstadoChat() {
        return liveChat.obtenerEstado();
    }

    @GetMapping("/estado-tickets")
    public Map<String, Object> verEstadoTickets() {
        return sistemaTickets.obtenerEstado();
    }
}