package bgu.spl.net.impl.BGSServer.Opcodes.ServerSide;

import java.nio.ByteBuffer;

public class ERROR extends Server_OPCODE {

    private short msgOpcode;

    public ERROR (short msgOpcode) {
        super((short)11);
        this.msgOpcode = msgOpcode;
    }


    public byte[] encode(){
        ByteBuffer buffer = ByteBuffer.allocate(4); //check if fine
        buffer.putShort((short)11);
        buffer.putShort(msgOpcode);
        return buffer.array();
    }

    public short getMsgOpcode() {
        return msgOpcode;
    }


}