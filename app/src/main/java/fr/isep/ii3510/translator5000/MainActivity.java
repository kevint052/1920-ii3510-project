package fr.isep.ii3510.translator5000;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Class intents[] = {PictureTranslation.class, TextVocalTranslationActivity.class};
    private List<String> buttonMethodsNames = Arrays.asList("imageTranslation", "textTranslation");
    private Button imageTranslation, textTranslation ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Resources res = getResources();

        // intents for button methods
        for (int i = 0; i < buttonMethodsNames.size(); i++) {

            String id = buttonMethodsNames.get(i);
            Button method = (Button) findViewById(res.getIdentifier(id, "id", getPackageName()));
            method.setTag(intents[i]);
            method.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    System.out.println(v.getTag());
                    startActivity(new Intent(MainActivity.this, (Class<?>) v.getTag()));

                }
            });
        }

    }
}



