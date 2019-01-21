package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mPathOutTv;
    private Button mSelectSound1Btn;
    private Button mSelectSound2Btn;
    private Button mMerge;
    private ConstraintLayout mContainer;

    static int REQUEST_SOUND_FIRST = 1;
    static int REQUEST_SOUND_SECOND = 2;
    String filePath1 = "";
    String filePath2 = "";
    String outputPath = "";

    String path = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();

    }


    public void selectSound(int requestCode) {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, requestCode);
    }

    private void initView() {
        mPathOutTv = (TextView) findViewById(R.id.tv_pathOut);
        mSelectSound1Btn = (Button) findViewById(R.id.btn_select_sound1);
        mSelectSound1Btn.setOnClickListener(this);
        mSelectSound2Btn = (Button) findViewById(R.id.btn_select_sound2);
        mSelectSound2Btn.setOnClickListener(this);
        mMerge = (Button) findViewById(R.id.merge);
        mMerge.setOnClickListener(this);
        mContainer = (ConstraintLayout) findViewById(R.id.container);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_sound1:
                selectSound(REQUEST_SOUND_FIRST);
                break;
            case R.id.btn_select_sound2:
                selectSound(REQUEST_SOUND_SECOND);
                break;
            case R.id.merge:
                Intent i = new Intent(this, ConvertActivity.class);
                i.putExtra("p1", filePath1);
                i.putExtra("p2", filePath2);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SOUND_FIRST) {
            if (resultCode == RESULT_OK) {
                //the selected audio.
                Uri uri = data.getData();
                File f = new File(String.valueOf(uri));
                filePath1 = f.toString();
                path += "\nPath1: " + filePath1;
                mPathOutTv.setText(path);
            }
        }

        if (requestCode == REQUEST_SOUND_SECOND) {
            if (resultCode == RESULT_OK) {
                //the selected audio.
                Uri uri = data.getData();
                File f = new File(String.valueOf(uri));
                filePath2 = f.toString();
                path += "\nPath2: " + filePath2;
                mPathOutTv.setText(path);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isRunning() {
        return FFmpeg.getInstance(this).isFFmpegCommandRunning();
    }

}