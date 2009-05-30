package generate;

public class Gen{
	private int label;
	private int code;
	private int addr1;
	private int addr2;
	private int result;
	private int out_port=0;
	private String op;

	    public String getop() {
	        return op;
	    }

	    public int getlabel() {
	        return label;
	    }

	    public int getcode() {
	        return code;
	    }
	 
	    public int getaddr1() {
	        return addr1;
	    }

	    public int getaddr2() {
	        return addr2;
	    }

	    public int getresult() {
	        return result;
	    }

	   public int getout_port() {
	        return out_port;
	    }

	    public void setlabel(int label) {
	        this.label = label;
	    }

	    public void setcode(int code) {
	        this.code = code;
	    }
	    
	    public void setaddr1(int addr1) {
	        this.addr1= addr1;
	    }

	    public void setaddr2(int addr2) {
	        this.addr2= addr2;
	    }

	    public void setresult(int result) {
	        this.result = result;
	    }

	    public void setop(String op) {
	        this.op = op;
	    }

	    public void setout_port(int out_port) {
	        this.out_port = out_port;
	    }
	}
