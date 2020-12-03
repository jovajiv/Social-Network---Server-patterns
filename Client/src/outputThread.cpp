#include "../include/outputThread.h"

using namespace std;
using std::string;

outputThread ::outputThread(ConnectionHandler &connectionHandler , int *Terminate): kill(Terminate),_connectionHandler(connectionHandler) {}

void outputThread::run() {
    while (true) {

        char* answer = new char;
        if (!(_connectionHandler.getBytes(answer,2))) { //OPCODE of the answer from the server
            cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if (!DecodeMsg(answer)){ //as given in example echo client

            *kill = -1;  //we receive  ACK LOGOUT, should kill.

            break;

        }

    }

}

bool outputThread ::DecodeMsg(char* result) {
    bool stop = true;
    char opcodeArr[2];
    opcodeArr[0] = result[0];
    opcodeArr[1] = result[1];
    short opcode = bytesToShort(opcodeArr);  //find the opcode of the msg


    if(opcode == 11 ){  //error.
        _connectionHandler.getBytes(&result[2],2);
        char originalOpcodeArr[2];
        originalOpcodeArr[0] = result[2];
        originalOpcodeArr[1] = result[3];
        short  originalOpcode = bytesToShort(originalOpcodeArr);
        if (originalOpcode==3) {
            *kill = 1; //error for LOGIN - change kill from 0 to 1!
        }
        cout << "ERROR " + to_string(originalOpcode) << endl;

    }
    if (opcode == 10){   //ack
        _connectionHandler.getBytes(&result[2],2);
        char messageOpcodeArr[2];
        messageOpcodeArr[0] = result[2];
        messageOpcodeArr[1] = result[3];
        short messageOpcode = bytesToShort(messageOpcodeArr);

        if(messageOpcode == 3){   //logout
            cout << "ACK 3" <<endl;
            stop = false;
        }

        if(messageOpcode == 1 || messageOpcode == 2||messageOpcode == 5|| messageOpcode == 6){//regolar ack
            cout << "ACK "+ to_string(messageOpcode) << endl;
        }

        if(messageOpcode == 4){   //follow
            _connectionHandler.getBytes(&result[4],2);
            char numOfUsersArry[2];
            numOfUsersArry[0] = result[4];
            numOfUsersArry[1] = result[5];
            short numOfUsers = bytesToShort(numOfUsersArry);
            string userList;
            int counter = 0;
            while(counter<numOfUsers) {
                _connectionHandler.getFrameAscii(userList, '\0');   //convert the userList into string
                userList = userList + ' ';  // instead of zero bytes -> spaces.
                counter++;
            }
            for(unsigned int i = 0; i<userList.size();i++){
                if(userList[i] == '\0') {
                    userList[i] = ' ';  // instead of zero bytes -> spaces.
                }
            }
            cout << "ACK " + to_string(messageOpcode) + ' ' + to_string(numOfUsers) + ' ' + userList<<endl;
        }
        if(messageOpcode == 7){   //USERLIST
            _connectionHandler.getBytes(&result[4],2);
            char numOfUsersArr[2];
            numOfUsersArr[0] = result[4];
            numOfUsersArr[1] = result[5];
            short numOfUsers = bytesToShort(numOfUsersArr);
            string userList;
            int userCounter = 0;
            while(userCounter<numOfUsers) {
                _connectionHandler.getFrameAscii(userList, 0);  //convert the userList into string
                userCounter+=1;
            }
            for(unsigned int i = 0; i<userList.size();i++){
                if(userList[i] == '\0'){
                    userList[i] = ' '; // instead of zero bytes -> spaces.
                }
            }
            string output = "ACK " + to_string(messageOpcode) + " " + to_string(numOfUsers)+ " " + userList;
            cout << output <<endl;
        }
        if(messageOpcode == 8){   //Stat

            _connectionHandler.getBytes(&result[4],2); //get the num of post
            char numOfPostsArray[2];
            numOfPostsArray[0] = result[4];
            numOfPostsArray[1] = result[5];
            short numOfPosts = bytesToShort(numOfPostsArray);

            _connectionHandler.getBytes(&result[6],2); //get the num of followers
            char numOFollowersArray[2];
            numOFollowersArray[0] = result[6];
            numOFollowersArray[1] = result[7];
            short numOFollowers = bytesToShort(numOFollowersArray);

            _connectionHandler.getBytes(&result[8],2); //get the num of following
            char numOFollowingArray[2];
            numOFollowingArray[0] = result[8];
            numOFollowingArray[1] = result[9];
            short numOFollowing = bytesToShort(numOFollowingArray);

            string output = "ACK " + to_string(messageOpcode) + " " + to_string(numOfPosts) + " "+ to_string(numOFollowers)+ " " +to_string(numOFollowing);
            cout << output << endl;
        }
    }
    if(opcode == 9){   //NOTIFICATION
        _connectionHandler.getBytes(&result[2],1); //NotificationType is one byte.
        char NotificationType[1];
        NotificationType[0] = result[2];
        string type;
        if(NotificationType[0]== 0) {
            type = "PM ";
        }
        else if (NotificationType[0]== 1){
            type = "Public ";
        }

        string sender;
        _connectionHandler.getFrameAscii(sender,'\0');
        sender = sender.substr(0, sender.size()-1);
        string content;
        _connectionHandler.getFrameAscii(content,'\0');
        content = content.substr(0, content.size()-1);  //from here and on it's the content itself, cuz we get the rest from getFrameAscii
        for(unsigned int i = 0; i<content.size();i++){
            if(content[i] == '\0'){
                content[i] = ' '; // instead of zero bytes -> spaces.
            }
        }
        string output = "NOTIFICATION " + type + sender + " " + content;
                cout << output << endl;
    }
    return stop;
}



short outputThread::bytesToShort(char *bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
