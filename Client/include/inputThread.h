

#ifndef CLIENT_INPUTTHREAD_H
#define CLIENT_INPUTTHREAD_H

#include "connectionHandler.h"

using namespace std;

class inputThread {
private:
    int * kill;
    ConnectionHandler & _connectionHandler;
public:
    inputThread(ConnectionHandler & connectionHandler , int *Terminate);

    void Encode(char* result, string msg);

    unsigned long msgSize(string msg);

    void shortToBytes(short num, char* bytesArr);

    void EncodeREGISTER (char* result, string content);
    void EncodeLOGIN(char* result, string content);
    void EncodeLOGOUT(char* result, string content);
    void EncodeFOLLOW(char* result,string content);
    void EncodePOST(char* result,string content);
    void EncodePM(char* result,string content);
    void EncodeUSERLIST(char* result,string content);
    void EncodeSTAT(char* result,string content);



};


#endif
