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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.microsoft.projectoxford.face.contract.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

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


    private JSONToEmoji jsonToEmoji;
    private boolean caps = false;
    private Handler handler;


    private static Context context;

    private Set<String> emojis;
    private InputConnection ic;

    private void updateEmojis(Face[] faces) {
        ic = getCurrentInputConnection();
        EmotionData emotionData = new EmotionData(faces[0]);
        Log.d("SIMPLEIME", "Calling jsonToEmoji");
        Set<String> emojis = jsonToEmoji.getEmojis(emotionData.exportMap(), 1);
        Log.d("SIMPLEIME", "Finished jsonToEmoji");
        String toCommit = emojis.iterator().next();
        Log.d("SIMPLEIME", "Committing");
        Log.d("SIMPLEIME", "Committing: " + toCommit);
        int i = Integer.valueOf(toCommit, 16);
        String s = new String(Character.toChars(i));
        ic.commitText(s, 1);

    }
 
    private boolean numbers = false;

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
        try {
            jsonToEmoji = new JSONToEmoji();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);

        View view = setView(false);
        return view;
    }

    private View setView(boolean iSet) {
        if( numbers ) {
            kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
            keyboard = new Keyboard(this, R.xml.numbers);
        }
        else {
            kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
            keyboard = new Keyboard(this, R.xml.qwerty);
        }


        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);

        if (iSet) {
            setInputView(kv);
        }

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
        ic = getCurrentInputConnection();
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
                break;

//                case 97:
//                    int unicode = 0x1F603;
//                    String s = new String(Character.toChars(unicode));
//                    ic.commitText(s, 1);
//                break;
            case -6:
                numbers = !numbers;
                setView(true);
                break;
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

            Bitmap bitmap = (Bitmap) resultData.getParcelable(KEY_MESSAGE);


            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            Log.d("SIMPLEIME", "Starting azure api");
            AzureAPI a = new AzureAPI();
            CompletableFuture<Face[]> future = a.sendBitmap(bitmap);
            future.thenApply((Face[] faces) -> {
                Log.d("SIMPLEIME", "Starting then apply");
                if (faces == null || faces.length == 0) {
                    Log.d("SIMPLEIME", "Stopping due to no face");
                    showToast("No face detected");
                    System.out.println("no face");
                } else {
                    updateEmojis(faces);
                }
                return faces;
            });
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