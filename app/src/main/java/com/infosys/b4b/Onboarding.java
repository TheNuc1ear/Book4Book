package com.infosys.b4b;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class Onboarding extends MaterialIntroActivity {

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
                .image(R.drawable.discover)
                .buttonsColor(R.color.orange)
                .backgroundColor(R.color.purple_200)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Immerse")
                .image(R.drawable.discover)
                .buttonsColor(R.color.orange)
                .backgroundColor(R.color.purple_200)
                .build());
    }
}