package cn.tuyucheng.taketoday.repositoryvsdaopattern;

import java.util.ArrayList;
import java.util.List;

public class TweetDaoImpl implements TweetDao {

	@Override
	public List<Tweet> fetchTweets(String email) {
		List<Tweet> tweets = new ArrayList<>();

		// call Twitter API and prepare Tweet object
		return tweets;
	}
}