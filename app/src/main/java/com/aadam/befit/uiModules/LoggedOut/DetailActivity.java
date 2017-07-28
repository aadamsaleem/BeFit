package com.aadam.befit.uiModules.LoggedOut;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.aadam.befit.Constants;
import com.aadam.befit.R;
import com.aadam.befit.animation.ViewAnimation;
import com.aadam.befit.database.LoginDatabaseAdapter;
import com.aadam.befit.helpers.CustomException;
import com.aadam.befit.helpers.ValidationHelper;
import com.aadam.befit.models.User;
import com.aadam.befit.uiModules.LoggedIn.MainActivity;
import com.aadam.befit.util.PrefUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    //Properties
    private Spinner heightSpinner;
    private Spinner genderSpinner;
    private EditText fullNameEditText;
    private TextView alertTextView;
    private User user;

    private LoginDatabaseAdapter loginDatabaseAdapter;

    //region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        user = PrefUtils.getCurrentUser(this);
        if (user == null) {
            user = new User();
            Intent intent = getIntent();
            user.setEmail(intent.getStringExtra(Constants.KEY_EMAIL));
            user.setId(intent.getIntExtra(Constants.KEY_USERID, -1));
            user.setTwitterID(intent.getStringExtra(Constants.KEY_TWITTER_ID));
        }

        setupViews();

        loginDatabaseAdapter = new LoginDatabaseAdapter(this);
        loginDatabaseAdapter = loginDatabaseAdapter.open();

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginDatabaseAdapter.close();
    }
    //endregion

    //region Private Methods
    private void setupViews() {
        getIDs();

        setupHeightSpinner();
        setupGenderSpinner();

        fullNameEditText.setText(user.getName());

        ValidationHelper.resetEditText(getApplicationContext(), this.fullNameEditText);
    }

    private void setupHeightSpinner() {

        heightSpinner.setPrompt("HEIGHT IN CM");

        List<String> heightList = new ArrayList<String>();
        for (int i = 100; i <= 300; i++) {
            heightList.add("" + i);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, heightList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightSpinner.setAdapter(dataAdapter);

        String height = user.getHeight();
        if (height != null)
            heightSpinner.setSelection(dataAdapter.getPosition(height));

        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setHeight(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setupGenderSpinner() {

        genderSpinner.setPrompt("SELECT GENDER");

        List<String> genderList = new ArrayList<String>();
        genderList.add("MALE");
        genderList.add("FEMALE");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);

        String gender = user.getGender();
        if (gender != null)
            genderSpinner.setSelection(dataAdapter.getPosition(gender));

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setGender(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getIDs() {
        heightSpinner = (Spinner) findViewById(R.id.spinner_height);
        genderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        fullNameEditText = (EditText) findViewById(R.id.nameEditText);
        alertTextView = (TextView) findViewById(R.id.alert);
    }

    private void continueAction() {

        ValidationHelper.hideSoftKeyboard(DetailActivity.this);

        try {

            ValidationHelper.validateEmptyEditText(this.fullNameEditText);

            user.setName(fullNameEditText.getText().toString());

            loginDatabaseAdapter.updateEntry(user.getEmail(), user.getName(), user.getGender(), user.getHeight(), user.getTwitterID());

            PrefUtils.setCurrentUser(user, DetailActivity.this);

            callMainActivity();
        } catch (CustomException e) {

            this.alertTextView.setText(e.getMessage());
            ViewAnimation.showAlert(this.alertTextView);
        }
    }

    private void callMainActivity() {

        Intent homeInt = new Intent(this, MainActivity.class);
        homeInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeInt);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
    //endregion

    //region Button Actions
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.continueAction:

                continueAction();

                break;

            default:
                break;
        }
    }
    //endregion


}
