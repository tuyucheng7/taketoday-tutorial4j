package cn.tuyucheng.taketoday.retrofit.rx;

import cn.tuyucheng.taketoday.retrofit.models.Contributor;
import cn.tuyucheng.taketoday.retrofit.models.Repository;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

import java.util.List;

public interface GitHubRxApi {

	/**
	 * List GitHub repositories of user
	 *
	 * @param user GitHub Account
	 * @return GitHub repositories
	 */
	@GET("users/{user}/repos")
	Observable<List<Repository>> listRepos(@Path("user") String user);

	/**
	 * List Contributors of a GitHub Repository
	 *
	 * @param user GitHub Account
	 * @param repo GitHub Repository
	 * @return GitHub Repository Contributors
	 */
	@GET("repos/{user}/{repo}/contributors")
	Observable<List<Contributor>> listRepoContributors(@Path("user") String user, @Path("repo") String repo);

}
