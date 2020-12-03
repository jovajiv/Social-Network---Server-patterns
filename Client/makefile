CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/BGSclient.o bin/outputThread.o bin/inputThread.o $(LDFLAGS) 

BGSclient: bin/connectionHandler.o bin/BGSclient.o bin/outputThread.o bin/inputThread.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp
	
bin/outputThread.o: src/outputThread.cpp
	g++ $(CFLAGS) -o bin/outputThread.o src/outputThread.cpp
	
bin/inputThread.o: src/inputThread.cpp
	g++ $(CFLAGS) -o bin/inputThread.o src/inputThread.cpp	
	
bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp
	
.PHONY: clean
clean:
	rm -f bin/* 
