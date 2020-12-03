#include "../include/inputThread.h"


using namespace std;

inputThread::inputThread(ConnectionHandler &connectionHandler , int *Terminate): kill(Terminate),_connectionHandler(connectionHandler) {}


unsigned long inputThread::msgSize(string msg) {

    if(msg == "LOGOUT" || msg == "USERLIST"){
        return 2; //cuz no need for more than who bytes in theses cases.
    }
    unsigned long  space = msg.find_first_of(' ');
    string command = msg.substr(0, space); //the command
    msg = msg.substr(space+1,msg.size()); //the rest of the message

    if(command == "REGISTER"){ //zero byte in the middle counting! no need for him. so 2 for opcode +1 for terminating zero byte. total = 3
        return msg.size()+3;
    }
    else if(command == "LOGIN"){ //zero byte in the middle counting! no need for him. so 2 for opcode +1 for terminating zero byte. total = 3
        return msg.size()+3;
    }

    else if(command == "FOLLOW"){
        unsigned long FollowOrUnfollow = msg.find_first_of(' '); //follow or unfollow num
        msg = msg.substr(FollowOrUnfollow+1,msg.size());
        unsigned long NumOfUsers = msg.find_first_of(' '); //num of users num
        msg = msg.substr(NumOfUsers+1, msg.size()); //users to follow

        return msg.size()+6 ;  //2 for opcode + 1  for follow + 2  for numOfUsers + 1 - for the '\0' + size for users. total = 6
    }
    else if(command == "POST"){
        return msg.size()+3; // 2 for opcode +1 for terminating zero byte. total = 3
    }
    else if(command == "PM"){
        return msg.size()+3; //zero byte in the middle counting! no need for him. so 2 for opcode +1 for terminating zero byte. total = 3
    }

    else { //STAT
        return msg.size()+3; // 2 for opcode +1 for terminating zero byte. total = 3
    }
}


void inputThread::Encode(char* result, string msg){

    unsigned long space = msg.find_first_of(' ');
    string command = msg.substr(0,space); //the command
    msg = msg.substr(space+1); //the rest of the message
    if(command == "REGISTER"){
        EncodeREGISTER(result,msg);
    }
    else if(command == "LOGIN"){
        EncodeLOGIN(result,msg);
    }
    else if(command == "LOGOUT"){
        EncodeLOGOUT(result,msg);
    }
    else if(command == "FOLLOW"){
        EncodeFOLLOW(result,msg);
    }
    else if(command == "POST"){
        EncodePOST(result,msg);
    }
    else if(command == "PM"){
        EncodePM(result,msg);
    }
    else if(command == "USERLIST"){
        EncodeUSERLIST(result,msg);
    }
    else { //must be STAT
        EncodeSTAT(result,msg);
    }

}

void inputThread:: shortToBytes(short num, char* bytesArr) { //given in SPL site
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~all of the commands Encoders here~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
void inputThread:: EncodeREGISTER(char* charMsg, string content){

    shortToBytes(1,charMsg); //put the opcode in charMsg

    for(unsigned int  i = 0; i<content.size(); i++){
        if(content[i] == ' '){//separated between the username and password
            charMsg[i+2] = '\0'; //+2 cuz of the first two bytes of opcode
        }
        else{
            charMsg[i+2] = content[i]; //no space, normal put.
        }
    }
    charMsg[content.size()+2]='\0';

}
void inputThread:: EncodeLOGIN(char* charMsg, string content){

    shortToBytes(2,charMsg); //put the opcode in charMsg

    for(unsigned int  i = 0; i<content.size(); i++){
        if(content[i] == ' '){  //separated between the username and password
            charMsg[i+2] = '\0'; //+2 cuz of the first two bytes of opcode
        }
        else{
            charMsg[i+2] = content[i]; //no space, normal put.
        }

    }
    charMsg[content.size()+2]='\0';

}

void inputThread:: EncodeLOGOUT(char* charMsg, string content){

    shortToBytes(3,charMsg); //put the opcode in charMsg
    *kill = 0; //cuz of logout! 0 cuz not sure if terminate of not. might be not if ERROR will accure.

}

void inputThread::EncodeFOLLOW(char* charMsg,string content) {

    shortToBytes(4,charMsg); //put the opcode in charMsg

    //follow or unfollow
    charMsg[2] = content[0]-'0';//content[0] = 48 or 49 - 1 or 0 in ASCII!! so we put -'0' to get 1 or 0.

    content = content.substr(2,content.size()); //remove the follow\unfollow
    unsigned long space = content.find_first_of(' ');//for NumOfUsers
    string numOfUsersString = content.substr(0, space);
    short numOfUsersShort = (short)stoi(numOfUsersString);//convert the string to int
    char numOfUsersArr[2];
    shortToBytes(numOfUsersShort,numOfUsersArr);//convert the numOfUsers into bytes (2)
    charMsg[3] = numOfUsersArr[0];
    charMsg[4] = numOfUsersArr[1];
    content = content.substr(space+1,content.size());//cut the numOfUsers from the content string

    char userListArr[content.size()];
    for(unsigned int i = 0; i<content.size(); i++){//convert the userLis into bytes array
        if(content[i] == ' '){
            userListArr[i] = '\0';
        }
        else{
            userListArr[i]  = content[i]; //no space, normal put.
        }
    }

    for(unsigned int i = 0; i < content.size();i++){
        charMsg[i+5] = userListArr[i];
    }
    charMsg[5+content.size()] = '\0';//the zero after the last person

}
void inputThread::EncodePOST(char* charMsg,string content) {
    shortToBytes(5,charMsg); //put the opcode in charMsg
    for(unsigned int i = 0; i<content.size(); i++){//convert the content of the post to the arry of char
        charMsg[i+2] = content[i]; //+2 cuz of the first two bytes of opcode
    }
    charMsg[content.size()+2] = '\0'; //zero byte to terminate msg.

}

void inputThread::EncodePM(char* charMsg,string content) {
    shortToBytes(6,charMsg); //put the opcode in charMsg
    unsigned long contentSize = content.size();
    unsigned long space = content.find_first_of(' ');
    string username = content.substr(0,space);//save the user name in a diffrent string
    for(unsigned int i = 0; i<username.size();i++){//put the username at the arry
        charMsg[2+i] = username[i]; //+2 cuz of the first two bytes of opcode
    }
    charMsg[2+username.size()] = '\0';//put a "0" after the username
    string Pm = content.substr(space+1,content.size());//save the content of the pm
    for(unsigned int i = 0;i < Pm.size(); i++){
        charMsg[i+3+username.size()] = Pm[i];
    }
    charMsg[contentSize+2] = '\0'; //put a zero at the last cell

}

void inputThread::EncodeUSERLIST(char* charMsg,string content) {

    shortToBytes(7,charMsg); //put the opcode in charMsg
    //no need to encode anything else.
}

void inputThread::EncodeSTAT(char* charMsg,string content) {

    shortToBytes(8,charMsg); //put the opcode in charMsg

    for(unsigned int i = 0; i < content.size();i++){
        charMsg[i+2] = content[i]; //+2 cuz of the first two bytes of opcode
    }
    charMsg[content.size()+2] = '\0';

}
