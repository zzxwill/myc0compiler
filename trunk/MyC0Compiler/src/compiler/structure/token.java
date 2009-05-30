package compiler.structure;
/*
 * 定义一种词法分析表中的各项的类型
 */
public class token {
	private int label=0;
	private String name="";
	private int code=0;
	private int addr=0;
	/*
	 * Can not understand now!
	 */
	    
	    public int getlabel() {
	        return label;
	    }

	    public String getname() {
	        return name;
	    }

	    public int getcode() {
	        return code;
	    }

	    public int getaddr() {
	        return addr;
	    }

	    public void setlabel(int label) {
	        this.label = label;
	    }

	    public void setname(String name) {
	        this.name = name;
	    }

	    public void setcode(int code) {
	        this.code = code;
	    }

	    public void setaddr(int addr) {
	        this.addr= addr;
	    }
}
