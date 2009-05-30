package view;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.JSplitPane;

import compiler.structure.KeyWord;
import compiler.structure.symble;

import generate.gen_token;

public class RunFile extends JInternalFrame implements DocumentListener {
	
	public String filename;
	float var[]=new float[300];
	int out_pro;
	String var_temp[]=new String[200];
	private JPanel jContentPane = null;
	private JScrollPane inScrollPane = null;
	private JTextArea outTextArea = null;
	private JScrollPane outScrollPane = null;
	private JTextArea inTextArea = null;
	private JSplitPane splitPane = null;
	private Document indocument;
	private boolean edited = true;
	/**
	 * 初始化InScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getInScrollPane() {
		if (inScrollPane == null) {
			inScrollPane = new JScrollPane();
			inScrollPane.setViewportView(getInTextArea());

		}
		return inScrollPane;
	}
	
	/**
	 * 初始化 InTextArea
	 */
	private JTextArea getInTextArea() {
		if (inTextArea == null) {
			inTextArea = new JTextArea();
			inTextArea.setEditable(false);
			inTextArea.setForeground(Color.BLUE);
			inTextArea.setFont(new Font(null, Font.BOLD, 12));
		}
		return inTextArea;
	}
	
	/**
	 * 初始化OutScrollPane
	 */
	private JScrollPane getOutScrollPane() {
		if (outScrollPane == null) {
			outScrollPane = new JScrollPane();
			outScrollPane.setViewportView(getOutTextArea());
		}
		return outScrollPane;
	}
	
	/**
	 * 初始化 outTextField
	 
	 */
	private JTextArea getOutTextArea() {
		if (outTextArea == null) {
			outTextArea = new JTextArea();
			outTextArea.setEditable(false);
			indocument = outTextArea.getDocument();
			indocument.addDocumentListener(this);
			outTextArea.setForeground(Color.BLUE);
			outTextArea.setFont(new Font(null, Font.BOLD, 12));
		}
		return outTextArea;
	}
	
	/**
	 * 初始化splitPane
	 */
	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					getInScrollPane(), getOutScrollPane());
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(150);

		}
		return splitPane;
	}
	
	
	public RunFile(String name) {
		super();
		setVisible(true);
		filename=name;
		initialize();
		init();
		getEqu();
		run();
	}
	
	/**
	 * 设置编译窗口
	 */
	private void initialize() 
	{
		this.setContentPane(getJContentPane());
		this.setClosable(true);
		this.setTitle("运行窗口");
		this.setBounds(200, 150, 480, 360);
		this.setVisible(true);
	}
	
	/**
	 * 设置面板布局
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getSplitPane(), java.awt.BorderLayout.CENTER);
			}
		return jContentPane;
	}
	
	
	public void changedUpdate(DocumentEvent e) {
	}

	public void insertUpdate(DocumentEvent e) {
		if (!edited) {
			edited = true;
			setTitle(getTitle() + " *");
		}
	}

	public void removeUpdate(DocumentEvent e) {
		if (!edited) {
			edited = true;
			setTitle(getTitle() + " *");
		}
	}
	
	/*
	 * 获取测试文件
	 */
	public void getEqu(){
		try
	      {
	      File target_code=new File(filename+".txt");
	      inTextArea.read(new FileReader(target_code), null);
	      }
	      catch (IOException ioe)
	      {
		     	JOptionPane.showMessageDialog(this, "无法打开文件!",
	  				"Error", JOptionPane.ERROR_MESSAGE);
	      }
	    
	}
	
