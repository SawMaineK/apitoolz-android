package com.msdt.apitoolz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
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
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.msdt.apitoolz.clients.NetworkEngine;
import com.msdt.apitoolz.models.User;
import com.msdt.apitoolz.views.CustomDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccountActivity extends BaseAppCompatActivity {

    private static final int REQUEST_PHONE_VERIFY_CODE = 1;
    private MaterialEditText edtFName;
    private MaterialEditText edtLName;
    private MaterialEditText edtEmail;
    private CountryCodePicker edtPhoneCode;
    private MaterialEditText edtPhoneNumber;
    private RadioGroup rdoGender;
    private Button btnEditAccount;
    private String verifyPhoneNumber;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_close.toString(), R.color.colorPrimary, 16));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtFName 	        = (MaterialEditText) findViewById(R.id.edt_fname);
        edtLName 	        = (MaterialEditText) findViewById(R.id.edt_lname);
        edtEmail 	        = (MaterialEditText) findViewById(R.id.edt_email);
        edtPhoneCode        = (CountryCodePicker) findViewById(R.id.edt_phone_code);
        edtPhoneNumber      = (MaterialEditText) findViewById(R.id.edt_phone_number);
        rdoGender           = (RadioGroup) findViewById(R.id.rdo_gender);
        btnEditAccount 	    = (Button)findViewById(R.id.btn_edit_account);

        user = (User) BaseApplication.getUser();
        edtFName.setText(user.getFirstName());
        edtLName.setText(user.getLastName());
        edtEmail.setText(user.getEmail());
        String phoneNumber = user.getPhone();
        if(user.getPhone().startsWith(edtPhoneCode.getSelectedCountryCode())){
            phoneNumber = user.getPhone().replaceFirst(edtPhoneCode.getSelectedCountryCode(),"");
        }
        edtPhoneNumber.setText(phoneNumber);
        if(user.getGender() != null && user.getGender().equals("female")){
            rdoGender.check(R.id.rdo_female);
        }

        btnEditAccount.setOnClickListener(onclick);
    }


    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == btnEditAccount){
                if(checkValidation()){
                    if(!user.getPhone().equals(edtPhoneCode.getSelectedCountryCode()+edtPhoneNumber.getText().toString())){
                        verifyPhoneNumber(edtPhoneNumber.getText().toString());
                    }else{
                        performLoading(true);
                        verifyPhoneNumber = edtPhoneCode.getSelectedCountryCode()+edtPhoneNumber.getText().toString();
                        register(user);
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
                        register(user);
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                        Log.e("Account Kit Error", "Account Kit Error:" + error.getUserFacingMessage());
                    }
                });
            }
        }
    }

    private void register(final User user) {
        user.setFirstName(edtFName.getText().toString());
        user.setLastName(edtLName.getText().toString());
        user.setUsername(edtFName.getText().toString().toLowerCase()+edtLName.getText().toString().toLowerCase());
        user.setEmail(edtEmail.getText().toString());
        user.setPhone(verifyPhoneNumber);
        user.setGender(rdoGender.getCheckedRadioButtonId() == R.id.rdo_male ? "male" : "female");

        NetworkEngine.getInstance().editProfile(user.getId(),user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                performLoading(false);
                if(response.isSuccessful()){
                    BaseApplication.login(response.body());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    recreate();
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
                            final CustomDialog dialog = new CustomDialog(EditAccountActivity.this);
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

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        onBackPressed();
        return super.getSupportParentActivityIntent();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }
}
