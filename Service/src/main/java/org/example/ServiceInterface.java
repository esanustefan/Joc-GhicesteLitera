package org.example;

import java.util.List;

public interface ServiceInterface {
    Jucator login(Jucator jucator, ObserverInterface client) throws Exception;

    List<Clasament> getClasamente() throws Exception;

    Configuratie generateRandomConfiguration() throws Exception;

    void addClasament(Clasament clasament,ObserverInterface client) throws Exception;

    void notifyAll(ObserverInterface mainController) throws Exception;

    void logout(Jucator jucator, ObserverInterface mainController) throws Exception;

    // Long getPoints(String cuvant, Configuratie configuratie, ObserverInterface client) throws Exception;

    String generateRandomLetter(Configuratie configuratie, ObserverInterface client) throws Exception;

    Long getPointsForChosenLetter(String litera, Configuratie configuratie, String literaRandomDeLaServer, ObserverInterface client) throws Exception;
}
