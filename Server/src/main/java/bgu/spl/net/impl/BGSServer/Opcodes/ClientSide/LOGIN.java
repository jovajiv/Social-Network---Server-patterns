package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.NOTIFICATION;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ACK;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ERROR;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.Server_OPCODE;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LOGIN extends Client_OPCODE {


    String username;
    String password;

    public LOGIN(short opcode,byte[] arr){
        super(opcode);
        int startOfWord=2;
        int endOfWord=findNextZeroByte(arr,startOfWord);
        this.username=new String(arr,startOfWord,endOfWord-startOfWord);
        startOfWord=endOfWord+1;
        endOfWord=findNextZeroByte(arr,startOfWord);
        this.password=new String(arr,startOfWord,endOfWord-startOfWord);
    }
    public void process(BGS dataBase, int connectionId, Connections<OPCODE> connections){
        Server_OPCODE result;
        Boolean response=dataBase.attemptLogin(username,password,connectionId);
        if(response){
            result = new ACK(this.getOpcode());              // 1 is opcode value of REGISTER
            connections.send(connectionId,result);
            ConcurrentLinkedQueue<NOTIFICATION> queue =dataBase.getUserByName(username).getPendingNotifications();
            while (!queue.isEmpty()){
                connections.send(connectionId,queue.poll());
            }
        }
        else {
            result = new ERROR(this.getOpcode());
            connections.send(connectionId,result);
        }

     }







    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



}
