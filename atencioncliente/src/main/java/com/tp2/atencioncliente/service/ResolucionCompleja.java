package com.tp2.atencioncliente.service;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class ResolucionCompleja {
    private final int NUM_AGENTES = 5;
    private final Lock[] basesDeDatos = new ReentrantLock[NUM_AGENTES];
    
    // Almacena el estado de cada agente de forma segura para hilos
    private final AtomicReferenceArray<String> estadosAgentes = new AtomicReferenceArray<>(NUM_AGENTES);

    public ResolucionCompleja() {
        for (int i = 0; i < NUM_AGENTES; i++) {
            basesDeDatos[i] = new ReentrantLock();
            estadosAgentes.set(i, "Iniciando...");
        }
    }

    @PostConstruct
    public void iniciarTurnoDeResolucion() {
        for (int i = 0; i < NUM_AGENTES; i++) {
            final int idAgente = i;
            Thread t = new Thread(() -> {
                int dbIzquierda = idAgente;
                int dbDerecha = (idAgente + 1) % NUM_AGENTES;
                int primerLock = Math.min(dbIzquierda, dbDerecha);
                int segundoLock = Math.max(dbIzquierda, dbDerecha);

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // 1. Pensando / Analizando
                        estadosAgentes.set(idAgente, "Pensando (Analizando caso complejo)");
                        Thread.sleep((long) (Math.random() * 5000) + 2000);
                        
                        // 2. Intentando tomar recursos
                        estadosAgentes.set(idAgente, "Esperando herramientas (DB " + dbIzquierda + " y " + dbDerecha + ")");
                        
                        basesDeDatos[primerLock].lock();
                        basesDeDatos[segundoLock].lock();

                        try {
                            // 3. Resolviendo (Sección Crítica)
                            estadosAgentes.set(idAgente, "Resolviendo caso usando DB " + primerLock + " y " + segundoLock);
                            System.out.println("[EXPERTOS] Agente " + idAgente + " trabajando.");
                            Thread.sleep(4000); // Tiempo de resolución
                        } finally {
                            // Liberación de recursos
                            basesDeDatos[segundoLock].unlock();
                            basesDeDatos[primerLock].unlock();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

    // Método que llamará el controlador para obtener la foto del momento
    public String[] obtenerEstadosDeAgentes() {
        String[] copiaEstados = new String[NUM_AGENTES];
        for (int i = 0; i < NUM_AGENTES; i++) {
            copiaEstados[i] = "Agente Senior " + i + ": " + estadosAgentes.get(i);
        }
        return copiaEstados;
    }
}