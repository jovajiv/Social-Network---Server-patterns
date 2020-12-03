package bgu.spl.net.impl.BGSServer;


import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) {

        BGS dataBase = new BGS(); //one shared object

        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () ->  new BidiProtocol(dataBase),//protocol factory
                BidiEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}