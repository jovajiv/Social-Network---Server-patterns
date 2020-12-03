package bgu.spl.net.impl.BGSServer.Opcodes.ClientSide;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ACK;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.ERROR;
import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.Server_OPCODE;
import bgu.spl.net.impl.BGSServer.PassiveObjects.BGS;
import bgu.spl.net.impl.BGSServer.Opcodes.OPCODE;
import bgu.spl.net.impl.BGSServer.PassiveObjects.User;

import java.util.Vector;

public class FOLLOW extends Client_OPCODE {

    private int follow;             // 0 = follow, 1=unfollow.
    private int numOfFollows;       //amount of users to Iterate over received in this msg.
    private Vector<String> follows=new Vector<>();

    // if follow =0 then follow, if follow=1 then unfollow.

    public FOLLOW(short opcode, byte[] arr){
        super(opcode);
        this.follow=arr[2];
        byte[] number={arr[3],arr[4]};
        this.numOfFollows=bytesToShort(number);
        int startOfWord=5;
        int endOfWord=0;
        for(int i=0;i<numOfFollows;i++){
            endOfWord=findNextZeroByte(arr,startOfWord);
            follows.add(new String(arr,startOfWord,endOfWord-startOfWord));
            startOfWord=endOfWord+1;
        }
    }

    public void process(BGS dataBase, int connectionId, Connections<OPCODE> connections){
        Server_OPCODE result;
        Vector<String> successfulUserList=new Vector<>();
        String requestingUser;
        User destUser;
        if((requestingUser=dataBase.getUserNameByID(connectionId)) != null){           //if requestingUser is logged in
            for(String destUsername:follows){                                           //for each destUser  received list from NetClient
                if((destUser=dataBase.getUserByName(destUsername)) != null)             //if destUser is an actuall user
                    if( addOrRemove(dataBase,requestingUser,destUsername,destUser))     // function addOrRemove  adds or remove user from the followers list, returns true if operation succeded, false otherwise.
                        successfulUserList.add(destUsername);
            }
        }


        if(successfulUserList.size()!=0)
            result=new ACK(this.getOpcode(),successfulUserList);
        else
            result=new ERROR(this.getOpcode());


        connections.send(connectionId,result);
    }

    public int getFollow() {
        return follow;
    }

    public int getNumOfFollowers() {
        return numOfFollows;
    }

    public Vector<String> getFollowers() {
        return follows;
    }

    //adds or removes from following list , according to @numOfFollows private field.
    public boolean addOrRemove(BGS dataBase,String requestingUser,String destUsername,User destUser) {
        if (follow == 0) {  // FOLLOW
            if (!dataBase.getUserByName(requestingUser).getFollowing().contains(destUsername)) {   // if requesting user isnt already following destUser,
                destUser.addFollower(requestingUser);                                               // update destUser , so his "who is following me" list gets updated
                dataBase.getUserByName(requestingUser).addFollowing(destUsername);                  // update requestingUser, so his "Who im following" list gets updated
                return true;
            }
        } else if(follow==1) {              // UNFOLLOW
            if (dataBase.getUserByName(requestingUser).getFollowing().contains(destUsername)) {   // if requesting user is following destUser,
                destUser.removeFollower(requestingUser);
                dataBase.getUserByName(requestingUser).removeFollowing(destUsername);
                return true;
            }
        }
        return false;
    }




    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }



}
