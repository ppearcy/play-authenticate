package controllers;

import forms.MessageTweet;
import indexing.UserDoc;

import java.util.List;

import models.User;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import utils.TwitterHelper;
import views.html.timeline;


/**
 * @author ppearcy
 *
 * Holds the main controller interfaces for the twitter client app
 *
 */
public class TwitterApp extends Controller {

	/**
	 * 
	 * 
	 * @return
	 * @throws TwitterException
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public static Result timeline() throws TwitterException {
		
		final User localUser = Application.getLocalUser(session());
		if (localUser == null) {
			return Application.login();
		}
		
		UserDoc user = UserDoc.find.byId(Long.toString(localUser.id));
		
		List<twitter4j.Status> statusList;
		AccessToken twitterAccessToken = user.getTwitterAccessToken();
		if (twitterAccessToken == null) {
			return Account.link();
		}
		
		try {
			statusList = TwitterHelper.getTimeline(twitterAccessToken);
		} catch (TwitterHelper.TwitterBadTokens e) {
			return com.feth.play.module.pa.controllers.Authenticate.authenticate("twitter");			
		}
		
		// Truncate to last to
		play.Configuration config = Play.application().configuration();
		int tweetsToDisplay = config.getInt("play-twitter-client.timeline_count", 10);
		
		statusList = statusList.subList(0, tweetsToDisplay);
		
		return ok(timeline.render(localUser, statusList, MessageTweet.TWEET_FORM));
	}

	/**
	 * This method accepts post and updates the users twitter status
	 * 
	 * @return Response to render
	 * @throws TwitterException
	 */
	@Restrict(@Group(Application.USER_ROLE))
	public static Result doTweet() throws TwitterException {
		// For some reason, wasn't getting form correctly serialized into class. 
		// Need to figure out why, but for now, this works
		//final Form<MessageTweet> filledForm  = MessageTweet.TWEET_FORM.bindFromRequest();
		String message = request().body().asFormUrlEncoded().get("Message")[0];
		
		// Ensure message is 140 characters. If it is any more than this, just truncated it and carry on
		message = message.substring(0, Math.min(message.length(), 140));
		
		final User localUser = Application.getLocalUser(session());
		UserDoc user = UserDoc.find.byId(Long.toString(localUser.id));

		AccessToken twitterAccessToken = user.getTwitterAccessToken();
		if (twitterAccessToken == null) {
			return com.feth.play.module.pa.controllers.Authenticate.authenticate("twitter");
		}
		
		try {
			TwitterHelper.postTwitterMessage(message, twitterAccessToken);
		} catch (TwitterHelper.TwitterBadTokens e) {
			return com.feth.play.module.pa.controllers.Authenticate.authenticate("twitter");			
		}

		return timeline();
	}
}
