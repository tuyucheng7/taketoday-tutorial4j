package cn.tuyucheng.taketoday.okhttp.interceptors;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

public class CacheControlResponeInterceptor implements Interceptor {

	@Override
	public Response intercept(Chain chain) throws IOException {
		Response response = chain.proceed(chain.request());
		return response.newBuilder()
			.header("Cache-Control", "no-store")
			.build();
	}

}
