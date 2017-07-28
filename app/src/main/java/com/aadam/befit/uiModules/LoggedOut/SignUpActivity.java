package com.aadam.befit.uiModules.LoggedOut;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aadam.befit.Constants;
import com.aadam.befit.R;
import com.aadam.befit.animation.ViewAnimation;
import com.aadam.befit.database.LoginDatabaseAdapter;
import com.aadam.befit.helpers.CustomException;
import com.aadam.befit.helpers.ValidationHelper;
import com.aadam.befit.util.SecurityUtil;

public class SignUpActivity extends AppCompatActivity {

    //properties
    private EditText emailET;
    private EditText passwordET;
    private EditText confirmPasswordET;
    private View contentView;
    private TextView alertTextView;

    private String userEmail;
    private String userPassword;

    private LoginDatabaseAdapter loginDatabaseAdapter;

    //region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setupViews();

        loginDatabaseAdapter=new LoginDatabaseAdapter(this);
        loginDatabaseAdapter=loginDatabaseAdapter.open();


    }

    @Override
    protected void onStart() {
        super.onStart();

        ViewAnimation.moveViewWithAlpha(contentView, 1f, 1300, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginDatabaseAdapter.close();
    }
    //endregion

    //region Private Methods
    private void setupViews(){
        getIDs();
    }

    private void getIDs(){

        emailET = (EditText) findViewById(R.id.edittext_email);
        passwordET = (EditText) findViewById(R.id.editText_password);
        confirmPasswordET = (EditText) findViewById(R.id.editText_confirm_password);

        contentView = findViewById(R.id.topView);
        contentView.setAlpha(0f);

        alertTextView = (TextView) findViewById(R.id.alert);


    }

    private void callDetailsActivity() {

        Intent detailInt = new Intent(this, DetailActivity.class);
        detailInt.putExtra(Constants.KEY_EMAIL, userEmail);
        detailInt.putExtra(Constants.KEY_USERID,loginDatabaseAdapter.getUserID(userEmail));
        startActivity(detailInt);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void signUp() {

        ValidationHelper.hideSoftKeyboard(SignUpActivity.this);

        try {

            userEmail = ValidationHelper.validateEmail(getApplicationContext(), emailET);
            ValidationHelper.validatePassword(passwordET);
            userPassword = SecurityUtil.md5(ValidationHelper.stringComparison(passwordET, confirmPasswordET));

            if(!loginDatabaseAdapter.checkUserExists(userEmail)){
                loginDatabaseAdapter.insertNewUser(userEmail, userPassword);
                callDetailsActivity();
            }
            else{
                alertTextView.setText("Email already registered.");
                ViewAnimation.showAlert(alertTextView);
                ViewAnimation.shake(emailET);
                emailET.setTextColor(getResources().getColor(R.color.red));
            }
        } catch (CustomException e) {

            alertTextView.setText(e.getMessage());
            ViewAnimation.showAlert(alertTextView);
        }
    }
    //endregion

    //region Touch Methods
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        ValidationHelper.hideSoftKeyboard(SignUpActivity.this);

        return false;
    }
    //endregion

    //region Button Actions
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.signUpAction:

                signUp();

                break;

            case R.id.close:

                finish();

                break;

            default:
                break;
        }
    }
    //endregion


}
