/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {

    public static void main(String[] args) {
        // Crear una instancia de HostBlackListsValidator
        HostBlackListsValidator hblv = new HostBlackListsValidator();

        // IP para realizar las pruebas
        String ipAddress = "202.24.34.55";

        // experimentos
        //runExperiment(hblv, ipAddress, 1);
        //runExperiment(hblv, ipAddress, Runtime.getRuntime().availableProcessors());
        //runExperiment(hblv, ipAddress, 2 * Runtime.getRuntime().availableProcessors());
        //runExperiment(hblv, ipAddress, 50);
        runExperiment(hblv, ipAddress, 100);
    }
    private static void runExperiment(HostBlackListsValidator hblv, String ipAddress, int numThreads) {
        // tiempo de inicio
        long startTime = System.currentTimeMillis();

        // busqueda
        List<Integer> blackListOccurrences = hblv.checkHost(ipAddress, numThreads);

        // tiempo de finalizacion
        long endTime = System.currentTimeMillis();

        // tiempo de ejecucion
        long elapsedTime = endTime - startTime;
        double elapsedTimeSeconds = elapsedTime / 1000.0;

        // resultados
        System.out.println("Experiment with " + numThreads + " threads:");
        System.out.println("The host was found in the following blacklists: " + blackListOccurrences);
        System.out.println("Execution Time: " + elapsedTimeSeconds + " seconds");
        System.out.println();
    }
}
