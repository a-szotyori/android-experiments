package net.gini.android.experiments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrientationActivity extends AppCompatActivity {

    @Bind(R.id.text_orientation)
    TextView mTextOrientation;
    @Bind(R.id.text_orientation2)
    TextView mTextOrientation2;
    @Bind(R.id.text_vector)
    TextView mTextVector;

    private Display mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();

        ButterKnife.bind(this);

        SensorManager sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);
        RotationObserver rotationObserver = new RotationObserver(sensorManager);
        rotationObserver.startObserving();
        rotationObserver.setListener(new RotationObserver.Listener() {
            @Override
            public void onValuesChanged(float[] values, RotationObserver.Rotation rotation, RotationObserver.Rotation portraitOrLandscapeOrientation) {
                mTextOrientation.setText(rotation.toString());
                mTextOrientation2.setText(portraitOrLandscapeOrientation.toString());
                mTextVector.setText("x: " + values[0] + "\ny: " + values[1] + "\nz: " + values[2]);
            }
        });
    }

    public static class RotationObserver implements SensorEventListener {

        public enum Rotation {
            PORTRAIT(0),
            PORTRAIT_USPIDE_DOWN(180),
            /**
             * Home button on the left
             */
            LANDSCAPE_LEFT(90),
            /**
             * Home button on the right
             */
            LANDSCAPE_RIGHT(270),
            /**
             * Screen is facing up.
             */
            FACE_UP(0),
            /**
             * Screen is facing down.
             */
            FACE_DOWN(0),
            UNKNOWN(0);

            private int mDegrees;

            Rotation(int degrees) {
                mDegrees = degrees;
            }

            public int getDegrees() {
                return mDegrees;
            }
        }

        public interface Listener {
            void onValuesChanged(float[] values, RotationObserver.Rotation rotation, RotationObserver.Rotation portraitOrLandscapeOrientation);
        }

        /**
         * Maximum acceleration on the z axis. Assuming 9 and is updated if larger acceleration is received.
         */
        private float mMaxZ = 9f;
        /**
         * Minimum acceleration threshold, which when exceeded by one of the axis the rotation is updated.
         */
        private float mMinThresholdXY = 3.5f;
        /**
         * Maximum acceleration threshold, which when exceeded by one of the axis the rotation is updated.
         */
        private float mMaxThresholdXY = 6.5f;

        private Listener mListener = new Listener() {
            @Override
            public void onValuesChanged(float[] values, Rotation rotation, Rotation portraitOrLandscapeOrientation) {
            }
        };

        private final SensorManager mSensorManager;
        private Sensor mAccelerationSensor;
        private Rotation mRotation = Rotation.UNKNOWN;
        private Rotation mPortraitOrLandscapeRotation = Rotation.PORTRAIT;

        public RotationObserver(final SensorManager sensorManager) {
            this.mSensorManager = sensorManager;
            mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        public void setListener(Listener listener) {
            mListener = listener;
        }

        public void startObserving() {
            if (mAccelerationSensor != null) {
                mSensorManager.registerListener(this, mAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        public void stopObserving() {
            mSensorManager.unregisterListener(this);
        }

        public synchronized Rotation getRotation() {
            return mRotation;
        }

        private synchronized void setRotation(Rotation rotation) {
            mRotation = rotation;
        }

        private synchronized void setPortraitOrLandscapeRotation(Rotation rotation) {
            mPortraitOrLandscapeRotation = rotation;
        }

        public synchronized Rotation getPortraitOrLandscapeRotation() {
            return mPortraitOrLandscapeRotation;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            sensorChanged(event.values);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        /**
         * Updates the rotation using the accelerometer values. The values represent the amount of acceleration along
         * 3 axis:
         * <pre>
         *  ______________________________
         * |               -y             |
         * |               ^       -z     |
         * |               |       _      |
         * |               |     _        |
         * |               |   _          |
         * |               | _            |
         * | +x <--------------------> -x |
         * |             _ |              |
         * |           _   |              |
         * |         _     |              |
         * |       _       |              |
         * |       +z      |              |
         * |               +y             |
         * |                              |
         * |                              |
         * |                              |
         * |______________________________|
         * |           ________           |
         * |          |        |          |
         * |          |________|          |
         * |______________________________|
         * </pre>
         * <p>
         * Rotation is updated whenever the acceleration on one of the axis exceeds a threshold. The threshold for
         * x and y is dynamically set between a fixed interval depending on the acceleration on the z axis. For the z
         * axis the threshold is fixed.
         * </p>
         * <p>
         * The dynamic threshold for x and y is used to increase the sensitivity when the device is being moved to a
         * horizontal position or being held close to horizontal. This is important for us to detected the portrait
         * and landscape orientation near horizontal, too.
         * </p>
         *
         * @param values accelerometer event values
         */
        void sensorChanged(float[] values) {
            float x = values[0];
            float y = values[1];
            float z = values[2];

            mMaxZ = Math.max(Math.abs(z), mMaxZ);
            float thresholdXY = Math.min(mMaxThresholdXY, Math.max(mMaxZ - Math.abs(z), mMinThresholdXY));

            if (Math.abs(x) > thresholdXY) {
                if (x > 0) {
                    setRotation(Rotation.LANDSCAPE_RIGHT);
                    setPortraitOrLandscapeRotation(Rotation.LANDSCAPE_RIGHT);
                } else {
                    setRotation(Rotation.LANDSCAPE_LEFT);
                    setPortraitOrLandscapeRotation(Rotation.LANDSCAPE_LEFT);
                }
            } else if (Math.abs(y) > thresholdXY) {
                if (y > 0) {
                    setRotation(Rotation.PORTRAIT);
                    setPortraitOrLandscapeRotation(Rotation.PORTRAIT);
                } else {
                    setRotation(Rotation.PORTRAIT_USPIDE_DOWN);
                    setPortraitOrLandscapeRotation(Rotation.PORTRAIT_USPIDE_DOWN);
                }
            } else if (Math.abs(z) > 8) {
                if (z > 0) {
                    setRotation(Rotation.FACE_UP);
                } else {
                    setRotation(Rotation.FACE_DOWN);
                }
            }

            mListener.onValuesChanged(values, mRotation, mPortraitOrLandscapeRotation);
        }
    }
}
