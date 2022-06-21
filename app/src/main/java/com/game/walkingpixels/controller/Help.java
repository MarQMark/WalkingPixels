package com.game.walkingpixels.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.walkingpixels.R;
import com.game.walkingpixels.view.ResponsiveButton;

public class Help extends AppCompatActivity {

    private static final int maxPage = 3;

    private int page = 1;
    private ImageView imgHelp;
    private ResponsiveButton btnNext;
    private ResponsiveButton btnPrevious;
    private TextView lblPageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        imgHelp = findViewById(R.id.img_help);
        btnNext = findViewById(R.id.btn_help_next);
        btnNext.setOnClickListener(e -> {
            page++;
            setPage(page);
        });
        btnPrevious = findViewById(R.id.btn_help_previous);
        btnPrevious.setOnClickListener(e -> {
            page--;
            setPage(page);
        });
        lblPageNumber = findViewById(R.id.lbl_help_page_number);

        setPage(page);
    }

    private void setPage(int page){
        btnPrevious.setEnabled(page != 1);
        btnNext.setEnabled(page != maxPage);
        lblPageNumber.setText(getResources().getString(R.string.lbl_help_page_number, page, maxPage));

        switch (page){
            case 1:
                imgHelp.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.help_stamina));
                break;
            case 2:
                imgHelp.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.help_leveling));
                break;
            case 3:
                imgHelp.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.help_fighting));
                break;
        }
    }
}