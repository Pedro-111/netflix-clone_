package com.example.netflix_clone;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Content;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Content content = (Content) getIntent().getSerializableExtra("content");

        ImageView contentImage = findViewById(R.id.content_image);
        TextView contentTitle = findViewById(R.id.content_title);
        TextView contentDescription = findViewById(R.id.content_description);
        Button watchButton = findViewById(R.id.watch_button);

        Glide.with(this).load(content.getPoster_path()).into(contentImage);
        contentTitle.setText(content.getName());
        contentDescription.setText(content.getOverview());

        watchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement video playback here
            }
        });
    }
}