package bgu.spl.net.impl.BGSServer.Opcodes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;

public interface OPCODE {

    public short getOpcode();

    public void process(BGS instance, int connectionId, Connections<OPCODE> connections );


    public  byte[] encode();




}

