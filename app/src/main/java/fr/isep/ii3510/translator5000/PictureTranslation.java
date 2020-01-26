package fr.isep.ii3510.translator5000;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.*;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PictureTranslation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap imageBtmp;
    private List<String> buttonMethodsNames = Arrays.asList("picturesButton", "filesButton", "shareButton", "back");
    private List<String> languagesList = new ArrayList<>();
    private Map<String, String> mapLanguages = new HashMap<String, String>();
    private ImageView mImageView;
    private TextView textDetected;
    private Spinner selectLanguage;
    private Button picturesButton;
    private Button filesButton;
    private Button translateButton;
    private Button shareButton;
    private String detectText;
    private Integer counter = 0;
    private TextView mSourceLang, mTranslatedText, mTargetLang;
    private String sourceText;
    private Button back;
    private List<String> detectTextList = Arrays.asList();
    private FirebaseAuth mAuth;
    private Integer counterBitmaps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_picturetranslation);


        mImageView = findViewById(R.id.imageView);
        textDetected = findViewById(R.id.textDetected);
        selectLanguage = findViewById(R.id.toSpinner);

        mSourceLang = findViewById(R.id.sourceLang);
        mTargetLang = findViewById(R.id.targetLang);
        mTranslatedText = findViewById(R.id.translatedText);

        // Read all languages available
        Scanner scanner = new Scanner(
                getResources().openRawResource(R.raw.languages));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] language = line.split(",");
            languagesList.add(language[0]);
            mapLanguages.put(language[0], language[1]);

        }
        scanner.close();


        Resources res = getResources();

        selectLanguage = (Spinner) findViewById(R.id.toSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        selectLanguage.setAdapter(adapter);


        selectLanguage.setOnItemSelectedListener(this);

        // we initiate the spinner with int id = 17 which corresponds to French
        selectLanguage.setSelection(17);
        mTargetLang.setText(mapLanguages.get(17));


        // intents for button methods
        for (int i = 0; i < buttonMethodsNames.size(); i++) {

            String id = buttonMethodsNames.get(i);
            Button method = (Button) findViewById(res.getIdentifier(id, "id", getPackageName()));
            method.setTag(id);
            method.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    switch ((String) v.getTag()) {
                        case "back":
                            back();

                            break
                                    ;
                        case "picturesButton":
                            dispatchTakePictureIntent();


                            break;


                        case "filesButton":
                            pickImage();
                            break;
                        case "shareButton":
                            share();
                            break;


                    }
                }


            });
        }
        selectLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String langCode = languagesList.get(position);
                mTargetLang.setText(mapLanguages.get(langCode));
                translateText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }


    // Methods

    // Share picture with others
    private void share() {
        if (imageBtmp == null) {
            Toast.makeText(getApplicationContext(), "Nothing to share", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "The status update text");
            startActivity(Intent.createChooser(intent, "Dialog title text"));
        }
    }

    // Come back to Main Activity
    protected void back() {
        finish();
        Intent myIntent = new Intent(PictureTranslation.this, MainActivity.class);
        startActivity(myIntent);

    }

    // Rotate image selected to try the text Detection
    private void imageRotate() {


        mImageView.setRotation((float) 90.0); // It will rotate your image in 90 degree
        translateText();


    }

    // test translation
    protected void translateButton() {
        if (imageBtmp == null) {
            Toast.makeText(getApplicationContext(), "Bitmap is null", Toast.LENGTH_SHORT).show();

        } else {


            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBtmp);
            FirebaseVisionTextRecognizer firebaseVisionTextDetector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            firebaseVisionTextDetector.processImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                displayTextFromImage(firebaseVisionText);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {


                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PictureTranslation.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Error: ", e.getMessage());

                }
            });

        }

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }


    }

    // select existing picture
    private void pickImage() {
        Intent i = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select a file"), 123);


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBtmp = imageBitmap;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBtmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

            ContentResolver cr = getContentResolver();
            //Save with user editable title
            String title = "image_" + counterBitmaps;
            String description = "Bitmap saved by Android-er";
            String savedURL = MediaStore.Images.Media
                    .insertImage(cr, imageBitmap, title, description);

            mImageView.setImageBitmap(imageBitmap);

        }
        if (requestCode == 123 && resultCode == RESULT_OK && null != data) {
            Uri selectedfile = data.getData(); //The uri with the location of the file

            String fileExt = GetFileExtension(selectedfile);


            if (fileExt.contains("pdf") || fileExt.contains("doc")) {

                Toast.makeText(this, "This kind of file can't be read", Toast.LENGTH_SHORT).show();


            }
            ;
            if (!fileExt.contains("pdf")) {

                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedfile);
                    imageBtmp = imageBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            ;


            mImageView = (ImageView) findViewById(R.id.imageView);
            mImageView.setImageBitmap(imageBtmp);

        }
        translateButton();
    }


    // Get file extension
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Return file Extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<String> terms = new ArrayList<String>();
        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();

        if (blockList.size() == 0) {

            if (counter < 4) {
                counter += 1;
                imageRotate();
            } else {
                Toast.makeText(this, "No text", Toast.LENGTH_SHORT).show();

            }


        } else {
            counterBitmaps += 1;
            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                String text = block.getText();
                terms.add(text);
//


            }


            String joined = String.join(System.getProperty("line.separator"), terms);
            textDetected.setText(joined);
            translateText();

        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        CharSequence charSequence = (CharSequence) parent.getItemAtPosition(pos);
//        System.out.println("Item : " + charSequence.toString());
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    // translate text detected
    private void translateText() {
        sourceText = textDetected.getText().toString();
        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

//        mSourceLang.setText("Detecting");
        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {


                if (!languagesList.contains(s) || s == "und") {
                    Toast.makeText(getApplicationContext(), "Language Not Identified :" + s, Toast.LENGTH_SHORT).show();
                    mSourceLang.setText("unknown");
                    mTranslatedText.setText("can not translate this picture");


                } else {
                    getLanguageCode(s);
                }
            }
        });
    }

    private void getLanguageCode(String langCode) {
        int languageCode;
        String language = mapLanguages.get(langCode);
        languageCode = FirebaseTranslateLanguage.languageForLanguageCode(langCode.toUpperCase());
        mSourceLang.setText(language);


        translation(languageCode);
    }
//    private void getLanguageCode(String language) {
//        int langCode;
//        switch (language) {
//            case "hi":
//                langCode = FirebaseTranslateLanguage.HI;
//                mSourceLang.setText("Hindi");
//                break;
//            case "ar":
//                langCode = FirebaseTranslateLanguage.AR;
//                mSourceLang.setText("Arabic");
//                break;
//            case "es":
//                langCode = FirebaseTranslateLanguage.ES;
//                mSourceLang.setText("Spanish");
//                break;
//
//
//            case "fr":
//                langCode = FirebaseTranslateLanguage.FR;
//                mSourceLang.setText("French");
//                break;
//            case "en":
//                langCode = FirebaseTranslateLanguage.EN;
//                mSourceLang.setText("English");
//                break;
//            case "it":
//                langCode = FirebaseTranslateLanguage.IT;
//                mSourceLang.setText("Italian");
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + language);
//
//        }
//        ;
//        translation(langCode);
//    }


    private void translation(int langCode) {
        mTranslatedText.setText("Translating..");


        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                //from language
                .setSourceLanguage(langCode)
                // to language
                .setTargetLanguage(FirebaseTranslateLanguage.languageForLanguageCode(selectLanguage.getSelectedItem().toString()))
                .build();

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi()
                .build();


        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mTranslatedText.setText(s);
                    }
                });
            }
        });

    }
}
