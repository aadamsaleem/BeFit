package com.aadam.befit.helpers;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.aadam.befit.R;
import com.aadam.befit.animation.ViewAnimation;


/**
 * Created by aadam on 14/4/2017.
 */

public abstract class ValidationHelper {

    static CustomException customException;

    public static void resetEditText(final Context context, final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                editText.setTextColor(context.getResources().getColor(R.color.black));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static String validateEmail(Context context, EditText email) throws CustomException {

        String emailRegex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Boolean isValid = email.getText().toString().matches(emailRegex);

        if (!isValid) {

            ViewAnimation.shake(email);

            email.setTextColor(context.getResources().getColor(R.color.red));

            customException = new CustomException("The email you entered is incorrect.");
            throw customException;
        } else {
            return email.getText().toString();
        }
    }

    public static String stringComparison(EditText firstString, EditText secondString) throws CustomException {

        if (!firstString.getText().toString().equals(secondString.getText().toString())) {

            ViewAnimation.shake(firstString);
            ViewAnimation.shake(secondString);
            customException = new CustomException("The password you entered is incorrect.\nConfrim password didn't match.");
            throw customException;
        } else {
            return firstString.getText().toString();
        }
    }

    public static String validatePassword(EditText editText) throws CustomException {

        if (editText.getText().toString().length() < 5) {

            ViewAnimation.shake(editText);
            customException = new CustomException("Password must have at least 5 characters.");
            throw customException;
        } else {
            return editText.getText().toString();
        }
    }

    public static String validateEmptyEditText(EditText editText) throws CustomException {

        if (editText.getText().toString().length() == 0) {

            ViewAnimation.shake(editText);
            customException = new CustomException("Please, fill all the fields.");
            throw customException;
        } else {
            return editText.getText().toString();
        }
    }

    public static void hideSoftKeyboard(AppCompatActivity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
