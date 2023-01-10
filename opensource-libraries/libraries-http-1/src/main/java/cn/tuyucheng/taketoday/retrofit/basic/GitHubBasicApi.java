package cn.tuyucheng.taketoday.retrofit.basic;

import cn.tuyucheng.taketoday.retrofit.models.Contributor;
import cn.tuyucheng.taketoday.retrofit.models.Repository;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface GitHubBasicApi {

	/**
	 * List GitHub repositories of user
	 *
	 * @param user GitHub Account
	 * @return GitHub repositories
	 */
	@GET("users/{user}/repos")
	Call<List<Repository>> listRepos(@Path("user") String user);

	/**
	 * List Contributors of a GitHub Repository
	 *
	 * @param user GitHub Account
	 * @param repo GitHub Repository
	 * @return GitHub Repository Contributors
	 */
	@GET("repos/{user}/{repo}/contributors")
	Call<List<Contributor>> listRepoContributors(@Path("user") String user, @Path("repo") String repo);

}
