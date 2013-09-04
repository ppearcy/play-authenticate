import static org.junit.Assert.assertEquals;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import play.test.FakeApplication;
import static org.junit.Assert.*;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.*;

public class TestTwitterHelper {

    @Test
    public void testGetClient() {
        FakeApplication fa = play.test.Helpers.fakeApplication();
    	
        play.test.Helpers.running(fa, new Runnable() {
	        public void run() {    	
		    	AccessToken accessToken  = new AccessToken("1724520780-qZbfbugVTNoBeGGu3OidKVZwKJPSuKoPvfjktnK", "0iHULPkB5tXRl4N2yQcsvPA2J50gmWx4osR0jjAU8");
		    	Twitter twit = utils.TwitterHelper.getTwitterClient(accessToken);
		    	
		    	assertNotNull(twit);
	        }
        });
    }
    
    @Test
    public void testPostMessage() throws TwitterException {
        FakeApplication fa = play.test.Helpers.fakeApplication();
    	
        play.test.Helpers.running(fa, new Runnable() {
	        public void run() {    	
		    	AccessToken accessToken  = new AccessToken("1724520780-qZbfbugVTNoBeGGu3OidKVZwKJPSuKoPvfjktnK", "0iHULPkB5tXRl4N2yQcsvPA2J50gmWx4osR0jjAU8");
		    	
		    	String latestStatus = "This a test " + DateTime.now().toString();
		    	Status stat = null;
				try {
					stat = utils.TwitterHelper.postTwitterMessage(latestStatus, accessToken);
				} catch (TwitterException e) {
					assertFalse(true);
				}
		    	
		    	assertEquals(stat.getText(), latestStatus);
	        }
        });
    }
    
    @Test
    public void testTimeline() throws TwitterException {
        FakeApplication fa = play.test.Helpers.fakeApplication();
    	
        play.test.Helpers.running(fa, new Runnable() {
	        public void run() {    	
		    	AccessToken accessToken  = new AccessToken("1724520780-qZbfbugVTNoBeGGu3OidKVZwKJPSuKoPvfjktnK", "0iHULPkB5tXRl4N2yQcsvPA2J50gmWx4osR0jjAU8");
		    	
		    	String latestStatus = "This a test " + DateTime.now().toString();
		    	Status stat = null;
				try {
					stat = utils.TwitterHelper.postTwitterMessage(latestStatus, accessToken);
				} catch (TwitterException e) {
					assertFalse(true);
				}
		    	
		    	assertEquals(stat.getText(), latestStatus);
		    	
		    	List<Status> statusList = null;
				try {
					statusList = utils.TwitterHelper.getTimeline(accessToken);
				} catch (TwitterException e) {
					assertFalse(true);
				}
		    	assertEquals(statusList.iterator().next().getText(), latestStatus);
	        }
        });
    }
    
}
