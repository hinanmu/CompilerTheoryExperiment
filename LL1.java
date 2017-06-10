package LL1;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import java.io.StringReader;
import javax.security.auth.Subject;
import javax.swing.text.AbstractDocument.BranchElement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.omg.CORBA.FieldNameHelper;
public class LL1 {
	//private char[] VN = {'E', 'T', 'G', 'F', 'S'};//非终结符
	//private char[] VT = {'+', '-', '*', '/', '(', ')', 'i', 'e'};//终结符
	//private String[] F = {"E->TG", "G->+TG|-TG", "G->e", "T->FS", "S->*FS|/FS", "S->e", "F->(E)", "F->i"};
	private String[] F;
	private char[] VN;
	private char[] VT;
	
	private Vector<String> simF  = new Vector<String>();
	private StringBuffer[] FirstVN ;
	private StringBuffer[] FollowVN ;
	private boolean[] VNE ;
	private char emp = 'e';
	private Map<String, String> paTable;
	private String tempVT ;
	private Vector<String> stackChange = new Vector<String>();
	private Vector<String> inputStrChange = new Vector<String>();
	private Vector<String> actionChange = new Vector<String>();
	private Stack<Character> symStack = new Stack<Character>();
	
	private JFrame frame, table;
	private JTable jt;
	private JButton chooseButton, startButton, resetButton;
	private JTextField fileField,inputStrField;;
	private JPanel northPanel, centralPanel, southPanel;
	private JTextArea mapArea;
	private String file = "";
	private Vector<String> inputF = new Vector<String>();
	
	
	public LL1(){
		createGui();
	}
	
	public void dealInputF()
	{
		F = new String[inputF.size()];
		for(int i = 0;i<inputF.size();i++)
		{
			F[i] = inputF.elementAt(i);
		}
		simF();
		
		StringBuffer getVN = new StringBuffer();
		for(int i = 0;i<simF.size();i++)
		{
			if(!getVN.toString().contains(String.valueOf(simF.elementAt(i).charAt(0))))
			{
				getVN.append(simF.elementAt(i).charAt(0));
			}
		}
		VN = new char[getVN.length()];
		for(int i = 0;i<getVN.length();i++)
		{
			VN[i] = getVN.charAt(i);
		}
		
		StringBuffer getVT = new StringBuffer();
		for(int i = 0;i<simF.size();i++)
		{
			for(int j = 3;j<simF.elementAt(i).length();j++)
			{
				if(!getVN.toString().contains(String.valueOf(simF.elementAt(i).charAt(j))))
				{
					if(!getVT.toString().contains(String.valueOf(simF.elementAt(i).charAt(j))))
					{
						getVT.append(simF.elementAt(i).charAt(j));
					}
				}
			}
		}
		VT = new char[getVT.length()];
		for(int i = 0;i<getVT.length();i++)
		{
			VT[i] = getVT.charAt(i);
		}
		
		
		FirstVN = new StringBuffer[VN.length];
	    FollowVN = new StringBuffer[VN.length];
	    VNE = new boolean[VN.length];
	    tempVT= String.valueOf(VT)+"#";
	    
	    for(int i = 0;i<VN.length;i++)
	    {
	    	System.out.println(VN[i]);
	    }
	    for(int i = 0;i<VT.length;i++)
	    {
	    	System.out.println(VT[i]);
	    }
	}
	
	public void simF()//去除文法中的|（或）符号，简化
	{
		for(int i = 0;i<F.length;i++)
		{
			if(F[i].contains("|"))
			{
				int orPos1 = 3;
				int orpos2 = 0;
				for(int j = 0;j<F[i].length();j++)
				{
					if(F[i].charAt(j)=='|')
					{
						orpos2 = j;
						simF.addElement(F[i].substring(0, 3)+F[i].substring(orPos1, orpos2));
						orPos1 = orpos2+1;
					}
				}
				simF.addElement(F[i].substring(0, 3)+F[i].substring(orPos1,F[i].length()));
			}
			else
			{
				String  temp = F[i];
				simF.addElement(temp);
			}

		}
	}
	
