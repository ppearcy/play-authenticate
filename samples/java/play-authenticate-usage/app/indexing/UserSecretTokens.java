package indexing;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import com.feth.play.module.pa.user.AuthUser;
import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.annotations.IndexType;
/*
 * 
 */
/**
 * @author ppearcy
 *
 * This class persists authUser to elasticsearch which holds sensitive information for user API access.
 *  
 */
@IndexType(name = "UserSecretTokens")
public class UserSecretTokens extends Index {
	/**
	 * Data for the secret tokens in map form
	 */
	public Map<String, Object> 	data;

	/**
	 * Object mapper for going from Object to map
	 */
	final static ObjectMapper mapper = new ObjectMapper();

    /**
     * Find method static for play-elasticsearc
     */
    public static Finder<UserSecretTokens> find = new Finder<UserSecretTokens>(UserSecretTokens.class);	
    
    /**
     * Default constructor, required by play-elasticsearch
     */
    public UserSecretTokens() {
    	super();
	}

    /**
     * Generates an indexable item from an actual authUser
     * 
     * @param authUser
     */
    public UserSecretTokens(AuthUser authUser) {
    	super();

        // Convert to a map going in.
    	this.data = mapper.convertValue(authUser, Map.class);
    	
    	// Ensure we never save the cleartext password accidently
    	if (this.data.containsKey("password"))
    		this.data.remove("password");
    	
    	this.id = authUser.getProvider() + "-" + authUser.getId();
	}

	/**
	 * @see com.github.cleverage.elasticsearch.Indexable#fromIndex(java.util.Map)
	 */
	@Override
	public Indexable fromIndex(Map map) {
		this.data = map;

        return this;    
    }

	/**
	 * @see com.github.cleverage.elasticsearch.Indexable#toIndex()
	 */
	@Override
	public Map toIndex() {
		return this.data;
	}
}
