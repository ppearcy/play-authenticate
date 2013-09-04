package forms;
import static play.data.Form.form;
import play.data.Form;

/**
 * 
 * @author ppearcy
 *
 * This class simply encapsulates a users tweet message for displaying in a form
 */
public class MessageTweet {
	public MessageTweet() {
	}
	
	public String Message;
	public static final Form<MessageTweet> TWEET_FORM = form(MessageTweet.class);

}
