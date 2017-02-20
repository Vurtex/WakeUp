package com.vurtex.wakeup.view;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.VideoView;

/**
 * @author Vurtex
 * Created by Vurtex on 2017/2/20.
 * 视频播放,主要是因为手机的大小很多，不能保证原生的VideoView能实现全屏
 * Created by lgl on 16/2/18.
 */
public class FillParentVideoView extends VideoView {

    public FillParentVideoView(Context context) {
        super(context);
    }

    public FillParentVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FillParentVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //我们重新计算高度
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        super.setOnPreparedListener(l);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
