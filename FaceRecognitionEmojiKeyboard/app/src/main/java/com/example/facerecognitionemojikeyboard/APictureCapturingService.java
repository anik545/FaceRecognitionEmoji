package com.example.facerecognitionemojikeyboard;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.SparseIntArray;
import android.view.Surface;

/**
 * Abstract Picture Taking Service.
 *
 * @author hzitoun (zitoun.hamed@gmail.com)
 */
public abstract class APictureCapturingService {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private final Service service;
    final Context context;
    final CameraManager manager;

    /***
     * constructor.
     *
     * @param service the activity used to get display manager and the application context
     */
    APictureCapturingService(final Service service) {
        this.service = service;
        this.context = service.getApplicationContext();
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    /***
     * @return  orientation
     */
    int getOrientation() {
//        final int rotation = this.activity.getWindowManager().getDefaultDisplay().getRotation();
//        return ORIENTATIONS.get(rotation);
        return ORIENTATIONS.get(0);
    }


    /**
     * starts pictures capturing process.
     *
     * @param listener picture capturing listener
     */
    public abstract void startCapturing(final PictureCapturingListener listener);
}
