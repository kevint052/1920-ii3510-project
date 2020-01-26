package fr.isep.ii3510.translator5000;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Arrays;
import java.util.List;

public class TextVocalTranslationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private List<String> buttonMethodsNames = Arrays.asList("talk", "swap", "listen", "shareButton", "back");
    private Button picturesButton, filesButton, translateButton, back;


    private TextView mSourceLang;
    private EditText mSourceText;
    private Button mTranslateBtn;
    private TextView mTranslatedText;
    private String sourceText;
    private Spinner mLanguageFrom;
    private Spinner mLanguageTo;


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


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageFrom.setAdapter(adapter);
        mLanguageFrom.setOnItemSelectedListener(this);

        mLanguageTo.setAdapter(adapter);
        mLanguageTo.setOnItemSelectedListener(this);


        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguagesCodes(mLanguageFrom.getSelectedItem().toString(), mLanguageTo.getSelectedItem().toString());
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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




    public void translate2(int langFromCode, int langToCode) {

        mTranslatedText.setText("Translating..");
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

        mTranslatedText.setText(mLanguageFrom.getSelectedItem().toString());
    }

}
