package devintensiv.softdesign.com.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import devintensiv.softdesign.com.devintensive.R;
import devintensiv.softdesign.com.devintensive.data.managers.DataManager;
import devintensiv.softdesign.com.devintensive.utils.ConstantManager;
import devintensiv.softdesign.com.devintensive.utils.RoundedAvatarDrawable;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;

    @BindView(R.id.call_img) ImageView mCallImg;
    @BindView(R.id.mail_img) ImageView mMailImg;
    @BindView(R.id.vk_img) ImageView mVkImg;
    @BindView(R.id.git_img) ImageView mGitImg;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;

    @BindView(R.id.phone_et) EditText mUserPhone;
    @BindView(R.id.email_et) EditText mUserMail;
    @BindView(R.id.profile_vk_et) EditText mUserVk;
    @BindView(R.id.repository_et) EditText mUserGit;
    @BindView(R.id.about_me_et) EditText mUserBio;

    @BindView(R.id.user_info_rait_txt) TextView mUserValueRating;
    @BindView(R.id.user_info_code_lines_txt) TextView mUserValueCodeLines;
    @BindView(R.id.user_info_projects_txt) TextView mUserValueProjects;

    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.profile_placeholder) RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;

    private List<EditText> mUserInfoViews;
    private List<TextView> mUserValueViews;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private ImageView mAvatarImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mUserValueViews = new ArrayList<>();
        mUserValueViews.add(mUserValueRating);
        mUserValueViews.add(mUserValueCodeLines);
        mUserValueViews.add(mUserValueProjects);

        mCallImg.setOnClickListener(this);
        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mGitImg.setOnClickListener(this);
        mMailImg.setOnClickListener(this);
        mVkImg.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_photo) // сделать плейсхолдер и transform +crop
                .into(mProfileImage);


        if (savedInstanceState == null) {
            showSnackBar("Активити запускается впервые");

        } else {
            showSnackBar("Активити уже запускалось");
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_img:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    String phone = mUserPhone.getText().toString();
                    if (!phone.equals("") || !phone.equals("null")) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phone, null));
                        startActivity(callIntent);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{
                                android.Manifest.permission.CALL_PHONE
                        }, ConstantManager.CALL_PHONE_PERMISSION_CODE);

                        Snackbar.make(mCoordinatorLayout, "Для корректной работы необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                                .setAction("Разрешить", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openApplicationSettings();
                                    }
                                }).show();
                    }
                }
                break;

            case R.id.mail_img:
                String email = mUserMail.getText().toString();
                if (!email.equals("") || !email.equals("null")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
                    startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));
                }
                break;

            case R.id.vk_img:
                String vkUrl = mUserVk.getText().toString();
                if (!vkUrl.equals("") || !vkUrl.equals("null")) {
                    Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/" + vkUrl));
                    startActivity(vkIntent);
                }
                break;

            case R.id.git_img:
                String gitUrl = mUserVk.getText().toString();
                if (!gitUrl.equals("") || !gitUrl.equals("null")) {
                    Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/" + gitUrl));
                    startActivity(vkIntent);
                }
                break;

            case R.id.fab:
                showSnackBar("Click");
                if (mCurrentEditMode == 1) {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                } else {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                }
                break;
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_CONSTANT_PHOTO);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserFields();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        assert navigationView != null;

        setupDrawerHeader();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackBar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

    }

    private void setupDrawerHeader() {
        View headerLayout = mNavigationView.getHeaderView(0);

        TextView userName = (TextView) headerLayout.findViewById(R.id.user_name_txt);
        userName.setText(mDataManager.getPreferencesManager().loadUserName());

        TextView userMail = (TextView) headerLayout.findViewById(R.id.user_email_txt);
        userMail.setText(mDataManager.getPreferencesManager().loadUserMail());

        mAvatarImg = (ImageView) headerLayout.findViewById(R.id.avatar_iv);

        Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.avatar)).getBitmap();
       /* Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.avatar);*/
        RoundedAvatarDrawable roundedAvatarDrawable = new RoundedAvatarDrawable(bitmap);
        mAvatarImg.setImageDrawable(roundedAvatarDrawable);

    }

    /**
     * Получение результата из другой Activity (фото из камеры или галлереи)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    /**
     * переключает режим редактирования
     *
     * @param mode если 1 режим редактирования, если 0 режим просмотра
     */
    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {

                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {

                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unLockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

                saveUserFields();
            }
        }
    }

    private void initUserFields() {
        showSnackBar("initUserFields");
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        Log.e(TAG, userData.toString());


        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserFields() {
        showSnackBar("saveUserFields");
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserInfoValue() {
        List <String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    private void runWithDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        }, 5000);
    }

    private void loadPhotoFromGallery() {

        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);

    }

    private void loadPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //// TODO тут обрабаывает разрещение (разрешение получено)
                // вывести сообщение или обработать логикой
            }
        }

        if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //// TODO тут обрабаывает разрещение (разрешение получено)
            // вывести сообщение или обработать логикой
        }

        if (requestCode == ConstantManager.CALL_PHONE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //// TODO тут обрабаывает разрещение (разрешение получено)
                showSnackBar("Ok");
            }
        }
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private void unLockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_CONSTANT_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                loadPhotoFromGallery();
                                showSnackBar("галлерея");
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                showSnackBar("камера");
                                break;
                            case 2:
                                dialog.cancel();
                                showSnackBar("отмена");
                                break;
                        }
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File stоrageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", stоrageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    public void openApplicationSettings() {

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);

    }
}