	public boolean isInVN(char c)//c是不是VN数组里的一个元素
	{
		String s = String.valueOf(VN);
		if(s.contains(String.valueOf(c)))
			return true;
		else
			return false;
	}
	
	public boolean isInVT(char c)//c是不是VT数组里的一个元素
	{
		String s = String.valueOf(VT);
		if(s.contains(String.valueOf(c)))			
			return true;
		else
			return false;
	}
	
	public void createFirstVN()//求全体非终结符的First集合，放在FirstVN[]中
	{
		FirstVN = new StringBuffer[VN.length];
		
		for(int i = 0;i<FirstVN.length;i++)
		{	
			FirstVN[i] = new StringBuffer();	
		}
		
		for(int i = 0;i < FirstVN.length;i++)
		{
			if(FirstVN[i].toString().isEmpty())//如果还没求过的话
			{
				myFirstVN(VN[i]);
			}
		}
		
		for(int i = 0;i < VN.length;i++)
		{
			if(VNE[i]&&(!FirstVN[i].toString().contains("e")))
			{
				FirstVN[i].append(emp);
			}
		
		}
	}
	
	public void myFirstVN(char c)//求单个非终结符的first集合
	{
		String svn = String.valueOf(VN);//中间变量
		for(int i = 0;i < simF.size();i++)
		{
			String s = simF.elementAt(i);
			if(c == s.charAt(0))//产生式左边是和c一样的非终结符
			{
				for(int j = 3;j<s.length();j++)//S=>AB..则从箭头后面开始读取符号处理
				{
					if(isInVT(s.charAt(j)))//当为终结符的时候直接加入到FIrst集合当中
					{
						if(s.charAt(j) == emp)
						{
							VNE[svn.indexOf(s.charAt(0))] = true;
						}
						FirstVN[svn.indexOf(s.charAt(0))].append(s.charAt(j));
						break;

					}
					else if(isInVN(s.charAt(j)))//阻止A=>..A..这样的情况，防止出现左递归，当为非终结符且没有左递归,
					{
						if(c==s.charAt(j))//防止出现左递归
						{
							break;
						}
	
						if(FirstVN[svn.indexOf(s.charAt(j))].toString().isEmpty())//如果这个非终结符还没有求得话，求此非终结符的first集合
						{
							myFirstVN(s.charAt(j));
						}
						if(FirstVN[svn.indexOf(s.charAt(j))].toString().contains("e"))//S->A A 如果此非终结符first集合含有空
						{
							FirstVN[svn.indexOf(s.charAt(0))].append(FirstVN[svn.indexOf(s.charAt(j))].deleteCharAt(FirstVN[svn.indexOf(s.charAt(j))].toString().indexOf('e')));
							if(j == s.length()-1)//如果此非终结符是最后一位，则标记此非终结符能推出空
							{
								VNE[svn.indexOf(s.charAt(0))] = true;
							}
							continue;
						}
						else//如果不是最后一位的话
						{
							FirstVN[svn.indexOf(s.charAt(0))].append(FirstVN[svn.indexOf(s.charAt(j))]);
							break;
						}
							
					}
				}
			}
		}
		
	}
	
	public void createFollowVN()//求所有非终结符follow集合
	{
		for(int i = 0;i<FollowVN.length;i++)
		{	
			FollowVN[i] = new StringBuffer();	
		}
		for(int i = 0;i<FollowVN.length;i++)
		{
			if(FollowVN[i].toString().isEmpty())//如果还没求过的话
			{
				myFollowVN(VN[i]);
			}
		}
	}
	
