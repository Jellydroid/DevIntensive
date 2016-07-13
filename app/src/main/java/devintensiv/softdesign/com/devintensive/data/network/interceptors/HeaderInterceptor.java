package devintensiv.softdesign.com.devintensive.data.network.interceptors;


import java.io.IOException;

import devintensiv.softdesign.com.devintensive.data.managers.DataManager;
import devintensiv.softdesign.com.devintensive.data.managers.PreferencesManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        PreferencesManager pm = DataManager.getInstance().getPreferencesManager();

        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .header("X-Access-Token", pm.getAuthToken())
                .header("Request-User-Id", pm.getUserId())
                .header("User-Agent", "Devintensive");

        Request request = requestBuilder.build();

        return chain.proceed(request);
    }
}
