package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.util.*;
import java.util.LinkedList;

import bgu.spl.net.impl.BGSServer.Opcodes.ClientSide.*;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.Server_OPCODE;
import com.google.common.primitives.Bytes;

public class BidiEncoderDecoder implements MessageEncoderDecoder<OPCODE> {

    private List<Byte> bytes=new LinkedList<>();
    private short opcode=-1;
    private short zeroCounter=-1;


    public BidiEncoderDecoder(){
    }
    /**
     * add the next byte to the decoding process
     *
     * @param nextByte the next byte to consider for the currently decoded
     * message
     * @return a message if this byte completes one or null if it doesnt.
     */
    public OPCODE decodeNextByte(byte nextByte){


        pushByte(nextByte);
        if(bytes.size()==2) {
            opcode = bytesToShort(Bytes.toArray(bytes));
            if (opcode > 0 & opcode < 9) {
                if( opcode == 3 || opcode == 7 ) {
                    zeroCounter = 0;
                }
                else if( opcode == 5 || opcode == 8 ) {
                    zeroCounter = 1;
                }
                else if( opcode == 1 || opcode == 2 || opcode == 6 ) {
                    zeroCounter = 2;
                }

            } else {
                System.out.println("Invalid opcode recieved" + opcode);
            }
        }

        if(opcode==4 && bytes.size()==5 ){          //Handles Follow opcode
            byte[] arr={bytes.get(3),bytes.get(4)};
            zeroCounter=bytesToShort(arr);          // amount of users

        }


        if(zeroCounter!=0) {
            if(nextByte==0)
                zeroCounter--;
            if(zeroCounter!=0)
                return null;                //keep reading letters.
        }

        byte[] arr=Bytes.toArray(bytes);
        bytes.clear();
        short tempopcode=opcode;
        opcode=-1;
        zeroCounter=-1;
        return opCodeSender(tempopcode,arr);
    }



    private void pushByte(byte nextByte) {
        bytes.add(nextByte);
    }

    /**
     * encodes the given message to bytes array
     *
     * @param message the message to encode
     * @return the encoded bytes
     */
    public byte[] encode(OPCODE message){
       return message.encode();

    }


    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }



    private Client_OPCODE opCodeSender(short opcode, byte[] arr){
        Client_OPCODE msg=null;
        switch(opcode) {
            case 1:
                msg=new REGISTER(opcode,arr);
                break;
            case 2:
                msg=new LOGIN(opcode,arr);
                break;
            case 3:
                msg=new LOGOUT(opcode);
                break;
            case 4:
                msg=new FOLLOW(opcode,arr);
                break;
            case 5:
                msg=new POST(opcode,arr);
                break;
            case 6:
                msg=new PM(opcode,arr);
                break;
            case 7:
                msg=new USERLIST(opcode);
                break;
            case 8:
                msg=new STAT(opcode,arr);
                break;


        }

        return msg;


    }


}
