package Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.net.*;

import Exceptions.ImplementationError;
import Exceptions.TryAgainException;

public class ImageFinder {
    private static final String[] KEYS = {"************************************", "************************************", "************************************", "************************************"};
    private static final String ENGINE_ID = "******************";
    private static final int RESULTS_NUM = 10;
    
    private static final String[] EXCLUDED = {"github"};
        
    // Returns [0] the image URL of the first result and [1] a backup link
    public static String[] getImage(String searchParameters) throws InterruptedException {
        if (isUrl(searchParameters))
            return new String[] { searchParameters, searchParameters };
                
        for (String key : KEYS) {
            try {
                return attemptGetImage(searchParameters, key);
            }
            catch (TryAgainException e) {
                // Keep trying until we get a result or run out of tries
            }
            
            // Check for interrupts
            if (Thread.interrupted()) throw new InterruptedException();
        }
        // No key worked, give up
        throw new InterruptedException();
    }
    
    private static String[] attemptGetImage(String searchParameters, String key) throws TryAgainException {
        String query =
            "key=" + key +
            "&cx=" + ENGINE_ID +
            "&q=" + trim(searchParameters) +
            "&searchType=image" +
            //"&safe=active" +
            "&num=" + RESULTS_NUM;
            
        URI uri;
        try {
            uri = new URI("https", "www.googleapis.com", "/customsearch/v1", query, null);
        } catch (URISyntaxException e) {
            throw new ImplementationError("Invalid URI: " + e.getMessage());
        }
        
        
        
        // get the JSON from the URL
        String json = getJson(uri);
        
        // pseudo-parse the JSON
        ArrayList<String> links = getAttributes(json, "link");
        ArrayList<String> context = getAttributes(json, "contextLink");
        ArrayList<String> backupLinks = getAttributes(json, "thumbnailLink");
        if (links.size() != context.size() || links.size() != backupLinks.size()) {
            throw new ImplementationError("Incorrect number of parsed attributes");
        }
        for (int i = 0; i < links.size(); ++i) {
            boolean isExcluded = false;
            for (String excluded : EXCLUDED) {
                if (context.get(i).contains(excluded)) {
                    isExcluded = true;
                    break;
                }
            }
            if (!isExcluded) {
                return new String[] { links.get(i), backupLinks.get(i) };
            }
        }
        // No image found, try again with a different key
        throw new TryAgainException();
    }
    
    private static String getJson(URI uri) throws TryAgainException {
        StringBuilder sb = new StringBuilder();
        URLConnection urlCon;
        try {
            URL urlObj = uri.toURL();
            urlCon = urlObj.openConnection();
        } catch (IOException e) {
            throw new RuntimeException("Could not open URL [" + uri.toASCIIString() + "]: " + e.getMessage());
        }
        InputStream connectionInputStream = null;
        try {
            connectionInputStream = urlCon.getInputStream();
        } catch (IOException e) {
            // Google refused connection (probably because of too many requests)
            throw new TryAgainException();
        }
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionInputStream));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new ImplementationError("Error while reading from URL [" + uri.toASCIIString() + "]: " + e.getMessage());
        }
        return sb.toString();
    }
    
    private static ArrayList<String> getAttributes(String json, String attribute) {
        final String MATCH = "\"" + attribute + "\": \"";
        ArrayList<String> urls = new ArrayList<>();
        
        // find all instances of IMAGE_ATTR
        int index = json.indexOf(MATCH);
        while (index != -1) {
            // get the URL
            int startIndex = index + MATCH.length();
            int endIndex = json.indexOf("\"", startIndex);
            String url = json.substring(startIndex, endIndex);
            urls.add(url);
            // find the next instance of IMAGE_ATTR
            index = json.indexOf(MATCH, endIndex);
        }
        return urls;
    }
    
    private static String trim(String s) {
        // if the image begins with /, remove it
        if (s.charAt(0) == '/') {
            s = s.substring(1);
        }
        // remove the extension
        int dot = s.lastIndexOf(".");
        if (dot != -1) {
            s = s.substring(0, dot);
        }
        return s;
    }
    
    private static boolean isUrl(String s) {
        boolean start = s.startsWith("http://") || s.startsWith("https://");
        boolean end = s.endsWith(".jpg") || s.endsWith(".png") || s.endsWith(".gif");
        return start && end;
    }
}
