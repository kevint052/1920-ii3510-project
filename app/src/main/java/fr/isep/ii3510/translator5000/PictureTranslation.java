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

public class PictureTranslation extends AppCompatActivity {

    private List<String> buttonMethodsNames = Arrays.asList("picturesButton", "filesButton", "translateButton", "back");
    private Button picturesButton, filesButton, translateButton, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picturetranslation);

        Resources res = getResources();

        // intents for button methods
        for (int i = 0; i < buttonMethodsNames.size(); i++) {

            String id = buttonMethodsNames.get(i);
            Button method = (Button) findViewById(res.getIdentifier(id, "id", getPackageName()));
            method.setTag(id);
            method.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Method classMethod= null;
                    try {
                        classMethod = this.getClass().getMethod((String) v.getTag(), new Class[]{});
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    try {
                        Object invoke = classMethod.invoke(this);

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }
            });


        }
    }

    public void picturesButton() {

    }

    public void filesButton() {

    }

    public void translateButton() {

    }

    public void back() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
