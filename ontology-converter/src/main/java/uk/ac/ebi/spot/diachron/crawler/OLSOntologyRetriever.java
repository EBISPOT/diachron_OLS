package uk.ac.ebi.spot.diachron.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by olgavrou on 06/11/2015.
 */
public class OLSOntologyRetriever {


    private Logger log = LoggerFactory.getLogger(getClass());



    public InputStream getOntology(String fileLocation) {
        HttpURLConnection conn = null;
        URLConnection connection = null;
        try {
            URL url = new URL(fileLocation);
            connection = url.openConnection();
            conn = (HttpURLConnection) connection;
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/html");
            conn.connect();
            // handle redirects
            int responseCode = conn.getResponseCode();
            if (responseCode == 302 || responseCode == 301) {
                String location = conn.getHeaderField("Location");
                if (location != null) {
                    conn.disconnect();
                    ((HttpURLConnection) connection).disconnect();
                    url = new URL(location);
                    connection = url.openConnection();
                    if (location.contains("ftp") || location.contains("FTP")){
                        return connection.getInputStream();
                    }
                    conn = (HttpURLConnection) connection;
                    conn.connect();

                }
            }
            return conn.getInputStream();
        } catch (IOException | ClassCastException e) {
            log.info(e.toString());
            return null;
        }
    }

}
