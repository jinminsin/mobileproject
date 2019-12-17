package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class FirstSetting extends AppCompatActivity {
    private DBHelper helper;
    private SQLiteDatabase db;
    private Character character;
    private Cursor cursor;

    private EditText nameText;
    private EditText heightText;
    private EditText weightText;
    private RadioGroup characterChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting);

        helper = new DBHelper(this);
        character = new Character("human",0,0,0,0,0,0,0,0,0);//초기화

        nameText = findViewById(R.id.editName);
        heightText = findViewById(R.id.editHeight);
        weightText = findViewById(R.id.editWeight);
        characterChecked = findViewById(R.id.characterButton);
    }

    public void onClickbutton(View view) {
        switch(view.getId())
        {
            case R.id.completebutton:
                if(nameText.length() == 0 || heightText.length() == 0 || weightText.length() == 0) return;

                character.setName(nameText.getText().toString());
                character.setHeight(Float.parseFloat(heightText.getText().toString()));
                character.setWeight(Float.parseFloat(weightText.getText().toString()));

                switch(characterChecked.getCheckedRadioButtonId())
                {
                    case R.id.character1Button:
                        character.setCharacter(1);
                        break;
                    case R.id.character2Button:
                        character.setCharacter(2);
                        break;
                    case R.id.character3Button:
                        character.setCharacter(3);
                        break;
                }
                character.getLevel().setLevel(1);
                helper.update(character);
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.resetbutton:
                nameText.setText("");
                heightText.setText("");
                weightText.setText("");
                characterChecked.clearCheck();
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
