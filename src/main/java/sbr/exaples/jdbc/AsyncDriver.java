package sbr.exaples.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author senyasdr
 */
public class AsyncDriver implements AutoCloseable {

    private final String username;
    private final String database;
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    public AsyncDriver(String username, String database, int port) throws IOException {
        this.username = username;
        this.database = database;
        this.socket = new Socket();
        socket.connect(new InetSocketAddress(port));
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        out.write(getConnParams());
        out.flush();
    }

    public void executeQuery(String s) throws IOException {
        out.write(decodeMsg(s));
        out.flush();
    }

    public String getResult() throws IOException {
        byte[] b = new byte[65532];
        in.read(b);
        return new String(b, StandardCharsets.UTF_8);
    }

    private byte[] getConnParams() {
        String params = String.format("user\0%s\0database\0%s\0\0", username, database);
        ByteBuffer buf = ByteBuffer.allocate(4 + 4 + params.length());
        buf.putInt(buf.capacity());
        buf.putInt(196608);
        buf.put(params.getBytes());
        return buf.array();
    }

    private static byte[] decodeMsg(String msg) {
        ByteBuffer buf = ByteBuffer.allocate(1 + 4 + msg.length() + 1);
        buf.put((byte) 'Q');
        buf.putInt(msg.length() + 5);
        buf.put(msg.getBytes());
        buf.put((byte)'\0');
        return buf.array();
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
