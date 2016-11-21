package com.example.alexapperley.augmentedrealitytest;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    static final int GAME_ACTIVIY_INTENT = 1;
    static final int SCORE_ACTIVIY_INTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        View startButton = findViewById(R.id.start);
        View scoreButton = findViewById(R.id.scoreboard);
        startButton.setOnClickListener(this);
        scoreButton.setOnClickListener(scoreListener);
    }


    private View.OnClickListener scoreListener = new View.OnClickListener() {
        public void onClick(View v) {
            //start scoreboard
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivityForResult(intent,GAME_ACTIVIY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GAME_ACTIVIY_INTENT)
        {
            EditText nameObject = (EditText) findViewById(R.id.name);
            String name = nameObject.getText().toString();
            int score = data.getIntExtra("score",0);
            if(score > 0)
            {
                Toast.makeText(this,name + "'s New Score - " + score,Toast.LENGTH_LONG).show();
            }
        }
    }
}
