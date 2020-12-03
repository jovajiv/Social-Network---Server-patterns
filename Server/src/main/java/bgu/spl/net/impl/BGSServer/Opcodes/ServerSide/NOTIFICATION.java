package bgu.spl.net.impl.BGSServer.Opcodes.ServerSide;


import org.apache.commons.lang3.ArrayUtils;

public class NOTIFICATION extends Server_OPCODE {

    private char NotificationType;
    private String postingUser;
    private String content;
    private short msgOpcode;

    public NOTIFICATION(short msgOpcode, String postingUser, String content) {
        super((short)9);
        this.msgOpcode=msgOpcode;
        if(msgOpcode==5)
            this.NotificationType = 1;  // this is a Public post
        else
            this.NotificationType=0;    //this is a private message
        this.postingUser = postingUser;
        this.content = content;
    }


    public byte[] encode(){

        byte [] ACK_OPCODE = shortToBytes(this.getOpcode());
        byte [] NotificationTypeToByte = {(byte)NotificationType};
        byte [] PositionUserToByte = postingUser.getBytes();
        byte [] contentToByte = content.getBytes();
        byte [] zeroByteArray = {0};

        byte [] merge1 = ArrayUtils.addAll(ACK_OPCODE,NotificationTypeToByte);
        byte [] merge2 = ArrayUtils.addAll(PositionUserToByte,zeroByteArray);
        byte [] merge3 = ArrayUtils.addAll(contentToByte,zeroByteArray);

        byte [] output = ArrayUtils.addAll(merge1,merge2); //1+2
        output = ArrayUtils.addAll(output,merge3); //(1+2)+3

        return output;
    }

    public char getNotificationType() {
        return NotificationType;
    }

    public String getPositionUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }
}