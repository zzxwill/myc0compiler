package compiler.structure;

public class var {
	private int value;
	private String name;
	private int type;
	private int addr;
	    
	    public int getvalue() {
	        return value;
	    }

	    public String getname() {
	        return name;
	    }

	    public int gettype() {
	        return type;
	    }

	    public int getaddr() {
	        return addr;
	    }

	    public void setvalue(int value) {
	        this.value = value;
	    }

	    public void setname(String name) {
	        this.name = name;
	    }

	    public void settype(int type) {
	        this.type = type;
	    }

	    public void setaddr(int addr) {
	        this.addr= addr;
	    }
}
