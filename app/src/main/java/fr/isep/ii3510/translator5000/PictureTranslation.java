package fr.isep.ii3510.translator5000;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.*;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PictureTranslation extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap imageBtmp;
    private List<String> buttonMethodsNames = Arrays.asList("picturesButton", "filesButton", "translateButton", "detectText", "back");
    private ImageView mImageView;
    private TextView textDetected;
    private Button picturesButton, filesButton, translateButton, detectText, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picturetranslation);
        mImageView = findViewById(R.id.imageView);

        Resources res = getResources();


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
                            picturesButton();


                            ;
                            break;
                        case "detectText":
                            detectText();

                    }
                }


            });
        }

    }

    protected void back() {
        finish();
        System.out.println("Break **********------------000000000");
        Intent myIntent = new Intent(PictureTranslation.this, MainActivity.class);
        startActivity(myIntent);
    }

    protected void picturesButton() {

        dispatchTakePictureIntent();

    }


    protected void filesButton() {

    }


    protected void translateButton() {


    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBtmp = imageBitmap;
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectText() {

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBtmp);
        FirebaseVisionTextRecognizer FirebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        FirebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {


            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PictureTranslation.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Error: ", e.getMessage());

            }
        });


    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();
        if (blockList.size() == 0) {

            Toast.makeText(this, "No text", Toast.LENGTH_SHORT).show();

        } else {
            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                String text = block.getText();
                textDetected.setText(text);

            }
        }
    }

}