package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JSplitPane;
import compiler.structure.KeyWord;
import compiler.structure.equ;
import compiler.structure.Stack;
import compiler.structure.symble;
import compiler.structure.token;
import compiler.structure.var;
import generate.Gen;
import generate.gen_token;
import generate.Oper;
import generate.regis;

public class AnalyseFrame extends JFrame implements DocumentListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 文件名
	String filename;

	// 词法分析声明变量
	char ch;
	/*
	 * 临时获取的字符变量
	 */
	int tempchar;
	/*
	 * 已经存入了符号表的符号个数
	 */
	int var_count;
	/*
	 * Increase error_count when use Error(int a)
	 */
	int error_count;
	int label_count;
	int code_count;
	int addr_count;
	int LineOfPro;
	int token_num;
	int check_pro = 0;
	int prev_num;
	int pro_var;
	boolean Is_Program = false;
	KeyWord[] key = new KeyWord[100];
	/*
	 * 临时
	 */
	token CurrentToken = new token();
	
	symble Currentsymble = new symble();

	symble[] SymbleList = new symble[100];
	symble[] SymbleList_Pro = new symble[100];
	token[] tokenList = new token[1024];

	InputStreamReader reader;

	// 语法分析声明变量
	int token_pos;
	int code;
	int address;
	int LineOfEqu;
	int total_var;
	/*
	 * 堆栈段的实例
	 */
	Stack[] stack_expr = new Stack[100];
	/*
	 * 四元式结构
	 */
	equ[] Equ = new equ[1024];
	/*
	 * 变量表
	 */
	var[] VarList = new var[100];
	String ID;
	/*
	 * pos是进栈的符号个数。
	 * 这符号，有可能是
	 * 也就是Push(code,address)执行的次数。
	 * 
	 */
	int pos;
	int now_addr;
	int len_count;
	boolean E_Contrl;
	boolean printf_num = false;
	int E_rtn;
	int temp_count;
	int let_count;
	int gen_pos;
	BufferedReader token_reader;

	// 目标代码生成声明变量
	gen_token TokenTable[] = new gen_token[1024];
	Gen GenStack[] = new Gen[1024];
	Gen CurrentGen = new Gen();
	Oper operation[] = new Oper[21];
	regis bx = new regis();
	regis cx = new regis();
	regis dx = new regis();
	int gen_count;
	int token_count;
	BufferedReader generate_reader;

	// the following variables are used in the GUI
	private JPanel jContentPane = null;
	private JToolBar toolBar = null;
	private Action openAction = null;
	private Action newAction = null;
	private Action analyseAction = null;
	private JScrollPane inScrollPane = null;
	private JTextArea outTextArea = null;
	private JScrollPane outScrollPane = null;
	private JTextArea inTextArea = null;
	private File sourFile = null;// source file you want to compile
	private JSplitPane splitPane = null;
	private JDesktopPane desktop = new javax.swing.JDesktopPane();
	private JPanel jContentPane1 = null;
	private JPanel jContentPane2 = null;
	private JSplitPane splitPane1 = null;
	private JScrollPane inScrollPane1 = null;
	private JTextArea inTextArea1 = null;
	private JScrollPane outScrollPane1 = null;
	private JTextArea outTextArea1 = null;

	private Document indocument;
	private boolean edited = false;// judge whether the file is edited or not

	/**
	 * 初始化工具条
	 */
	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.add(getOpenAction());
			/*
			 * 在工具条上找开按钮
			 */
			toolBar.add(getNewAction());
			toolBar.add(getAnalyseAction());

		}
		return toolBar;
	}

	/**
	 * 初始化打开按钮
	 */
	private Action getOpenAction() {
		if (openAction == null) {
			openAction = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					outTextArea.setText("");
					inTextArea1.setText("");
					outTextArea1.setText("");
					openFile();
				}
			};
			openAction.putValue(Action.NAME, "打开");

		}
		return openAction;
	}

	/**
	 * 初始化新建按钮
	 */
	private Action getNewAction() {
		if (newAction == null) {
			newAction = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					outTextArea.setText("");
					inTextArea1.setText("");
					outTextArea1.setText("");
					newFile();
				}
			};
			newAction.putValue(Action.NAME, "新建");

		}
		return newAction;
	}

	/**
	 * 初始化编译按钮
	 */
	private Action getAnalyseAction() {
		if (analyseAction == null) {
			analyseAction = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					outTextArea.setText("");
					inTextArea1.setText("");
					outTextArea1.setText("");
					clearObj();
					analyseFile();
				}
			};
			analyseAction.putValue(Action.NAME, "编译");

		}
		return analyseAction;
	}

	/**
	 * 初始化JScrollPane面板
	 */
	private JScrollPane getInScrollPane() {
		if (inScrollPane == null) {
			inScrollPane = new JScrollPane();
			inScrollPane.setViewportView(getInTextArea());

		}
		return inScrollPane;
	}

	private JScrollPane getInScrollPane1() {
		if (inScrollPane1 == null) {
			inScrollPane1 = new JScrollPane();
			inScrollPane1.setViewportView(getInTextArea1());

		}
		return inScrollPane1;
	}

	/**
	 * 初始化文本域
	 */
	private JTextArea getInTextArea() {
		if (inTextArea == null) {
			inTextArea = new JTextArea();
			indocument = inTextArea.getDocument();
			indocument.addDocumentListener(this);
			inTextArea.setEditable(false);
			inTextArea.setForeground(Color.magenta);
			inTextArea.setFont(new Font(null, Font.BOLD, 14));
		}
		return inTextArea;
	}

	private JTextArea getInTextArea1() {
		if (inTextArea1 == null) {
			inTextArea1 = new JTextArea();
			inTextArea1.setEditable(false);
			inTextArea1.setForeground(Color.BLUE);
			inTextArea1.setFont(new Font(null, Font.BOLD, 12));
		}
		return inTextArea1;
	}

	/**
	 * 初始化JScrollPane
	 */
	private JScrollPane getOutScrollPane() {
		if (outScrollPane == null) {
			outScrollPane = new JScrollPane();
			outScrollPane.setViewportView(getOutTextArea());
		}
		return outScrollPane;
	}

	private JScrollPane getOutScrollPane1() {
		if (outScrollPane1 == null) {
			outScrollPane1 = new JScrollPane();
			outScrollPane1.setViewportView(getOutTextArea1());
		}
		return outScrollPane1;
	}

	/**
	 * 初始化outTextField
	 */
	private JTextArea getOutTextArea() {
		if (outTextArea == null) {
			outTextArea = new JTextArea();
			outTextArea.setEditable(false);
			outTextArea.setForeground(Color.BLUE);
			outTextArea.setFont(new Font(null, Font.BOLD, 12));
		}
		return outTextArea;
	}

	private JTextArea getOutTextArea1() {
		if (outTextArea1 == null) {
			outTextArea1 = new JTextArea();
			outTextArea1.setEditable(false);
			outTextArea1.setForeground(Color.BLUE);
			outTextArea1.setFont(new Font(null, Font.BOLD, 12));
		}
		return outTextArea1;
	}

	/**
	 *初始化分隔栏
	 */
	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					getInScrollPane(), getOutScrollPane());
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(250);

		}
		return splitPane;
	}

	private JSplitPane getSplitPane1() {
		if (splitPane1 == null) {
			splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					getInScrollPane1(), getOutScrollPane1());
			splitPane1.setOneTouchExpandable(true);
			splitPane1.setDividerLocation(150);

		}
		return splitPane1;
	}

	public static void main(String[] args) {
		new AnalyseFrame();

	}

	/**
	 * This is the default constructor
	 */
	public AnalyseFrame() {
		super();
		initialize();
	}

	/**
	 * 初始化面板
	 */
	private void initialize() {
		// this.setSize(600, 400);
		this.setContentPane(getJContentPane());
		jContentPane1 = new JPanel();
		jContentPane1.setLayout(new BorderLayout());
		jContentPane2 = new JPanel();
		jContentPane2.setLayout(new BorderLayout());
		jContentPane1.add(getSplitPane(), java.awt.BorderLayout.CENTER);
		jContentPane2.add(getSplitPane1(), java.awt.BorderLayout.CENTER);
		jContentPane1.setPreferredSize(new Dimension(400, 90));
		desktop.add(jContentPane1, java.awt.BorderLayout.WEST);
		desktop.add(jContentPane2, java.awt.BorderLayout.CENTER);
		this.setTitle("CoCompiler-36060320-周正喜");
		this.setBounds(200, 150, 800, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * 设置布局
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel(new FlowLayout());
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(desktop, java.awt.BorderLayout.CENTER);
			desktop.setLayout(new BorderLayout());
			desktop.add(getToolBar(), java.awt.BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * 找开文件
	 * 
	 * @return void
	 */
	private void openFile() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			sourFile = fc.getSelectedFile();
			try {
				inTextArea.read(new FileReader(sourFile), null);
				this.setTitle("CoCompiler-36060320-周正喜 " + sourFile.getName());
				inTextArea.setEditable(true);
				edited = false;
				indocument = inTextArea.getDocument();
				indocument.addDocumentListener(this);
				outTextArea.setText("");
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(this, "无法打开文件!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 *新建文件
	 */
	private void newFile() {

		sourFile = null;
		inTextArea.setText("");
		this.setTitle("CoCompiler-36060320-周正喜");
		inTextArea.setEditable(true);
		outTextArea.setText("");
		indocument = inTextArea.getDocument();
		indocument.addDocumentListener(this);
		edited = false;

	}

	/**
	 * 编译文件
	 */

	private void analyseFile() {
		filename = sourFile.getName().substring(0,
				sourFile.getName().indexOf("."));
		System.out.print(filename);
		/*
		 * 词法
		 */
		word_analysis();
		/*
		 * 语法和语义
		 */
		parser();

		if (error_count == 0) {
			generate();
		} else
			outTextArea1.append("该程序存在语法或词法错误!\n无法生成目标代码");

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

	/* 词法分析函数 */
	public void word_analysis() {
		int i = 0;
		code_count = 0;
		LineOfPro = 0;
		var_count = 0;
		pro_var = 0;
		prev_num = 1002;
		addr_count = 1;
		label_count = 1;
		token_num = 1;
		/*
		 * To initial the example of class Symble,KeyWord,Token.
		 */
		for (i = 0; i < 100; i++) {
			/*
			 * 最终要将结果写入到keyword.txt，token.txt,symble.txt中，
			 * 估计，keyword.txt,symble.txt最多为 100项 token.txt为1024项。
			 * 它们都是通过SymbleList[i]，SymbleList_Pro[i]，key[i]获取，然后，存入到
			 * 这些文档中的。因此，选初始化
			 */
			SymbleList[i] = new symble();
			SymbleList_Pro[i] = new symble();
			key[i] = new KeyWord();
		}
		for (i = 0; i < 1024; i++)
			tokenList[i] = new token();
		Scanner();
	}

	// 主程序
	public void Scanner() {
		/*
		 * The total number of error
		 */
		error_count = 0;

		// 读入编码表,key[i]中存入的是单词的名称和编码
		/*
		 * It seems that it is useless. Not correctly. It has nothing to do with
		 * word analysis. But it has to other function.
		 */
		ScannerInit();

		System.out.println("词法分析程序开始\n生成token表如下:\n");// 词法分析
		outTextArea.append("词法分析程序开始\n生成token表如下:\n");
		/*
		 * source file to be compiled.
		 */
		File file = sourFile;

		try {
			// 一次读一个字符
			reader = new InputStreamReader(new FileInputStream(file));
			/*
			 * Read a char from the source file every time.
			 */
			tempchar = reader.read();
			while (tempchar != -1) {

				ch = (char) tempchar;
				/*
				 * 0-9
				 */
				if (ch > 47 && ch < 58)
					IsNumber();
				else {
					/*
					 * A-Z或a-z或_
					 */
					if (((ch > 64) && (ch < 91)) || ((ch > 96) && (ch < 123))

					|| ch == '_')
						/*
						 * 识别保留字和标识符
						 */
						IsAlpha();
					else {
						if (ch == '/')
							/*
							 * 处理除号和注释
							 */
							IsAnotation();
						else if (ch == '"')
							/*
							 * To test whether it is a char or not.
							 */
							IsChar();
						else
							/*
							 * To test other situation
							 */
							IsOther();
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (error_count > 0)
			outTextArea.append("\n词法分析完毕.\n共有" + error_count + "个错误!");
		else
			outTextArea.append("\n词法分析完毕.!");

		// 词法分析完毕 判断各个单词的种类
	}

	// 读入编码表
	/*
	 * Read the Keyword.txt to key[i]
	 */
	public void ScannerInit() {
		int i = 0;
		File file = new File("keyword.txt");
		BufferedReader reader1 = null;
		try {
			reader1 = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader1.readLine()) != null) {
				i++;
				String[] temp = tempString.split(" ");
				/*
				 * 存入单词的名字和编码
				 */
				key[i].setname(temp[0]);
				/*
				 * Change the String read into Integer
				 */
				key[i].setcode(Integer.parseInt(temp[1]));
				System.out.println(temp[0] + " " + temp[1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 数字处理
	public void IsNumber() {
		boolean flag = false;
		char ch1;
		CurrentToken.setname("");
		while ((ch > 47) && (ch < 58))
		/*
		 * 48和57对应的ASCII码分别是1和9
		 */
		{
			CurrentToken.setname(CurrentToken.getname() + ch);
			try {
				if (tempchar != -1) {
					tempchar = reader.read();
					ch = (char) tempchar;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (ch == '.') {
				flag = true;
				break;
			}
		}
		/*
		 * 整常数
		 */
		CurrentToken.setcode(28);
		CurrentToken.setaddr(addr_count++);
		CurrentToken.setlabel(label_count++);

		if (flag) {
			try {
				tempchar = reader.read();
				ch1 = (char) tempchar;
				if ((ch1 > 47) && (ch1 < 58))
					CurrentToken.setname(CurrentToken.getname() + ch);
				else
					Error(2);
				ch = ch1;
				while ((ch > 47) && (ch < 58)) {
					CurrentToken.setname(CurrentToken.getname() + ch);
					tempchar = reader.read();
					ch = (char) tempchar;
				}
				/*
				 * 实常数
				 */
				CurrentToken.setcode(29);
				if (ch == '.') {
					Error(2);
					tempchar = reader.read();
					ch = (char) tempchar;
					while ((ch > 47) && (ch < 58)) {
						tempchar = reader.read();
						ch = (char) tempchar;
					}
				}
				if (((ch > 64) && (ch < 90)) || ((ch > 96) && (ch < 123)))
				/*
				 * A--Z
				 */
				{
					Error(2);
					while (((ch > 64) && (ch < 90))
							|| ((ch > 96) && (ch < 123))) {
						tempchar = reader.read();
						ch = (char) tempchar;
						while ((ch > 47) && (ch < 58)) {
							tempchar = reader.read();
							ch = (char) tempchar;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		OutPut();
	}

	// 字母处理
	private void IsAlpha() {
		int i;
		boolean h = false;
		CurrentToken.setname("");
		while (((ch > 64) && (ch < 90)) || ((ch > 96) && (ch < 123))
				|| ch == '_') {
			/*
			 * A--Z a--z
			 */
			CurrentToken.setname(CurrentToken.getname() + ch);
			try {
				tempchar = reader.read();
				ch = (char) tempchar;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 判断是否为保留字
		for (i = 1; i < 60; i++) {

			if (CurrentToken.getname().equals(key[i].getname())) {
				h = true;
				break;
			}
		}
		if (h) {
			CurrentToken.setcode(key[i].getcode());
			CurrentToken.setaddr(-1);
		} else if (Is_Program) {
			/*
			 * 标志符
			 */
			CurrentToken.setcode(27);
			CurrentToken.setaddr(prev_num);
			prev_num += 2;
		} else {
			CurrentToken.setcode(27);
			CurrentToken.setaddr(addr_count++);
		}
		CurrentToken.setlabel(label_count++);
		OutPut();
	}

	// 注释处理
	private void IsAnotation() {
		CurrentToken.setname("");
		try {

			tempchar = reader.read();
			ch = (char) tempchar;

			if (ch == '*') {
				for (;;) {
					tempchar = reader.read();
					ch = (char) tempchar;
					if (tempchar == -1) {
						Error(3);
						break;
					}
					if (ch == '*') {
						tempchar = reader.read();
						ch = (char) tempchar;
						if (ch == '/') {
							tempchar = reader.read();
							break;
						}
					}
				}

			} else {
				/*
				 * 为“/”
				 */
				CurrentToken.setcode(39);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname("/");
				OutPut();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 字符串处理
	private void IsChar() {
		CurrentToken.setname("");
		try {
			/*
			 * 字符常数
			 */
			CurrentToken.setcode(30);
			for (;;) {
				tempchar = reader.read();
				ch = (char) tempchar;
				if (ch != '"')
					CurrentToken.setname(CurrentToken.getname() + ch);
				else
					break;
			}
			CurrentToken.setaddr(addr_count++);
			CurrentToken.setlabel(label_count++);
			OutPut();
			tempchar = reader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 其它情况的处理
	public void IsOther() {
		CurrentToken.setname("");
		try {
			char ch1;
			CurrentToken.setname("");
			switch (ch) {
			case '(':
				if (tokenList[token_num - 1].getcode() == 27
						&& (tokenList[token_num - 2].getcode() == 49 || tokenList[token_num - 2]
								.getcode() == 14)) {
					Is_Program = true;
				}
				CurrentToken.setcode(32);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname("(");
				OutPut();
				tempchar = reader.read();
				break;
			case ')':
				CurrentToken.setcode(33);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname(")");
				OutPut();
				tempchar = reader.read();
				break;
			case '{':
				if (Is_Program)
					check_pro++;
				CurrentToken.setcode(51);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname("{");
				OutPut();
				tempchar = reader.read();
				break;
			case '}':
				if (Is_Program) {
					check_pro--;
					if (check_pro == 0) {
						pro_var = 0;
						Is_Program = false;
					}
				}
				CurrentToken.setcode(52);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname("}");
				OutPut();
				tempchar = reader.read();
				break;
			case '*':
				CurrentToken.setcode(34);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname("*");
				OutPut();
				tempchar = reader.read();
				break;
			case '%':
				CurrentToken.setcode(56);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname("%");
				OutPut();
				tempchar = reader.read();
				break;
			case '+':
				CurrentToken.setcode(35);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname("+");
				OutPut();
				tempchar = reader.read();
				break;
			case ',':
				CurrentToken.setcode(37);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname(",");
				OutPut();
				tempchar = reader.read();
				break;
			case '-':
				CurrentToken.setcode(36);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname("-");
				OutPut();
				tempchar = reader.read();
				break;
			case '.':
				CurrentToken.setcode(38);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname(".");
				OutPut();
				tempchar = reader.read();
				break;
			case ':':
				ch1 = ch;
				tempchar = reader.read();
				ch = (char) tempchar;
				if (ch != '=') {
					CurrentToken.setcode(40);
					CurrentToken.setaddr(-1);
					CurrentToken.setlabel(label_count++);
					CurrentToken.setname(":");
					OutPut();
				} else {
					CurrentToken.setcode(41);
					CurrentToken.setaddr(-1);
					CurrentToken.setlabel(label_count++);
					CurrentToken.setname(":=");
					OutPut();
					tempchar = reader.read();
				}
				break;
			case ';':
				CurrentToken.setcode(42);
				CurrentToken.setaddr(-1);
				CurrentToken.setlabel(label_count++);
				CurrentToken.setname(";");
				OutPut();
				tempchar = reader.read();
				break;
			case '<':
				tempchar = reader.read();
				ch1 = (char) tempchar;
				if (ch1 == '=') {
					CurrentToken.setcode(44);
					CurrentToken.setaddr(-1);
					CurrentToken.setlabel(label_count++);
					CurrentToken.setname("<=");
					OutPut();
					tempchar = reader.read();
				} else {
					if (ch1 == '>') {
						CurrentToken.setcode(45);
						CurrentToken.setaddr(-1);
						CurrentToken.setlabel(label_count++);
						CurrentToken.setname("<>");
						OutPut();
						tempchar = reader.read();
					} else {
						CurrentToken.setcode(43);
						CurrentToken.setaddr(-1);
						CurrentToken.setlabel(label_count++);
						CurrentToken.setname("<");
						OutPut();
					}
				}
				break;
			case '=':
				ch1 = ch;
				tempchar = reader.read();
				ch = (char) tempchar;
				if (ch != '=') {
					CurrentToken.setcode(41);
					CurrentToken.setaddr(-1);
					CurrentToken.setlabel(label_count++);
					CurrentToken.setname("=");
					OutPut();
				} else {
					CurrentToken.setcode(46);
					CurrentToken.setaddr(-1);
					CurrentToken.setlabel(label_count++);
					CurrentToken.setname("==");
					OutPut();
					tempchar = reader.read();
				}
				break;
			case '!':
				ch1 = ch;
				tempchar = reader.read();
				ch = (char) tempchar;
				if (ch == '=') {
					CurrentToken.setcode(45);
					CurrentToken.setaddr(-1);
					CurrentToken.setlabel(label_count++);
					CurrentToken.setname("!=");
					OutPut();
					tempchar = reader.read();
				}
				break;
			case '>':
				tempchar = reader.read();
				ch1 = (char) tempchar;
				if (ch1 == '=') {
					CurrentToken.setcode(48);
					CurrentToken.setaddr(-1);
					CurrentToken.setlabel(label_count++);
					CurrentToken.setname("> =");
					OutPut();
					tempchar = reader.read();
				} else {
					CurrentToken.setcode(47);
					CurrentToken.setaddr(-1);
					CurrentToken.setlabel(label_count++);
					CurrentToken.setname(">");
					OutPut();
				}
				break;
			case 10:
				LineOfPro++;
				tempchar = reader.read();
				break;
			case 13:
				LineOfPro++;
				tempchar = reader.read();
				break;
			case ' ':
				tempchar = reader.read();
				break;
			default:
				Error(1);
				tempchar = reader.read();
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 输出模块
	public void OutPut() {
		boolean flag = false;
		if (!Is_Program) {
			/*
			 * 标识符 27 整常数 28 实常数 29 字符常数 30
			 */
			if ((CurrentToken.getcode() == 27)
					|| (CurrentToken.getcode() == 30)
					|| (CurrentToken.getcode() == 28)
					|| (CurrentToken.getcode() == 29)) {
				/*
				 * Token private int label=0; private String name=""; private
				 * int code=0; private int addr=0;
				 * 
				 * Symble int number; // 序号 int type; // 类型 String name; // 名称
				 */
				Currentsymble.setnumber(CurrentToken.getaddr());
				Currentsymble.settype(CurrentToken.getcode());
				Currentsymble.setname(CurrentToken.getname());
				flag = WordHave();
				if (((CurrentToken.getcode() == 27) && (flag == true))
						|| (CurrentToken.getcode() == 30)
						|| (CurrentToken.getcode() == 28)
						|| (CurrentToken.getcode() == 29))
					append("symble.txt", Currentsymble.getnumber(),
							Currentsymble.gettype(), Currentsymble.getname());
			}
		} else {
			if ((CurrentToken.getcode() == 27)
					|| (CurrentToken.getcode() == 30)
					|| (CurrentToken.getcode() == 28)
					|| (CurrentToken.getcode() == 29)) {
				Currentsymble.setnumber(CurrentToken.getaddr());
				Currentsymble.settype(CurrentToken.getcode());
				Currentsymble.setname(CurrentToken.getname());
				WordHave_pro();
				/*
				 * 数
				 */
				if ((CurrentToken.getcode() == 30)
						|| (CurrentToken.getcode() == 28)
						|| (CurrentToken.getcode() == 29))
					append("symble.txt", Currentsymble.getnumber(),
							Currentsymble.gettype(), Currentsymble.getname());
			}
		}
		append("token.txt", CurrentToken.getlabel(), CurrentToken.getcode(),
				CurrentToken.getaddr(), CurrentToken.getname());
		outTextArea.append(CurrentToken.getlabel() + "  "
				+ CurrentToken.getname() + "  " + CurrentToken.getcode() + "  "
				+ CurrentToken.getaddr() + "\n");
		tokenList[token_num++].setcode(CurrentToken.getcode());
		System.out.print("out put " + tokenList[token_num - 1].getcode() + " "
				+ token_num + "\n");

	}

	// 打印错误，a为错误类型的编码
	public void Error(int a) {
		error_count++;
		switch (a) {
		case 1:
			outTextArea.append("error" + error_count + ":非法字符出现在第" + LineOfPro
					+ "行!\n");
			break;
		case 2:
			outTextArea.append("error" + error_count + ":实常数错误出现于第" + LineOfPro
					+ "行!\n");
			break;
		case 3:
			outTextArea.append("error" + error_count + ":没有匹配的注释符号");
			break;
		case 4:
			inTextArea1.append("error" + "格式出错,第一行少main函数语法出错\n");
			break;
		case 5:
			inTextArea1.append("error" + "格式出错,第一行少程序名\n");
			break;
		case 6:
			inTextArea1.append("error" + "第" + LineOfPro + "条声明语句出错\n");
			break;
		case 7:
			inTextArea1.append("error" + "第" + LineOfPro + "赋值语句出错\n");
			break;
		case 8:
			inTextArea1.append("error" + "第" + LineOfPro + "条，缺少'}'不匹配\n");
			break;
		case 9:
			inTextArea1.append("error" + "第" + LineOfPro + "条语句出错\n");
			break;
		case 10:
			inTextArea1.append("error" + "第" + LineOfPro + "条表达式语句出错\n");
			break;
		case 11:
			inTextArea1.append("error" + "第" + LineOfPro + "条，if语句出错\n");
			break;
		case 12:
			inTextArea1.append("error" + "第" + LineOfPro + "条,while语句出错\n");
			break;
		case 13:
			inTextArea1.append("error" + "第" + LineOfPro + "条,for语句出错\n");
			break;
		case 14:
			inTextArea1.append("error" + "main函数缺少')'出错\n");
			break;
		case 15:
			inTextArea1.append("error" + "第" + LineOfPro + "条布尔表达式语句出错\n");
			break;
		case 16:
			inTextArea1.append("error" + "第" + LineOfPro + "条布尔表达式语句缺少')'出错\n");
			break;
		case 17:
			inTextArea1.append("error" + "main函数缺少'('出错\n");
			break;
		case 18:
			inTextArea1.append("error" + "main函数缺少'{'出错\n");
			break;
		case 19:
			inTextArea1.append("error" + "main函数缺少'}'出错\n");
			break;
		case 20:
			inTextArea1.append("error" + "缺少main函数出错\n");
			break;
		case 52:
			inTextArea1.append("error" + "程序结束缺乏.号\n");
			break;
		case 53:
			inTextArea1.append("error" + "未写任何语句\n");
			break;
		case 54:
			inTextArea1.append("error" + "第" + LineOfPro + "条语句缺乏': '号\n");
			break;
		default:
			break;
		}
	}

	// 判断标识符是否存在
	public boolean WordHave_pro() {
		int i;
		for (i = 0; i < pro_var; i++) {
			if (Currentsymble.getname().equals(SymbleList_Pro[i].getname())
					&& Currentsymble.gettype() == 27) {
				CurrentToken.setaddr(SymbleList_Pro[i].getnumber());
				return false;
			}
		}
		SymbleList_Pro[pro_var].setname(CurrentToken.getname());
		SymbleList_Pro[pro_var].settype(CurrentToken.getcode());
		SymbleList_Pro[pro_var].setnumber(CurrentToken.getaddr());
		pro_var++;
		return true;
	}

	// 判断单词是否存在

	public boolean WordHave() {
		int i;
		/*
		 * var_count is the variable which has been added into symble.txt
		 */
		for (i = 0; i < var_count; i++) {
			if (Currentsymble.getname().equals(SymbleList[i].getname())
					&& Currentsymble.gettype() == 27) {
				/*
				 * 将symble中的符号的序号numble加入到CurrentToken中。
				 */
				CurrentToken.setaddr(SymbleList[i].getnumber());
				return false;
			}
		}

		/*
		 * CurrentToken is used as a temporary variable.
		 */
		SymbleList[var_count].setname(CurrentToken.getname());
		SymbleList[var_count].settype(CurrentToken.getcode());
		SymbleList[var_count].setnumber(CurrentToken.getaddr());
		var_count++;
		return true;
	}

	// 写入文件
	public void append(String fileName, int number, int type, String name) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(number + " ");
			writer.write(type + " ");
			writer.write(name + " \r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void append(String fileName, int label, int code, int addr,
			String name) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(label + " ");
			writer.write(name + " ");
			writer.write(code + " ");
			writer.write(addr + " \r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 生成目标代码
	 */
	public void gen_target(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 清空symble,token文件
	public void clearObj() {
		try {
			FileWriter writer = new FileWriter("symble.txt");
			writer.write("");
			FileWriter writer1 = new FileWriter("token.txt");
			writer1.write("");
			FileWriter writer2 = new FileWriter("equ.txt");
			writer1.write("");
			writer.close();
			writer1.close();
			writer2.close();
		} catch (IOException ioe) {
		}
	}

	
	// 语法分析主程序,也包括语义分析程序
	/*
	 * 1。处理main之前的声明
	 * 2。处理main中的声明
	 * 3。处理main中的其它部分
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	public void parser() {
		/*
		 * 变量说明：
		 * 1.	code =Integer.parseInt(temp[2]);也就是单词的编码
		 * 2.	VarList[]	存放的声明的变量。
		 * 3.	SymbleList[]存入的是所有的变量和整常数，实常数
		 * 
		 * 
		 * 
		 */
		int gen_pos = 0;
		int Line;
		int i;
		pos = 0;
		let_count = 0;
		LineOfPro = 0;
		address = 0;
		LineOfEqu = 0;
		temp_count = 0;
		error_count = 0;
		/*
		 * 初始化
		 */
		for (i = 0; i < 100; i++) {
			stack_expr[i] = new Stack();
			VarList[i] = new var();
		}

		for (i = 0; i < 1023; i++)
			Equ[i] = new equ();
		
		InitStack();

		inTextArea1.append("语法分析程序开始\n生成四元式如下:\n");
		System.out.println("语法分析程序开始\n生成四元式如下:\n");

		try {
			File file = new File("token.txt");
			token_reader = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * declaration before main function,
		 *	main函数之前的变量声明
		 */
		Declear_BeforeMain();

		parser_pro();
		
		/*
		 * 测试VarList[]中的内容
		 */
		/*
		for(int k=0;k<var_count;k++){
			System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[k].getaddr());
			System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[k].getvalue());
			System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[k].getname());
		}
		*/
		/*
		 * **************************程序*********************************
		 * int num;
float number;
int function(int n){
return n;
}
void main()
{
int a=2;
int b=6;
int e;
int d;
e=a*b;
d=b/a;
num=999;
printf("a*b=",e);
printf("a/b=",d);
printf("num=",num);
printf("funtion1()=",function(14));
}


		 * **************************程序*********************************
		 * 
		 * 
		 * **************************结果*********************************
		 * Declear_BeforeMain()中VarList[var_count]的值：	1
Declear_BeforeMain()中VarList[var_count]的值：	27
Declear_BeforeMain()中VarList[var_count]的值：	num
Declear_BeforeMain()中VarList[var_count]的值：	2
Declear_BeforeMain()中VarList[var_count]的值：	27
Declear_BeforeMain()中VarList[var_count]的值：	number
Declear_BeforeMain()中VarList[var_count]的值：	3
Declear_BeforeMain()中VarList[var_count]的值：	27
Declear_BeforeMain()中VarList[var_count]的值：	function
Declear_BeforeMain()中VarList[var_count]的值：	1002
Declear_BeforeMain()中VarList[var_count]的值：	27
Declear_BeforeMain()中VarList[var_count]的值：	n
Declear_BeforeMain()中VarList[var_count]的值：	0
Declear_BeforeMain()中VarList[var_count]的值：	0
Declear_BeforeMain()中VarList[var_count]的值：	null
           **************************结果*********************************
           *
           *
           *
           *因此：VarList存入的就是声明变量（全局变量）和函数定义
		 */
		
		
		
		
		
		System.out.print("入口主程序" + code + " " + address + "\n");
		
		switch (code) {
		/*
		 * void 49
		 * 对声明的函数的修饰
		 */
		case 49:
		/*
		 * 0,0,0,1代表开始。
		 */
			EquPush(0, 0, 0, 1);
			/*
			 * void 49 main 50
			 */
			GetNext();
			token_pos++;
			
			/*
			 * main 50
			 */
			if (code == 50) {
				GetNext();
				token_pos++;
				/*
				 * ; 42
				 * main后面为什么有可能;
				 * 这不是说一定是在main后面，
				 * 反正，出现了一个；程序的行数就要加一。
				 */
				
				if (code == 42) {
					GetNext();
					token_pos++;
					LineOfPro++;
				}
				
				/*
				 *  ( 32
				 */
				if (code == 32)
				
				{
					GetNext();
					token_pos++;
					/*
					 * ) 33
					 */
					if (code == 33) {
						token_pos++;
						GetNext();
					} else
						/*
						 * case 14:
			inTextArea1.append("error" + "main函数缺少')'出错\n");
			break;
		case 17:
			inTextArea1.append("error" + "main函数缺少'('出错\n");
			break;
						 */
						Error(14);
				} else
					Error(17);
				
				/*
				 * { 51
				 */
				if (code == 51) {
					
					GetNext();
					token_pos++;
					/*
					 * 已经存入的数据的类型是,char,int,float 则要检查是否已经声明过。
					 * void main(){
					 * 
					 */
					if (code == 6 || code == 14 || code == 19) {
					//	System.out.print("声明变量");
						/*
						 * 函数里边变量的声明。
						 * 现在是main函数里边的变量。
						 */
						Declear_c0();
						
						for (i = 0; i < total_var; i++) {
							System.out.println("SymbleList[k].getname():	"+SymbleList[i].getname());
						}
							
						Is_Declear();
					}
					/*
					 * 语句分析
					 * Language Analysis
					 */
					L_Analize();
				} else
					/*
					 * case 18:
			inTextArea1.append("error" + "main函数缺少'{'出错\n");
			break;
		case 20:
			inTextArea1.append("error" + "缺少main函数出错\n");
			break;
			case 4:
			inTextArea1.append("error" + "格式出错,第一行少main函数语法出错\n");
			break;
					 */
					Error(18);
			} else
				Error(20);
			break;
		default:
			Error(4);
			break;

		}

		Line = LineOfEqu;
		LineOfEqu = 0;
		/*
		 * 如果还有四元式没有生成完，
		 * 就继续生成。
		 */
		while (gen_pos < Line) {
			gen(Equ[gen_pos].getop(), Equ[gen_pos].getop1(), Equ[gen_pos]
					.getop2(), Equ[gen_pos].getresult());
			gen_pos++;
		}
		/*
		 * 结束标志
		 */
		gen(0, 0, 0, 0);
		
		for(int k=0;k<VarList.length;k++){
			System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[k].getaddr());
			System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[k].getvalue());
			System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[k].getname());
		}
		

		/*
		 * 记录的总的错误个数。
		 */
		if (error_count > 0)
			inTextArea1.append("\n语法分析完毕.\n共有" + error_count + "个错误!n");
		else
			inTextArea1.append("\n语法分析完毕.");
	}

	/*
	

	 */
	public void parser_pro() {
		int flag = 0;
		int addr;
		int type;
		/*
		 * code是，int,float,void的编码 
		 * 下一个字符的编码是27，代表是标志符－－函数名
		 * 这些应该是函数前面对于函数的修饰符。
		 */
		
		/*
		 * 标志符和保留字如int,float,char是两类单词
		 */
		if ((code == 6 || code == 14 || code == 19 || code == 49)
				&& tokenList[token_pos + 1].getcode() == 27) {
			
			type = code;
			GetNext();
			token_pos++;
			/*
			 * 27 标志符
			 */
			if (code == 27) {
				addr = address;
				/*
				 * 四元式进栈，进栈
				 * 函数名
				 */
				EquPush(16, type, 0, addr);
				/*
				 * 还要将信息存入到变量表中。
				 */
				VarList[var_count].setaddr(address);
				VarList[var_count].setvalue(code);
				VarList[var_count].setname(ID);
				var_count++;
				GetNext();
				token_pos++;
				/*
				 * ; 42
				 */
				if (code == 42) {
					GetNext();
					token_pos++;
					LineOfPro++;
				}
				/*
				 * ( 32
				 */
				if (code == 32) {
					/*
					 * *******************************************************************
					 * 
					 * 
					 * 
					 * 
					 * 其它函数存在那？
					 * 
					 * 
					 * 
					 * 
					 * 
					 * *********************************************************************
					 * 
					 * 
					 */
					Declear_prev();
					/*
					 * ) 33
					 */
					
					if (code == 33) {
						token_pos++;
						GetNext();
					} else
						/*
						 * case 14:
							inTextArea1.append("error" + "main函数缺少')'出错\n");
							break;
		
						case 17:
							inTextArea1.append("error" + "main函数缺少'('出错\n");
							break;
						 */
						Error(14);
				} else
					Error(17);
				/*
				 * { 前面是标志符
				 * { 51
				 */
				if (code == 51) {
					GetNext();
					token_pos++;
					/*
					 * ,int,char,float，等
					 */
					if (code == 6 || code == 14 || code == 19) {
						/*
						 * 声明变量在函数内部？
						 */
						System.out.print("声明变量\n");
						Declear_c0();
					}
					L_Analize();
					System.out.print("返回语句" + code + " " + type + "\n");
					/*
					 * return 55
					 */
					if (code == 55
							&& (type == 3 || type == 6 || type == 14 || type == 19)) {
						flag = E_Analize();
						if (flag != 0)
							EquPush(1, flag, 0, addr);
						if (code == 42) {
							GetNext();
							token_pos++;
							LineOfPro++;
						}
					}
					EquPush(6, 0, 0, 5001);
					EquPush(17, type, 0, addr);
					if (code == 52) {
						GetNext();
						token_pos++;
						if ((code == 6 || code == 14 || code == 19 || code == 49)
								&& tokenList[token_pos + 1].getcode() == 27)
							parser_pro();
					}
				} else
					Error(18);
			} else
				Error(20);
		}
	}

	// 赋值语句分析
	public int S_Let(int a) {
		int addr, rtn;
		int flag;
		int prev = 5002;
		flag = 0;
		rtn = 0;
		InitStack();
		rtn = LineOfEqu + 1;
		
		System.out.print("\n" + "子函数调用! " + tokenList[token_pos + 1].getcode()
				+ " " + LineOfEqu + "\n");
		/*
		 * ( 32
		 * 不是函数
		 */
		if (code == 27 && tokenList[token_pos + 1].getcode() != 32) {
			addr = address;
			if (a != 0)
				let_count = addr;
			var_count++;
			GetNext();
			token_pos++;
			/*
			 * = 41
			 */
			if (code == 41) {
				flag = E_Analize();
				if (flag != 0) {
					EquPush(1, flag, 0, addr);
					System.out.println(flag + " " + addr + "\n" + rtn);
				}
			}
		} else if (code == 27 && tokenList[token_pos + 1].getcode() == 32) {
			addr = address;
			GetNext();
			token_pos++;
			while (code != 33) {
				if (code == 27 || code == 28)
					/*
					 * 标识符 27 整常数 28
					 */
					EquPush(1, address, 0, prev++);
				GetNext();
				token_pos++;
				if (code == 37) {
					GetNext();
					token_pos++;
				}
			}
			if (find_pro(addr) != -1) {
				/*
				 * case 19:
				writer.write(":=x ");
				 *case 6:
				writer.write("j ");
				 *case 18:
				writer.write("call ");

				 */
				EquPush(19, LineOfEqu, 0, 5001);
				EquPush(6, 0, 0, find_pro(addr) + 1);
				EquPush(18, 0, 0, addr);
				
			}
			GetNext();
			token_pos++;
		} else
			/*
			 * case 7:
			inTextArea1.append("error" + "第" + LineOfPro + "赋值语句出错\n");
			break;
			 */
			Error(7);
		return rtn;
	}

	// 复合语句分析
	public int S_Begin() {
		int rtn = 1;
		if (code == 42) {
			GetNext();
			token_pos++;
			LineOfPro++;
		}
		if (code == 2 || code == 51) {
			LineOfPro++;
			rtn = LineOfEqu + 1;
			GetNext();
			token_pos++;
			L_Analize();
			if (code == 52) {
				code = 42;
			} else if (code == 42 && tokenList[token_pos].getcode() == 52) {
				GetNext();
				token_pos++;
				code = 42;
				return 0;
			}

		}
		return rtn;
	}

	// 语句序列分析
	public int L_Analize() {
		int rtn = 0;
		switch (code) {
		/*
		 * 标识符 27
		 */
		case 27:
			S_Let(0);
			rtn = E_rtn;
			break;
		/*
		 * if 13
		 */
		case 13:
			rtn = S_If();
			break;
		/*
		 * while 26
		 */
		case 26:
			rtn = S_While();
			break;

		case 12:
			rtn = S_For();
			break;

		case 54:
			rtn = S_printf();
			break;

		case 53:
			rtn = S_scanf();
			break;
		default:
			break;
		}
		/*
		 * ; 42
		 */
		if (code == 42) {
			LineOfPro++;
			GetNext();
			token_pos++;
			switch (code) {
			case 27:
				L_Analize();
				return rtn;
			case 26:
				L_Analize();
				return rtn;
			case 12:
				L_Analize();
				return rtn;
			case 13:
				L_Analize();
				return rtn;
			case 54:
				L_Analize();
				break;
			case 53:
				L_Analize();
				break;
			default:
				return rtn;
			}
		}
		return rtn;
	}

	// 输出语句分析
	public int S_printf() {
		System.out.print("\n输出语句" + code);
		int rtn;
		int flag = 0;
		rtn = LineOfEqu + 1;
		GetNext();
		token_pos++;
		if (code == 32) {
			GetNext();
			token_pos++;
			if (code == 30) {
				EquPush(13, 0, 0, address);
				GetNext();
				token_pos++;
				if (code == 37) {
					flag = E_Analize();
					if (flag != 0) {
						System.out.print("算术表达式" + flag + "\n");
						EquPush(14, 0, 0, flag);
					}
				}
			} else if (code == 27 || code == 28 || code == 29) {
				printf_num = true;
				flag = E_Analize();
				if (flag != 0) {
					EquPush(14, 0, 0, flag);
				}
			}
			System.out.print("打印结束" + code + "\n");
			if (code != 33)
				Error(5);
			else {
				GetNext();
				token_pos++;
			}

		}
		return rtn;
	}

	public int S_scanf() {
		System.out.print("\n输入语句" + code);
		int rtn;
		rtn = LineOfEqu + 1;
		GetNext();
		token_pos++;
		if (code == 32) {
			GetNext();
			token_pos++;
			if (code == 27) {
				EquPush(15, 0, 0, address);
				GetNext();
				token_pos++;
				while (code == 37) {
					GetNext();
					token_pos++;
					EquPush(15, 0, 0, address);
					GetNext();
					token_pos++;
				}
			}
			if (code != 33)
				Error(5);
			else {
				GetNext();
				token_pos++;
			}

		}
		return rtn;
	}

	// if语句分析
	public int S_If() {
		int rtn = 0;
		int True_address;
		int False_address;
		int temp = 0;
		int as;
		int b_addr = 0, tt = 0;
		if (code == 13) {
			temp = LineOfEqu + 1;
			B_Init();
			b_addr = B_Analize();
			True_address = EquPush(11, b_addr, 50000, 1);
			False_address = EquPush(6, 0, 0, 0);
			System.out.print("ddddd-" + code);
			switch (code) {
			case 27:
				rtn = S_Let(0);
				break;
			case 13:
				rtn = S_If();
				break;
			case 26:
				rtn = S_While();
				break;
			case 12:
				rtn = S_For();
				break;
			case 2:
				rtn = S_Begin();
				break;
			case 51:
				rtn = S_Begin();
				break;
			case 54:
				rtn = S_printf();
				break;
			case 53:
				rtn = S_scanf();
				break;
			default:
				Error(9);
				break;
			}
			BackPatch(True_address, rtn);
			tt = LineOfEqu + 1;
			as = EquPush(6, 0, 0, tt);
			if (code == 42 && tokenList[token_pos + 1].getcode() == 9) {
				GetNext();
				token_pos++;
				GetNext();
				token_pos++;
				switch (code) {
				case 27:
					S_Let(0);
					break;
				case 13:
					rtn = S_If();
					break;
				case 26:
					rtn = S_While();
					break;
				case 12:
					rtn = S_For();
					break;
				case 2:
					rtn = S_Begin();
					break;
				case 51:
					rtn = S_Begin();
					break;
				case 54:
					rtn = S_printf();
					break;
				case 53:
					rtn = S_scanf();
					break;
				default:
					Error(9);
					break;
				}
			}

			BackPatch(False_address, tt + 1);
			BackPatch(as, LineOfEqu + 1);

		}
		return temp;
	}

	// While语句分析
	public int S_While() {
		int rtn = 0;
		int temp = 0;
		int b_addr = 0;
		int True_address;
		int False_address;
		if (code == 42) {
			GetNext();
			token_pos++;
			LineOfPro++;
		}
		if (code == 26) {
			temp = LineOfEqu + 1;
			B_Init();
			b_addr = B_Analize();
			True_address = EquPush(11, b_addr, 50000, 1);
			False_address = EquPush(6, 0, 0, 0);
			// GetNext();
			// token_pos++;
			switch (code) {
			case 27:
				rtn = S_Let(0);
				break;
			case 13:
				rtn = S_If();
				break;
			case 26:
				rtn = S_While();
				break;
			case 12:
				rtn = S_For();
				break;
			case 2:
				rtn = S_Begin();
				break;
			case 51:
				rtn = S_Begin();
				break;
			case 54:
				rtn = S_printf();
				break;
			case 53:
				rtn = S_scanf();
				break;
			default:
				Error(9);
				break;
			}
			EquPush(6, 0, 0, temp);
			// GetNext();
			// token_pos++;
			BackPatch(True_address, rtn);
			BackPatch(False_address, LineOfEqu + 1);
		}
		return temp;
	}

	// FOR语句分析
	public int S_For() {
		int rtn = 0, jmp_addr = 0;
		int addr2;
		if (code == 42) {
			GetNext();
			token_pos++;
			LineOfPro++;
		}
		if (code == 12) {
			GetNext();
			token_pos++;
			rtn = S_Let(1);
			if (code == 22) {
				GetNext();
				token_pos++;
				if ((code == 27) || (code == 28)) {
					addr2 = address;
					GetNext();
					token_pos++;
					if (code == 8) {
						GetNext();
						token_pos++;
						switch (code) {
						case 27:
							jmp_addr = S_Let(0);
							break;
						case 13:
							jmp_addr = rtn = S_If();
							break;
						case 26:
							jmp_addr = rtn = S_While();
							break;
						case 12:
							jmp_addr = rtn = S_For();
							break;
						case 2:
							jmp_addr = S_Begin();
							break;
						case 51:
							jmp_addr = S_Begin();
							break;
						case 54:
							rtn = S_printf();
							break;
						case 53:
							rtn = S_scanf();
							break;
						default:
							Error(9);
							break;
						}
						EquPush(2, let_count, 50001, let_count);
						EquPush(7, let_count, addr2, jmp_addr);
					} else {
						Error(13);
						return 0;
					}
				} else {
					Error(13);
					return 0;
				}
			} else {
				Error(13);
				return 0;
			}
		}
		return rtn;
	}

	// 布尔表达式分析
	public int B_Analize() {
		int rtn;
		rtn = B_OR();
		if (stack_expr[now_addr].getcode() != 0) {
			// Error(15);
			System.out.print(stack_expr[now_addr].getcode() + " ");
		}
		return rtn;
	}

	// 布尔表达式初始化
	public void B_Init() {
		boolean flag = true;
		InitStack();
		int i = 0;
		while (flag) {
			GetNext();
			token_pos++;
			switch (code) {
			case 27:
				Push(code, address);
				break;
			case 28:
				Push(code, address);
				break;
			case 1:
				Push(code, address);
				break;
			case 43:
				Push(code, address);
				break;
			case 44:
				Push(code, address);
				break;
			case 45:
				Push(code, address);
				break;
			case 46:
				Push(code, address);
				break;
			case 47:
				Push(code, address);
				break;
			case 48:
				Push(code, address);
				break;
			case 17:
				Push(code, address);
				break;
			case 23:
				Push(code, address);
				break;
			case 11:
				Push(code, address);
				break;
			case 32:
				Push(code, address);
				i++;
				break;
			case 33:
				Push(code, address);
				i--;
				if (i == 0) {
					GetNext();
					token_pos++;
					flag = false;
					break;
				}
			case 34:
				Push(code, address);
				break;
			case 35:
				Push(code, address);
				break;
			case 36:
				Push(code, address);
				break;
			default:
				flag = false;
				break;
			}
		}
	}

	/*
	 * or 语句
	 */
	public int B_OR() {
		int rtn = 0;
		int t1;
		rtn = L_AND();
		t1 = rtn;
		rtn = B1_OR(t1);
		return rtn;
	}

	public int B1_OR(int a) {
		int t1, rtn;
		rtn = a;
		if (stack_expr[now_addr].getcode() == 17) {
			now_addr++;
			int op = 2;
			int op2 = L_AND();
			t1 = NewTemp();
			EquPush(op, rtn, op2, t1);
			rtn = t1;
			if (stack_expr[now_addr].getcode() == 17)
				rtn = B1_OR(t1);
		}
		return rtn;
	}

	/*
	 * And
	 */
	public int L_AND() {
		int rtn = 0;
		int t1;
		rtn = M_NOT();
		t1 = rtn;
		rtn = L1_AND(t1);
		return rtn;
	}

	public int L1_AND(int a) {
		int rtn, t1;
		rtn = a;
		if (stack_expr[now_addr].getcode() == 1) {
			int op = 4;
			int op2 = M_NOT();
			t1 = NewTemp();
			EquPush(op, rtn, op2, t1);
			rtn = t1;
			if (stack_expr[now_addr].getcode() == 1)
				rtn = L1_AND(t1);
			now_addr++;
		}
		return rtn;
	}

	/*
	 * NOT
	 */
	public int M_NOT() {
		int rtn = 0;
		int temp = 0;
		int op2 = 0;
		if (stack_expr[now_addr].getcode() == 15) {
			int op = 3;
			now_addr++;
			op2 = K_END();
			temp = NewTemp();
			EquPush(op, 50001, op2, temp);
		} else {
			rtn = K_END();
			temp = rtn;
		}
		return temp;
	}

	public int K_END() {
		int rtn = 0;
		int temp, a = 0;
		temp = 0;
		switch (stack_expr[now_addr].getcode()) {
		case 27:
			temp = NewTemp();
			now_addr++;
			a = K_CMP();
			if (a != 0) {
				rtn = a;
				break;
			} else {
				EquPush(1, stack_expr[now_addr - 1].getaddr(), 0, temp);
			}
			rtn = temp;
			break;
		case 28:
			temp = NewTemp();
			now_addr++;
			a = K_CMP();
			if (a != 0) {
				rtn = a;
				break;
			} else {
				EquPush(1, stack_expr[now_addr - 1].getaddr(), 0, temp);
			}
			rtn = temp;
			break;
		case 32:
			now_addr++;
			rtn = B_Analize();
			if (stack_expr[now_addr].getcode() != 33) {
				Error(16);
				rtn = 50000;
				System.out.print("\n递归函数" + stack_expr[now_addr].getcode()
						+ "\n");
				now_addr++;
			} else
				now_addr++;
			break;
		case 11:
			rtn = 50000;
			break;
		case 23:
			rtn = 50001;
			break;
		default:
			break;
		}
		return rtn;
	}

	/*
	 * 比较运算符
	 */
	public int K_CMP() {
		int rtn = 0;
		int t1, t2;
		t1 = NewTemp();
		t2 = NewTemp();
		if ((stack_expr[now_addr].getcode() > 42)
				&& (stack_expr[now_addr].getcode() < 49)) {
			EquPush(3, stack_expr[now_addr - 1].getaddr(),
					stack_expr[now_addr + 1].getaddr(), t1);
			switch (stack_expr[now_addr].getcode()) {
			case 43:
				EquPush(7, t1, 50000, LineOfEqu + 4);
				break;
			case 44:
				EquPush(10, t1, 50000, LineOfEqu + 4);
				break;
			case 45:
				EquPush(11, t1, 50000, LineOfEqu + 4);
				break;
			case 46:
				EquPush(8, t1, 50000, LineOfEqu + 4);
				break;
			case 47:
				EquPush(12, t1, 50000, LineOfEqu + 4);
				break;
			case 48:
				EquPush(9, t1, 50000, LineOfEqu + 4);
				break;
			}
			EquPush(1, 50000, 0, t2);
			EquPush(6, 0, 0, LineOfEqu + 3);
			EquPush(1, 50001, 0, t2);
			rtn = t2;
			now_addr++;
			now_addr++;
		}
		return rtn;
	}

	// 算术表达式分析
	public int E_Analize() {
		
		int ans;
		ans = 0;
		now_addr = 0;
		E_Init();
		ans = E_AddSub();
		return ans;
	}

	public void E_Init() {
		boolean flag = true;
		E_Contrl = true;
		int i, j = 0;
		pos = 0;
		E_rtn = 0;
		for (i = 0; i < 100; i++) {
			stack_expr[i].setcode(0);
			stack_expr[i].setaddr(0);
		}
		
		if (printf_num)
			Push(code, address);

		while (flag) {
			GetNext();
			token_pos++;
			switch (code) {
			case 27:
				Push(code, address);
				break;
				/*
				 * 整常数
				 */
			case 28:
				Push(code, address);
				break;
				/*
				 * 实常数
				 */
			case 29:
				Push(code, address);
				break;
				/*
				 * ( 32
				 */
			case 32:
				Push(code, address);
				j++;
				break;
				/*
				 * ) 33
				 */
				
				
			case 33:
				/*
				 * 如果j=0,说明，）前面没有（
				 */
				if (j == 0) {
					flag = false;
					break;
				} else
					j--;
				Push(code, address);
				break;
				/*
				 *
* 34
+ 35
- 36
, 37
				 */
			case 34:
				Push(code, address);
				break;
			case 35:
				Push(code, address);
				break;
			case 36:
				Push(code, address);
				break;
			case 37:
				Push(code, address);
				break;
				/*
				 * / 39
				 */
			case 39:
				Push(code, address);
				break;
				/*
				 * % 56
				 */
			case 56:
				Push(code, address);
				break;
			default:
				flag = false;
				break;
			}
		}
		printf_num = false;
	}

	/*
	 * +和－
	 */
	public int E_AddSub() {
		int t1;
		int rtn = T_MulDiv();
		t1 = rtn;
		rtn = E1_AddSub(t1);
		return rtn;
	}

	public int E1_AddSub(int a) {
		int rtn, t1;
		rtn = a;
		/*
		 * + 35
- 36
		 */
		if (stack_expr[now_addr].getcode() == 35
				|| stack_expr[now_addr].getcode() == 36) {
			int op = stack_expr[now_addr++].getcode();
			int opr2 = T_MulDiv();
			t1 = NewTemp();
			if (op == 35) {
				if (E_Contrl) {
					E_Contrl = false;
					E_rtn = EquPush(2, rtn, opr2, t1);
					System.out.print(2 + " " + rtn + " " + opr2 + " " + t1
							+ "\n");
				} else {
					EquPush(2, rtn, opr2, t1);
				}
				rtn = t1;
			} else {
				if (E_Contrl) {
					E_Contrl = false;
					E_rtn = EquPush(3, rtn, opr2, t1);
				} else {
					EquPush(3, rtn, opr2, t1);
				}
				rtn = t1;
			}
			if (stack_expr[now_addr].getcode() == 35
					|| stack_expr[now_addr].getcode() == 36)
				rtn = E1_AddSub(t1);
		}
		return rtn;
	}

	
	/*
	 * 除法 求余
	 */
	public int T_MulDiv() {
		int t1;
		int rtn = F_Number();
		t1 = rtn;
		rtn = T1_MulDiv(t1);
		return rtn;
	}

	public int T1_MulDiv(int a) {
		int rtn, t1;
		rtn = a;
		/*
		 *
* 34
/ 39
% 56
		 */
		if (stack_expr[now_addr].getcode() == 34
				|| stack_expr[now_addr].getcode() == 39
				|| stack_expr[now_addr].getcode() == 56) {
			int op = stack_expr[now_addr++].getcode();
			int opr2 = F_Number();
			t1 = NewTemp();
			if (op == 34) {
				if (E_Contrl) {
					E_Contrl = false;
					/*
					 * case 4:
				writer.write("* ");

					 */
					E_rtn = EquPush(4, rtn, opr2, t1);
				} else {
					EquPush(4, rtn, opr2, t1);
				}
				rtn = t1;
			} else if (op == 39) {
				if (E_Contrl) {
					E_Contrl = false;
					E_rtn = EquPush(5, rtn, opr2, t1);
				} else {
					EquPush(5, rtn, opr2, t1);
				}
				rtn = t1;
			} else if (op == 56) {
				if (E_Contrl) {
					E_Contrl = false;
					E_rtn = EquPush(20, rtn, opr2, t1);
				} else {
					EquPush(20, rtn, opr2, t1);
				}
				rtn = t1;
			}
			if (stack_expr[now_addr].getcode() == 34
					|| stack_expr[now_addr].getcode() == 39)
				rtn = T1_MulDiv(t1);
		}
		return rtn;
	}

	public int F_Number() {
		int rtn = 0;
		int prev = 5003;
		int addr;
		/*
		 * ( 32
		 */
		if (stack_expr[now_addr].getcode() == 32) {
			now_addr++;
			rtn = E_AddSub();
		} else {
			switch (stack_expr[now_addr].getcode()) {
			/*
			 * 标志符 27
			 */
			case 27:
				addr = now_addr;
				/*
				 * ( 32
				 */
				if (stack_expr[now_addr + 1].getcode() == 32) {
					System.out.print("获得分程序入口! " + now_addr + " " + LineOfEqu
							+ "\n");
					now_addr++;
					/*
					 * ) 33
					 */
					
					/*
					 * while这里要做的就是将函数调用里边的参数存入
					 * stack_expr[]中
					 * 
					 * 同时，指明四元式的操作为：
					 * EquPush(1, stack_expr[now_addr].getaddr(), 0, prev);
					 */
					while (stack_expr[now_addr].getcode() != 33) {
						System.out.print("跟踪stack_expr " + now_addr + " \n");
						
						if (stack_expr[now_addr].getcode() == 27
								|| stack_expr[now_addr].getcode() == 28) {
							
							EquPush(1, stack_expr[now_addr].getaddr(), 0, prev);
							prev += 2;
						}
						now_addr++;
						/*
						 * , 37
						 */
						if (stack_expr[now_addr].getcode() == 37)
							now_addr++;
					}
					
					
					
					/*
					 * !=-1
					 * 意味着找到了。
					 */
					if (find_pro(stack_expr[addr].getaddr()) != -1) {
						/*
						 * case 19:
						writer.write(":=x ");
						 *case 6:
						writer.write("j ");
						 *case 18:
						writer.write("call ");
						 */
						EquPush(19, LineOfEqu, 0, 5001);
						EquPush(6, 0, 0,find_pro(stack_expr[addr].getaddr()) + 1);
						EquPush(18, 0, 0, stack_expr[addr].getaddr());
					}
					
				}
				rtn = stack_expr[addr].getaddr();
				break;
				/*
				 * getaddr()是为了填到函数定义时，
				 * 对应的参数列表中。
				 */
			case 28:
				rtn = stack_expr[now_addr].getaddr();
				break;
			case 29:
				rtn = stack_expr[now_addr].getaddr();
				break;
				/*
				 * 字符常数 30
				 */
			case 30:
				rtn = stack_expr[now_addr].getaddr();
				break;
			default:
				Error(10);
				break;
			}
			var_count++;
			now_addr++;
		}
		return rtn;
	}

	// 四元式进栈
	public int EquPush(int op, int a, int b, int r) {
		int i = LineOfEqu;
		Equ[LineOfEqu].setop(op);
		Equ[LineOfEqu].setop1(a);
		Equ[LineOfEqu].setop2(b);
		Equ[LineOfEqu].setresult(r);
		LineOfEqu++;
		return i;
	}

	// 查找分程序入口
	public int find_pro(int temp) {
		int i;
		/*
		 * 所有的分程序信息都放在了equ这个四元式的数组里边了。
		 * 
		 */
		for (i = 0; i < LineOfEqu; i++) {
			/*
			 * 操作编码为16？
			 * case 16:
				writer.write("BP ");
			 */
			if (Equ[i].getresult() == temp && Equ[i].getop() == 16)
				return i;
		}
		return -1;
	}

	// 取得下一个单词 from the text of token.txt
	public void GetNext() {
		try {
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			if ((tempString = token_reader.readLine()) != null) {
				String[] temp = tempString.split(" ");
				/*
				 * 如token中的一行：2 main 50 -1，则： ID=main code=50 address=-1
				 */
				code = Integer.parseInt(temp[2]);
				ID = temp[1];
				address = Integer.parseInt(temp[3]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 添加四元式到文件中。
	 */
	public void append_equ(String fileName, int op, int a, int b, int r) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(LineOfEqu + " ");
			inTextArea1.append(LineOfEqu + " ");
			switch (op) {
			case 0:
				inTextArea1.append("0 " + a + " " + b + " " + r + " \n");
				writer.write("0 ");
				break;
			case 1:
				inTextArea1.append(":= " + a + " " + b + " " + r + " \n");
				writer.write(":= ");
				break;
			case 2:
				inTextArea1.append("+ " + a + " " + b + " " + r + " \n");
				writer.write("+ ");
				break;
			case 3:
				inTextArea1.append("- " + a + " " + b + " " + r + " \n");
				writer.write("- ");
				break;
			case 4:
				inTextArea1.append("* " + a + " " + b + " " + r + " \n");
				writer.write("* ");
				break;
			case 5:
				inTextArea1.append("/ " + a + " " + b + " " + r + " \n");
				writer.write("/ ");
				break;
			case 6:
				inTextArea1.append("j " + a + " " + b + " " + r + " \n");
				writer.write("j ");
				break;
			case 7:
				inTextArea1.append("j< " + a + " " + b + " " + r + " \n");
				writer.write("j< ");
				break;
			case 8:
				inTextArea1.append("j= " + a + " " + b + " " + r + " \n");
				writer.write("j= ");
				break;
			case 9:
				inTextArea1.append("j> " + a + " " + b + " " + r + " \n");
				writer.write("j>= ");
				break;
			case 10:
				inTextArea1.append("j<= " + a + " " + b + " " + r + " \n");
				writer.write("j<= ");
				break;
			case 11:
				inTextArea1.append("j<> " + a + " " + b + " " + r + " \n");
				writer.write("j<> ");
				break;
			case 12:
				inTextArea1.append("j> " + a + " " + b + " " + r + " \n");
				writer.write("j> ");
				break;
			case 13:
				inTextArea1.append("@ " + a + " " + b + " " + r + " \n");
				writer.write("@ ");
				break;
			case 14:
				inTextArea1.append("@x " + a + " " + b + " " + r + " \n");
				writer.write("@x ");
				break;
			case 15:
				inTextArea1.append("&x " + a + " " + b + " " + r + " \n");
				writer.write("&x ");
				break;
			case 16:
				inTextArea1.append("BP " + a + " " + b + " " + r + " \n");
				writer.write("BP ");
				break;
			case 17:
				inTextArea1.append("EP " + a + " " + b + " " + r + " \n");
				writer.write("EP ");
				break;
			case 18:
				inTextArea1.append("call " + a + " " + b + " " + r + " \n");
				writer.write("call ");
				break;
			case 19:
				inTextArea1.append(":=x " + a + " " + b + " " + r + " \n");
				writer.write(":=x ");
				break;
			case 20:
				inTextArea1.append("% " + a + " " + b + " " + r + " \n");
				writer.write("% ");
				break;
			default:
				break;
			}
			writer.write(a + " ");
			writer.write(b + " ");
			writer.write(r + " \r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 生成四元式
	public int gen(int op, int a, int b, int r) {
		LineOfEqu++;
		if (op >= 0 && op < 21)
			append_equ("equ.txt", op, a, b, r);
		return LineOfEqu;
	}

	/*
	 * 用于对函数内的变量
	 * 也就是局部变量的处理
	 * 
	 * 
	 * 
	 * 不对！
	 * 是对main函数内部的处理
	 */
	public void Declear_c0() {
		int i = 0;
		InitStack();
		i = 0;
		int addr;

		/*
		 * char,int,float,const
		 */
		while (code == 6 || code == 14 || code == 19 || code == 7) {
			/*
			 * 初始化堆栈后，将变量压栈到堆栈中。
			 * 也就是说，获取的变量或常量（main函数里边的）及它人的类型
			 * 会存入在stack中。
			 */
			Push(code, address);
			GetNext();
			token_pos++;
			/*
			 * ; 42
			 */
			while (code != 42) {
				Push(code, address);
				GetNext();
				token_pos++;
			}
			GetNext();
			token_pos++;
		}
		i = 0;
		// total_var=var_count;
		// var_count=0;
		while (pos > 0) {
			/***********************************************************
			 * 
			 * 
			 * 
			 * 
			 * 
			 * stack_expr[]里边存的是什么？
			 * 
			 * 
			 * 
			 * 
			 * 
			 ************************************************************/
			if (stack_expr[i].getcode() == 27) {
				/*
				 * Add the item from Stack to Variable Table.
				 * 
				 * 仅仅存变量？不存变量的类型。
				 * 
				 * 
				 * 
				 * 
				 */
				VarList[var_count].setaddr(stack_expr[i].getaddr());
				VarList[var_count].setvalue(stack_expr[i].getcode());
				VarList[var_count].setname(stack_expr[i].getname());
				/*
				 * := 41 不可能出现这种情况
				 */
				/*
				 * 只有四元式中才有可存在:= 41
				 *这是对前面的声明的变量赋值。
				 */
				if (stack_expr[i + 1].getcode() == 41) {
					addr = stack_expr[i].getaddr();
					i++;
					pos--;
					
					if (stack_expr[i + 1].getcode() == 28) {
						System.out.print("\n声明变量跟踪"+ stack_expr[i + 1].getaddr() + " \n");
						EquPush(1, stack_expr[i + 1].getaddr(), 0, addr);
						i++;
						pos--;
					}

				}
				
				
				i++;
				var_count++;
				pos--;
			} else {
				/*
				 * , 37
				 */
				if (stack_expr[i].getcode() == 37) {
					i++;
					pos--;
				} else if (stack_expr[i].getcode() == 3
						|| stack_expr[i].getcode() == 6
						|| stack_expr[i].getcode() == 14
						|| stack_expr[i].getcode() == 19
						|| stack_expr[i].getcode() == 7) {
					i++;
					pos--;
				} else {
					System.out.println("声明3错误" + pos);
					Error(6);
					return;
				}
			}
		}

	}

	// main主函数前的变量声明
	/*
	 * main函数前面的申明有两种
	 * 1。变量（全局变量）
	 * 2。函数
	 * 
	 * 
	 * 
	 * 
	 * ＜声明头部＞   ::=  int＜标识符＞ |float ＜标识符＞|char＜标识符＞
	 * 那么，
	 * 1。声明只可能是变量的声明
	 * 2。声明的变量的类型只可能有三种－int,float,char
	 * 3。声明只能是这样的：int num;而不能有值，如int num=9;
	 */
	
	public void Declear_BeforeMain() {
		int i;
//		int addr;
		token_pos = 0;
		InitStack();
		/*
		 * 从GetNext()中得到三个量 ID = temp[1];－－－－
		 * 单词 code =Integer.parseInt(temp[2]);－－
		 * 单词编码 address =Integer.parseInt(temp[3]);－－
		 * 单词值 每调用一次读入一行
		 */
		GetNext();
		/*
		 * 词法分析后的第N条信息？
		 * 这个变量的作用是用来标志在记法分析程序生成的token.txt中的行数。
		 * 用来
		 */
		token_pos++;
		/*
		 * bool 3 char 6 const 7 int 14 float 19
		 * code是通过GetNext获得的单词的编码。
		 */
		while ((code == 6 || code == 14 || code == 19 || code == 7)
				/*
				 * token_pos+1=3;如果程序是 void main () 那么， tokenList[token_pos +
				 * 2]即为（

				 * !=32即不是（，说明不是函数
				 * 说明是变量声明。
				 * 
				 * tokenList是：token tokenList[]
				 */
				&& tokenList[token_pos + 2].getcode() != 32) {
			
			/*
			 * 将变量声明入栈
			 * 从下面的语句可以看出：
			 * 声明的变量存放在 tokenList[]中了。
			 */
			Push(code, address);
			/*
			 * 从词法分析的结果中取得单词。
			 */
			GetNext();
			
			token_pos++;
			/*
			 * ; 42
			 * ，也存进去了。
			 * 
			 * 
			 * 这是不对的！！！！！！！！！！！！！！！！！！
			 * 变量声明只有可能为：
			 * int var1;
			 * char ch;
			 * float number;
			 * void main(){.....}
			 * 
			 * 因此，除了; 就只有
			 * 声明的变量和修饰变量的类型。
			 */
			
			while (code != 42) {
				Push(code, address);
				GetNext();
				token_pos++;
			}
			GetNext();
			token_pos++;

		}
		i = 0;
		total_var = var_count;
		var_count = 0;

		/*
		 * pos是进栈的符号个数。
		 * 这符号，有可能是
		 * 也就是Push(code,address)执行的次数。
		 * 
		 */
		while (pos > 0) {
			/*
			 * 标识符 27
			 */
			if (stack_expr[i].getcode() == 27) {
				/*
				 * 获取堆栈中的变量存入到变量表
				 * 那么，VarList[]存入的应该是
				 * 与stack相对的变量表。
				 * 
				 * 仅仅将变量的名字放入到变量表中
				 * 去掉了,
				 * 
				 * ****************************************************************
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 现在，VarList[]里存的是声明的变量及其类型
				 * 
				 * 
				 * VarList[]是存放的声明的变量的数组。
				 * 
				 * 
				 * 
				 * 
				 * ***************************************************************
				 */
				VarList[var_count].setaddr(stack_expr[i].getaddr());
				VarList[var_count].setvalue(stack_expr[i].getcode());
				VarList[var_count].setname(stack_expr[i].getname());
				
				
			//	System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[var_count].getaddr());
			//	System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[var_count].getvalue());
			//	System.out.println("Declear_BeforeMain()中VarList[var_count]的值：	"+VarList[var_count].getname());
				
				
				/*
				 * := 41
				 */
				
				
				/*
				 * 这个在co方法中是没有的！！！
				 */
				/*if (stack_expr[i + 1].getcode() == 41) {
					addr = stack_expr[i].getaddr();
					i++;
					pos--;
					
					if (stack_expr[i + 1].getcode() == 28) {
						System.out.print("\n声明变量跟踪"
								+ stack_expr[i + 1].getaddr() + " \n");
						EquPush(1, stack_expr[i + 1].getaddr(), 0, addr);
						i++;
						pos--;
					}

				}*/
				i++;
				var_count++;
				pos--;
			} else if (stack_expr[i].getcode() != 27){
				/*
				 * , 37
				 */
				
				
				/*
				if (stack_expr[i].getcode() == 37) {
					i++;
					pos--;
					
				} 
				
				else */
					
					/*
					 * 不是标志符
					 * 是保留字
					 */
					
					if (stack_expr[i].getcode() == 3
						|| stack_expr[i].getcode() == 6
						|| stack_expr[i].getcode() == 14
						|| stack_expr[i].getcode() == 19
						|| stack_expr[i].getcode() == 7) {
					i++;
					pos--;
				
				} 
					else {
					System.out.println("声明3错误" + pos);
					Error(6);
					return;
				}
			}
		}
	}

	// main主函数前的变量声明
	/*( 32
	 * if (code == 32) {
					Declear_prev();
	 */
	public void Declear_prev() {
		int i;
		int prev = 5003;
		InitStack();
		GetNext();
		token_pos++;
		/*
		 * 
		 * ) 33
		 */
		
		/*
		 * 应该是将函数的    参数表     存入到stack_exper中吧。
		 */
		while (code != 33) {
			Push(code, address);
			GetNext();
			/*
			 * token_pos相当于一个指针
			 */
			token_pos++;
		}
		i = 0;
		
		/*
		 * 也就是说入栈的声明的个数>0
		 * 
		 */
		while (pos > 0) {
			if (stack_expr[i].getcode() == 27) {
				/*
				 * 四元式进栈
				 */
				EquPush(1, prev, 0, stack_expr[i].getaddr());
				VarList[var_count].setaddr(stack_expr[i].getaddr());
				VarList[var_count].setvalue(stack_expr[i].getcode());
				VarList[var_count].setname(stack_expr[i].getname());
				prev += 2;
				i++;
				var_count++;
				pos--;
			} else {
				if (stack_expr[i].getcode() == 37) {
					i++;
					pos--;
				} else if (stack_expr[i].getcode() == 3
						|| stack_expr[i].getcode() == 6
						|| stack_expr[i].getcode() == 14
						|| stack_expr[i].getcode() == 19) {
					i++;
					pos--;
				} else {
					System.out.println("声明3错误" + pos);
					Error(6);
					return;
				}
			}
		}

	}

	// 判断所有变量是否声明－－属于语义分析
	public boolean Is_Declear() {
		boolean flag = true;
		int i;
		for (i = 1; i < total_var; i++) {
				
			/*
			 * SymbleList[]的内容是：
			 * 标志符和整数
			 * SymbleList[k].getname():	const_number
SymbleList[k].getname():	num
SymbleList[k].getname():	number
SymbleList[k].getname():	function
SymbleList[k].getname():	a
SymbleList[k].getname():	2
SymbleList[k].getname():	b
SymbleList[k].getname():	6
SymbleList[k].getname():	e
SymbleList[k].getname():	d
SymbleList[k].getname():	999
SymbleList[k].getname():	a*b=
SymbleList[k].getname():	a/b=
SymbleList[k].getname():	num=
SymbleList[k].getname():	funtion1()=
SymbleList[k].getname():	14

			 */
			
			
			if (Is_exist(SymbleList[i])) {
				System.out.print(SymbleList[i].getname() + " ");
				continue;
			} else {
				error_count++;
				inTextArea1.append("error" + "变量" + SymbleList[i].getname()
						+ "未声明\n");
				flag = false;
			}
		}
		return flag;
	}

	// 判断单个变量是否存在于声明变量数组中
	public boolean Is_exist(symble temp) {
		int i;
		/*
		 * 如果temp.gettype()!=27
		 * 那么，temp.gettype()就是整常量或实常量
		 */
		if (temp.gettype() != 27)
			return true;
		for (i = 0; i < var_count; i++) {
			if (temp.getname().equals(VarList[i].getname())) {
				return true;
			}
		}
		return false;
	}

	// 初始化堆栈
	public void InitStack() {
		int i;
		pos = 0;
		now_addr = 0;
		for (i = 0; i < 100; i++) {
			stack_expr[i].setaddr(0);
			stack_expr[i].setcode(0);
			stack_expr[i].setname("");
		}
	}

	// 进栈
	public void Push(int code, int addr) {
		stack_expr[pos].setaddr(addr);
		stack_expr[pos].setcode(code);
		stack_expr[pos].setname(ID);
		pos++;
	}

	// 临时变量还剩下的个数。
	public int NewTemp() {
		int a;
		temp_count--;
		a = temp_count;
		return a;
	}

	// 出口回填函数
	public void BackPatch(int addr, int addr2) {
		Equ[addr].setresult(addr2);
		return;
		/*
		 * addr相当于lineOfEqu.
		 */
	}

	// 目标代码生成主程序
	public void generate() {
		clearAsm();
		int i = 0, j = 0;
		boolean flag;
		boolean printf = false;
		boolean scanf = false;
		int op = 0;

		for (i = 0; i < 1024; i++)
			TokenTable[i] = new gen_token();

		for (i = 0; i < 1024; i++)
			GenStack[i] = new Gen();

		for (i = 0; i < 21; i++)
			operation[i] = new Oper();

		try {
			File file = new File("equ.txt");
			generate_reader = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

		InitTarget();
		for (i = 0; i < gen_count; i++)
			for (j = 0; j < 21; j++) {
				flag = operation[j].getopp().equals(GenStack[i].getop());
				if (flag) {
					GenStack[i].setcode(operation[j].getcode());
					op = operation[j].getcode();
					SortDGA(i, op);
					break;
				}
			}
		System.out.print("四元式" + gen_count);
		gen_target(filename + ".asm", "Stack segment para Stack\r\n");
		gen_target(filename + ".asm", "dw 100h dup(?);\r\n");
		gen_target(filename + ".asm", "Stack ends\r\n\r\n");
		gen_target(filename + ".asm", "data segment\r\n");
		for (i = 0; i < token_count; i++) {
			if (TokenTable[i].getcode() == 27)
				gen_target(filename + ".asm", TokenTable[i].getname()
						+ " dw ?;\r\n");
			else if (TokenTable[i].getcode() == 30)
				gen_target(filename + ".asm", "str" + TokenTable[i].getlabel()
						+ " db '" + TokenTable[i].getname() + "$';\r\n");
		}
		gen_target(filename + ".asm", "prev_len EQU 100;\r\n");
		gen_target(filename + ".asm", "temp_len EQU 10;\r\n");
		gen_target(filename + ".asm", "prev dw ?;\r\n");
		gen_target(filename + ".asm", "dw prev_len dup(?);\r\n");
		gen_target(filename + ".asm", "temp_prev dw ?;\r\n");
		gen_target(filename + ".asm", "dw temp_len dup(?);\r\n");
		gen_target(filename + ".asm",
				"display db 'this is an example of simple compiler by zhouzhengxi 36060320.$'\r\n");
		gen_target(filename + ".asm", "data ends\r\n\r\n");
		gen_target(filename + ".asm", "code segment\r\n");
		gen_target(filename + ".asm",
				"      assume cs:code,ds:data,ss:Stack\r\n\r\n");
		for (i = 0; i < gen_count; i++) {
			if (GenStack[i].getop().endsWith("@x"))
				printf = true;
			if (GenStack[i].getop().endsWith("&x"))
				scanf = true;
			if (GenStack[i].getop().endsWith("0")
					&& GenStack[i].getresult() == 0) {
				if (GenStack[i].getout_port() == 1)
					gen_target(filename + ".asm", "lab"
							+ GenStack[i].getlabel() + ":\r\n");
				break;
			}
			if ((GenStack[i].getout_port()) == 1
					&& (GenStack[i].getlabel() < gen_count + 1))
				gen_target(filename + ".asm", "lab" + GenStack[i].getlabel()
						+ ":\r\n");
			Produre(GenStack[i].getcode(), GenStack[i].getaddr1(), GenStack[i]
					.getaddr2(), GenStack[i].getresult());
		}
		gen_count++;
		gen_target(filename + ".asm", "lab" + gen_count + ":\r\n");
		gen_target(filename + ".asm", "      mov ah,9h;\r\n");
		gen_target(filename + ".asm", "      mov dx,offset display;\r\n");
		gen_target(filename + ".asm", "      int 21h;\r\n");
		Produre(21, 0, 0, 0);
		gen_target(filename + ".asm", "main endp\r\n\r\n");
		if (printf)
			gen_printfnum();
		if (scanf)
			gen_scanfnum();
		gen_target(filename + ".asm", "code ends\r\n");
		gen_target(filename + ".asm", "end start\r\n");

		if (error_count == 0) {
			outTextArea1.append("目标代码生成开始:\n");
			try {
				File target_code = new File(filename + ".asm");
				outTextArea1.read(new FileReader(target_code), null);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(this, "无法打开文件!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			outTextArea1.append("目标代码生成结束!\n");
		}

	}

	// 产生将数字转化为ASCII码并显示输出代码
	public void gen_printfnum() {
		gen_target(filename + ".asm", "PrintfNum  proc\r\n");
		gen_target(filename + ".asm", "LP1:  xor  dx,dx;\r\n");
		gen_target(filename + ".asm", "      div  bx;\r\n");
		gen_target(filename + ".asm", "      or   dl,30h;\r\n");
		gen_target(filename + ".asm", "      push dx;\r\n");
		gen_target(filename + ".asm", "      loop LP1;\r\n");
		gen_target(filename + ".asm", "      mov  cx,5;\r\n");
		gen_target(filename + ".asm", "LP2:  pop  dx;\r\n");
		gen_target(filename + ".asm", "      mov  ah,2;\r\n");
		gen_target(filename + ".asm", "      int  21h;\r\n");
		gen_target(filename + ".asm", "      loop LP2;\r\n");
		gen_target(filename + ".asm", "      ret;\r\n");
		gen_target(filename + ".asm", "PrintfNum endp;输出算术表达式\r\n\r\n");
	}

	// 产生读入语句
	public void gen_scanfnum() {
		gen_target(filename + ".asm", "ScanfNum  proc\r\n");
		gen_target(filename + ".asm", "      xor bx,bx;\r\n");
		gen_target(filename + ".asm", "      mov ah,1;\r\n");
		gen_target(filename + ".asm", "      int 21h;\r\n");
		gen_target(filename + ".asm", "      sub al,30h;\r\n");
		gen_target(filename + ".asm", "      mov cx,10;\r\n");
		gen_target(filename + ".asm", "      mul cx;\r\n");
		gen_target(filename + ".asm", "      mov bl,al;\r\n");
		gen_target(filename + ".asm", "      mov ah,1;\r\n");
		gen_target(filename + ".asm", "      int 21h;\r\n");
		gen_target(filename + ".asm", "      sub al,30h;\r\n");
		gen_target(filename + ".asm", "      add bl,al;\r\n");
		gen_target(filename + ".asm", "      ret;\r\n");
		gen_target(filename + ".asm", "ScanfNum endp;读入语句\r\n\r\n");
	}

	/*
	 * 初始化目标代码
	 */
	public void InitTarget() {
		boolean flag;
		int i = 0;
		InitOp();
		bx.setnumber(1);
		bx.setallocate(0);
		bx.setname("bx");
		cx.setnumber(2);
		cx.setallocate(0);
		cx.setname("cx");
		dx.setnumber(3);
		dx.setallocate(0);
		dx.setname("dx");
		gen_count = 0;
		flag = GetNextGen();
		while (flag) {
			GenStack[i].setlabel(CurrentGen.getlabel());
			GenStack[i].setaddr1(CurrentGen.getaddr1());
			GenStack[i].setaddr2(CurrentGen.getaddr2());
			GenStack[i].setresult(CurrentGen.getresult());
			GenStack[i].setop(CurrentGen.getop());
			i++;
			gen_count++;
			flag = GetNextGen();
		}
		read_symble();
	}

	// 读入符号表
	public void read_symble() {
		int i = 0;
		File file = new File("symble.txt");
		BufferedReader reader1 = null;
		try {
			reader1 = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader1.readLine()) != null) {
				String[] temp = tempString.split(" ");
				TokenTable[i].setname(temp[2]);
				TokenTable[i].setlabel(Integer.parseInt(temp[0]));
				TokenTable[i].setcode(Integer.parseInt(temp[1]));
				System.out.println(temp[0] + " " + temp[1] + " " + temp[2]);
				token_count++;
				i++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 取得下一个四元式
	public boolean GetNextGen() {
		try {
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			if ((tempString = generate_reader.readLine()) != null) {
				String[] temp = tempString.split(" ");
				CurrentGen.setlabel(Integer.parseInt(temp[0]));
				CurrentGen.setop(temp[1]);
				CurrentGen.setaddr1(Integer.parseInt(temp[2]));
				CurrentGen.setaddr2(Integer.parseInt(temp[3]));
				CurrentGen.setresult(Integer.parseInt(temp[4]));
				gen_count++;
				return true;
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 初始化操作表
	public void InitOp() {
		int i;
		for (i = 0; i < 21; i++)
			operation[i].setcode(i);
		operation[0].setopp("0");
		operation[1].setopp(":=");
		operation[2].setopp("+");
		operation[3].setopp("-");
		operation[4].setopp("*");
		operation[5].setopp("/");
		operation[6].setopp("j");
		operation[7].setopp("j<");
		operation[8].setopp("j=");
		operation[9].setopp("j>=");
		operation[10].setopp("j<=");
		operation[11].setopp("j<>");
		operation[12].setopp("j>");
		operation[13].setopp("@");
		operation[14].setopp("@x");
		operation[15].setopp("&x");
		operation[16].setopp("BP");
		operation[17].setopp("EP");
		operation[18].setopp("call");
		operation[19].setopp(":=x");
		operation[20].setopp("%");
	}

	// 目标代码生成函数
	public void Produre(int op, int a, int b, int r) {
		switch (op) {
		case 0:
			if (r == 1) {
				gen_target(filename + ".asm", "main proc far\r\n");
				gen_target(filename + ".asm", "start:push ds;\r\n");
				gen_target(filename + ".asm", "      mov ax,0;\r\n");
				gen_target(filename + ".asm", "      push ax;\r\n");
				gen_target(filename + ".asm", "      mov ax,data;\r\n");
				gen_target(filename + ".asm", "      mov ds,ax;\r\n\r\n");
			}
			break;
		case 1:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r) + ",ax;为"
					+ Getname(r) + "赋值为" + Getname(a) + "\r\n");
			break;
		case 2:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      add ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r)
					+ ",ax;做加法赋值给" + Getname(r) + "\r\n");
			break;
		case 3:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r)
					+ ",ax;做减法赋值给" + Getname(r) + "\r\n");
			break;
		case 4:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov cx," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mul cx;\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r)
					+ ",ax;做乘法赋值给" + Getname(r) + "\r\n");
			break;
		case 5:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      xor dx,dx;\r\n");
			gen_target(filename + ".asm", "      mov cx," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      div cx;\r\n");
			// gen_target(filename+".asm","      mov al,ah;\r\n");
			// gen_target(filename+".asm","      mov ah,0;\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r)
					+ ",ax;做除法赋值给" + Getname(r) + "\r\n");
			break;
		case 6:
			if (r != 5001 && Equ[r - 1].getop() != 16)
				gen_target(filename + ".asm", "      jmp lab" + r + ";跳转至" + r
						+ "\r\n");
			break;
		case 7:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      js  lab" + r + ";跳转至" + r
					+ "\r\n");
			break;
		case 8:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      je  lab" + r + ";跳转至" + r
					+ "\r\n");
			break;
		case 9:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      je  lab" + r + ";跳转至" + r
					+ "\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      jns  lab" + r + ";跳转至" + r
					+ "\r\n");
			break;
		case 10:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      jb  lab" + r + ";跳转至" + r
					+ "\r\n");
			break;
		case 11:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      jne  lab" + r + ";跳转至" + r
					+ "\r\n");
			break;
		case 12:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      jns  lab" + r + ";跳转至" + r
					+ "\r\n");
			break;
		case 13:
			gen_target(filename + ".asm", "      mov dx,offset str" + r
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov ah,09h;\r\n");
			gen_target(filename + ".asm", "      int 21h;输出字符串变量str" + r
					+ "\r\n");
			break;
		case 14:
			gen_target(filename + ".asm", "      mov ax," + Getname(r)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov bx,10;\r\n");
			gen_target(filename + ".asm", "      mov cx,5;\r\n");
			gen_target(filename + ".asm", "      call PrintfNum;输出算术表达式子结果\r\n");
			break;
		case 15:
			gen_target(filename + ".asm", "      call ScanfNum;\r\n");
			gen_target(filename + ".asm", "      mov  " + Getname(r)
					+ ",bx;读入语句\r\n");
			break;
		case 16:
			gen_target(filename + ".asm", Getname(r) + "_pro proc\r\n");
			break;
		case 17:
			gen_target(filename + ".asm", "      ret\r\n");
			gen_target(filename + ".asm", Getname(r) + "_pro endp\r\n\r\n");
			break;
		case 18:
			gen_target(filename + ".asm", "      call " + Getname(r)
					+ "_pro;\r\n");
			break;
		case 20:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov cx," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      xor dx,dx;\r\n");
			gen_target(filename + ".asm", "      div cx;\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r)
					+ ",dx;做余数赋值给" + Getname(r) + "\r\n");
			break;
		case 21:
			gen_target(filename + ".asm", "      mov ax,4c00h;\r\n");
			gen_target(filename + ".asm", "      int 21h;\r\n");
			break;
		default:
			break;
		}
	}

	// 寄存器分配，获得临时变量
	public String Getname(int a) {
		int i;
		int cr;
		if (a == 50001)
			return "1";
		if (a == 50000)
			return "0";
		if (a > 5000 && a < 10000) {
			a = a - 5000;
			return "temp_prev+" + a;
		}
		if (a > 1000) {
			a = a - 1000;
			return "prev+" + a;
		}
		for (i = 0; i < token_count; i++) {
			if (TokenTable[i].getlabel() == a)
				return TokenTable[i].getname();
		}
		if ((bx.getallocate() == 0) || (bx.getallocate() == a)) {
			bx.setallocate(a);
			return bx.getname();
		} else {
			if ((dx.getallocate() == 0) || (dx.getallocate() == a)) {
				dx.setallocate(a);
				return dx.getname();
			} else {
				cr = (999 + a) % 2;
				switch (cr) {
				case 0:
					bx.setallocate(a);
					return bx.getname();
				case 1:
					dx.setallocate(a);
					return dx.getname();
				}
			}
		}
		return bx.getname();
	}

	// 查找基本块入口
	public void SortDGA(int address, int op_code) {
		int addr, op_cod, next_p1 = 0;
		op_cod = op_code;
		addr = address;
		switch (op_cod) {
		case 6:
			next_p1 = GenStack[addr].getresult();
			if (next_p1 == 5001)
				break;
			if (!GenStack[next_p1 - 1].getop().endsWith("BP"))
				GenStack[next_p1 - 1].setout_port(1);
			break;
		case 7:
			next_p1 = GenStack[addr++].getresult();
			if (next_p1 == 5001)
				break;
			GenStack[next_p1 - 1].setout_port(1);
			GenStack[addr].setout_port(1);
			break;
		case 8:
			next_p1 = GenStack[addr++].getresult();
			if (next_p1 == 5001)
				break;
			GenStack[next_p1 - 1].setout_port(1);
			GenStack[addr].setout_port(1);
			break;
		case 9:
			next_p1 = GenStack[addr++].getresult();
			if (next_p1 == 5001)
				break;
			GenStack[next_p1 - 1].setout_port(1);
			GenStack[addr].setout_port(1);
			break;
		case 10:
			next_p1 = GenStack[addr++].getresult();
			if (next_p1 == 5001)
				break;
			GenStack[next_p1 - 1].setout_port(1);
			GenStack[addr].setout_port(1);
			break;
		case 11:
			next_p1 = GenStack[addr++].getresult();
			if (next_p1 == 5001)
				break;
			GenStack[next_p1 - 1].setout_port(1);
			GenStack[addr].setout_port(1);
			break;
		case 12:
			next_p1 = GenStack[addr++].getresult();
			if (next_p1 == 5001)
				break;
			GenStack[next_p1 - 1].setout_port(1);
			GenStack[addr].setout_port(1);
			break;
		default:
			break;
		}
	}

	// 清空asm文件
	public void clearAsm() {
		try {
			FileWriter writer = new FileWriter(filename + ".asm");
			writer.write("");
			writer.close();
		} catch (IOException ioe) {
		}
	}
}