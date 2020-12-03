package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ACK;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ERROR;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.Server_OPCODE;

public class LOGOUT extends Client_OPCODE {
    private boolean response;

    public LOGOUT(short opcode) {
        super(opcode);
    }

    public void process(BGS dataBase, int connectionId, Connections<OPCODE> connections){
        Server_OPCODE result;
        Boolean response=dataBase.attemptLogout(connectionId);
        this.response=response;
        if(response){
            result = new ACK(this.getOpcode());              // 1 is opcode value of REGISTER
        }
        else {
            result = new ERROR(this.getOpcode());
        }

        connections.send(connectionId,result);
    }


    public boolean sucessfullLogOut(){return response;}








}
