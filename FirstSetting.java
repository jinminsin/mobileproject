package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class FirstSetting extends AppCompatActivity {
    private EditText nameText;
    private EditText heightText;
    private EditText weightText;
    private RadioGroup characterChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting);

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

                Intent result = new Intent();
                result.putExtra("name",nameText.getText().toString());
                result.putExtra("height",Float.parseFloat(heightText.getText().toString()));
                result.putExtra("weight",Float.parseFloat(weightText.getText().toString()));
                switch(characterChecked.getCheckedRadioButtonId())
                {
                    case R.id.character1Button:
                        result.putExtra("character",1);
                        break;
                    case R.id.character2Button:
                        result.putExtra("character",2);
                        break;
                    case R.id.character3Button:
                        result.putExtra("character",3);
                        break;
                }
                setResult(RESULT_OK,result);
                finish();
                break;
            case R.id.resetbutton:
                break;
        }
    }
}
