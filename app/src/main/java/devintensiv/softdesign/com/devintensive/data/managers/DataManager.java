package devintensiv.softdesign.com.devintensive.data.managers;


import android.content.Context;

import devintensiv.softdesign.com.devintensive.data.network.RestService;
import devintensiv.softdesign.com.devintensive.data.network.ServiceGenerator;
import devintensiv.softdesign.com.devintensive.data.network.req.UserLoginReq;
import devintensiv.softdesign.com.devintensive.data.network.res.UserModelRes;
import devintensiv.softdesign.com.devintensive.utils.DevIntensiveApplication;
import retrofit2.Call;


public class DataManager {

    private static DataManager INSTANCE = null;
    private PreferencesManager mPreferencesManager;
    private Context mContext;
    private RestService mRestService;

    public DataManager() {
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevIntensiveApplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Context getContext() {
        return mContext;
    }

    //region ============== Network ===============

    public Call<UserModelRes> loginUser (UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }
    //endregion
}
