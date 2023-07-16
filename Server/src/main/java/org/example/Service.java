package org.example;

import org.example.interfaces.ClasamentRepository;
import org.example.interfaces.ConfiguratieRepository;
import org.example.interfaces.JucatorRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements ServiceInterface {
    private final JucatorRepository jucatorRepository;
    private final ConfiguratieRepository configuratieRepository;
    private final ClasamentRepository clasamentRepository;
    private Map<Long, ObserverInterface> loggedClients;

    public Service(JucatorRepository jucatorRepository, ConfiguratieRepository configuratieRepository, ClasamentRepository clasamentRepository) {
        this.jucatorRepository = jucatorRepository;
        this.configuratieRepository = configuratieRepository;
        this.clasamentRepository = clasamentRepository;
        loggedClients = new ConcurrentHashMap<>();
    }
    @Override
    public Jucator login(Jucator jucator, ObserverInterface client) throws Exception {
        Jucator jucator1 = jucatorRepository.getAccount(jucator.getUsername());
        if (jucator1 != null) {
            if (loggedClients.get(jucator1.getId()) != null)
                throw new Exception("Jucator already logged in.");
            loggedClients.put(jucator1.getId(), client);
            System.out.println("Logged users " + loggedClients);
        } else
            throw new Exception("Authentication failed.");
        return jucator1;
    }

    @Override
    public synchronized List<Clasament> getClasamente() throws SQLException {
        Iterable<Clasament> clasamente = clasamentRepository.findAll();
        List<Clasament> clasamenteList = new ArrayList<>();
        for (Clasament clasament : clasamente) {
            clasamenteList.add(clasament);
        }
        return clasamenteList;
    }

    @Override
    public Configuratie generateRandomConfiguration() throws SQLException {
        Iterable<Configuratie> configuratii = configuratieRepository.findAll();
        List<Configuratie> configuratiiList = new ArrayList<>();
        for (Configuratie configuratie : configuratii) {
            configuratiiList.add(configuratie);
        }
        Random random = new Random();
        int index = random.nextInt(configuratiiList.size());
        return configuratiiList.get(index);
    }

    @Override
    public synchronized void addClasament(Clasament clasament, ObserverInterface client) throws Exception {
        clasamentRepository.save(clasament);
        notifyAll(client);
    }

    @Override
    public synchronized void notifyAll(ObserverInterface client) throws Exception {
        notifyUpdateClasament();
    }

    @Override
    public void logout(Jucator jucator, ObserverInterface mainController) throws Exception {
        if(loggedClients.get(jucator.getId()) == null)
            throw new Exception("Jucator not logged in.");
        loggedClients.remove(jucator.getId());
        System.out.println("Logged users " + loggedClients);
    }

    /*@Override
    public Long getPoints(String cuvant, Configuratie configuratie, ObserverInterface client) throws Exception {
        return null;
    }*/

    @Override
    public String generateRandomLetter(Configuratie configuratie, ObserverInterface client) throws Exception {
        var list = List.of(configuratie.getLitere().split(", "));
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    @Override
    public Long getPointsForChosenLetter(String litera, Configuratie configuratie, String literaRandomDeLaServer, ObserverInterface client) throws Exception {
        List<String> litere = List.of(configuratie.getLitere().split(", "));
        if(litera.equals(literaRandomDeLaServer)) {
            return 0L;
        }
        Long clientValue =0L;
        Long serverValue =0L;
        Long[] values = new Long[]{configuratie.getPunctaj1(), configuratie.getPunctaj2(), configuratie.getPunctaj3(), configuratie.getPunctaj4()};
        for(int i = 0; i < 4; i++) {
            if(litere.get(i).equals(litera)) {
                clientValue = values[i];
            }
            if(litere.get(i).equals(literaRandomDeLaServer)) {
                serverValue = values[i];
            }
        }
        if (clientValue > serverValue) {
            return clientValue + serverValue;
        }
        else {
            return -1L;
        }
    }

    private synchronized void notifyUpdateClasament() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for(Long key : loggedClients.keySet()) {
            ObserverInterface client = loggedClients.get(key);
            executor.execute(() -> {
                try {
                    client.updateClasament();
                } catch (Exception e) {
                    System.err.println("Error notifying client " + e);
                }
            });
        }
    }
}