//	按指定行读取equ表
	public String readEqu(int pos)
	{
     int i=0;
   	  File file = new File("equ.txt");
	  BufferedReader reader1 = null;
	   try 
	   {
		   reader1 = new BufferedReader(new FileReader(file));
	  	   String tempString = null;
	 	   //一次读入一行，直到读入null为文件结束
	  	   while ((tempString = reader1.readLine())!= null)
	  	   {
	  	   i++;
	  	   if(i==pos)
	  		   return tempString;
	       }
 
	   } 
	   catch (IOException e) 
	   {
	    e.printStackTrace();
	   }
	   return "end";
   }
	
	
   public void run()
   {
	   int i;
	   int a,b,r;
       i=0;
	   String[] temp;
	   var[0]=0;
	   var[199]=1;
	   while(true)
	   {
		  i++;
		  temp=readEqu(i).split(" ");
		  if(temp[1].equals("0")&&temp[4].equals("1"))
			  break;
	   }
	   System.out.print("主程序入口"+i+" \n");
	   while(true)
	   {
		 i++;
		 if(!readEqu(i).equals("end"))
		 {
		    temp=readEqu(i).split(" ");
		    /*
		     * 读入的是四元式中的各项内容
		     */
		    a=Integer.parseInt(temp[2]);
		    b=Integer.parseInt(temp[3]);
		    r=Integer.parseInt(temp[4]);
		    if(a<0) a+=100;
		    if(b<0) b+=100;
		    if(r<0) r+=100;
		    if(a>1000&&a<5000) a-=900;
		    if(b>1000&&b<5000) b-=900;
		    if(r>1000&&r<5000) r-=900;
		    if(r==5001&&temp[1].equals("j")){i=out_pro;continue;}
		    if(r>5001&&r<10000) r-=4800;
		    if(a>5001&&a<10000) a-=4800;
		    if(a==50000) a=0;
		    else if(a==50001) a=199;
		    if(b==50000) b=0;
		    else if(b==50001) b=199;
		    if(temp[1].equals("0")&&temp[4].equals("0"))
		    	break;
		    if(temp[1].equals(":="))
		    {
		    	var[r]=var[a];
		    	System.out.print(r+"赋值为 "+Float.toString(var[a])+" \n");
		    }
		    if(temp[1].equals(":=x"))
		    {
		    	out_pro=a+3;
		    	System.out.print("\n转移语句"+out_pro+"\n");
		    	continue;
		    }
		    else if(temp[1].equals("+"))
		    	var[r]=var[a]+var[b];
		    else if(temp[1].equals("-"))
			    var[r]=var[a]-var[b];
		    else if(temp[1].equals("*"))
			    var[r]=var[a]*var[b];
		    else if(temp[1].equals("/"))
			    var[r]=var[a]/var[b];
		    else if(temp[1].equals("%"))
			    var[r]=var[a]%var[b];
		    else if(temp[1].equals("j"))
		    {	i=r-1;continue;}
		    else if(temp[1].equals("j<")&&(var[a]<var[b]))
		    {	i=r-1;continue;}
		    else if(temp[1].equals("j=")&&(var[a]==var[b]))
		    {	i=r-1;continue;}
		    else if(temp[1].equals("j>=")&&(var[a]>=var[b]))
		    {	i=r-1;continue;}
		    else if(temp[1].equals("j<=")&&(var[a]<=var[b]))
		    {	i=r-1;continue;}
		    else if(temp[1].equals("j<>")&&(var[a]!=var[b]))
		    {	i=r-1;continue;}
		    else if(temp[1].equals("j>")&&(var[a]>var[b]))
		    {	i=r-1;continue;}
		    else if(temp[1].equals("@x"))
		    {	System.out.print(r+"标志位\n"); outTextArea.append(Float.toString(var[r])+"\n");}
		    else if(temp[1].equals("@"))
		    {	
		    	outTextArea.append(var_temp[r]+"\n");
		    }
		 }
		 else break;
	   }
	   
   }
   
	
//	读入符号表初始化
	public void init()
	{
	    int i=0;
	    for(i=0;i<200;i++)
	    	var_temp[i]=new String();
	    File file = new File("symble.txt");
	    BufferedReader reader1 = null;
	    try 
	    {
		reader1 = new BufferedReader(new FileReader(file));
		String tempString = null;
		//一次读入一行，直到读入null为文件结束
		while ((tempString = reader1.readLine())!= null)
		{
		  String[] temp=tempString.split(" ");
		  if(Integer.parseInt(temp[1])==28||Integer.parseInt(temp[1])==29)
		  {
			  var[Integer.parseInt(temp[0])]=Float.parseFloat(temp[2]);
			  System.out.print("\n"+temp[0]+" "+var[Integer.parseInt(temp[0])]+Float.parseFloat(temp[2])+var[4]+" float\n");
		  }
		  if(Integer.parseInt(temp[1])==30)
		  {
			  var_temp[Integer.parseInt(temp[0])]=temp[2];
		  }
          i++;
		}
	    } 
	    catch (IOException e) 
	    {
	      e.printStackTrace();
	    }
		
	}


}
