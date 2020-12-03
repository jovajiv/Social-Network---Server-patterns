package bgu.spl.net.impl.BGSServer.Opcodes.ServerSide;


import org.apache.commons.lang3.ArrayUtils;
import java.util.Vector;


//TODO you do not have direct Access to BGS shared Object anymore, nor do you need one.  All info passed to you is verified, no reason i cant think of requiers you to directly access the Database.

public class ACK extends Server_OPCODE {

    //    private String user; //only for stat
    private Vector<String> userlist; //userlist & follow
    private int numFollow; //only for follow
    private short msgOpcode;
    private int NumPosts;
    private int NumFollowers;
    private int NumFollowing;


    public ACK (short msgOpcode) {
        super((short)10);
        this.msgOpcode = msgOpcode;

    }

    public ACK (short msgOpcode, int NumPosts,int NumFollowers, int NumFollowing) { //only for stat
        super((short)10);
        this.msgOpcode = msgOpcode;
        this.NumPosts=NumPosts;
        this.NumFollowers=NumFollowers;
        this.NumFollowing=NumFollowing;

    }

    public ACK (short msgOpcode, Vector<String> userlist ) { //only for follow & for userlist
        super((short)10);
        this.msgOpcode = msgOpcode;
        this.userlist=userlist; // ASSUME WITH SPACE between users!!!!  TODO , Given a Vector<String> instead of regular String
        this.numFollow = userlist.size();

    }

    public byte[] encode(){
        byte[] output;
        byte[] ACK_OPCODE = shortToBytes((short)10);
        byte[] MSG_OPCODE = shortToBytes(msgOpcode);
        byte[] optional=null; //is null is ok? or should we declare it as empty arr?
        byte[] ZeroByte = {0};

        switch (msgOpcode) {
            case 1:  // register - no optional needed
                break;
            case 2: //log in - no optional needed
                break;
            case 3: //log out - no optional needed
                break;
            case 4://follow - todo NEEDED   TODO REDO THIS . YOU ARE GIVEN A Vector of Strings, not a string with Spaces in it.
                short Num_follow = (short)numFollow;
                byte [] NumOfUsers = shortToBytes(Num_follow);
                if (!userlist.isEmpty()) {
                    byte [] UserNameList = null;
                    for (String str : userlist) {
                        UserNameList = ArrayUtils.addAll(UserNameList, str.getBytes());//prev arr+the current name to add
                        UserNameList = ArrayUtils.addAll(UserNameList, ZeroByte); //push the 0 byte to separate names.
                    }
                    optional = ArrayUtils.addAll(NumOfUsers, UserNameList);//will push 0 byte in the end anyway
                }
                else //no users in the list, just '0' and 0 byte.
                    optional = ArrayUtils.addAll(NumOfUsers,ZeroByte);

                break;
            case 5:// post - no optional needed
                break;
            case 6: //pm - no optional needed
                break;
            case 7://userlist - NEEDED
                short totalUsers = (short)userlist.size();
                byte [] totalUsersBytes = shortToBytes(totalUsers);
                if (!userlist.isEmpty()) {
                    byte [] userNameList = null;
                    for (String str : userlist) {
                        userNameList = ArrayUtils.addAll(userNameList, str.getBytes());//prev arr+the current name to add
                        userNameList = ArrayUtils.addAll(userNameList, ZeroByte); //push the 0 byte to separate names.
                    }
                    optional = ArrayUtils.addAll(totalUsersBytes,userNameList); //will push 0 byte in the end anyway
                }
                else
                    optional = ArrayUtils.addAll(totalUsersBytes,ZeroByte);


                break;
            case 8://stat - NEED to get the user itself ( via constructor)

                short numPosts = (short) NumPosts;
                byte [] numOfPostsArr = shortToBytes(numPosts);

                short numFollowers = (short)NumFollowers;
                byte [] NumFollowersArr = shortToBytes(numFollowers);

                short numFollowing = (short)NumFollowing;
                byte [] NumFollowingArr = shortToBytes(numFollowing);

                optional = ArrayUtils.addAll(numOfPostsArr,NumFollowersArr);
                optional = ArrayUtils.addAll(optional,NumFollowingArr);
                break;
        }


        output= ArrayUtils.addAll(ACK_OPCODE,MSG_OPCODE);

        if (optional!=null)
            output = ArrayUtils.addAll(output,optional);

        return output;
    }

    public short getMsgOpcode() {
        return msgOpcode;
    }


}

