package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ACK;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ERROR;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.NOTIFICATION;

import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.PassiveObjects.User;


import java.util.Set;
import java.util.TreeSet;



public class POST extends Client_OPCODE {

    String content;
    Set<String> specifiedInContent=new TreeSet<>();           //Vector of users Specified in the Content of the MSG.


    public POST(short opcode,byte[] arr){
        super(opcode);
        int startOfWord=2;
        int endOfWord=findNextZeroByte(arr,startOfWord);
        this.content=new String(arr,startOfWord,endOfWord-startOfWord);
        while( startOfWord<arr.length  && (startOfWord=findNextShtrudel(arr,startOfWord)) != -1){
            if((endOfWord=findNextWhiteSpace(arr,startOfWord)) != -1) {
                specifiedInContent.add(new String(arr, startOfWord+1, endOfWord - startOfWord-1));
                startOfWord = endOfWord + 1;
            }
            else {
                specifiedInContent.add(new String(arr, startOfWord+1, arr.length - 1 - startOfWord-1));                // will be used in case there is no WhiteSpace, meaning, @username is the last word in Content
                startOfWord = arr.length;
            }
        }
    }


    public void process(BGS dataBase, int connectionId, Connections<OPCODE> connections){
        NOTIFICATION result;
        String requestingUser;
        User destUser;
        Integer destConId;

        if((requestingUser=dataBase.getUserNameByID(connectionId)) != null){        //if requesting user is logged in
            dataBase.getUserByName(requestingUser).addPosts(content);                 // add current message to Source users History of Post messages.
            result=new NOTIFICATION(this.getOpcode(),requestingUser,content);
            specifiedInContent.addAll(dataBase.getUserByName(requestingUser).getFollowers());       // concatenate to one big lists with all destinations requesting the msg.
            for (String destUsername:specifiedInContent){
                if((destUser=dataBase.getUserByName(destUsername)) !=null) {                      // if destination user is registered
                    destConId = dataBase.getIdByUsername(destUsername);
                    sendToDestination( destConId, destUser, result, dataBase, connections);

                }
            }
            connections.send(connectionId,new ACK(this.getOpcode()));
        }
        else
            connections.send(connectionId,new ERROR(this.getOpcode()));


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


    private int findNextShtrudel(byte[] arr,int startingIndex){
        byte shtrudlebyte =64;
        for (int index=startingIndex ;index<arr.length;index++){
            if(arr[index]== shtrudlebyte){
                return index;
            }
        }
        return -1;
    }


    private int findNextWhiteSpace(byte[] arr,int startingIndex){
        byte whiteSpacebyte =32;
        for (int index=startingIndex ;index<arr.length;index++){
            if(arr[index]== whiteSpacebyte){
                return index;
            }
        }
        return -1;
    }




}
