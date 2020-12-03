package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ACK;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ERROR;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.Server_OPCODE;

public class REGISTER extends Client_OPCODE {

    private String username;
    private String password;

    public REGISTER(short opcode,byte[] arr) {
        super(opcode);
        int startOfWord=2;
        int endOfWord=findNextZeroByte(arr,startOfWord);
        this.username=new String(arr,startOfWord,endOfWord-startOfWord);
        startOfWord=endOfWord+1;
        endOfWord=findNextZeroByte(arr,startOfWord);
        this.password=new String(arr,startOfWord,endOfWord-startOfWord);
    }
    public void process(BGS dataBase,int connectionId,Connections<OPCODE> connections ){
        Server_OPCODE result;
        Boolean response=dataBase.Register(username,password);
        if(response){
            result = new ACK(this.getOpcode());              // 2 is opcode value of Login
        }
        else {
            result = new ERROR(this.getOpcode());
        }
        connections.send(connectionId,result);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


}
