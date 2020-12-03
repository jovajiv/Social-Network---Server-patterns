package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingConnectionHandler<T> implements ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final SocketChannel chan;
    private final Reactor reactor;
    private final ConnectionsImpl<T> connections;           //TODO should be final ?
    private int connectionID;

    public NonBlockingConnectionHandler(
            MessageEncoderDecoder<T> reader,
            BidiMessagingProtocol<T> protocol,
            ConnectionsImpl<T> connections,
            SocketChannel chan,
            Reactor reactor) {
        this.chan = chan;
        this.encdec = reader;
        this.protocol = protocol;
        this.connections=connections;
        this.reactor = reactor;
        this.connectionID=connections.connect( this);       // give this Connection a unique ID , map it to the connections table
        this.protocol.start(connectionID,connections);              // update the protocol with the current connectionID.

    }

    public Runnable continueRead() {
        ByteBuffer buf = leaseBuffer();
        if (!protocol.shouldTerminate()) {
            boolean success = false;
            try {
                success = chan.read(buf) != -1;
            } catch (IOException ex) {

            }

            if (success) {
                buf.flip();
                return () -> {
                    try {
                        while (buf.hasRemaining()) {
                            T nextMessage = encdec.decodeNextByte(buf.get());
                            if (nextMessage != null) {
                                protocol.process(nextMessage);

                            }
                        }
                    } finally {
                        releaseBuffer(buf);
                    }
                };
            } else {
                releaseBuffer(buf);
                close();
                return null;
            }

        }
        return null;
    }

    public void close() {
        try {
            chan.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isClosed() {
        return !chan.isOpen();
    }

    public void continueWrite() {
        while (!writeQueue.isEmpty()  ) {
            try {
                ByteBuffer top = writeQueue.peek();
                chan.write(top);
                if (top.hasRemaining()) {
                    return;
                } else {
                    writeQueue.remove();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                close();
            }
        }

        if (writeQueue.isEmpty()) {
            if (protocol.shouldTerminate()){
                connections.disconnect(connectionID);
                close();
            }
            else reactor.updateInterestedOps(chan, SelectionKey.OP_READ);
        }
    }

    private static ByteBuffer leaseBuffer() {
        ByteBuffer buff = BUFFER_POOL.poll();
        if (buff == null) {
            return ByteBuffer.allocateDirect(BUFFER_ALLOCATION_SIZE);
        }

        buff.clear();
        return buff;
    }

    private static void releaseBuffer(ByteBuffer buff) {
        BUFFER_POOL.add(buff);
    }


    public void send(T msg) {
        if(!protocol.shouldTerminate()) {
            writeQueue.add(ByteBuffer.wrap(encdec.encode(msg)));
            reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }

    }
}
