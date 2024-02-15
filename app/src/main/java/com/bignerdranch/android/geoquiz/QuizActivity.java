package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mResetButton;
    private Button mCheatButton;
    private Button mResultButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private TextView mRemainingCheatTextView;
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_SCORE = "score";
    private static final String KEY_FINISH= "finishans";
    private static final String KEY_REMAINING= "remaining";
    private static final String KEY_CHEATER= "cheater";
    private static final String KEY_CHEATED= "cheated";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true, false),
            new Question(R.string.question_oceans, true, false),
            new Question(R.string.question_mideast, false, false),
            new Question(R.string.question_africa, false, false),
            new Question(R.string.question_americas, true, false),
            new Question(R.string.question_asia, true, false),
    };

    private boolean[] mAnswered = new boolean[mQuestionBank.length];
    private boolean[] mIsCheater = new boolean[mQuestionBank.length];
    private boolean[] mWasCheater = new boolean[mQuestionBank.length];
    private int mScore = 0;
    private int mCurrentIndex = 0;
    private int mFinishAns = 0;
    private int mPercentage = 0;
    private int mRemainingCheat = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnswered = savedInstanceState.getBooleanArray(KEY_ANSWERED);
            mScore = savedInstanceState.getInt(KEY_SCORE, 0);
            mFinishAns = savedInstanceState.getInt(KEY_FINISH, 0);
            mRemainingCheat = savedInstanceState.getInt(KEY_REMAINING, 3);
            mIsCheater = savedInstanceState.getBooleanArray(KEY_CHEATER);
            mWasCheater = savedInstanceState.getBooleanArray(KEY_CHEATED);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex == 4){
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    nextbutton(false);
                    isAnswered(mCurrentIndex);

                }else if(mCurrentIndex >= 0 && mCurrentIndex < 5) {
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    prevbutton(true);
                    isAnswered(mCurrentIndex);
                    updateQuestion();
                }
                updateQuestion();
            }
        });

        mRemainingCheatTextView = (TextView) findViewById(R.id.remaining_cheat_text_view);
        mRemainingCheatTextView.setText("Your remaining cheat: " + mRemainingCheat);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                mQuestionBank[mCurrentIndex].setAnswered(true);
                checkAnswer(true);
                mFinishAns += 1;
                showscore();
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                mQuestionBank[mCurrentIndex].setAnswered(true);
                checkAnswer(false);
                mFinishAns += 1;
                showscore();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        if(mCurrentIndex == 5) {
            nextbutton(false);
        }
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                if (mCurrentIndex == 5){
                    nextbutton(false);
                    isAnswered(mCurrentIndex);
                    mIsCheater[mCurrentIndex] = false;
                    updateQuestion();

                }else if(mCurrentIndex > 0 && mCurrentIndex < 5) {
                    prevbutton(true);
                    isAnswered(mCurrentIndex);
                    mIsCheater[mCurrentIndex] = false;
                    updateQuestion();
                }
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        if(mCurrentIndex == 0) {
            prevbutton(false);
        }
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                if (mCurrentIndex > 0 && mCurrentIndex < 5) {
                    nextbutton(true);
                    isAnswered(mCurrentIndex);
                    updateQuestion();

                } else if (mCurrentIndex == 0) {
                    prevbutton(false);
                    isAnswered(mCurrentIndex);
                    updateQuestion();
                }
            }
        });

        mResetButton = (Button) findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                resetScore();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){
                //start CheatActivity
//                Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
//                startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mResultButton = (Button) findViewById(R.id.result_button);
        mResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("EXTRA_QUESTION_ANSWERED", mFinishAns);
                intent.putExtra("EXTRA_SCORE", mScore);
                intent.putExtra("EXTRA_PERCENTAGE", mPercentage);
                intent.putExtra("EXTRA_CHEAT", (3 - mRemainingCheat));
                startActivity(intent);
            }
        });
        updateQuestion();
    }


    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "OnResume() called");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "Onpause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_ANSWERED, mAnswered);
        savedInstanceState.putInt(KEY_SCORE, mScore);
        savedInstanceState.putInt(KEY_FINISH, mFinishAns);
        savedInstanceState.putInt(KEY_REMAINING, mRemainingCheat);
        savedInstanceState.putBooleanArray(KEY_CHEATER, mIsCheater);
        savedInstanceState.putBooleanArray(KEY_CHEATED, mWasCheater);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "OnStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            if(mWasCheater[mCurrentIndex] == false) {
                if (mIsCheater[mCurrentIndex] == true) {
                    mWasCheater[mCurrentIndex] = true;
                    mRemainingCheat -= 1;
                    if(mWasCheater[mCurrentIndex] == true){
                        mCheatButton.setClickable(true);
                        mCheatButton.setEnabled(true);
                    }
                }
            }
            mRemainingCheatTextView.setText("Your remaining cheat token: " + mRemainingCheat);
        }
    }

    private void updateQuestion(){
        Log.d(TAG, "Updating question text", new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        mTrueButton.setEnabled(!mAnswered[mCurrentIndex]);
        mFalseButton.setEnabled(!mAnswered[mCurrentIndex]);
        if (mRemainingCheat == 0) {
            mCheatButton.setClickable(false);
            mCheatButton.setEnabled(false);
            if (mWasCheater[mCurrentIndex] == true) {
                mCheatButton.setClickable(true);
                mCheatButton.setEnabled(true);
            }
        }else{
            mCheatButton.setClickable(true);
            mCheatButton.setEnabled(true);
        }
    }


    private void prevbutton(boolean prevButton){
        if(prevButton == false) {
            mPrevButton.setEnabled(false);
            mPrevButton.setClickable(false);
            mPrevButton.setVisibility(View.INVISIBLE);
        }else{
            mPrevButton.setEnabled(true);
            mPrevButton.setClickable(true);
            mPrevButton.setVisibility(View.VISIBLE);
        }
    }

    private void nextbutton(boolean nextButton){
        if(nextButton == false) {
            mNextButton.setEnabled(false);
            mNextButton.setClickable(false);
            mNextButton.setVisibility(View.INVISIBLE);
        }else{
            mNextButton.setEnabled(true);
            mNextButton.setClickable(true);
            mNextButton.setVisibility(View.VISIBLE);
        }
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        mAnswered[mCurrentIndex] = true;
        int messageResId;

        if(mIsCheater[mCurrentIndex] == true){
            messageResId = R.string.judgment_toast;
        }else{
            if(userPressedTrue == answerIsTrue){
                messageResId = R.string.correct_toast;
                mScore +=1;
            }else{
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 120);
        toast.show();
    }

    private void showscore(){
        mPercentage =  (mScore * 100) / mQuestionBank.length;
        if(mFinishAns == mQuestionBank.length){
            Toast toast = Toast.makeText(this, "Your grade is " + Integer.toString(mPercentage) + "%", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 120);
            toast.show();
        }
    }

    private void resetScore(){
        Intent i = getApplicationContext().getPackageManager()
                .getLaunchIntentForPackage(getApplicationContext().getPackageName() );

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }

    private void isAnswered(int currentIndex){
        if(mQuestionBank[mCurrentIndex].isAnswered() == true){
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }else{
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }
}
