package fraglab.simple;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MainActivity extends AppCompatActivity {

    private final Timer timer = new Timer();
    private final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final TextView red = findViewById(R.id.red);
        final TextView green = findViewById(R.id.green);
        final TextView blue = findViewById(R.id.blue);
        final TextView message = findViewById(R.id.message);
        final TextView state = findViewById(R.id.state);
        state.setText(server());
        final Switch lowLightIntensitySwitch = findViewById(R.id.low_light_intensity_switch);
        initialize(red, green, blue, message, lowLightIntensitySwitch);

        final Consumer<Integer> consumer = color -> runOnUiThread(() -> state.setBackgroundResource(color));
        final TimerTask timerTask = new HealthCheckTimerTask(executorService, consumer, server());
        timer.scheduleAtFixedRate(timerTask, 1000L, 5000L);
    }

    private void initialize(TextView red, TextView green, TextView blue, TextView message, Switch lowLightIntensity) {
        final String colorValue = "0";
        final boolean isLowIntensity = true;

        red.setText(colorValue);
        green.setText(colorValue);
        blue.setText(colorValue);

        Supplier<Integer[]> rgbSupplier = () -> collect(red, green, blue);
        Consumer<String> messageConsumer = message::setText;

        red.addTextChangedListener(new ColorValueWatcher(red, rgbSupplier, messageConsumer, executorService, server()));
        green.addTextChangedListener(new ColorValueWatcher(green, rgbSupplier, messageConsumer, executorService, server()));
        blue.addTextChangedListener(new ColorValueWatcher(blue, rgbSupplier, messageConsumer, executorService, server()));

        lowLightIntensity.setChecked(isLowIntensity);
        lowLightIntensity.setOnCheckedChangeListener((buttonView, isChecked) ->
                executorService.submit(new LightIntensityRunnable(isChecked, server()))
        );
        executorService.submit(new LedRunnable(new Integer[]{0, 0, 0}, server()));
        executorService.submit(new LightIntensityRunnable(isLowIntensity, server()));
    }

    private Integer[] collect(TextView red, TextView green, TextView blue) {
        return new Integer[]{
                Integer.parseInt(red.getText().toString()),
                Integer.parseInt(green.getText().toString()),
                Integer.parseInt(blue.getText().toString())
        };
    }

    public String server() {
        return String.format("%s://%s:%s", getString(R.string.protocol), getString(R.string.server), getString(R.string.port));
    }

}
