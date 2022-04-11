package com.infosys.b4b;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class onBoarding extends MaterialIntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(new SlideFragmentBuilder()
                .title("Discover")
                .image(R.drawable.discover)
                .buttonsColor(R.color.orange)
                .backgroundColor(R.color.purple_200)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Exchange")
                .image(R.drawable.exchange)
                .buttonsColor(R.color.orange)
                .backgroundColor(R.color.purple_200)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Immerse")
                .image(R.drawable.immerse)
                .buttonsColor(R.color.orange)
                .backgroundColor(R.color.purple_200)
                .build());
    }

    String prevStarted = "yes";
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        } else {
            moveToSecondary();
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        startActivity(new Intent(onBoarding.this, loginActivity.class));
    }

    public void moveToSecondary(){
        // use an intent to travel from one activity to another.
        Intent intent = new Intent(this, mainActivity.class);
        startActivity(intent);
    }
}