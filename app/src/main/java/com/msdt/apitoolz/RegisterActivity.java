package com.msdt.apitoolz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.hbb20.CountryCodePicker;
import com.msdt.apitoolz.clients.NetworkEngine;
import com.msdt.apitoolz.models.User;
import com.msdt.apitoolz.views.CustomDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseAppCompatActivity {

    private static final int REQUEST_PHONE_VERIFY_CODE = 1;
    private MaterialEditText edtFName;
    private MaterialEditText edtLName;
    private MaterialEditText edtEmail;
    private MaterialEditText edtPassword;
    private CountryCodePicker edtPhoneCode;
    private MaterialEditText edtPhoneNumber;
    private RadioGroup rdoGender;
    private Button btnRegister;
    private String verifyPhoneNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFName 	        = (MaterialEditText) findViewById(R.id.edt_fname);
        edtLName 	        = (MaterialEditText) findViewById(R.id.edt_lname);
        edtEmail 	        = (MaterialEditText) findViewById(R.id.edt_email);
        edtPassword	        = (MaterialEditText) findViewById(R.id.edt_password);
        edtPhoneCode        = (CountryCodePicker) findViewById(R.id.edt_phone_code);
        edtPhoneNumber      = (MaterialEditText) findViewById(R.id.edt_phone_number);
        rdoGender           = (RadioGroup) findViewById(R.id.rdo_gender);
        btnRegister 	    = (Button)findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(onclick);
    }

    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == btnRegister){
                if(checkValidation()){
                    // Verify to phone number if available google servier
                    if(isGooglePlayServicesAvailable()){
                        verifyPhoneNumber(edtPhoneNumber.getText().toString());
                    }else{
                        verifyPhoneNumber = edtPhoneCode.getSelectedCountryCode()+edtPhoneNumber.getText().toString();
                        register();
                    }


                }
            }
        }
    };

    private boolean checkValidation() {
        if(edtFName.getText().length() == 0) {
            edtFName.setError(getResources().getString(R.string.pls_enter_fname));
            return false;
        }
        if(edtLName.getText().length() == 0) {
            edtLName.setError(getResources().getString(R.string.pls_enter_lname));
            return false;
        }
        if(edtEmail.getText().length() == 0) {
            edtEmail.setError(getResources().getString(R.string.pls_enter_email));
            return false;
        }
        if(edtPassword.getText().length() == 0) {
            edtPassword.setError(getResources().getString(R.string.pls_enter_password));
            return false;
        }
        if(edtPhoneNumber.getText().length() == 0) {
            edtPhoneNumber.setError(getResources().getString(R.string.pls_enter_phone));
            return false;
        }
        return true;
    }

    private void verifyPhoneNumber(String phone) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        if(phone.length() > 0){
            PhoneNumber phoneNumber = new PhoneNumber(edtPhoneCode.getSelectedCountryCode().toString(),phone);
            configurationBuilder.setInitialPhoneNumber(phoneNumber);
        }
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, REQUEST_PHONE_VERIFY_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHONE_VERIFY_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
            } else if (loginResult.wasCancelled()) {
            } else {
                performLoading(true);
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        verifyPhoneNumber = phoneNumber.getCountryCode()+phoneNumber.getPhoneNumber();
                        performLoading(true);
                        NetworkEngine.getInstance().checkUser("phone:equal:"+verifyPhoneNumber).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                if(response.isSuccessful()){
                                    if(response.body().size() == 0){
                                        register();
                                    }else{
                                        register(response.body().get(0));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                        Log.e("Account Kit Error", "Account Kit Error:" + error.getUserFacingMessage());
                    }
                });
            }
        }
    }

    private void register() {

        final User user = new User();
        user.setFirstName(edtFName.getText().toString());
        user.setLastName(edtLName.getText().toString());
        user.setUsername(edtFName.getText().toString().toLowerCase()+edtLName.getText().toString().toLowerCase());
        user.setEmail(edtEmail.getText().toString());
        user.setPassword(edtPassword.getText().toString());
        user.setPasswordConfirmation(edtPassword.getText().toString());
        user.setPhone(verifyPhoneNumber);
        user.setGender(rdoGender.getCheckedRadioButtonId() == R.id.rdo_male ? "male" : "female");

        NetworkEngine.getInstance().register(user).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    authenticate(user.getEmail(), user.getPassword());
                } else {
                    switch (response.code()) {
                        case 400:
                            String error = null;
                            try {
                                error = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                            dialog.setTitle(getResources().getString(R.string.invalid_value));
                            dialog.setMessageType(CustomDialog.Error);
                            dialog.setMessage(error);
                            dialog.setOnClickPositiveListener(getResources().getString(R.string.try_again), new CustomDialog.OnClickPositiveListener() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void register(final User user) {

        user.setFirstName(edtFName.getText().toString());
        user.setLastName(edtLName.getText().toString());
        user.setUsername(edtFName.getText().toString().toLowerCase()+edtLName.getText().toString().toLowerCase());
        user.setEmail(edtEmail.getText().toString());
        user.setPassword(edtPassword.getText().toString());
        user.setPasswordConfirmation(edtPassword.getText().toString());
        user.setPhone(verifyPhoneNumber);
        user.setGender(rdoGender.getCheckedRadioButtonId() == R.id.rdo_male ? "male" : "female");

        NetworkEngine.getInstance().editProfile(user.getId(),user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    authenticate(user.getEmail(), user.getPassword());
                } else {
                    performLoading(false);
                    switch (response.code()) {
                        case 400:
                            String error = null;
                            try {
                                error = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                            dialog.setTitle(getResources().getString(R.string.invalid_value));
                            dialog.setMessageType(CustomDialog.Error);
                            dialog.setMessage(error);
                            dialog.setOnClickPositiveListener(getResources().getString(R.string.try_again), new CustomDialog.OnClickPositiveListener() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void authenticate(String email, String password){
        NetworkEngine.getInstance().authenticate(email,password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                performLoading(false);
                if(response.isSuccessful()) {
                    BaseApplication.login(response.body());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    switch (response.code()) {
                        case 400:
                            String error = null;
                            try {
                                error = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                            dialog.setTitle(getResources().getString(R.string.invalid_authenticate));
                            dialog.setMessageType(CustomDialog.Error);
                            dialog.setMessage(error);
                            dialog.setOnClickPositiveListener(getResources().getString(R.string.try_again), new CustomDialog.OnClickPositiveListener() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}
