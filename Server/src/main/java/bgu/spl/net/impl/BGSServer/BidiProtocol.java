package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Opcodes.ClientSide.LOGOUT;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;


public class BidiProtocol implements BidiMessagingProtocol<OPCODE> {

    private boolean shouldTerminate=false;
    private BGS bgsInstance;
    private int connectionId;
    private Connections<OPCODE> connections;


    public BidiProtocol(BGS bgsInstance) {
        this.bgsInstance = bgsInstance;
    }

    public void process(OPCODE message){
        if(!shouldTerminate)
            message.process(bgsInstance,connectionId,connections);
            if (message.getOpcode()==3 && ((LOGOUT)message).sucessfullLogOut())     // logout opcode
                shouldTerminate=true;


}

    public void start(int connectionId, Connections<OPCODE> connections){
        this.connectionId=connectionId;
        this.connections=connections;



    }

    public boolean shouldTerminate(){
        return shouldTerminate;
    }





}