package org.example.utils;

import org.example.ServiceInterface;
import org.example.rpcprotocol.ClientRpcWorker;

import java.net.Socket;

public class RpcConcurrentServer extends AbsConcurrentServer{

    private ServiceInterface server;

    public RpcConcurrentServer(int port, ServiceInterface server) {
        super(port);
        this.server = server;
        System.out.println("RpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcWorker worker=new ClientRpcWorker(server, client);
        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop() {
        System.out.println("Stopping services ...");
    }
}
