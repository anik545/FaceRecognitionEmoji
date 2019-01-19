package com.example.facerecognitionemojikeyboard;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class SimpleIME extends InputMethodService
    implements KeyboardView.OnKeyboardActionListener {
     
    private KeyboardView kv;
    private Keyboard keyboard;
     
    private boolean caps = false;

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
    public View onCreateInputView() {

        // Setting context
        context = getApplicationContext();

        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
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
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
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
                //TODO: when camera is pressed
                test();
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
    }

    public static Context getAppContext() {
        return context;
    }

    // TODO: TESTING FUNCTION
    private void test() {
        AzureAPI a = new AzureAPI();
    }
}