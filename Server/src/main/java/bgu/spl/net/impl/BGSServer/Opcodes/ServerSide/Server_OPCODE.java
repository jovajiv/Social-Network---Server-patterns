package bgu.spl.net.impl.BGSServer.Opcodes.ServerSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;

public abstract class Server_OPCODE implements OPCODE {


    private short opcode;

    // Abstract class, all other OPCODEs inherit this class, each on have different amount of fields ,yet in all, first Field is Client_OPCODE
    public Server_OPCODE(short opcode){
        this.opcode=opcode;
    }

    public short getOpcode() {
        return opcode;
    }



    public abstract byte[] encode();


   public void process(BGS instance, int connectionId, Connections<OPCODE> connections ){
       //Not needed by Server Opcodes
   }


    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }


    public int findNextZeroByte(byte[] arr,int startingIndex){
        byte zerobyte =0;
        for (int index=startingIndex ;index<arr.length;index++){
            if(arr[index]== zerobyte){
                return index;
            }
        }
        return -1;
    }
}


