package fraglab.simple;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

class HealthCheckTimerTask extends TimerTask {

    private final ExecutorService executorService;
    private final Consumer<Integer> consumer;
    private final String server;

    public HealthCheckTimerTask(ExecutorService executorService, Consumer<Integer> consumer, String server) {
        this.executorService = executorService;
        this.consumer = consumer;
        this.server = server;
    }

    @Override
    public void run() {
        Future<Boolean> future = executorService.submit(() -> {
            URL url = new URL(String.format("%s/health", server));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d("health", "Health Check: Connect");
            return connection.getResponseCode() == 200;
        });

        try {
            if (future.get(3L, TimeUnit.SECONDS)) {
                Log.d("health", "Health Check: OK");
                consumer.accept(R.color.colorStateOK);
            } else {
                Log.d("health", "Health Check: NOK");
                consumer.accept(R.color.colorStateNOK);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.d("health", "Health Check: NOK");
            consumer.accept(R.color.colorStateNOK);
        }

    }
}
