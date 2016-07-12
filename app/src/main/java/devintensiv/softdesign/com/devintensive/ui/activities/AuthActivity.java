package devintensiv.softdesign.com.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import devintensiv.softdesign.com.devintensive.R;
import devintensiv.softdesign.com.devintensive.data.managers.DataManager;
import devintensiv.softdesign.com.devintensive.data.network.req.UserLoginReq;
import devintensiv.softdesign.com.devintensive.data.network.res.UserModelRes;
import devintensiv.softdesign.com.devintensive.utils.NetworkStatusChecker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.login_btn)
    Button mSignIn;
    @BindView(R.id.remember_txt)
    TextView mRememberPassword;
    @BindView(R.id.login_email_et)
    EditText mLogin;
    @BindView(R.id.login_password_et)
    EditText mPassword;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                signIn();
                break;

            case R.id.remember_txt:
                rememberPassword();
                break;
        }

    }

    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);

    }

    private void loginSuccess(UserModelRes userModel) {
        showSnackBar(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        saveUserValues(userModel);
        saveUserData(userModel);
        saveUserPhoto(userModel);
        saveUserName(userModel);
        saveUserAvatar(userModel);

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);

    }

    private void signIn() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {

            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue((new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        showSnackBar("Неверный логин или пароль");
                    } else
                        showSnackBar("Все пропало");
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    //TODO: 11.07.16 обработать ошибки  ретрофита

                }
            }));
        } else {
            showSnackBar("Сеть на данный момент недоступна, попробуйте позже");
        }
    }

    private void saveUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };

        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    public void saveUserData(UserModelRes userModel) {
        List<String> userFields = new ArrayList<>();
        userFields.add(userModel.getData().getUser().getContacts().getPhone());
        userFields.add(userModel.getData().getUser().getContacts().getEmail());
        userFields.add(userModel.getData().getUser().getContacts().getVk());
        userFields.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        userFields.add(userModel.getData().getUser().getPublicInfo().getBio());

        mDataManager.getPreferencesManager().saveUserProfileData(userFields);
    }

    public void saveUserPhoto(UserModelRes userModel) {
        Uri userPhoto = Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto());

        mDataManager.getPreferencesManager().saveUserPhoto(userPhoto);
    }

    public void saveUserName(UserModelRes userModel) {
        String firstName = userModel.getData().getUser().getFirstName();
        String secondName = userModel.getData().getUser().getSecondName();

        mDataManager.getPreferencesManager().saveUserName(firstName,secondName);
    }

    public void saveUserAvatar(UserModelRes userModel) {
        Uri avatar = Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar());

        mDataManager.getPreferencesManager().saveUserAvatar(avatar);
    }
}
