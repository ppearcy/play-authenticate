package indexing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.LinkedAccount;
import models.User;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectMapper.DefaultTyping;
import org.elasticsearch.common.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import utils.TwitterHelper;

import com.feth.play.module.pa.providers.oauth1.twitter.TwitterAuthUser;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.annotations.IndexType;

/**
 * @author ppearcy
 *
 * This class holds a user item and allows it to be saved to elasticsearch.
 * It has experimental support for persisting a seriabliazable POJO, but this does not appear
 * to be working correctly,  
 *
 */
@IndexType(name = "UserInfo")
public class UserDoc extends Index {
	/**
	 * Holds the user info in map form
	 */
	Map<String, Object> 	userMap;
	/**
	 * Instance of the user object
	 */
	User	 				user;
	/**
	 * The type of the user
	 */
	String					userType;

	/**
	 * For objects to Maps
	 */
	final static ObjectMapper mapper = new ObjectMapper();

	Logger logger = LoggerFactory.getLogger("chapters.introduction.HelloWorld1");
	
	/**
     * Find method for play-elasticsearc
     */
    public static Finder<UserDoc> find = new Finder<UserDoc>(UserDoc.class);	
    
    /**
     * Default constructore required by play-elasticsearch 
     */
    public UserDoc() {
    	super();
    	this.userMap = null;
    	this.user = null;
    	this.id = null;
	}

    /**
     * Generates an object based on the user
     * 
     * @param user
     * @throws IOException 
     */
    public UserDoc(User user) throws IOException {
    	super();

    	this.user = user;
    	this.userType = user.getClass().getName();
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;

        oos = new ObjectOutputStream( baos );
	    oos.writeObject( user );
	    oos.close();

	    String UserString = new String( Base64.encodeBytes(baos.toByteArray() ) );    	

        // Convert to a map going in.
    	this.userMap = mapper.convertValue(user, Map.class);
    	this.userMap.put("Base64SerializedUserObject", UserString);
    	
    	this.id = user.getIdentifier();
	}
    
    /**
     * Generates a twitter access token for this user.
     * These keys are only stored in elasticsearch
     * 
     * @return AccessToken or null if none exists
     */
    public twitter4j.auth.AccessToken getTwitterAccessToken() {
        List<Map<String, Object>> linkedAccounts = (List<Map<String, Object>>) userMap.get("linkedAccounts"); 
    	for (Map<String, Object> account : linkedAccounts) {
    		String providerKey = (String) account.get("providerKey");
    		if (providerKey.compareTo("twitter") == 0) {
    			// Grab this account info from search
    			String providerUserId = (String) account.get("providerUserId");
    			UserSecretTokens secrets = UserSecretTokens.find.byId(providerKey + "-" + providerUserId);
    			Map<String, Object> oauthInfo = (Map<String, Object>) secrets.data.get("oauth1AuthInfo");
    			String accessToken = (String) oauthInfo.get("accessToken");
    			String accessTokenSecret = (String) oauthInfo.get("accessTokenSecret");
    			
    			return new twitter4j.auth.AccessToken(accessToken, accessTokenSecret);
    		}
    	}
		return null;
    }
    
    /* (non-Javadoc)
     * @see com.github.cleverage.elasticsearch.Indexable#fromIndex(java.util.Map)
     */
    @Override
	public Indexable fromIndex(Map map) {
		this.userMap = map;
		String UserString = (String) map.get("Base64SerializedUserObject");
		byte[] data;
        ObjectInputStream ois;
		try {
			data = Base64.decode(UserString);
			ois = new ObjectInputStream(new ByteArrayInputStream(  data ) );
			this.user  = (User) ois.readObject();
			ois.close();
		} catch (IOException e) {
			logger.error("Unable to serialize User java object back out", e);
			return null;
		} catch (ClassNotFoundException e) {
			logger.error("Unable to serialize User java object back out", e);
			return null;
		}

        return this;    
    }

	/* (non-Javadoc)
	 * @see com.github.cleverage.elasticsearch.Indexable#toIndex()
	 */
	@Override
	public Map toIndex() {
		return userMap;
	}

}
