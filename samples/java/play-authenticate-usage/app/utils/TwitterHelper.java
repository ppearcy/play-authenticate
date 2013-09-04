package utils;

import java.util.ArrayList;
import java.util.List;

import play.Play;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * @author ppearcy
 *
 * Helper class for all twitter operations
 */
public class TwitterHelper {
	/**
	 * @author ppearcy
	 *
	 * This exception is thrown when we detect our keys are invalid
	 */
	public static class TwitterBadTokens extends TwitterException {
		public TwitterBadTokens(Exception cause) {
			super(cause);
		}
	}
	
	/**
	 * 
	 * This will instantiate a twitter client for the user specified by the tokens
	 * 
	 * @param accessToken
	 * @return
	 */
	public static Twitter getTwitterClient(twitter4j.auth.AccessToken accessToken) {
		// The factory instance is re-useable and thread safe.
		play.Configuration config = Play.application().configuration();

		String consumerKey = config.getString("play-authenticate.twitter.consumerKey");
		String consumerSecret = config.getString("play-authenticate.twitter.consumerSecret");
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		twitter.setOAuthAccessToken(accessToken);
		return twitter;
	}
	
    /**
     * Retrieves the timeline for a user specified by the tokens
     * 
     * @param accessToken
     * @return
     * @throws TwitterException
     */
    public static List<twitter4j.Status> getTimeline(twitter4j.auth.AccessToken accessToken) throws TwitterException {
        List<twitter4j.Status> messages= new ArrayList<twitter4j.Status>();
        Twitter twit = getTwitterClient(accessToken);
        try {
			return twit.getHomeTimeline();
		} catch (TwitterException e) {
			if (isBadTwitterToken(e)) {
				throw new TwitterBadTokens(e);
			} else {
				throw e;
			}
		}        	
    }

	/**
	 * Posts a twitter message to a stream
	 * 
	 * @param message
	 * @param accessToken
	 * @return
	 * @throws TwitterException
	 */
	public static Status postTwitterMessage(String message, twitter4j.auth.AccessToken accessToken) throws TwitterException {
        Twitter twit = getTwitterClient(accessToken);
        try {
			return twit.updateStatus(message);
		} catch (TwitterException e) {
			if (isBadTwitterToken(e)) {
				throw new TwitterBadTokens(e);
			} else {
				throw e;
			}
		}
	}

	/**
	 * Checks if this exception is from bad tokens
	 * 
	 * @param e
	 * @return
	 */
	public static boolean isBadTwitterToken(TwitterException e) {
		return e.getErrorCode() == 89;
	}
}
