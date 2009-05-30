package compiler.structure;

/*
 * 针对KeyWord.txt及关键字提供一种数据结构。
 */
public class KeyWord {
	private String name="";
	private int code=0;

	    public String getname() {
	        return name;
	    }

	    public int getcode() {
	        return code;
	    }

	    public void setname(String name) {
	        this.name = name;
	    }

	    public void setcode(int code) {
	        this.code = code;
	    }
}
