package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ACK;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ERROR;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.NOTIFICATION;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.PassiveObjects.User;

public class PM extends Client_OPCODE {

    String content;
    String destUserName;
    public PM(short opcode,byte[] arr){
        super(opcode);
        int startOfWord=2;
        int endOfWord=findNextZeroByte(arr,startOfWord);
        this.destUserName=new String(arr,startOfWord,endOfWord-startOfWord);
        startOfWord=endOfWord+1;
        endOfWord=findNextZeroByte(arr,startOfWord);
        this.content=new String(arr,startOfWord,endOfWord-startOfWord);
    }


   //TODO save all messages to Data structure, did not do it yet
    public void process(BGS dataBase, int connectionId, Connections<OPCODE> connections){
        NOTIFICATION result;
        String requestingUser;
        User destUser;
        Integer destConId;

        if((requestingUser=dataBase.getUserNameByID(connectionId)) != null){        //if requesting user is logged in
            dataBase.getUserByName(requestingUser).addPMs(content);                 // add current message to Source users History of Private messages.
            result=new NOTIFICATION(this.getOpcode(),requestingUser,content);
            if((destUser=dataBase.getUserByName(destUserName)) !=null) {                      // if destination user is registered
                destConId = dataBase.getIdByUsername(destUserName);
                sendToDestination( destConId, destUser, result, dataBase, connections);
                connections.send(connectionId,new ACK(this.getOpcode()));
            }
            else
                connections.send(connectionId,new ERROR(this.getOpcode()));
        }
        else
            connections.send(connectionId,new ERROR(this.getOpcode()));


    }


    public String getUserName() {
        return destUserName;
    }

    public String getContent() {
        return content;
    }

    public void sendToDestination(Integer destConId,User destUser,NOTIFICATION result,BGS dataBase,Connections<OPCODE> connections) {
        synchronized (destConId) {
            if (destConId >= 0) {
                connections.send(destConId, result);
            }
            else{
                destUser.addNotification(result);
            }
        }
    }



}
