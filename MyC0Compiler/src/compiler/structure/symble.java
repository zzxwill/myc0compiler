package compiler.structure;

/*
 * 针对symble.txt及关键字提供一种数据结构，是符号表中的内容项
 * 符号表管理
// int number;		// 序号
//	int type;		// 类型
//	string name;	// 名称

 */
public class symble {
	private String name="";
	private int num=0;
	private int type=0;
	

	    public String getname() {
	    	
	        return name;
	    }

	    public int getnumber() {
	        return num;
	    }

	    public int gettype() {
	        return type;
	    }

	    public void setname(String name) {
	        this.name = name;
	    }

	    public void setnumber(int num) {
	        this.num = num;
	    }

	    public void settype(int type) {
	        this.type = type;
	    }
}
