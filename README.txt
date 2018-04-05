Author: Tom Shaw
Class : CS455
Assignment: PC2

#############################

Big Idea:

This program is meant as an emulation of a server that serves a high number of clients. The central concept is that each of the clients will connect to the server and send ~8kb of random bytes to the server who will hash the whole package and return the resulting hash code.

What makes this project Unique?
The server uses Java NIO which reduces the number of context switches meaning that the server can handle high numbers of clients without sacrificing throughput.

#############################

Important information:

The makefile needs to be called from outside the src directory to work properly.

Java files have only been tested to run while the working directory is ~/src/.

The only output from the programs are diagnostics and the server outputs when a connection has been made.

To run the files the commands from the description should be enough, if not the following commands are what I used.
java /cs455/scaling/server/Server <port> <number of threads>
java /cs455/scaling/client/Client <ip posted on the computers> <port> <message rate>

###############################

Class Descriptions:

Server: This class receives communications from clients and returns the hash value of the message to the client.

Client: Maintains a connection with server and sends a message consisting of 8kb random bytes and receives a hash from the server.

ThreadPool: This class holds and starts the threads that are used for running the messages.

ThreadpoolManager: handles the NIO connection for the server. Creates work units for the threadPool to complete.

Worker: The runnable class used to process work units. Handles reading, computing, and sending.

WorkUnit: Contains a run method and a SelectionKey, the run method contains all the work that needs to be done on this message.

BlockingList: This class is a threadsafe wrapper for LinkedList that only has a couple methods to interact with.
