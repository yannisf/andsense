package fraglab.simple;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorValueWatcher implements TextWatcher {

    private final TextView textView;
    private final Supplier<Integer[]> rgbSupplier;
    private final Consumer<String> messageConsumer;
    private final ExecutorService executorService;
    private final String server;

    public ColorValueWatcher(TextView textView, Supplier<Integer[]> rgbSupplier, Consumer<String> messageConsumer, ExecutorService executorService, String server) {
        this.textView = textView;
        this.rgbSupplier = rgbSupplier;
        this.messageConsumer = messageConsumer;
        this.executorService = executorService;
        this.server = server;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        textView.clearFocus();
        try {
            int value = Integer.parseInt(s.toString());

            if (value < 0) s.replace(0, s.length(), "0");
            else if (value > 9) s.replace(0, s.length(), "9");
            else if (s.length() > 1) s.replace(0, s.length(), String.valueOf(value));
            Integer[] rgb = rgbSupplier.get();
            messageConsumer.accept(String.format("%s-%s-%s", rgb[0].toString(), rgb[1].toString(), rgb[2].toString()));
        } catch (NumberFormatException nfe) {
            s.replace(0, s.length(), "0");
        }
        LedRunnable task = new LedRunnable(rgbSupplier.get(), server);
        executorService.submit(task);
    }

}
