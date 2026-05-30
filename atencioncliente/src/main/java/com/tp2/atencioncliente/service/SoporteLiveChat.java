package com.tp2.atencioncliente.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Service;

@Service
public class SoporteLiveChat {
    private final int SILLAS_TOTALES = 3;
    private int sillasDisponibles = SILLAS_TOTALES;
    
    private volatile String estadoAgente = "Durmiendo 💤"; 
    
    private final Semaphore clientes = new Semaphore(0);
    private final Semaphore agente = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);

    public SoporteLiveChat() {
        Thread hiloAgente = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    estadoAgente = "Esperando clientes... (Durmiendo) 💤";
                    clientes.acquire();
                    
                    mutex.acquire();
                    sillasDisponibles++;
                    mutex.release();
                    
                    agente.release();
                    
                    estadoAgente = "Atendiendo a un cliente 🗣️";
                    Thread.sleep(5000); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        hiloAgente.setDaemon(true);
        hiloAgente.start();
    }

    public String nuevoClienteWeb(int idCliente) {
        try {
            mutex.acquire();
            if (sillasDisponibles > 0) {
                sillasDisponibles--;
                mutex.release();
                new Thread(() -> {
                    try {
                        clientes.release();
                        agente.acquire();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
                return "Cliente " + idCliente + " en sala de espera.";
            } else {
                mutex.release();
                return "Cliente " + idCliente + " rechazado. La sala está llena.";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error en el sistema.";
        }
    }

    public Map<String, Object> obtenerEstado() {
        Map<String, Object> estado = new HashMap<>();
        estado.put("sillasLibres", sillasDisponibles);
        estado.put("sillasTotales", SILLAS_TOTALES);
        estado.put("estadoAgente", estadoAgente);
        return estado;
    }
}