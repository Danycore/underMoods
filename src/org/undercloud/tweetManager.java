package org.undercloud;

/**
 * Created by bautista on 14-11-24.
 */
import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import java.util.HashMap;

public class tweetManager {
	public static ArrayList<HashMap<String, String>> getTweets(String topic) {

		Twitter twitter = new TwitterFactory().getInstance();
		ArrayList<HashMap<String, String>> tweetList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> document;
		String country;
		try {
			Query query = new Query(topic);
			// Le pedimos a la API que nos traiga 100 resultados por página de búsqueda
			query.setCount(100);
			QueryResult result;
			int i = 0;
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					//tweetList.add(tweet.getText());

					// creamos el Hashmap para meter la información valiosa de cada tweet
					document = new HashMap<String, String>();

					// Se guarda el ID del tweet
					document.put("id", Long.toString((tweet.getId())));

					// Se guarda el texto del tweet
					document.put("tweet", tweet.getText());

					// Se guarda el localización del tweet
					if (tweet.getGeoLocation() != null) {
						document.put("location", tweet.getGeoLocation().toString());
					} else {
						document.put("location", "");
					}

					// Se guarda el lugar de origen del tweet
					if (tweet.getPlace() != null) {
						document.put("place", tweet.getPlace().toString());
						country = tweet.getPlace().getCountry();
						document.put("country", country);
					} else {
						document.put("place", "");
						document.put("country", "");
					}

					// Se guarda la fecha del tweet
					if (tweet.getCreatedAt() != null) {
						document.put("date", tweet.getCreatedAt().toString());
					} else {
						document.put("date", "");
					}

					// Se guarda el número de rt del tweet
					document.put("RT", Integer.toString(tweet.getRetweetCount()));

					// Se guarda el número de likes del tweet
					document.put("likes", Integer.toString(tweet.getFavoriteCount()));
					tweetList.add(document);
				}
				i++;
			} while ((query = result.nextQuery()) != null);
			//} while (i < 1);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
		}
		return tweetList;
	}
}
