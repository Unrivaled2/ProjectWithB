package com.lemonxq_laplace.pregnantmonitor.activity.getFrame;


import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class MediaDecoder {
    public interface OnMediaDecoderListener {
        void onDecodeFrame(Bitmap bitmap);
    }

    private static Reference<MediaDecoder> mediaDecoder;
    private static Reference<MediaMetadataRetriever> retriever;
    private OnMediaDecoderListener listener;
    private int type;
    private int flag;
    public final static int TYPE_VIDEO_LOCAL = 0x001;
    public final static int TYPE_VIDEO_NET = 0x002;
    public final static int VIDEO_START = 0x001;
    public final static int VIDEO_CUSTOM = 0x002;
    public final static int VIDEO_END = 0x003;

    @IntDef({TYPE_VIDEO_LOCAL, TYPE_VIDEO_NET})
    @Retention(RetentionPolicy.SOURCE)
    private @interface MediaDecoderType {

    }

    @IntDef({VIDEO_START, VIDEO_CUSTOM, VIDEO_END})
    @Retention(RetentionPolicy.SOURCE)
    private @interface MediaDecoderTime {

    }

    private static MediaDecoder getMediaDecoder() {
        if (!checkNotNull()) {
            mediaDecoder = new WeakReference<>(new MediaDecoder());
        }
        return mediaDecoder.get();
    }

    public static MediaDecoder create() {
        return getMediaDecoder();
    }

    public MediaDecoder setType(@MediaDecoderType int mediaType) {
        type = mediaType;
        return getMediaDecoder();
    }

    public MediaDecoder setListener(OnMediaDecoderListener onMediaDecoderListener) {
        mediaDecoder.get().listener = onMediaDecoderListener;
        return getMediaDecoder();
    }

    public MediaDecoder setDataSource(String path) {
        retriever = new WeakReference<>(new MediaMetadataRetriever());
        switch (type) {
            case TYPE_VIDEO_LOCAL: {
                retriever.get().setDataSource(path);
            }
            break;
            case TYPE_VIDEO_NET: {
                retriever.get().setDataSource(path, new HashMap<String, String>());
            }
            break;
        }
        return getMediaDecoder();
    }


    /**
     * @param flag 只有flag为VIDEO_CUSTOM时候需要传time
     * @param time 单位为毫秒
     */
    public void start(@MediaDecoderTime int flag, int... time) {
        this.flag = flag;
        new Thread(new GetFrameThread(time)).start();
    }

    private static boolean checkNotNull() {
        return (mediaDecoder != null && mediaDecoder.get() != null);
    }

    class GetFrameThread implements Runnable {

        private int time;

        GetFrameThread(int... time) {
            switch (flag) {
                case VIDEO_CUSTOM: {
                    if (time.length == 0) {
                        flag = VIDEO_START;
                    } else {
                        this.time = time[0];
                    }
                }
                break;
            }
        }

        @Override
        public void run() {
            Bitmap bitmap;
            try {
                //获得第一帧图片
                switch (flag) {
                    case VIDEO_END: {
                        String duration = retriever.get().extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        time = Integer.parseInt(duration);
                    }
                    case VIDEO_CUSTOM: {
                        //getFrameAtTime（单位为微秒）
                        bitmap = retriever.get().getFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
                    }
                    break;
                    default: {
                        bitmap = retriever.get().getFrameAtTime();
                    }
                    break;
                }
                if (listener != null) {
                    listener.onDecodeFrame(bitmap);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                if (retriever != null && retriever.get() != null) retriever.get().release();
            }
        }
    }
}
