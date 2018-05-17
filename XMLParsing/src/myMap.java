import java.util.HashMap;

public class myMap<K, V> extends HashMap<K, V> {
	
	/**
	 * Default Serial Value
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<K,V> og;
	
	public myMap(HashMap<K,V> og) {
		this.og = og;
	}
	
	public boolean add(K key, V value) {
		if(!this.og.containsKey(key)) {
			og.put(key, value);
			return true;
		}
		return false;
	}
}
