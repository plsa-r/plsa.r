package net.plsar.model;

public class NetworkHeader {
    String header;
    String content;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NetworkHeader(String header, String content) {
        this.header = header;
        this.content = content;
    }
}
