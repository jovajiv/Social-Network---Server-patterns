package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ACK;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ERROR;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.PassiveObjects.User;

public class STAT extends Client_OPCODE {

    String requestedUserName;           //get info on this user

    public STAT(short opcode,byte[] arr){
        super(opcode);
        int startOfWord=2;
        int endOfWord=findNextZeroByte(arr,startOfWord);
        this.requestedUserName=new String(arr,startOfWord,endOfWord-startOfWord);
    }

    public void process(BGS dataBase, int connectionId, Connections<OPCODE> connections){
        User requestedUserObject;
        int numPosts;
        int NumFollowers;
        int NumFollowing;
        if((dataBase.getUserNameByID(connectionId)) != null) {        //if requesting user is logged in
            if((requestedUserObject=dataBase.getUserByName(requestedUserName)) != null) {  //if requested user exists
                numPosts = requestedUserObject.getNumOfPosts();
                NumFollowers = requestedUserObject.getFollowers().size();
                NumFollowing =requestedUserObject.getFollowing().size();
                connections.send(connectionId,new ACK(this.getOpcode(),numPosts,NumFollowers,NumFollowing));
                return;
            }
        }                                                                 // if requsting user isnt logged in or if requested User doesnt exist.
        connections.send(connectionId,new ERROR(this.getOpcode()));



    }


    public String getUserName() {
        return requestedUserName;
    }



}
