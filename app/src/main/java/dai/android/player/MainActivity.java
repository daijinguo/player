package dai.android.player;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Surface mSurface;
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;

    private ImageView mVideoImage;
    private SeekBar mSeekBar;

    private Handler H = new Handler();
    private final Runnable TICKER = new Runnable() {
        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            H.postAtTime(TICKER, next);

            if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
                mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextureView = findViewById(R.id.viewTexture);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        mVideoImage = findViewById(R.id.videoImage);

        mSeekBar = findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                                      int width,
                                                      int height) {
                    mSurface = new Surface(surface);
                    PlayVideoThread playVideoThread = new PlayVideoThread();
                    playVideoThread.start();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                        int width,
                                                        int height) {
                    // the window size change will call this function
                    // now just ignore this
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    mSurface = null;
                    if (null != mMediaPlayer) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    // this SurfaceTexture update will call this
                    // now just ignore this
                }
            };

    private MediaPlayer.OnCompletionListener mOnCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mVideoImage.setVisibility(View.VISIBLE);
                    mSeekBar.setProgress(0);
                }
            };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.seekTo(seekBar.getProgress());
                    }
                }
            };

    private MediaPlayer.OnPreparedListener mOnPreparedListener =
            new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mVideoImage.setVisibility(View.GONE);
                    mMediaPlayer.start();

                    mSeekBar.setMax(mMediaPlayer.getDuration());
                    H.post(TICKER);
                }
            };

    private class PlayVideoThread extends Thread {
        @Override
        public void run() {
            if (null == mMediaPlayer) {
                mMediaPlayer = new MediaPlayer();
            }

            // http://112.253.22.157/17/z/z/y/u/zzyuasjwufnqerzvyxgkuigrkcatxr/hc.yinyuetai.com/D046015255134077DDB3ACA0D7E68D45.flv
            // http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4
            String strUrl = "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4";
            //Uri uri = Uri.parse(strUrl);
            try {
                mMediaPlayer.setDataSource(strUrl);
                mMediaPlayer.setSurface(mSurface);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
