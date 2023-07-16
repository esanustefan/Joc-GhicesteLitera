package org.example.rpcprotocol;

import org.example.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientRpcWorker implements Runnable, ObserverInterface {
    private ServiceInterface server;
    private Socket connection;

    private ObjectInputStream input;

    private ObjectOutputStream output;

    private volatile boolean connected;

    public ClientRpcWorker(ServiceInterface server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response "+response);
        output.writeObject(response);
        output.flush();
    }

    private Response handleRequest(Request request) {
        Response response=null;
        try{
            if(request.type() == RequestType.LOGIN){
                System.out.println("Login request ...");
                Jucator jucator1 = server.login( (Jucator)request.data() , this);
                response = new Response.Builder().type(ResponseType.OK).data(jucator1).build();
            }
            if(request.type() == RequestType.GENERATE_RANDOM_CONFIGURATION) {
                System.out.println("Generate random configuration request ...");
                Configuratie configuratie = server.generateRandomConfiguration();
                response = new Response.Builder().type(ResponseType.GENERATE_RANDOM_CONFIGURATION_RESPONSE).data(configuratie).build();
            }
            if(request.type() == RequestType.ADD_CLASAMENT) {
                System.out.println("Add clasament request ...");
                server.addClasament((Clasament) request.data(), this);
                response = new Response.Builder().type(ResponseType.ADD_CLASAMENT_RESPONSE).build();
            }
            if(request.type() == RequestType.GET_CLASAMENTE) {
                System.out.println("Get clasamente request ...");
                List<Clasament> clasamente = server.getClasamente();
                response = new Response.Builder().type(ResponseType.GET_CLASAMENTE_RESPONSE).data(clasamente).build();
            }
            if (request.type() == RequestType.GENERATE_RANDOM_LETTER) {
                System.out.println("Generate random letter request ...");
                String litera = server.generateRandomLetter((Configuratie) request.data(), this);
                response = new Response.Builder().type(ResponseType.GENERATE_RANDOM_LETTER_RESPONSE).data(litera).build();
            }
            if(request.type() == RequestType.GET_POINTS_FOR_CHOSEN_LETTER) {
                System.out.println("Get points for chosen letter request ...");
                ArrayList<?> requestData = (ArrayList<?>) request.data();
                String litera = requestData.get(0).toString();
                Configuratie configuratie = (Configuratie) requestData.get(1);
                String literaRandomDeLaServer = requestData.get(2).toString();
                Long points = server.getPointsForChosenLetter(litera, configuratie, literaRandomDeLaServer, this);
                response = new Response.Builder().type(ResponseType.GET_POINTS_FOR_CHOSEN_LETTER_RESPONSE).data(points).build();
            }
            /*if(request.type() == RequestType.NOTIFY_ALL) {
                System.out.println("Notify all request ...");
                server.notifyAll(this);
                response = new Response.Builder().type(ResponseType.OK).build();
            }*/
            /*if(request.type() == RequestType.GET_POINTS) {
                System.out.println("Get points request ...");
                ArrayList<?> requestData = (ArrayList<?>) request.data();
                String cuvant = requestData.get(0).toString();
                Configuratie configuratie = (Configuratie) requestData.get(1);
                Long points = server.getPoints(cuvant, configuratie, this);
                response = new Response.Builder().type(ResponseType.GET_POINTS_RESPONSE).data(points).build();
            }*/
            if(request.type() == RequestType.LOGOUT) {
                System.out.println("Logout request");
                server.logout((Jucator) request.data(), this);
                connected = false;
                return new Response.Builder().type(ResponseType.OK).build();
            }
        } catch (Exception e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
        return response;
    }


    @Override
    public void updateClasament() throws Exception {
        Response response = new Response.Builder().type(ResponseType.UPDATE).build();
        System.out.println("Notifying all clients ...");
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new Exception("Sending error: "+e);
        }
    }
}
