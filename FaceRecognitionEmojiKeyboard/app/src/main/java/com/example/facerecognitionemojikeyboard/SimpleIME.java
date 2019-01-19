package com.example.facerecognitionemojikeyboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class SimpleIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    public static final int RESULT_OK = -1;

    public static final String KEY_RECEIVER = "KEY_RECEIVER";

    public static final String KEY_MESSAGE = "KEY_MESSAGE";

    private KeyboardView kv;
    private Keyboard keyboard;

    protected static final int CAMERACHOICE = CameraCharacteristics.LENS_FACING_BACK;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession session;
    protected ImageReader imageReader;

    private boolean caps = false;
    private Handler handler;


    private static Context context;
 
    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    @Override
    public void onCreate() {
        // Handler will get associated with the current thread, 
        // which is the main thread.
        handler = new Handler(getMainLooper());

        super.onCreate();
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public View onCreateInputView() {

        // Setting context
        context = getApplicationContext();

        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d("s", "s");
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case -10:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(KEY_RECEIVER, new MessageReceiver());
                startActivity(intent);
//                pictureService.startCapturing(this);
                break;

//                case 97:
//                    int unicode = 0x1F603;
//                    String s = new String(Character.toChars(unicode));
//                    ic.commitText(s, 1);
//                break;

            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
        }
    }

    class MessageReceiver extends ResultReceiver {

        public MessageReceiver() {
            // Pass in a handler or null if you don't care about the thread
            // on which your code is executed.
            super(null);
        }

        /**
         * Called when there's a result available.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Define and handle your own result codes
            if (resultCode != RESULT_OK) {
                return;
            }

            // Let's assume that a successful result includes a message.
            String message = resultData.getString(KEY_MESSAGE);
            System.out.println(message);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(message, options);

            AzureAPI a = new AzureAPI();
            a.sendBitmap(bitmap);



            // Now you can do something with it.
        }

    }


    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
    }

    public static Context getAppContext() {
        return context;
    }

}