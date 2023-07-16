package org.example.rpcprotocol;


import org.example.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceRpcProxy implements ServiceInterface {

    private String host;

    private int port;
    private ObserverInterface client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServiceRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }
    @Override
    public synchronized Jucator login(Jucator jucator, ObserverInterface client) throws Exception {
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN).data(jucator).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new Exception(err);
        }
        else {
            if (response.type() == ResponseType.OK) {
                this.client = client;
            }
        }
        return (Jucator) response.data();
    }

    @Override
    public List<Clasament> getClasamente() throws Exception {
        Request req = new Request.Builder().type(RequestType.GET_CLASAMENTE).build();
        sendRequest(req);
        Response response = readResponse();
        if(response.type()==ResponseType.ERROR){
            String err=response.data().toString();
            System.out.println(err);
            // closeConnection();
            return null;
        }
        return (List<Clasament>) response.data();
    }

    @Override
    public Configuratie generateRandomConfiguration() throws Exception {
        Request req = new Request.Builder().type(RequestType.GENERATE_RANDOM_CONFIGURATION).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new Exception(err);
        }
        return (Configuratie) response.data();
    }

    @Override
    public void addClasament(Clasament clasament, ObserverInterface client) throws Exception {
        Request req = new Request.Builder().type(RequestType.ADD_CLASAMENT).data(clasament).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new Exception(err);
        }
    }

    @Override
    public void notifyAll(ObserverInterface mainController) throws Exception {
        Request req = new Request.Builder().type(RequestType.NOTIFY_ALL).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            System.out.println(err);
        }
    }



    @Override
    public void logout(Jucator jucator, ObserverInterface client) throws Exception {
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(jucator).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            System.out.println(err);
        }
    }

    /*@Override
    public Long getPoints(String cuvant, Configuratie configuratie, ObserverInterface client) throws Exception {
        Request req = new Request.Builder().type(RequestType.GET_POINTS).data(new ArrayList<>(Arrays.asList(cuvant, configuratie))).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            System.out.println(err);
        }
        return (Long) response.data();
    }*/

    @Override
    public String generateRandomLetter(Configuratie configuratie, ObserverInterface client) throws Exception {
        Request req = new Request.Builder().type(RequestType.GENERATE_RANDOM_LETTER).data(configuratie).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            System.out.println(err);
        }
        return (String) response.data();
    }

    @Override
    public Long getPointsForChosenLetter(String litera, Configuratie configuratie, String literaRandomDeLaServer, ObserverInterface client) throws Exception {
        Request req = new Request.Builder().type(RequestType.GET_POINTS_FOR_CHOSEN_LETTER).data(new ArrayList<>(Arrays.asList(litera, configuratie, literaRandomDeLaServer))).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            System.out.println(err);
        }
        return (Long) response.data();
    }


    private void sendRequest(Request request) throws Exception {
        try {
            //System.out.println(request);
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object "+e);
        }

    }

    private Response readResponse(){
        Response response=null;
        try{
            response = qresponses.take();
            //System.out.println(response);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        return response;
    }

    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }

    private void initializeConnection() {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleUpdate(Response response){
        if(response.type() == ResponseType.UPDATE){
            try{
                client.updateClasament();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type() == ResponseType.UPDATE;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received " + response);
                    if(isUpdate((Response) response)){
                        handleUpdate((Response) response);
                    }
                    else {
                        try {
                            qresponses.put((Response) response);
                            System.out.println("Recieved: " + qresponses);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
