
#include "../include/connectionHandler.h"
#include "../include/inputThread.h"
#include "../include/outputThread.h"
#include <thread>

using std::string;
using namespace std;

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    int * kill = new int(1); //todo can't use boolean because we need 3 options to kill. if user will type "LOGOUT" and he is not loged in - ERROR will come and we should not kill Thread.

    inputThread input (connectionHandler,kill);  //as seen in PS 10
    outputThread output(connectionHandler,kill);

    thread socketReader(&outputThread::run,&output); //reader for outputThread.

    //From here we will see the rest of the ehco client implementation:
    while (true) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);

        unsigned long msg_size = input.msgSize(line);
        char user_input[msg_size];
        input.Encode(user_input,line);
        if (!connectionHandler.sendBytes(user_input,(int)msg_size)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        while (*kill == 0) {
            // if log out will return ack - kill will be changed to -1. else - it will be back 1.
            //meanwhile, we want to do busy wait.
        }

        if(*kill == -1 ) {//the server return ACK for LOGOUT.
            break;
        }


    }
    socketReader.join();// make sure the socketReader will terminate before the main Thread

    delete kill;

    return 0;
}