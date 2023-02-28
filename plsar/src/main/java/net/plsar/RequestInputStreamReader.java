package net.plsar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class RequestInputStreamReader {

    InputStream requestInputStream;
    ByteArrayOutputStream byteArrayOutputStream;

    public void read() throws IOException {
        byteArrayOutputStream = new ByteArrayOutputStream();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 12);
        int bytesRead;
        while((bytesRead = requestInputStream.read(byteBuffer.array())) != -1){
            byteArrayOutputStream.write(byteBuffer.array(), 0, bytesRead);
            if(requestInputStream.available() == 0)break;
        }
    }

    public void setRequestInputStream(InputStream requestInputStream) {
        this.requestInputStream = requestInputStream;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }
}
