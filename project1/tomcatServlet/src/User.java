import java.util.HashMap;

/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */
public class User {

	 private final String username;
	 protected final HashMap<String, Integer> cart;

    public User(String username) {
        this.username = username;
        this.cart = new HashMap<String, Integer>();
    }

    public String getUsername() {
        return this.username;
    }

	public HashMap<String, Integer> getCart() {
		return cart;
	}
    
    
}
