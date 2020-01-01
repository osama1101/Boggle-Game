
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;

public class TrieSet extends AbstractSet<String>{
	int       size;     // the number of words in the set represented by this trie
	boolean   isWord;   // whether this trie node is the end of a word
	TrieSet[] subtries; // the children tries of this node
    static public final int RADIX = 26;   // the number of children in each node

	TrieSet(){
		isWord = false;
		subtries = new TrieSet[RADIX];
	}
	
	public int size() {//MY SIZE variable is not correct
		return size;
	}
	
	public boolean containsEmptyString() {
		if(isWord == true) {
			return true;
		}
		return false;
	}
	
	public boolean contains(String string) {
		if(string.equals("")) {
			return isWord;
		}
		char c = string.charAt(0);
	    c = Character.toLowerCase(c);
		if(subtries[c - 'a'] == null) {
			return false;
		}
		return subtries[c - 'a'].contains(string.substring(1));
	}
	
	public boolean containsPrefix(String prefix) {
		if(prefix.equals("")) {
			return true;
		}
		char c = prefix.charAt(0);
	    c = Character.toLowerCase(c);
		if(subtries[c - 'a'] == null) {
			return false;
		}
		return subtries[c - 'a'].containsPrefix(prefix.substring(1));
	}
	
	public boolean add(String string) {
		    if(string.equals("")){
		    	if(isWord == true)
		    		return false;
		        isWord = true;
		        //System.out.println("TESTING!!");///TESTING
		        //System.out.println("The size before incrementation is: " + size);///TESTING
		        size++;
		        //System.out.println("The size after incrementation is: " + size);///TESTING
		        return true;
		    }
		    char c = string.charAt(0);
		    c = Character.toLowerCase(c);
		    if(subtries[c - 'a'] == null){
		        subtries[c - 'a'] = new TrieSet();
		    }
		    if(subtries[c - 'a'].add(string.substring(1))) {
		    	size++;
		    	return true;
		    }
		    	return false;
		    	
		    	//IS THERE A PROBLEM HERE????
		    //return add(string.substring(1));
		}
	
	public boolean isEmpty() {
		if(size == 0)
			return true;
		return false;
	}
	
	private ArrayList<String> toList(){
		ArrayList<String> list = new ArrayList<String>();
		return helper("", list);
	}
	
	private ArrayList<String> helper(String string, ArrayList<String> list){
		if(subtries != null) {
			if(isWord) {
				list.add(string);
				//helper("", list);//Change
			}
			for(char c = 'a'; c <= 'z'; c++) {
				if(subtries[c - 'a'] != null)
				    subtries[c - 'a'].helper(string +  c, list);
			}
		}
		return list;
	}
	
	public String toString() {
		ArrayList<String> list = toList();
		String string = "";
		for(String x : list) {
			string = string + x + " ";
		}
		return string;
	}
	
	public Iterator<String> iterator(){
		ArrayList<String> list = toList();
		return list.iterator();
	}
}