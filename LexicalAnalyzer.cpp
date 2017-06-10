5.// 词法分析器.cpp : 定义控制台应用程序的入口点。
6.//
7
9.
10.#include<iostream>
11.#include<string>
12.//#include<stdio.h>
13.using namespace std;
14.
15.char ch = ' ';
16.static int line = 1;
17.static int row = 0;
18.int numberCount = 0;
19.int idCount = 0;
20.string arr = "";
21.
22.string Key[] = { "if", "else", "for", "while", "do", "return", "break", "continue" };
23.string Delimiter[] = { ";", "(", ")", "[", "]", ",", ".", "{", "}" };
24.string Operator[] = { "+", "-", "*", "/" };
25.string R_operators[] = { "<", "<=", "==", ">", ">=" };
26.string Number[100];
27.string Identifier[100];
28.
29.
30.int isKey(string s)//判断是否是标识符
31.{
32.	for (int i = 0; i <8; i++)
33.	{
34.		if (Key[i].compare(s) == 0)
35.			return 1;
36.	}
37.	return 0;
38.}
39.
40.int isLetter(char c)
41.{
42.	if (((c <= 'z') && (c >= 'a')) || ((c <= 'Z') && (c >= 'A')))
43.	{
44.		if ((c <= 'Z') && (c >= 'A'))
45.			ch = ch + 32;
46.		return 1;
47.	}
48.	return 0;
49.
50.}
51.
52.int isNumber(char c)
53.{
54.	if (c >= '0'&&c <= '9')
55.		return 1;
56.	return 0;
57.}
58.
59.int insertId(string s)
60.{
61.	for (int i = 0; i < idCount; i++)
62.	{
63.		if (Identifier[i] == s)
64.		{
65.			return i;
66.			break;
67.		}
68.		else if (idCount == i + 1)
69.		{
70.			Identifier[idCount] = s;
71.			return idCount;
72.			idCount++;//not understand
73.		}
74.	}
75.}
76.
77.int insertNumber(string s)
78.{
79.	for (int i = 0; i < numberCount; i++)
80.	{
81.		if (Number[i] == s)
82.		{
83.			return i;
84.			break;
85.		}
86.		else if (numberCount == i + 1)
87.		{
88.			Number[numberCount] = s;
89.			return numberCount;
90.			numberCount++;
91.		}
92.	}
93.}
94.void analyse(FILE *fpin)
95.{
96.	
97.	while ((ch = fgetc(fpin)) != EOF)//依次读数据
98.	{
99.		arr = "";
100.		if (ch == ' ' || ch == '\t' || ch == '\n')//如果ch为空格或者回车或者换行符
101.		{
102.			if (ch == '\n')//如果是换行则行加1 列置0
103.			{
104.				line++;
105.				row = 0;
106.			}
107.		}
108.		else if (isLetter(ch))//第一个字符如果是字母的话
109.		{
110.			while (isLetter(ch) || isNumber(ch))//如果是字母或者数字的话，相加并且往下读
111.			{
112.				arr = arr + ch;
113.				ch = fgetc(fpin);
114.			}
115.
116.			fseek(fpin, -1L, SEEK_CUR);//之后遇到了不是字母或者数字，则回退
117.
118.			if (isKey(arr))//判断刚刚得到的字符串是否为保留字
119.			{
120.				row++;
121.				cout << arr << "\t\t(1," << arr << ")" << "\t\t关键字" << "\t\t(" << line << "," << row << ")" << endl;
122.			}
123.			else//不是保留字的话就是标识符了
124.			{
125.				row++;
126.				insertId(arr);
127.				cout << arr << "\t\t(6," << arr << ")" << "\t\t标识符 " << "\t\t(" << line << "," << row << ")" << endl;
128.			}
129.		}
130.		else if (isNumber(ch))//第一个字符是数字的话
131.		{
132.			while (isNumber(ch) || (ch == '.'&&isNumber(fgetc(fpin))))//判断是否有小数的可能
133.			{
134.				arr = arr + ch;
135.				ch = fgetc(fpin);
136.			}
137.			if (isLetter(ch))//一旦遇到数字其实就是错误了，标识符不能以数字开头
138.			{
139.				while (isLetter(ch) || isNumber(ch))
140.				{
141.					arr = arr + ch;
142.					ch = fgetc(fpin);
143.				}
144.
145.				fseek(fpin, -1L, SEEK_CUR);
146.				row++;
147.				cout << arr << "\t\tError" << "\t\tError" << "\t\t(" << line << "," << row << ")" << endl;
148.			}
149.			else{
150.				insertNumber(arr);//插入刚刚得到的数字
151.				row++;
152.				fseek(fpin, -1L, SEEK_CUR);
153.				cout << arr << "\t\t(5," << arr << ")" << "\t\t常数 " << "\t\t(" << line << "," << row << ")" << endl;
154.
155.			}
156.
157.		}
158.		else
159.		{
160.			row++;
161.			arr = ch;
162.			switch (ch)
163.			{
164.			case'+':{
165.						ch = fgetc(fpin);
166.						if (ch == '(' || isNumber(ch) || isLetter(ch))
167.						{
168.							fseek(fpin, -1L, SEEK_CUR);
169.							cout << "+" << "\t\t( 3,+ )" << "\t\t算术运算符 " << "\t\t(" << line << "," << row << ")" << endl;
170.						}
171.						else
172.						{
173.							cout << arr + ch << "\t\tError" << "\t\tError" << "\t\t(" << line << "," << row << ")" << endl;
174.						}
175.						break;
176.			}
177.			case'-':{
178.						ch = fgetc(fpin);
179.						if (ch == '(' || isNumber(ch) || isLetter(ch))
180.						{
181.							fseek(fpin, -1L, SEEK_CUR);
182.							cout << '-' << "\t\t(3,-)" << "\t\t算术运算符" << "\t\t(" << line << "," << row << ")" << endl;
183.						}
184.						else
185.						{
186.							cout << arr + ch << "\t\tError" << "\t\tError" << "\t\t(" << line << "," << row << ")" << endl;
187.						}
188.						break;
189.			}
190.			case'*':cout << '/' << "\t\t(3,*)" << "\t\t算术运算符" << "\t\t(" << line << "," << row << ")" << endl;
191.			case'=':{
192.						ch = fgetc(fpin);
193.						if (ch == '=')
194.						{
195.							cout << "==" << "\t\t(4,==)" << "\t\t关系运算符 " << "\t\t(" << line << "," << row << ")" << endl;
196.						}
197.						else
198.						{
199.							fseek(fpin, -1L, SEEK_CUR);
200.							cout << "=" << "\t\t(4,=)" << "\t\t关系运算符 " << "\t\t(" << line << "," << row << ")" << endl;
201.						}
202.						break;
203.			}
204.			case'/':cout << '/' << "\t\t(3,/)" << "\t\t算术运算符" << "\t\t(" << line << "," << row << ")" << endl;
205.			case'(':
206.			case')':
207.			case'[':
208.			case']':
209.			case';':
210.			case'.':
211.			case',':
212.			case'{':
213.			case'}':cout << ch << "\t\t(2," << ch << ")" << "\t\t分界符 " << "\t\t(" << line << "," << row << ")" << endl;
214.				break;
215.			case'>':{
216.						ch = fgetc(fpin);
217.						if (ch == '=')
218.							cout << ">=" << "\t\t(4,>=)" << "\t\t关系运算符 " << "\t\t(" << line << "," << row << ")" << endl;
219.						else
220.						{
221.							cout << ">" << "\t\t(4,>)" << "\t\t关系运算符 " << "\t\t(" << line << "," << row << ")" << endl;
222.							fseek(fpin, -1L, SEEK_CUR);
223.						}
224.						break;
225.			}
226.			case'<':{
227.						ch = fgetc(fpin);
228.						if (ch == '=')
229.							cout << "<=" << "\t\t(4,<=)" << "\t\t关系运算符 " << "\t\t(" << line << "," << row << ")" << endl;
230.						else
231.						{
232.							cout << "<" << "\t\t(4,<)" << "\t\t关系运算符 " << "\t\t(" << line << "," << row << ")" << endl;
233.							fseek(fpin, -1L, SEEK_CUR);
234.						}
235.						break;
236.			}
237.			default:
238.				cout << ch << "\t\tError" << "\t\tError" << "\t\t(" << line << "," << row << ")" << endl;
239.				break;
240.
241.			}
242.		}
243.
244.	}
245.
246.}
247.int main()
248.{
249.	char in_fn[30]="cifafenxiqi.txt";
250.	FILE *fpin;
251.	cout << "请输入txt文件的绝对路径" << endl;
252.	for (;;){
253.		//cin >>in_fn;
254.		if ((fpin = fopen(in_fn, "r")) != NULL)
255.			break;
256.		else
257.			cout << "输入正确的路径" << endl;
258.	}
259.	analyse(fpin);
260.	fclose(fpin);
261.	return 0;
262.}
