package com.tp2.atencioncliente.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Service;

@Service
public class SistemaTickets {
    private final BlockingQueue<String> colaTickets = new ArrayBlockingQueue<>(5);
    
    // Estado del trabajador de tickets
    private volatile String estadoConsumidor = "Inactivo (Esperando tickets) ⏳";

    // Constructor que inicia el hilo consumidor para procesar los tickets
    public SistemaTickets() {
        Thread consumidor = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    estadoConsumidor = "Inactivo (Esperando tickets) ⏳";
                    String ticket = colaTickets.take();
                    
                    estadoConsumidor = "Procesando: " + ticket + " ⚙️";
                    Thread.sleep(4000); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        consumidor.setDaemon(true);
        consumidor.start();
    }

    // Método que llamará el controlador para recibir un nuevo ticket desde la web
    public String recibirFormularioWeb(String contenidoTicket) {
        boolean encolado = colaTickets.offer(contenidoTicket);
        if (encolado) {
            return "Ticket enviado con éxito.";
        } else {
            return "Servidor saturado.";
        }
    }

    // Método que llamará el controlador para obtener el estado del sistema de tickets
    public Map<String, Object> obtenerEstado() {
        Map<String, Object> estado = new HashMap<>();
        estado.put("ticketsEnCola", new ArrayList<>(colaTickets)); // Convertimos la cola a Lista para JSON
        estado.put("estadoConsumidor", estadoConsumidor);
        estado.put("espacioDisponible", colaTickets.remainingCapacity());
        return estado;
    }
}