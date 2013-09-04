
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static org.junit.Assert.assertEquals;

import indexing.UserSecretTokens;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.common.jackson.core.JsonFactory;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.feth.play.module.pa.providers.oauth1.OAuth1AuthInfo;
import com.feth.play.module.pa.providers.oauth1.OAuth1AuthUser;
import com.feth.play.module.pa.providers.oauth1.twitter.TwitterAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import static org.junit.Assert.*;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.*;

import play.test.Helpers.*;

public class TestTwitterSecretTokens {

    @Before
    public void setUp() throws JsonParseException, IOException {
    }

    @Test
    public void saveAndFind() {
        FakeApplication fa = play.test.Helpers.fakeApplication();
    	
        play.test.Helpers.running(fa, new Runnable() {
        public void run() {
        	OAuth1AuthInfo info = new OAuth1AuthInfo ("1724520780-qZbfbugVTNoBeGGu3OidKVZwKJPSuKoPvfjktnK", "0iHULPkB5tXRl4N2yQcsvPA2J50gmWx4osR0jjAU8");
        	
        	ObjectMapper mapper = new ObjectMapper();
        	org.codehaus.jackson.JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead
        	JsonParser jp;
        	JsonNode actualObj = null;
			try {
				jp = factory.createJsonParser("{\"id\":\"testid\",\"name\":\"testname\",\"lang\":\"testlocal\",\"screen_name\":\"CoolName\"}");
	        	actualObj = mapper.readTree(jp);
			} catch (IOException e) {
				e.printStackTrace();
				assertFalse(true);
			}

        	AuthUser testUser = new TwitterAuthUser (actualObj, info);
        	UserSecretTokens secrets = new UserSecretTokens(testUser);
        	secrets.index();
        	
        	UserSecretTokens fromSearch = UserSecretTokens.find.byId(secrets.id);
			Map<String, Object> oauthInfo = (Map<String, Object>) fromSearch.data.get("oauth1AuthInfo");
			String accessToken = (String) oauthInfo.get("accessToken");
			String accessTokenSecret = (String) oauthInfo.get("accessTokenSecret");
        	
			assertEquals(accessToken, info.getAccessToken());
			assertEquals(accessTokenSecret, info.getAccessTokenSecret());
        }
      });
    }
}
