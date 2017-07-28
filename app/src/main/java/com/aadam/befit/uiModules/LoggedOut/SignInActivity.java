package com.aadam.befit.uiModules.LoggedOut;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aadam.befit.R;
import com.aadam.befit.animation.ViewAnimation;
import com.aadam.befit.database.LoginDatabaseAdapter;
import com.aadam.befit.helpers.CustomException;
import com.aadam.befit.helpers.ValidationHelper;
import com.aadam.befit.models.User;
import com.aadam.befit.uiModules.LoggedIn.MainActivity;
import com.aadam.befit.util.PrefUtils;
import com.aadam.befit.util.SecurityUtil;

public class SignInActivity extends AppCompatActivity {

    //Properties
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView alertTextView;
    private View contentView;

    private LoginDatabaseAdapter loginDatabaseAdapter;

    //region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setupView();

        // create a instance of SQLite Database
        loginDatabaseAdapter = new LoginDatabaseAdapter(this);
        loginDatabaseAdapter = loginDatabaseAdapter.open();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ViewAnimation.moveViewWithAlpha(contentView, 1f, 1300, null);
    }
    //endregion

    //region Private Methods
    private void setupView() {

        getIDs();
    }

    private void getIDs() {
        contentView = findViewById(R.id.topView);
        contentView.setAlpha(0f);

        emailEditText = (EditText) findViewById(R.id.edittext_email);
        passwordEditText = (EditText) findViewById(R.id.editText_password);
        alertTextView = (TextView) findViewById(R.id.alert);

        ValidationHelper.resetEditText(getApplicationContext(), emailEditText);
    }

    private void callMainActivity() {

        Intent homeInt = new Intent(this, MainActivity.class);
        homeInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeInt);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    private void login() {

        ValidationHelper.hideSoftKeyboard(SignInActivity.this);

        String email = this.emailEditText.getText().toString();
        String password = SecurityUtil.md5(passwordEditText.getText().toString());

        try {

            ValidationHelper.validateEmail(getApplicationContext(), this.emailEditText);
            ValidationHelper.validateEmptyEditText(this.passwordEditText);

            if (loginDatabaseAdapter.comparePassword(email, password)) {
                User user = loginDatabaseAdapter.getUserByEmail(email);
                PrefUtils.setCurrentUser(user, SignInActivity.this);
                callMainActivity();
            } else {
                alertTextView.setText("Invalid Email or Password");
                ViewAnimation.showAlert(alertTextView);
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

        ValidationHelper.hideSoftKeyboard(SignInActivity.this);

        return false;
    }
    //endregion

    //region Button Actions
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.signInAction:

                login();

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
