//
// Created by yoavgi@wincs.cs.bgu.ac.il on 1/2/19.
//

#ifndef CLIENT_OUTPUTTHREAD_H
#define CLIENT_OUTPUTTHREAD_H

#include "connectionHandler.h"

class outputThread {
private:
    int * kill;
    ConnectionHandler & _connectionHandler;
public:

    outputThread(ConnectionHandler & connectionHandler , int *Terminate);
    void run();
    bool DecodeMsg(char* result);
    short bytesToShort(char *bytesArr);

};


#endif //CLIENT_OUTPUTTHREAD_H
