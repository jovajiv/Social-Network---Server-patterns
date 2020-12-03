package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ACK;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ERROR;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;

import java.util.Set;
import java.util.Vector;

public class USERLIST extends Client_OPCODE {

    public USERLIST(short opcode) {
        super(opcode);
    }



    public void process(BGS dataBase, int connectionId, Connections<OPCODE> connections){
        String requestingUser;
        Set<String> keySet = dataBase.getUsernameToData().keySet();
        Vector<String> keys= new Vector<>(keySet);
        if((requestingUser=dataBase.getUserNameByID(connectionId)) != null) {        //if requesting user is logged in
            connections.send(connectionId,new ACK(this.getOpcode(),keys));              //send the opcode and vector of Usernames registered, String Format, numOfUsers can be derived from size of vec

        }else{                                                                       // if requsting user isnt logged in
            connections.send(connectionId,new ERROR(this.getOpcode()));
        }



}


}




