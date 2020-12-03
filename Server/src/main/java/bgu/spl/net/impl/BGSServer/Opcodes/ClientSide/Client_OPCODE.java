package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;

public abstract class Client_OPCODE implements OPCODE {

    private short opcode;

    // Abstract class, all other OPCODEs inherit this class, each on have different amount of fields ,yet in all, first Field is Client_OPCODE
    public Client_OPCODE(short opcode){
        this.opcode=opcode;
    }

    public short getOpcode() {
        return opcode;
    }


    public abstract void process(BGS instance, int connectionId, Connections<OPCODE> connections );


    public int findNextZeroByte(byte[] arr,int startingIndex){
        byte zerobyte =0;
        for (int index=startingIndex ;index<arr.length;index++){
            if(arr[index]== zerobyte){
                return index;
            }
        }
        return -1;
    }

    public byte[] encode(){
        ////Not needed by Client Opcodes
        return null;
    }
}
