package fr.isep.ii3510.translator5000;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class TextVocalTranslationActivity extends AppCompatActivity {

    private List<String> buttonMethodsNames = Arrays.asList("talk", "swap", "listen", "shareButton", "back");
    private Button picturesButton, filesButton, translateButton, back;

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
    }


    protected void talk() {

    }

    protected void swap() {

    }


    protected void listen() {


    }

    protected void shareButton() {


    }


}
