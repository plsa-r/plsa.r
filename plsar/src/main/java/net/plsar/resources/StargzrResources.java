package net.plsar.resources;

import net.plsar.StargzrException;
import net.plsar.ViewConfig;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StargzrResources {

    public String getGuid(int n) {
        String CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder uuid = new StringBuilder();
        int divisor = n/4;
        Random rnd = new Random();
        for(int z = 0; z < n;  z++) {
            if( z % divisor == 0 && z > 0) {
                uuid.append("-");
            }
            int index = (int) (rnd.nextFloat() * CHARS.length());
            uuid.append(CHARS.charAt(index));
        }
        return uuid.toString();
    }

    public String getDefaultGuid(int n) {
        String CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder uuid = new StringBuilder();
        Random rnd = new Random();
        for(int z = 0; z < n;  z++) {
            int index = (int) (rnd.nextFloat() * CHARS.length());
            uuid.append(CHARS.charAt(index));
        }
        return uuid.toString();
    }

    public String getSecurityGuid(int n) {
        String CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder uuid = new StringBuilder();
        int divisor = n/4;
        Random rnd = new Random();
        for(int z = 0; z < n;  z++) {
            if( z % divisor == 0 && z > 0) {
                uuid.append(".");
            }
            int index = (int) (rnd.nextFloat() * CHARS.length());
            uuid.append(CHARS.charAt(index));
        }
        return uuid.toString();
    }

    public Long getTime(int days){
        LocalDateTime ldt = LocalDateTime.now().minusDays(days);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(this.getDateFormat());
        String date = dtf.format(ldt);
        return Long.valueOf(date);
    }

    public String getSecurityAttribute(Map<String, String> headers, String id){
        String value = null;
        String cookies = headers.get("cookie");
        if(cookies != null) {
            String[] bits = cookies.split(";");
            for (String completes : bits) {
                String[] parts = completes.split("=");
                String key = parts[0].trim();
                if (parts.length == 2) {
                    if (key.equals(id)) {
                        value = parts[1].trim();
                    }
                }
            }
        }
        return value;
    }

    public String getDateFormat() {
        return "yyyyMMddHHmmssSSS";
    }

    private String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

    public String getRedirect(String uri){
        String[] redirectBits = uri.split(":");
        if(redirectBits.length > 1)return redirectBits[1];
        return null;
    }

    public ByteArrayOutputStream getViewFileCopy(String viewKey, ConcurrentMap<String, byte[]> viewBytesMap) {
        if(viewBytesMap.containsKey(viewKey)){
            byte[] fileBytes = viewBytesMap.get(viewKey);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[fileBytes.length];
            int bytesRead;
            try {
                while ((bytesRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
                    outputStream.write(bytes, 0, bytesRead);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return outputStream;
        }
        return null;
    }

    public ConcurrentMap<String, byte[]> getViewBytesMap(ViewConfig viewConfig) throws StargzrException, FileNotFoundException {
        ConcurrentMap<String, byte[]> viewFilesBytesMap = new ConcurrentHashMap<>();

        Path viewsPath = Paths.get("src", "main", viewConfig.getViewsPath());
        File viewsDirectory = new File(viewsPath.toString());

        if(viewsDirectory.isDirectory()) {
            File[] viewFiles = viewsDirectory.listFiles();
            getFileBytesMap(viewFiles, viewConfig, viewFilesBytesMap);
        }

        Path resourcesPath = Paths.get("src", "main", viewConfig.getViewsPath(), viewConfig.getResourcesPath());
        File resourcesDirectory = new File(resourcesPath.toString());

        if(resourcesDirectory.isDirectory()) {
            File[] resourceFiles = resourcesDirectory.listFiles();
            getFileBytesMap(resourceFiles, viewConfig, viewFilesBytesMap);
        }

        return viewFilesBytesMap;
    }

    ConcurrentMap<String, byte[]> getFileBytesMap(File[] viewFiles, ViewConfig viewConfig, ConcurrentMap<String, byte[]> viewFilesBytesMap) throws FileNotFoundException {
        for (File viewFile : viewFiles) {

            if(viewFile.isDirectory()){
                File[] directoryFiles = viewFile.listFiles();
                getFileBytesMap(directoryFiles, viewConfig, viewFilesBytesMap);
                continue;
            }

            InputStream fileInputStream = new FileInputStream(viewFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead;
            try {
                while ((bytesRead = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                    outputStream.write(bytes, 0, bytesRead);
                }

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                byte[] viewFileBytes = outputStream.toByteArray();
                String viewKey = viewFile.toString().replace("src" + File.separator + "main" + File.separator + viewConfig.getViewsPath(), "");
                viewFilesBytesMap.put(viewKey, viewFileBytes);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return viewFilesBytesMap;
    }
}
