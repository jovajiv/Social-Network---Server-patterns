package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


//  SINGLETON
public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer,ConnectionHandler<T>> connectionsMap;
    private ConcurrentLinkedQueue<Integer> releasedNumbers;
    private AtomicInteger idGiver;


    public ConnectionsImpl(){
        connectionsMap=new ConcurrentHashMap<>();
        releasedNumbers=new ConcurrentLinkedQueue<>();
        idGiver=new AtomicInteger(0);
    }



    public int connect(ConnectionHandler<T> handler) {
        if(releasedNumbers.isEmpty()) {
            int tempid=idGiver.getAndIncrement();
            connectionsMap.put(tempid, handler);
            return tempid;
        }
        else {
            Integer temp=releasedNumbers.poll();
            if(temp==null) {
                temp = idGiver.getAndIncrement();
            }
            connectionsMap.put(temp, handler);
            releasedNumbers.remove(0);
            return temp;
        }


    }

    public boolean send(int connectionId, T msg){
        ConnectionHandler<T> handler=connectionsMap.get(connectionId);
        if(handler!=null) {
            handler.send(msg);
            return true;
        }
        else
            return false;


    }

    public void broadcast(T msg){
        for (ConnectionHandler<T> handler:connectionsMap.values()){
            handler.send(msg);
        }
    }

    public void disconnect(int connectionId){
        connectionsMap.remove(connectionId);
        releasedNumbers.add(connectionId);
    }
}
