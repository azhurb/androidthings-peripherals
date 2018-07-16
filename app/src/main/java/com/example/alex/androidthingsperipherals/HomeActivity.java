package com.example.alex.androidthingsperipherals;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class HomeActivity extends Activity {
    private static final String TAG = "HomeActivity";
    private static final String BUTTON_PIN_NAME = "GPIO6_IO14";

    // GPIO connection to button input
    private Gpio mButtonGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManager pioManager = PeripheralManager.getInstance();
        Log.d(TAG, "Available GPIO: " + pioManager.getGpioList());

        try {
            // Create GPIO connection.
            mButtonGpio = pioManager.openGpio(BUTTON_PIN_NAME);

            // Configure as an input, trigger events on every change.
            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            // Value is true when the pin is LOW
            mButtonGpio.setActiveType(Gpio.ACTIVE_LOW);
            // Register the event callback.
            mButtonGpio.registerGpioCallback(mCallback);
        } catch (IOException e) {
            Log.w(TAG, "Error opening GPIO", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close the button
        if (mButtonGpio != null) {
            mButtonGpio.unregisterGpioCallback(mCallback);
            try {
                mButtonGpio.close();
            } catch (IOException e) {
                Log.w(TAG, "Error closing GPIO", e);
            }
        }
    }

    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                Log.i(TAG, "GPIO changed, button " + gpio.getValue());
            } catch (IOException e) {
                Log.w(TAG, "Error reading GPIO");
            }

            // Return true to keep callback active.
            return true;
        }
    };
}