	public void myFollowVN(char c)//求单个非终结符follow集合
	{
		if(c == simF.elementAt(0).charAt(0))//c为开始符号时，将#加入c的follow集中'
		{
			FollowVN[0].append("#");//开始符号下标为0
		}
		String svn = String.valueOf(VN);
		for(int i = 0;i<simF.size();i++)
		{
			String s= simF.elementAt(i);
			if(s.substring(3).contains(String.valueOf(c)))
			{
				int cPos = s.substring(3).indexOf(c)+3;
				if(cPos != s.length()-1 )
				{
					for(int j = cPos+1;j<s.length();j++)
					{
						if(String.valueOf(VT).contains(String.valueOf(s.charAt(j))))//如果非终结符c的下一位是终结符则直接加入
						{
							if(!FollowVN[String.valueOf(VN).toString().indexOf(c)].toString().contains(String.valueOf(s.charAt(j))))
							{
								FollowVN[String.valueOf(VN).toString().indexOf(c)].append(s.charAt(j));
							}
							break;//判断下一文法
						}
						else//下一位为非终结符
						{
							if(VNE[String.valueOf(VN).indexOf(s.charAt(j))])//如果下一位的非终结符能推出空
							{
								if(j==s.length()-1)
								{
									int ePos = FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].indexOf("e");
									String temp  = FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].deleteCharAt(ePos).toString();
							    	FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].append("e");
									  if(!FollowVN[String.valueOf(VN).indexOf(c)].toString().contains(temp))
								    {

								    	FollowVN[String.valueOf(VN).indexOf(c)].append(FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].deleteCharAt(ePos));
									    FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].append("e");
								    }

								    if(FollowVN[String.valueOf(VN).indexOf(s.charAt(0))].toString().isEmpty())
									{
										myFollowVN(s.charAt(0));
									}
								    if(!FollowVN[String.valueOf(VN).indexOf(c)].toString().contains(FollowVN[String.valueOf(VN).indexOf(s.charAt(0))].toString()))
									{
									  FollowVN[String.valueOf(VN).indexOf(c)].append(FollowVN[String.valueOf(VN).indexOf(s.charAt(0))]);
									}
								}
								else
								{

									int ePos = FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].indexOf("e");
									String temp  = FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].deleteCharAt(ePos).toString();
							    	FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].append("e");
									  if(!FollowVN[String.valueOf(VN).indexOf(c)].toString().contains(temp))
								    {
								    	FollowVN[String.valueOf(VN).indexOf(c)].append(FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].deleteCharAt(ePos));
									    FirstVN[String.valueOf(VN).indexOf(s.charAt(j))].append("e");
								    }

								}
								
							}
							else//如果下一位的非终结符不能推出空
							{
								 if(!FollowVN[String.valueOf(VN).indexOf(c)].toString().contains(FirstVN[String.valueOf(VN).indexOf(s.charAt(j))]))
								{
									FollowVN[String.valueOf(VN).indexOf(c)].append(FirstVN[String.valueOf(VN).indexOf(s.charAt(j))]);
								}
								break;
							}
						}
					}
				}
				else
				{
					 if(FollowVN[String.valueOf(VN).indexOf(s.charAt(0))].toString().isEmpty())
						{
							myFollowVN(s.charAt(0));
						}
					 if(!FollowVN[String.valueOf(VN).indexOf(c)].toString().contains(FollowVN[String.valueOf(VN).indexOf(s.charAt(0))].toString()))
						{
						 FollowVN[String.valueOf(VN).indexOf(c)].append(FollowVN[String.valueOf(VN).indexOf(s.charAt(0))]);
						}
				}
			}
		}
	
			
	}
	
	public String createFirst(String s)//求一个字符串的first集合，便于求follow集合
	{
		StringBuffer bf = new StringBuffer();
		for(int i = 0;i < s.length();i++)
		{
			if(isInVT(s.charAt(i)))
			{//如果最左端是终结符的话
				bf.append(s.charAt(i));
				break;
			}
			else if(isInVN(s.charAt(i))&&VNE[String.valueOf(VN).indexOf(s.charAt(i))])
			{
				String temps = FirstVN[String.valueOf(VN).indexOf(s.charAt(i))].toString();
				if(i != s.length()-1)
				{
					bf.append(FirstVN[String.valueOf(VN).indexOf(s.charAt(i))].deleteCharAt(emp));
					
				}
				else
				{
					bf.append(FirstVN[String.valueOf(VN).indexOf(s.charAt(i))]);
				}
				
			}
			else//First(Xi)属于First(s)
			{
				bf.append(FirstVN[String.valueOf(VN).indexOf(s.charAt(i))]);
				break;
			}
		}
		return bf.toString();
	}
	
	public void parseTable()//构造分析表
	{
		paTable = new HashMap<String, String>();
		for (int i = 0; i < simF.size(); i++) 
		{
			String tempFir  = createFirst(simF.elementAt(i).substring(3));
			for(int j = 0;j<tempFir.length();j++)
			{
				paTable.put(String.valueOf(simF.elementAt(i).charAt(0))+" "+String.valueOf(tempFir.charAt(j)), simF.elementAt(i));
			}
			if(tempFir.contains("e"))
			{
				String tempFol = FollowVN[String.valueOf(VN).indexOf(simF.elementAt(i).charAt(0))].toString();
				for(int j = 0;j<tempFol.length();j++)
				{
					paTable.put(String.valueOf(simF.elementAt(i).charAt(0))+" "+String.valueOf(tempFol.charAt(j)), simF.elementAt(i));
				}
				
			}
			
		}
	}
	
	public void displayPaTable()
	{
		
		for(int i = 0;i<tempVT.length();i++)
		{
			System.out.print('\t');
			System.out.print(tempVT.charAt(i));
		}
		System.out.println();
		for(int i = 0;i<VN.length;i++)
		{
			System.out.print(VN[i]+":");;
			for(int j = 0;j<tempVT.length();j++)
			{
				String key = String.valueOf(VN[i])+" "+ String.valueOf(tempVT.charAt(j));
				System.out.print('\t');
				System.out.print(paTable.get(key));
			}
			System.out.println();
		}
	}
	public void displayFirst()
	{
		System.out.println("first集合");
		for(int i = 0;i<FirstVN.length;i++)
		{
			System.out.print(VN[i]);
			System.out.print(":");
			System.out.println(FirstVN[i]);
		}

		
	}
	
	public void displayFollow()
	{
		System.out.println("follow集合");
		for(int i = 0;i<FollowVN.length;i++)
		{
			System.out.print(VN[i]);
			System.out.print(":");
			System.out.println(FollowVN[i]);
		}

		
	}
	
	public void displaysimF()
	{
		for(int i = 0;i<simF.size();i++)
		{
			System.out.println(simF.elementAt(i));
		}
	}
	
	public void analyseStr()//分析输入字符串
	{
		String inputStr =inputStrField.getText();// "i+i*i#";
		symStack.push('#');
		symStack.push(simF.elementAt(0).charAt(0));//j加入开始符号
		stackChange.addElement("#"+String.valueOf(simF.elementAt(0).charAt(0)));
		actionChange.addElement("初始化");
		inputStrChange.addElement(inputStr);
		for(int i = 0 ;i<inputStr.length();i++)
		{
			while(symStack.peek()!='#')
			{
				String key= symStack.peek().toString()+" "+String.valueOf(inputStr.charAt(i));
				
				if(symStack.peek()==inputStr.charAt(i))
				{
					symStack.pop();
					
					StringBuffer tempStack = new StringBuffer();
					for(int j = 0;j<symStack.size();j++)
					{
						tempStack.append(symStack.elementAt(j));
					}
					stackChange.addElement(tempStack.toString());
					
					actionChange.addElement("next");
					inputStrChange.addElement(inputStr.substring(i+1));
					break;
				}
				else if(isInVT(symStack.peek()))
				{
					inputStrChange.addElement(inputStr.substring(i));
					stackChange.addElement("error");
					actionChange.addElement("error");
					break;
				}
				else if(paTable.get(key)== null)
				{
					inputStrChange.addElement(inputStr.substring(i));
					stackChange.addElement("error");
					actionChange.addElement("error");
					break;
				}
				else if(paTable.get(key)!= null)
				{
					String temp = paTable.get(key);
					symStack.pop();
					actionChange.addElement(temp);
					String tempSimple = temp.substring(3);
					
					for(int j = tempSimple.length()-1;j>-1;j--)
					{
						if(tempSimple.charAt(j)!=emp)
						{
							symStack.push(tempSimple.charAt(j));
						}
					
					}
					
					StringBuffer tempStack = new StringBuffer();
					for(int j = 0;j<symStack.size();j++)
					{
						tempStack.append(symStack.elementAt(j));
					}
					stackChange.addElement(tempStack.toString());
					inputStrChange.addElement(inputStr.substring(i));
				}
					
			}
			if(actionChange.elementAt(actionChange.size()-1)=="error")
			{
				break;
			}
			if(inputStr.charAt(i)=='#')
			{
				inputStrChange.addElement("#");
			}
		}

	}
		
	public void displayAnalyse()
	{
		for(int i = 0;i<actionChange.size();i++)
		{
			System.out.print('\t');
			System.out.print(stackChange.elementAt(i));//分析栈的变化
			System.out.print('\t');
			System.out.print(inputStrChange.elementAt(i));//输入的变化
			System.out.print('\t');
			System.out.println(actionChange.elementAt(i));//动作的变化
		}
	}
	

	public void guiTable()
	{
		String header[] = {"分析栈","输入串","动作"};
		DefaultTableModel md = new DefaultTableModel(header, 0);
		jt.setModel(md);
		for(int i = 0;i<actionChange.size();i++)
		{
			String[] forTable = {stackChange.elementAt(i),inputStrChange.elementAt(i),actionChange.elementAt(i)};
			md.addRow(forTable);
		}
		
		jt.setModel(md);
		jt.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scroll = new JScrollPane(jt);
		scroll.setSize(300, 200);
		table = new JFrame();
		table.add(scroll);
		table.setSize(500, 400);
		table.setVisible(true);
		table.setTitle("LL1-分析表");
		table.setLocationRelativeTo(null);
	}
	public void createAll()
	{
		dealInputF();
		//simF();
		//displaysimF();
		createFirstVN();
		//displayFirst();
		createFollowVN();
		//displayFollow();
		parseTable();
		displayPaTable();
		analyseStr();
		//displayAnalyse();
		guiTable();
		
	}
	
	public void createGui()
	{
		chooseButton = new JButton("文件");
		chooseButton.setBackground(Color.blue);
		chooseButton.setPreferredSize(new Dimension(70, 20));
		
		startButton = new JButton("开始");
		startButton.setBackground(Color.white);
		startButton.setPreferredSize(new Dimension(70, 20));
		
		resetButton = new JButton("重置");
		resetButton.setBackground(Color.white);
		resetButton.setPreferredSize(new Dimension(70, 20));
		
		fileField = new JTextField("", 23);
		fileField.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		mapArea = new JTextArea(12, 30);
		mapArea.setEditable(false);
		mapArea.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		northPanel = new JPanel();
		northPanel.setBackground(Color.blue);
		northPanel.add(fileField);
		northPanel.add(chooseButton);
		
		inputStrField= new JTextField("", 15);
		inputStrField.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		centralPanel = new JPanel();
		centralPanel.setBackground(Color.white);
		centralPanel.add(mapArea);
		
		southPanel = new JPanel();
		southPanel.setBackground(Color.ORANGE);
		southPanel.add(startButton);
		southPanel.add(resetButton);
		southPanel.add(inputStrField);
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		frame = new JFrame();
		frame.add(northPanel, BorderLayout.NORTH);
		frame.add(centralPanel, BorderLayout.CENTER);
		frame.add(southPanel, BorderLayout.SOUTH);
		frame.setTitle("LL1");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(350, 320);
		frame.setResizable(false);
		frame.setVisible(true);
		BoundButton();
		
		
	}
	
	public void BoundButton()
	{
		chooseButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JFileChooser tmp = new JFileChooser();
			tmp.showOpenDialog(null);
			File getFile = tmp.getSelectedFile();
			if (getFile != null) {
				file = getFile.getAbsolutePath();
				fileField.setText(file);
				mapArea.append("\n");
				jt = new JTable();
				FileReader fr = null;
				try {
					fr = new FileReader(file);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				BufferedReader in = new BufferedReader(fr);
				String line = "";
				try {
					while ((line = in.readLine()) != null) {
						inputF.addElement(line);
						mapArea.append(line + "\n");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
					}
				}
			}
		});
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createAll();
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileField.setText("");
				mapArea.setText("");
				inputStrField.setText("");
				table.dispose();
				jt = new JTable();
			}
		});
	}
	public static void main(String[] args)
	{
		new LL1();
	}
