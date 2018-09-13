package com.javarush.task.task30.task3008;

import java.net.Socket;
import java.io.Closeable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.SocketAddress;

public class Connection implements Closeable {
    
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    
    
    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        
    
    }
    
    public void send(Message message) throws IOException {
        synchronized(out) {
            this.out.writeObject(message);
            this.out.flush();
        }
    }
    
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized(in) {
            //String str = this.in.readObject().toString();
            Message message = (Message)this.in.readObject();
            return message;
            
        }
    }
    
    public SocketAddress getRemoteSocketAddress() {
        return this.socket.getRemoteSocketAddress();
    }
    
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}