package com.example.googlemap;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class SpeechManager {
    private static SpeechManager instance = new SpeechManager();
    private SpeechRecognizer speechRecognizer;

    private SpeechManager() {
    }

    public static SpeechManager getInstance() {
        return instance;
    }

    public static void prepare(final Context context) {
        if (instance.speechRecognizer == null) {
            instance.speechRecognizer =
                    SpeechRecognizer.createSpeechRecognizer(context);
        }
    }

    public void setRecognitionListener(RecognitionListener recognitionListener) {
        speechRecognizer.setRecognitionListener(recognitionListener);
    }

    public void startListen() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
//                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


//        Locale locale = Locale.getDefault();
////        Log.e("test", "default locale "+locale);
////        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,locale);

//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
//                "en-US"
//                );
        speechRecognizer.startListening(intent);
    }
}
