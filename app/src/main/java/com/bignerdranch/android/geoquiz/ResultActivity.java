package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext, ResultActivity.class);
        return intent;
    }

    private TextView mQuestionAnsweredTextView;
    private TextView mScoreTextView;
    private TextView mCheatAttemptsTextView;
    private int mQuestionAnswered;
    private int mScore;
    private int mPercentage;
    private int mCheatAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mQuestionAnswered = getIntent().getIntExtra("EXTRA_QUESTION_ANSWERED", 0);
        mScore = getIntent().getIntExtra("EXTRA_SCORE", 0);
        mPercentage = getIntent().getIntExtra("EXTRA_PERCENTAGE", 0);
        mCheatAttempts = getIntent().getIntExtra("EXTRA_CHEAT", 0);

        mQuestionAnsweredTextView = (TextView) findViewById(R.id.question_answered);
        mQuestionAnsweredTextView.setText("Total Question Answered: " + mQuestionAnswered);

        mScoreTextView = (TextView) findViewById(R.id.score);
        mScoreTextView.setText("Total Score: " + mPercentage + "%" + " (" + mScore + "/6" + ")");

        mCheatAttemptsTextView = (TextView) findViewById(R.id.cheat_attempts);
        mCheatAttemptsTextView.setText("Total Cheat Attempts: " + mCheatAttempts);
    }
}
