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

	// �ļ���
	String filename;

	// �ʷ�������������
	char ch;
	/*
	 * ��ʱ��ȡ���ַ�����
	 */
	int tempchar;
	/*
	 * �Ѿ������˷��ű�ķ��Ÿ���
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
	 * ��ʱ
	 */
	token CurrentToken = new token();
	
	symble Currentsymble = new symble();

	symble[] SymbleList = new symble[100];
	symble[] SymbleList_Pro = new symble[100];
	token[] tokenList = new token[1024];

	InputStreamReader reader;

	// �﷨������������
	int token_pos;
	int code;
	int address;
	int LineOfEqu;
	int total_var;
	/*
	 * ��ջ�ε�ʵ��
	 */
	Stack[] stack_expr = new Stack[100];
	/*
	 * ��Ԫʽ�ṹ
	 */
	equ[] Equ = new equ[1024];
	/*
	 * ������
	 */
	var[] VarList = new var[100];
	String ID;
	/*
	 * pos�ǽ�ջ�ķ��Ÿ�����
	 * ����ţ��п�����
	 * Ҳ����Push(code,address)ִ�еĴ�����
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

	// Ŀ�����������������
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
	 * ��ʼ��������
	 */
	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.add(getOpenAction());
			/*
			 * �ڹ��������ҿ���ť
			 */
			toolBar.add(getNewAction());
			toolBar.add(getAnalyseAction());

		}
		return toolBar;
	}

	/**
	 * ��ʼ���򿪰�ť
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
			openAction.putValue(Action.NAME, "��");

		}
		return openAction;
	}

	/**
	 * ��ʼ���½���ť
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
			newAction.putValue(Action.NAME, "�½�");

		}
		return newAction;
	}

	/**
	 * ��ʼ�����밴ť
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
			analyseAction.putValue(Action.NAME, "����");

		}
		return analyseAction;
	}

	/**
	 * ��ʼ��JScrollPane���
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
	 * ��ʼ���ı���
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
	 * ��ʼ��JScrollPane
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
	 * ��ʼ��outTextField
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
	 *��ʼ���ָ���
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
	 * ��ʼ�����
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
		this.setTitle("CoCompiler-36060320-����ϲ");
		this.setBounds(200, 150, 800, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * ���ò���
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
	 * �ҿ��ļ�
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
				this.setTitle("CoCompiler-36060320-����ϲ " + sourFile.getName());
				inTextArea.setEditable(true);
				edited = false;
				indocument = inTextArea.getDocument();
				indocument.addDocumentListener(this);
				outTextArea.setText("");
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(this, "�޷����ļ�!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 *�½��ļ�
	 */
	private void newFile() {

		sourFile = null;
		inTextArea.setText("");
		this.setTitle("CoCompiler-36060320-����ϲ");
		inTextArea.setEditable(true);
		outTextArea.setText("");
		indocument = inTextArea.getDocument();
		indocument.addDocumentListener(this);
		edited = false;

	}

	/**
	 * �����ļ�
	 */

	private void analyseFile() {
		filename = sourFile.getName().substring(0,
				sourFile.getName().indexOf("."));
		System.out.print(filename);
		/*
		 * �ʷ�
		 */
		word_analysis();
		/*
		 * �﷨������
		 */
		parser();

		if (error_count == 0) {
			generate();
		} else
			outTextArea1.append("�ó�������﷨��ʷ�����!\n�޷�����Ŀ�����");

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

	/* �ʷ��������� */
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
			 * ����Ҫ�����д�뵽keyword.txt��token.txt,symble.txt�У�
			 * ���ƣ�keyword.txt,symble.txt���Ϊ 100�� token.txtΪ1024�
			 * ���Ƕ���ͨ��SymbleList[i]��SymbleList_Pro[i]��key[i]��ȡ��Ȼ�󣬴��뵽
			 * ��Щ�ĵ��еġ���ˣ�ѡ��ʼ��
			 */
			SymbleList[i] = new symble();
			SymbleList_Pro[i] = new symble();
			key[i] = new KeyWord();
		}
		for (i = 0; i < 1024; i++)
			tokenList[i] = new token();
		Scanner();
	}

	// ������
	public void Scanner() {
		/*
		 * The total number of error
		 */
		error_count = 0;

		// ��������,key[i]�д�����ǵ��ʵ����ƺͱ���
		/*
		 * It seems that it is useless. Not correctly. It has nothing to do with
		 * word analysis. But it has to other function.
		 */
		ScannerInit();

		System.out.println("�ʷ���������ʼ\n����token������:\n");// �ʷ�����
		outTextArea.append("�ʷ���������ʼ\n����token������:\n");
		/*
		 * source file to be compiled.
		 */
		File file = sourFile;

		try {
			// һ�ζ�һ���ַ�
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
					 * A-Z��a-z��_
					 */
					if (((ch > 64) && (ch < 91)) || ((ch > 96) && (ch < 123))

					|| ch == '_')
						/*
						 * ʶ�����ֺͱ�ʶ��
						 */
						IsAlpha();
					else {
						if (ch == '/')
							/*
							 * ������ź�ע��
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
			outTextArea.append("\n�ʷ��������.\n����" + error_count + "������!");
		else
			outTextArea.append("\n�ʷ��������.!");

		// �ʷ�������� �жϸ������ʵ�����
	}

	// ��������
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
			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
			while ((tempString = reader1.readLine()) != null) {
				i++;
				String[] temp = tempString.split(" ");
				/*
				 * ���뵥�ʵ����ֺͱ���
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

	// ���ִ���
	public void IsNumber() {
		boolean flag = false;
		char ch1;
		CurrentToken.setname("");
		while ((ch > 47) && (ch < 58))
		/*
		 * 48��57��Ӧ��ASCII��ֱ���1��9
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
		 * ������
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
				 * ʵ����
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

	// ��ĸ����
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

		// �ж��Ƿ�Ϊ������
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
			 * ��־��
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

	// ע�ʹ���
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
				 * Ϊ��/��
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

	// �ַ�������
	private void IsChar() {
		CurrentToken.setname("");
		try {
			/*
			 * �ַ�����
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

	// ��������Ĵ���
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

	// ���ģ��
	public void OutPut() {
		boolean flag = false;
		if (!Is_Program) {
			/*
			 * ��ʶ�� 27 ������ 28 ʵ���� 29 �ַ����� 30
			 */
			if ((CurrentToken.getcode() == 27)
					|| (CurrentToken.getcode() == 30)
					|| (CurrentToken.getcode() == 28)
					|| (CurrentToken.getcode() == 29)) {
				/*
				 * Token private int label=0; private String name=""; private
				 * int code=0; private int addr=0;
				 * 
				 * Symble int number; // ��� int type; // ���� String name; // ����
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
				 * ��
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

	// ��ӡ����aΪ�������͵ı���
	public void Error(int a) {
		error_count++;
		switch (a) {
		case 1:
			outTextArea.append("error" + error_count + ":�Ƿ��ַ������ڵ�" + LineOfPro
					+ "��!\n");
			break;
		case 2:
			outTextArea.append("error" + error_count + ":ʵ������������ڵ�" + LineOfPro
					+ "��!\n");
			break;
		case 3:
			outTextArea.append("error" + error_count + ":û��ƥ���ע�ͷ���");
			break;
		case 4:
			inTextArea1.append("error" + "��ʽ����,��һ����main�����﷨����\n");
			break;
		case 5:
			inTextArea1.append("error" + "��ʽ����,��һ���ٳ�����\n");
			break;
		case 6:
			inTextArea1.append("error" + "��" + LineOfPro + "������������\n");
			break;
		case 7:
			inTextArea1.append("error" + "��" + LineOfPro + "��ֵ������\n");
			break;
		case 8:
			inTextArea1.append("error" + "��" + LineOfPro + "����ȱ��'}'��ƥ��\n");
			break;
		case 9:
			inTextArea1.append("error" + "��" + LineOfPro + "��������\n");
			break;
		case 10:
			inTextArea1.append("error" + "��" + LineOfPro + "�����ʽ������\n");
			break;
		case 11:
			inTextArea1.append("error" + "��" + LineOfPro + "����if������\n");
			break;
		case 12:
			inTextArea1.append("error" + "��" + LineOfPro + "��,while������\n");
			break;
		case 13:
			inTextArea1.append("error" + "��" + LineOfPro + "��,for������\n");
			break;
		case 14:
			inTextArea1.append("error" + "main����ȱ��')'����\n");
			break;
		case 15:
			inTextArea1.append("error" + "��" + LineOfPro + "���������ʽ������\n");
			break;
		case 16:
			inTextArea1.append("error" + "��" + LineOfPro + "���������ʽ���ȱ��')'����\n");
			break;
		case 17:
			inTextArea1.append("error" + "main����ȱ��'('����\n");
			break;
		case 18:
			inTextArea1.append("error" + "main����ȱ��'{'����\n");
			break;
		case 19:
			inTextArea1.append("error" + "main����ȱ��'}'����\n");
			break;
		case 20:
			inTextArea1.append("error" + "ȱ��main��������\n");
			break;
		case 52:
			inTextArea1.append("error" + "�������ȱ��.��\n");
			break;
		case 53:
			inTextArea1.append("error" + "δд�κ����\n");
			break;
		case 54:
			inTextArea1.append("error" + "��" + LineOfPro + "�����ȱ��': '��\n");
			break;
		default:
			break;
		}
	}

	// �жϱ�ʶ���Ƿ����
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

	// �жϵ����Ƿ����

	public boolean WordHave() {
		int i;
		/*
		 * var_count is the variable which has been added into symble.txt
		 */
		for (i = 0; i < var_count; i++) {
			if (Currentsymble.getname().equals(SymbleList[i].getname())
					&& Currentsymble.gettype() == 27) {
				/*
				 * ��symble�еķ��ŵ����numble���뵽CurrentToken�С�
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

	// д���ļ�
	public void append(String fileName, int number, int type, String name) {
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
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
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
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
	 * ����Ŀ�����
	 */
	public void gen_target(String fileName, String content) {
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ���symble,token�ļ�
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

	
	// �﷨����������,Ҳ���������������
	/*
	 * 1������main֮ǰ������
	 * 2������main�е�����
	 * 3������main�е���������
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	public void parser() {
		/*
		 * ����˵����
		 * 1.	code =Integer.parseInt(temp[2]);Ҳ���ǵ��ʵı���
		 * 2.	VarList[]	��ŵ������ı�����
		 * 3.	SymbleList[]����������еı�������������ʵ����
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
		 * ��ʼ��
		 */
		for (i = 0; i < 100; i++) {
			stack_expr[i] = new Stack();
			VarList[i] = new var();
		}

		for (i = 0; i < 1023; i++)
			Equ[i] = new equ();
		
		InitStack();

		inTextArea1.append("�﷨��������ʼ\n������Ԫʽ����:\n");
		System.out.println("�﷨��������ʼ\n������Ԫʽ����:\n");

		try {
			File file = new File("token.txt");
			token_reader = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * declaration before main function,
		 *	main����֮ǰ�ı�������
		 */
		Declear_BeforeMain();

		parser_pro();
		
		/*
		 * ����VarList[]�е�����
		 */
		/*
		for(int k=0;k<var_count;k++){
			System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[k].getaddr());
			System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[k].getvalue());
			System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[k].getname());
		}
		*/
		/*
		 * **************************����*********************************
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


		 * **************************����*********************************
		 * 
		 * 
		 * **************************���*********************************
		 * Declear_BeforeMain()��VarList[var_count]��ֵ��	1
Declear_BeforeMain()��VarList[var_count]��ֵ��	27
Declear_BeforeMain()��VarList[var_count]��ֵ��	num
Declear_BeforeMain()��VarList[var_count]��ֵ��	2
Declear_BeforeMain()��VarList[var_count]��ֵ��	27
Declear_BeforeMain()��VarList[var_count]��ֵ��	number
Declear_BeforeMain()��VarList[var_count]��ֵ��	3
Declear_BeforeMain()��VarList[var_count]��ֵ��	27
Declear_BeforeMain()��VarList[var_count]��ֵ��	function
Declear_BeforeMain()��VarList[var_count]��ֵ��	1002
Declear_BeforeMain()��VarList[var_count]��ֵ��	27
Declear_BeforeMain()��VarList[var_count]��ֵ��	n
Declear_BeforeMain()��VarList[var_count]��ֵ��	0
Declear_BeforeMain()��VarList[var_count]��ֵ��	0
Declear_BeforeMain()��VarList[var_count]��ֵ��	null
           **************************���*********************************
           *
           *
           *
           *��ˣ�VarList����ľ�������������ȫ�ֱ������ͺ�������
		 */
		
		
		
		
		
		System.out.print("���������" + code + " " + address + "\n");
		
		switch (code) {
		/*
		 * void 49
		 * �������ĺ���������
		 */
		case 49:
		/*
		 * 0,0,0,1����ʼ��
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
				 * main����Ϊʲô�п���;
				 * �ⲻ��˵һ������main���棬
				 * ������������һ���������������Ҫ��һ��
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
			inTextArea1.append("error" + "main����ȱ��')'����\n");
			break;
		case 17:
			inTextArea1.append("error" + "main����ȱ��'('����\n");
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
					 * �Ѿ���������ݵ�������,char,int,float ��Ҫ����Ƿ��Ѿ���������
					 * void main(){
					 * 
					 */
					if (code == 6 || code == 14 || code == 19) {
					//	System.out.print("��������");
						/*
						 * ������߱�����������
						 * ������main������ߵı�����
						 */
						Declear_c0();
						
						for (i = 0; i < total_var; i++) {
							System.out.println("SymbleList[k].getname():	"+SymbleList[i].getname());
						}
							
						Is_Declear();
					}
					/*
					 * ������
					 * Language Analysis
					 */
					L_Analize();
				} else
					/*
					 * case 18:
			inTextArea1.append("error" + "main����ȱ��'{'����\n");
			break;
		case 20:
			inTextArea1.append("error" + "ȱ��main��������\n");
			break;
			case 4:
			inTextArea1.append("error" + "��ʽ����,��һ����main�����﷨����\n");
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
		 * ���������Ԫʽû�������꣬
		 * �ͼ������ɡ�
		 */
		while (gen_pos < Line) {
			gen(Equ[gen_pos].getop(), Equ[gen_pos].getop1(), Equ[gen_pos]
					.getop2(), Equ[gen_pos].getresult());
			gen_pos++;
		}
		/*
		 * ������־
		 */
		gen(0, 0, 0, 0);
		
		for(int k=0;k<VarList.length;k++){
			System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[k].getaddr());
			System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[k].getvalue());
			System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[k].getname());
		}
		

		/*
		 * ��¼���ܵĴ��������
		 */
		if (error_count > 0)
			inTextArea1.append("\n�﷨�������.\n����" + error_count + "������!n");
		else
			inTextArea1.append("\n�﷨�������.");
	}

	/*
	

	 */
	public void parser_pro() {
		int flag = 0;
		int addr;
		int type;
		/*
		 * code�ǣ�int,float,void�ı��� 
		 * ��һ���ַ��ı�����27�������Ǳ�־������������
		 * ��ЩӦ���Ǻ���ǰ����ں��������η���
		 */
		
		/*
		 * ��־���ͱ�������int,float,char�����൥��
		 */
		if ((code == 6 || code == 14 || code == 19 || code == 49)
				&& tokenList[token_pos + 1].getcode() == 27) {
			
			type = code;
			GetNext();
			token_pos++;
			/*
			 * 27 ��־��
			 */
			if (code == 27) {
				addr = address;
				/*
				 * ��Ԫʽ��ջ����ջ
				 * ������
				 */
				EquPush(16, type, 0, addr);
				/*
				 * ��Ҫ����Ϣ���뵽�������С�
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
					 * �������������ǣ�
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
							inTextArea1.append("error" + "main����ȱ��')'����\n");
							break;
		
						case 17:
							inTextArea1.append("error" + "main����ȱ��'('����\n");
							break;
						 */
						Error(14);
				} else
					Error(17);
				/*
				 * { ǰ���Ǳ�־��
				 * { 51
				 */
				if (code == 51) {
					GetNext();
					token_pos++;
					/*
					 * ,int,char,float����
					 */
					if (code == 6 || code == 14 || code == 19) {
						/*
						 * ���������ں����ڲ���
						 */
						System.out.print("��������\n");
						Declear_c0();
					}
					L_Analize();
					System.out.print("�������" + code + " " + type + "\n");
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

	// ��ֵ������
	public int S_Let(int a) {
		int addr, rtn;
		int flag;
		int prev = 5002;
		flag = 0;
		rtn = 0;
		InitStack();
		rtn = LineOfEqu + 1;
		
		System.out.print("\n" + "�Ӻ�������! " + tokenList[token_pos + 1].getcode()
				+ " " + LineOfEqu + "\n");
		/*
		 * ( 32
		 * ���Ǻ���
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
					 * ��ʶ�� 27 ������ 28
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
			inTextArea1.append("error" + "��" + LineOfPro + "��ֵ������\n");
			break;
			 */
			Error(7);
		return rtn;
	}

	// ����������
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

	// ������з���
	public int L_Analize() {
		int rtn = 0;
		switch (code) {
		/*
		 * ��ʶ�� 27
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

	// ���������
	public int S_printf() {
		System.out.print("\n������" + code);
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
						System.out.print("�������ʽ" + flag + "\n");
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
			System.out.print("��ӡ����" + code + "\n");
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
		System.out.print("\n�������" + code);
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

	// if������
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

	// While������
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

	// FOR������
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

	// �������ʽ����
	public int B_Analize() {
		int rtn;
		rtn = B_OR();
		if (stack_expr[now_addr].getcode() != 0) {
			// Error(15);
			System.out.print(stack_expr[now_addr].getcode() + " ");
		}
		return rtn;
	}

	// �������ʽ��ʼ��
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
	 * or ���
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
				System.out.print("\n�ݹ麯��" + stack_expr[now_addr].getcode()
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
	 * �Ƚ������
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

	// �������ʽ����
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
				 * ������
				 */
			case 28:
				Push(code, address);
				break;
				/*
				 * ʵ����
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
				 * ���j=0,˵������ǰ��û�У�
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
	 * +�ͣ�
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
	 * ���� ����
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
			 * ��־�� 27
			 */
			case 27:
				addr = now_addr;
				/*
				 * ( 32
				 */
				if (stack_expr[now_addr + 1].getcode() == 32) {
					System.out.print("��÷ֳ������! " + now_addr + " " + LineOfEqu
							+ "\n");
					now_addr++;
					/*
					 * ) 33
					 */
					
					/*
					 * while����Ҫ���ľ��ǽ�����������ߵĲ�������
					 * stack_expr[]��
					 * 
					 * ͬʱ��ָ����Ԫʽ�Ĳ���Ϊ��
					 * EquPush(1, stack_expr[now_addr].getaddr(), 0, prev);
					 */
					while (stack_expr[now_addr].getcode() != 33) {
						System.out.print("����stack_expr " + now_addr + " \n");
						
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
					 * ��ζ���ҵ��ˡ�
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
				 * getaddr()��Ϊ�����������ʱ��
				 * ��Ӧ�Ĳ����б��С�
				 */
			case 28:
				rtn = stack_expr[now_addr].getaddr();
				break;
			case 29:
				rtn = stack_expr[now_addr].getaddr();
				break;
				/*
				 * �ַ����� 30
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

	// ��Ԫʽ��ջ
	public int EquPush(int op, int a, int b, int r) {
		int i = LineOfEqu;
		Equ[LineOfEqu].setop(op);
		Equ[LineOfEqu].setop1(a);
		Equ[LineOfEqu].setop2(b);
		Equ[LineOfEqu].setresult(r);
		LineOfEqu++;
		return i;
	}

	// ���ҷֳ������
	public int find_pro(int temp) {
		int i;
		/*
		 * ���еķֳ�����Ϣ��������equ�����Ԫʽ����������ˡ�
		 * 
		 */
		for (i = 0; i < LineOfEqu; i++) {
			/*
			 * ��������Ϊ16��
			 * case 16:
				writer.write("BP ");
			 */
			if (Equ[i].getresult() == temp && Equ[i].getop() == 16)
				return i;
		}
		return -1;
	}

	// ȡ����һ������ from the text of token.txt
	public void GetNext() {
		try {
			String tempString = null;
			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
			if ((tempString = token_reader.readLine()) != null) {
				String[] temp = tempString.split(" ");
				/*
				 * ��token�е�һ�У�2 main 50 -1���� ID=main code=50 address=-1
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
	 * �����Ԫʽ���ļ��С�
	 */
	public void append_equ(String fileName, int op, int a, int b, int r) {
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
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

	// ������Ԫʽ
	public int gen(int op, int a, int b, int r) {
		LineOfEqu++;
		if (op >= 0 && op < 21)
			append_equ("equ.txt", op, a, b, r);
		return LineOfEqu;
	}

	/*
	 * ���ڶԺ����ڵı���
	 * Ҳ���Ǿֲ������Ĵ���
	 * 
	 * 
	 * 
	 * ���ԣ�
	 * �Ƕ�main�����ڲ��Ĵ���
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
			 * ��ʼ����ջ�󣬽�����ѹջ����ջ�С�
			 * Ҳ����˵����ȡ�ı���������main������ߵģ������˵�����
			 * �������stack�С�
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
			 * stack_expr[]��ߴ����ʲô��
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
				 * �����������������������͡�
				 * 
				 * 
				 * 
				 * 
				 */
				VarList[var_count].setaddr(stack_expr[i].getaddr());
				VarList[var_count].setvalue(stack_expr[i].getcode());
				VarList[var_count].setname(stack_expr[i].getname());
				/*
				 * := 41 �����ܳ����������
				 */
				/*
				 * ֻ����Ԫʽ�в��пɴ���:= 41
				 *���Ƕ�ǰ��������ı�����ֵ��
				 */
				if (stack_expr[i + 1].getcode() == 41) {
					addr = stack_expr[i].getaddr();
					i++;
					pos--;
					
					if (stack_expr[i + 1].getcode() == 28) {
						System.out.print("\n������������"+ stack_expr[i + 1].getaddr() + " \n");
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
					System.out.println("����3����" + pos);
					Error(6);
					return;
				}
			}
		}

	}

	// main������ǰ�ı�������
	/*
	 * main����ǰ�������������
	 * 1��������ȫ�ֱ�����
	 * 2������
	 * 
	 * 
	 * 
	 * 
	 * ������ͷ����   ::=  int����ʶ���� |float ����ʶ����|char����ʶ����
	 * ��ô��
	 * 1������ֻ�����Ǳ���������
	 * 2�������ı���������ֻ���������֣�int,float,char
	 * 3������ֻ���������ģ�int num;��������ֵ����int num=9;
	 */
	
	public void Declear_BeforeMain() {
		int i;
//		int addr;
		token_pos = 0;
		InitStack();
		/*
		 * ��GetNext()�еõ������� ID = temp[1];��������
		 * ���� code =Integer.parseInt(temp[2]);����
		 * ���ʱ��� address =Integer.parseInt(temp[3]);����
		 * ����ֵ ÿ����һ�ζ���һ��
		 */
		GetNext();
		/*
		 * �ʷ�������ĵ�N����Ϣ��
		 * ���������������������־�ڼǷ������������ɵ�token.txt�е�������
		 * ����
		 */
		token_pos++;
		/*
		 * bool 3 char 6 const 7 int 14 float 19
		 * code��ͨ��GetNext��õĵ��ʵı��롣
		 */
		while ((code == 6 || code == 14 || code == 19 || code == 7)
				/*
				 * token_pos+1=3;��������� void main () ��ô�� tokenList[token_pos +
				 * 2]��Ϊ��

				 * !=32�����ǣ���˵�����Ǻ���
				 * ˵���Ǳ���������
				 * 
				 * tokenList�ǣ�token tokenList[]
				 */
				&& tokenList[token_pos + 2].getcode() != 32) {
			
			/*
			 * ������������ջ
			 * ������������Կ�����
			 * �����ı�������� tokenList[]���ˡ�
			 */
			Push(code, address);
			/*
			 * �Ӵʷ������Ľ����ȡ�õ��ʡ�
			 */
			GetNext();
			
			token_pos++;
			/*
			 * ; 42
			 * ��Ҳ���ȥ�ˡ�
			 * 
			 * 
			 * ���ǲ��Եģ�����������������������������������
			 * ��������ֻ�п���Ϊ��
			 * int var1;
			 * char ch;
			 * float number;
			 * void main(){.....}
			 * 
			 * ��ˣ�����; ��ֻ��
			 * �����ı��������α��������͡�
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
		 * pos�ǽ�ջ�ķ��Ÿ�����
		 * ����ţ��п�����
		 * Ҳ����Push(code,address)ִ�еĴ�����
		 * 
		 */
		while (pos > 0) {
			/*
			 * ��ʶ�� 27
			 */
			if (stack_expr[i].getcode() == 27) {
				/*
				 * ��ȡ��ջ�еı������뵽������
				 * ��ô��VarList[]�����Ӧ����
				 * ��stack��Եı�����
				 * 
				 * ���������������ַ��뵽��������
				 * ȥ����,
				 * 
				 * ****************************************************************
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * ���ڣ�VarList[]�����������ı�����������
				 * 
				 * 
				 * VarList[]�Ǵ�ŵ������ı��������顣
				 * 
				 * 
				 * 
				 * 
				 * ***************************************************************
				 */
				VarList[var_count].setaddr(stack_expr[i].getaddr());
				VarList[var_count].setvalue(stack_expr[i].getcode());
				VarList[var_count].setname(stack_expr[i].getname());
				
				
			//	System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[var_count].getaddr());
			//	System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[var_count].getvalue());
			//	System.out.println("Declear_BeforeMain()��VarList[var_count]��ֵ��	"+VarList[var_count].getname());
				
				
				/*
				 * := 41
				 */
				
				
				/*
				 * �����co��������û�еģ�����
				 */
				/*if (stack_expr[i + 1].getcode() == 41) {
					addr = stack_expr[i].getaddr();
					i++;
					pos--;
					
					if (stack_expr[i + 1].getcode() == 28) {
						System.out.print("\n������������"
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
					 * ���Ǳ�־��
					 * �Ǳ�����
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
					System.out.println("����3����" + pos);
					Error(6);
					return;
				}
			}
		}
	}

	// main������ǰ�ı�������
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
		 * Ӧ���ǽ�������    ������     ���뵽stack_exper�аɡ�
		 */
		while (code != 33) {
			Push(code, address);
			GetNext();
			/*
			 * token_pos�൱��һ��ָ��
			 */
			token_pos++;
		}
		i = 0;
		
		/*
		 * Ҳ����˵��ջ�������ĸ���>0
		 * 
		 */
		while (pos > 0) {
			if (stack_expr[i].getcode() == 27) {
				/*
				 * ��Ԫʽ��ջ
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
					System.out.println("����3����" + pos);
					Error(6);
					return;
				}
			}
		}

	}

	// �ж����б����Ƿ��������������������
	public boolean Is_Declear() {
		boolean flag = true;
		int i;
		for (i = 1; i < total_var; i++) {
				
			/*
			 * SymbleList[]�������ǣ�
			 * ��־��������
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
				inTextArea1.append("error" + "����" + SymbleList[i].getname()
						+ "δ����\n");
				flag = false;
			}
		}
		return flag;
	}

	// �жϵ��������Ƿ��������������������
	public boolean Is_exist(symble temp) {
		int i;
		/*
		 * ���temp.gettype()!=27
		 * ��ô��temp.gettype()������������ʵ����
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

	// ��ʼ����ջ
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

	// ��ջ
	public void Push(int code, int addr) {
		stack_expr[pos].setaddr(addr);
		stack_expr[pos].setcode(code);
		stack_expr[pos].setname(ID);
		pos++;
	}

	// ��ʱ������ʣ�µĸ�����
	public int NewTemp() {
		int a;
		temp_count--;
		a = temp_count;
		return a;
	}

	// ���ڻ����
	public void BackPatch(int addr, int addr2) {
		Equ[addr].setresult(addr2);
		return;
		/*
		 * addr�൱��lineOfEqu.
		 */
	}

	// Ŀ���������������
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
		System.out.print("��Ԫʽ" + gen_count);
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
			outTextArea1.append("Ŀ��������ɿ�ʼ:\n");
			try {
				File target_code = new File(filename + ".asm");
				outTextArea1.read(new FileReader(target_code), null);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(this, "�޷����ļ�!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			outTextArea1.append("Ŀ��������ɽ���!\n");
		}

	}

	// ����������ת��ΪASCII�벢��ʾ�������
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
		gen_target(filename + ".asm", "PrintfNum endp;����������ʽ\r\n\r\n");
	}

	// �����������
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
		gen_target(filename + ".asm", "ScanfNum endp;�������\r\n\r\n");
	}

	/*
	 * ��ʼ��Ŀ�����
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

	// ������ű�
	public void read_symble() {
		int i = 0;
		File file = new File("symble.txt");
		BufferedReader reader1 = null;
		try {
			reader1 = new BufferedReader(new FileReader(file));
			String tempString = null;
			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
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

	// ȡ����һ����Ԫʽ
	public boolean GetNextGen() {
		try {
			String tempString = null;
			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
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

	// ��ʼ��������
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

	// Ŀ��������ɺ���
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
			gen_target(filename + ".asm", "      mov " + Getname(r) + ",ax;Ϊ"
					+ Getname(r) + "��ֵΪ" + Getname(a) + "\r\n");
			break;
		case 2:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      add ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r)
					+ ",ax;���ӷ���ֵ��" + Getname(r) + "\r\n");
			break;
		case 3:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r)
					+ ",ax;��������ֵ��" + Getname(r) + "\r\n");
			break;
		case 4:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov cx," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mul cx;\r\n");
			gen_target(filename + ".asm", "      mov " + Getname(r)
					+ ",ax;���˷���ֵ��" + Getname(r) + "\r\n");
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
					+ ",ax;��������ֵ��" + Getname(r) + "\r\n");
			break;
		case 6:
			if (r != 5001 && Equ[r - 1].getop() != 16)
				gen_target(filename + ".asm", "      jmp lab" + r + ";��ת��" + r
						+ "\r\n");
			break;
		case 7:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      js  lab" + r + ";��ת��" + r
					+ "\r\n");
			break;
		case 8:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      je  lab" + r + ";��ת��" + r
					+ "\r\n");
			break;
		case 9:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      je  lab" + r + ";��ת��" + r
					+ "\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      jns  lab" + r + ";��ת��" + r
					+ "\r\n");
			break;
		case 10:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      jb  lab" + r + ";��ת��" + r
					+ "\r\n");
			break;
		case 11:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      jne  lab" + r + ";��ת��" + r
					+ "\r\n");
			break;
		case 12:
			gen_target(filename + ".asm", "      mov ax," + Getname(a)
					+ ";\r\n");
			gen_target(filename + ".asm", "      sub ax," + Getname(b)
					+ ";\r\n");
			gen_target(filename + ".asm", "      cmp ax,0;\r\n");
			gen_target(filename + ".asm", "      jns  lab" + r + ";��ת��" + r
					+ "\r\n");
			break;
		case 13:
			gen_target(filename + ".asm", "      mov dx,offset str" + r
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov ah,09h;\r\n");
			gen_target(filename + ".asm", "      int 21h;����ַ�������str" + r
					+ "\r\n");
			break;
		case 14:
			gen_target(filename + ".asm", "      mov ax," + Getname(r)
					+ ";\r\n");
			gen_target(filename + ".asm", "      mov bx,10;\r\n");
			gen_target(filename + ".asm", "      mov cx,5;\r\n");
			gen_target(filename + ".asm", "      call PrintfNum;����������ʽ�ӽ��\r\n");
			break;
		case 15:
			gen_target(filename + ".asm", "      call ScanfNum;\r\n");
			gen_target(filename + ".asm", "      mov  " + Getname(r)
					+ ",bx;�������\r\n");
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
					+ ",dx;��������ֵ��" + Getname(r) + "\r\n");
			break;
		case 21:
			gen_target(filename + ".asm", "      mov ax,4c00h;\r\n");
			gen_target(filename + ".asm", "      int 21h;\r\n");
			break;
		default:
			break;
		}
	}

	// �Ĵ������䣬�����ʱ����
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

	// ���һ��������
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

	// ���asm�ļ�
	public void clearAsm() {
		try {
			FileWriter writer = new FileWriter(filename + ".asm");
			writer.write("");
			writer.close();
		} catch (IOException ioe) {
		}
	}
}