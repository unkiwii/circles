package toys.male.circles;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Circles extends AppCompatActivity {
    private View content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circles);
        content = findViewById(R.id.fullscreen_content);
        content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // get pointer index from the event object
                int pointerIndex = event.getActionIndex();

                // get pointer ID
                int pointerId = event.getPointerId(pointerIndex);

                // get masked (not specific to a pointer) action
                int maskedAction = event.getActionMasked();

                if (maskedAction == MotionEvent.ACTION_DOWN || maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
                    down(event, pointerIndex);
                }

                return true;
            }
        });
    }

    private void down(MotionEvent event, int index) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View c = vi.inflate(R.layout.circle, null);
        final Circle circle = c.findViewWithTag("circle");
        circle.setX(event.getX(index) - (circle.getWidth()));
        circle.setY(event.getY(index) - (circle.getHeight()));

        ((ViewGroup)content).addView(c);

        circle.animate()
                .setDuration(1000)
                .scaleX(10f)
                .scaleY(10f);

        c.animate()
                .setDuration(1000)
                .alpha(0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        ((ViewGroup)c.getParent()).removeView(c);
                    }
                });

        play(colorToFrequency(circle.getColor()), 0.2f);
    }

    final int MIN_FREQ = 400;
    final int MAX_FREQ = 1600;
    private int colorToFrequency(int color) {
        return color % (MAX_FREQ - MIN_FREQ) + MIN_FREQ;
    }

    Map<Pair<Integer, Integer>, short[]> buffers = new HashMap<>();
    private void play(final int frequency, final float duration) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int mBufferSize = AudioTrack.getMinBufferSize(44100,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_8BIT);

                AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        mBufferSize, AudioTrack.MODE_STREAM);

                int samples = (int)(duration * 44100);

                short[] mBuffer;
                Pair<Integer, Integer> p = new Pair<>(frequency, samples);
                if (buffers.containsKey(p)) {
                    mBuffer = buffers.get(p);
                } else {
                    mBuffer = new short[samples];
                    for (int i = 0; i < mBuffer.length; i++) {
                        double sound = Math.sin((2.0*Math.PI * i/(44100/frequency)));
                        mBuffer[i] = (short) (sound * Short.MAX_VALUE);
                    }
                    buffers.put(p, mBuffer);
                }

                mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
                mAudioTrack.play();
                mAudioTrack.write(mBuffer, 0, mBuffer.length);
                mAudioTrack.stop();
                mAudioTrack.release();
            }
        }).start();
    }
}
