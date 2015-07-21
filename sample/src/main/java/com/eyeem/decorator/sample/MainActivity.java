package com.eyeem.decorator.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by budius on 21.07.15.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new DecoratedFragment())
                    .commit();
        }
    }
}
