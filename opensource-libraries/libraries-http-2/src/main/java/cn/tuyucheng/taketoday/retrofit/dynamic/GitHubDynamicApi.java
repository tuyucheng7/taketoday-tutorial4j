package cn.tuyucheng.taketoday.retrofit.dynamic;

import cn.tuyucheng.taketoday.retrofit.models.Contributor;
import cn.tuyucheng.taketoday.retrofit.models.Repository;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

import java.util.List;

public interface GitHubDynamicApi {

	@GET
	Call<List<Repository>> reposList(@Url String url);

	@GET("{fullUrl}")
	Call<List<Contributor>> contributorsList(@Path(value = "fullUrl", encoded = true) String fullUrl);
}
