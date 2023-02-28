package net.plsar.resources;

import java.util.HashMap;
import java.util.Map;

public class MimeResolver {

    String resourceUri;
    Map<String,String> MIME_MAP;

    public MimeResolver(String resourceUri){
        this.resourceUri = resourceUri;
        this.MIME_MAP = new HashMap<>();
        MIME_MAP.put("appcache", "text/cache-manifest");
        MIME_MAP.put("css", "text/css");
        MIME_MAP.put("gif", "image/gif");
        MIME_MAP.put("html", "text/html");
        MIME_MAP.put("js", "application/javascript");
        MIME_MAP.put("json", "application/json");
        MIME_MAP.put("jpg", "image/jpeg");
        MIME_MAP.put("jpeg", "image/jpeg");
        MIME_MAP.put("mp4", "video/mp4");
        MIME_MAP.put("mp3", "audio/mp3");
        MIME_MAP.put("pdf", "application/pdf");
        MIME_MAP.put("png", "image/png");
        MIME_MAP.put("svg", "image/svg+xml");
        MIME_MAP.put("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_MAP.put("xml", "application/xml");
        MIME_MAP.put("zip", "application/zip");
        MIME_MAP.put("md", "text/plain");
        MIME_MAP.put("txt", "text/plain");
        MIME_MAP.put("php", "text/plain");
    }

    public Boolean within(String key){
        return MIME_MAP.containsKey(key);
    }

    public String resolve() {
        String key = getExt(resourceUri).toLowerCase();
        return MIME_MAP.containsKey(key) ? MIME_MAP.get(key) : "text/html";
    }

    public static String getExt(String path) {
        int slashIndex = path.lastIndexOf('/');
        String basename = (slashIndex < 0) ? path : path.substring(slashIndex + 1);

        int dotIdx = basename.lastIndexOf('.');
        if (dotIdx >= 0) {
            return basename.substring(dotIdx + 1);
        } else {
            return "";
        }
    }
}