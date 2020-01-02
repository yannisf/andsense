package fraglab.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class LightIntensityRunnable implements Runnable {

    private final boolean lowLight;
    private final String server;


    LightIntensityRunnable(boolean lowLight, String server) {
        this.lowLight = lowLight;
        this.server = server;
    }


    @Override
    public void run() {
        InputStream is = null;
        try {
            String value = lowLight ? "true" : "false";
            URL url = new URL(String.format("%s/low/%s", server, value));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
