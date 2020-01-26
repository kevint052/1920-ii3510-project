package fr.isep.ii3510.translator5000;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class History extends AppCompatActivity {

    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private TextView mText5;
    private TextView mText6;
    private TextView mText7;

    private Button mTranslateButton1;
    private Button mTranslateButton2;
    private Button mTranslateButton3;
    private Button mTranslateButton4;
    private Button mTranslateButton5;
    private Button mTranslateButton6;
    private Button mTranslateButton7;
    private Button mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Resources res = getResources();

        mText1 = findViewById(R.id.text1);
        mText2 = findViewById(R.id.text2);
        mText3 = findViewById(R.id.text3);
        mText4 = findViewById(R.id.text4);
        mText5 = findViewById(R.id.text5);
        mText6 = findViewById(R.id.text6);
        mText7 = findViewById(R.id.text7);

        mTranslateButton1 = findViewById(R.id.translate1);
        mTranslateButton2 = findViewById(R.id.translate2);
        mTranslateButton3 = findViewById(R.id.translate3);
        mTranslateButton4 = findViewById(R.id.translate4);
        mTranslateButton5 = findViewById(R.id.translate5);
        mTranslateButton6 = findViewById(R.id.translate6);
        mTranslateButton7 = findViewById(R.id.translate7);
        mBackButton = findViewById(R.id.backButton);

        mTranslateButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateAgain(mText1.getText().toString());
            }
        });

        mTranslateButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateAgain(mText2.getText().toString());
            }
        });

        mTranslateButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateAgain(mText3.getText().toString());
            }
        });

        mTranslateButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateAgain(mText4.getText().toString());
            }
        });

        mTranslateButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateAgain(mText5.getText().toString());
            }
        });

        mTranslateButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateAgain(mText6.getText().toString());
            }
        });

        mTranslateButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateAgain(mText7.getText().toString());
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTextVocalTranslationActivity();
            }
        });


    }

    public void translateAgain(String text){

        Intent intent = new Intent(this, TextVocalTranslationActivity.class);
        Bundle b = new Bundle();
        b.putString("text", text);
        intent.putExtras(b);
        startActivity(intent);

    }

    public void openTextVocalTranslationActivity(){
        Intent intent = new Intent(this, TextVocalTranslationActivity.class);
        startActivity(intent);
    }

    }
