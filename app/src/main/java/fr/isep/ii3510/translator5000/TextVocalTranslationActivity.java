package fr.isep.ii3510.translator5000;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TextVocalTranslationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private List<String> buttonMethodsNames = Arrays.asList("speakButton", "swapButton", "listenButton", "historyButton", "back");
    private Button picturesButton, filesButton, translateButton, back;


    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public String text;

    private TextView mSourceLang;
    private EditText mSourceText;
    private Button mTranslateBtn;
    private TextView mTranslatedText;
    private String sourceText;
    private Spinner mLanguageFrom;
    private Spinner mLanguageTo;
    private Button mListenButton;
    private Button mSpeakButton;
    private Button mSwapButton;
    private Button mHistoryButton;
    private TextToSpeech mTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textvocaltranslation);

        Resources res = getResources();


        // intents for button methods
        for (int i = 0; i < buttonMethodsNames.size(); i++) {

            String id = buttonMethodsNames.get(i);
            Button method = (Button) findViewById(res.getIdentifier(id, "id", getPackageName()));
            method.setTag(id);
            method.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {

                    if (v.getTag() == "back") {
                        System.out.println(TextVocalTranslationActivity.this);
                        Intent myIntent = new Intent(TextVocalTranslationActivity.this, MainActivity.class);
                        startActivity(myIntent);
                    } else {
                        try {
//
                            Method classMethod = PictureTranslation.class.getDeclaredMethod((String) v.getTag());
                            classMethod.setAccessible(true);
                            classMethod.invoke(new PictureTranslation());
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });


        }

        mSourceLang = findViewById(R.id.sourceLang);
        mSourceText = findViewById(R.id.textFrom);
        mTranslateBtn = findViewById(R.id.translate);
        mTranslatedText = findViewById(R.id.textTo);
        mLanguageFrom = findViewById(R.id.languageFrom);
        mLanguageTo = findViewById(R.id.languageTo);
        mListenButton = findViewById(R.id.listenButton);
        mSpeakButton = findViewById(R.id.speakButton);
        mSwapButton = findViewById(R.id.swapButton);
        mHistoryButton = findViewById(R.id.historyButton);


        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.FRENCH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mListenButton.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        mListenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        });



        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistoryActivity();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageFrom.setAdapter(adapter);
        mLanguageFrom.setOnItemSelectedListener(this);

        mLanguageTo.setAdapter(adapter);
        mLanguageTo.setOnItemSelectedListener(this);

        mSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sourceLangPosition = mLanguageFrom.getSelectedItemPosition();
                mLanguageFrom.setSelection(mLanguageTo.getSelectedItemPosition());
                mLanguageTo.setSelection(sourceLangPosition);

                String outputText = mTranslatedText.getText().toString();
                String inputText = sourceText;
                mSourceText.setText(outputText);
                mTranslatedText.setText(inputText);
            }
        });


        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(sourceText);
                setLanguagesCodes(mLanguageFrom.getSelectedItem().toString(), mLanguageTo.getSelectedItem().toString());
            }
        });


        mSpeakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "click",
                        Toast.LENGTH_SHORT).show();
                speak();
            }
        });
    }

    //this part is for the history activity

    public void saveData(String text) {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, text);
        editor.apply();

        Toast.makeText(this, "Input saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,0);
        text = sharedPreferences.getString(TEXT, "");

    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }




    public void openHistoryActivity() {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }




    //this part is for the Voice Input and Hear the translation buttons

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something...");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! An error occurred.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    //get text array from voice intent
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    mSourceText.setText(text);
                }
                break;
            }
        }
    }

    private void listen() {
        String text = mTranslatedText.getText().toString();
        mTTS.setPitch(1.0f);
        mTTS.setSpeechRate(1.0f);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    //We translate in two steps : 1. we retrieve the languages the user selected and get the codes
    // and 2. we use the translator with the apprioriate codes and source text

    private void setLanguagesCodes(String languageFrom, String languageTo) {
        int langFromCode;
        int langToCode;
        switch (languageFrom) {
            case "German":
                langFromCode = FirebaseTranslateLanguage.DE;
                mSourceLang.setText("German");
                break;
            case "Arabic":
                langFromCode = FirebaseTranslateLanguage.AR;
                mSourceLang.setText("Arabic");

                break;
            case "Spanish":
                langFromCode = FirebaseTranslateLanguage.ES;
                mSourceLang.setText("Spanish");

                break;
            case "French":
                langFromCode = FirebaseTranslateLanguage.FR;
                mSourceLang.setText("French");

                break;
            case "Chinese":
                langFromCode = FirebaseTranslateLanguage.ZH;
                mSourceLang.setText("Chinese");

                break;
            case "Russian":
                langFromCode = FirebaseTranslateLanguage.RU;
                mSourceLang.setText("Russian");

                break;
            case "English":
                langFromCode = FirebaseTranslateLanguage.EN;

                mSourceLang.setText("English");
                break;
            default:
                langFromCode = 0;
        }

        switch (languageTo) {
            case "German":
                langToCode = FirebaseTranslateLanguage.DE;

                break;
            case "Arabic":
                langToCode = FirebaseTranslateLanguage.AR;

                break;
            case "Spanish":
                langToCode = FirebaseTranslateLanguage.ES;

                break;
            case "French":
                langToCode = FirebaseTranslateLanguage.FR;


                break;
            case "Chinese":
                langToCode = FirebaseTranslateLanguage.ZH;

                break;
            case "Russian":
                langToCode = FirebaseTranslateLanguage.RU;

                break;
            case "English":
                langToCode = FirebaseTranslateLanguage.EN;

                break;
            default:
                langToCode = 0;
        }


        translate(langFromCode, langToCode);
    }


    public void translate(int langFromCode, int langToCode) {
        mTranslatedText.setText("Translating..");
        sourceText = mSourceText.getText().toString();
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                //from language
                .setSourceLanguage(langFromCode)
                // to language
                .setTargetLanguage(langToCode)
                .build();

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        // Model downloaded successfully. Okay to start translating.
                        // (Set a flag, unhide the translation UI, etc.)
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                            }
                        });

        translator.translate(sourceText)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                // Translation successful.
                                mTranslatedText.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error.
                                // ...
                            }
                        });
    }


}
