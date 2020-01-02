package fraglab.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class LedRunnable implements Runnable {

    private final Integer[] rgb;
    private final String server;

    LedRunnable(Integer[] rgb, String server) {
        this.rgb = rgb;
        this.server = server;
    }


    @Override
    public void run() {
        InputStream is = null;
        try {
            String value = "" + this.rgb[0] + this.rgb[1] + this.rgb[2];
            URL url = new URL(String.format("%s/light/%s", server, value));
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
