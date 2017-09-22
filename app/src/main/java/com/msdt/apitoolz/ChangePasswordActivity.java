package com.msdt.apitoolz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.msdt.apitoolz.clients.NetworkEngine;
import com.msdt.apitoolz.models.User;
import com.msdt.apitoolz.views.CustomDialog;
import java.io.IOException;
import retrofit2.Call;

public class ChangePasswordActivity extends BaseAppCompatActivity {

    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_close.toString(), R.color.colorPrimary, 16));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNewPassword          = (EditText) findViewById(R.id.edt_new_password);
        edtConfirmPassword      = (EditText) findViewById(R.id.edt_confirm_password);
        btnChangePassword       = (Button) findViewById(R.id.btn_change_password);
        btnChangePassword.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == btnChangePassword){
                if(checkValidate())
                    changePassword();
            }
        }
    };

    private boolean checkValidate(){
        if(edtNewPassword.getText().length() == 0){
            edtNewPassword.setError(getResources().getString(R.string.pls_enter_new_password));
            return false;
        }
        if(edtConfirmPassword.getText().length() == 0){
            edtConfirmPassword.setError(getResources().getString(R.string.pls_enter_confirm_password));
            return false;
        }
        if(!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            edtConfirmPassword.setError(getResources().getString(R.string.pls_match_confirm_password));
            return false;
        }
        return true;
    }

    private void changePassword(){
        performLoading(true);
        User user = (User) BaseApplication.getUser();
        user.setPassword(edtNewPassword.getText().toString());
        user.setPasswordConfirmation(edtConfirmPassword.getText().toString());
        NetworkEngine.getInstance().editProfile(user.getId(), user).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                performLoading(false);
                if(response.isSuccessful()){
                    BaseApplication.login(response.body());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    closeAllActivities();
                }else {
                    switch (response.code()) {
                        case 400:
                            String error = null;
                            try {
                                error = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this);
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
