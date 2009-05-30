Stack segment para Stack
dw 100h dup(?);
Stack ends

data segment
const_number dw ?;
num dw ?;
function dw ?;
a dw ?;
b dw ?;
e dw ?;
i dw ?;
str24 db 'a*b=$';
str26 db 'num=$';
str28 db 'funtion1()=$';
prev_len EQU 100;
temp_len EQU 10;
prev dw ?;
dw prev_len dup(?);
temp_prev dw ?;
dw temp_len dup(?);
display db 'this is an example of simple compiler by zhouzhengxi 36060320.$'
data ends

code segment
      assume cs:code,ds:data,ss:Stack

function_pro proc
      mov ax,temp_prev+3;
      mov prev+2,ax;为prev+2赋值为temp_prev+3
      mov ax,prev+2;
      mov function,ax;为function赋值为prev+2
      ret
function_pro endp

main proc far
start:push ds;
      mov ax,0;
      push ax;
      mov ax,data;
      mov ds,ax;

      mov ax,2;
      mov a,ax;为a赋值为2
      mov ax,6;
      mov b,ax;为b赋值为6
      mov ax,1;
      mov i,ax;为i赋值为1
      mov ax,a;
      mov cx,b;
      mul cx;
      mov bx,ax;做乘法赋值给bx
      mov ax,bx;
      mov e,ax;为e赋值为bx
      mov ax,0;
      mov num,ax;为num赋值为0
lab13:
      mov ax,i;
      sub ax,3;
      mov dx,ax;做减法赋值给dx
      mov ax,dx;
      cmp ax,0;
      jne  lab17;跳转至17
lab15:
      mov ax,0;
      mov dx,ax;为dx赋值为0
      jmp lab18;跳转至18
lab17:
      mov ax,1;
      mov dx,ax;为dx赋值为1
lab18:
      mov ax,dx;
      cmp ax,0;
      jne  lab20;跳转至20
lab19:
      jmp lab25;跳转至25
lab20:
      mov ax,num;
      add ax,i;
      mov bx,ax;做加法赋值给bx
      mov ax,bx;
      mov num,ax;为num赋值为bx
      mov ax,i;
      add ax,1;
      mov dx,ax;做加法赋值给dx
      mov ax,dx;
      mov i,ax;为i赋值为dx
      jmp lab13;跳转至13
lab25:
      mov dx,offset str24;
      mov ah,09h;
      int 21h;输出字符串变量str24
      mov ax,e;
      mov bx,10;
      mov cx,5;
      call PrintfNum;输出算术表达式子结果
      mov dx,offset str26;
      mov ah,09h;
      int 21h;输出字符串变量str26
      mov ax,num;
      mov bx,10;
      mov cx,5;
      call PrintfNum;输出算术表达式子结果
      mov dx,offset str28;
      mov ah,09h;
      int 21h;输出字符串变量str28
      mov ax,14;
      mov temp_prev+3,ax;为temp_prev+3赋值为14
      call function_pro;
      mov ax,function;
      mov bx,10;
      mov cx,5;
      call PrintfNum;输出算术表达式子结果
lab71:
      mov ah,9h;
      mov dx,offset display;
      int 21h;
      mov ax,4c00h;
      int 21h;
main endp

PrintfNum  proc
LP1:  xor  dx,dx;
      div  bx;
      or   dl,30h;
      push dx;
      loop LP1;
      mov  cx,5;
LP2:  pop  dx;
      mov  ah,2;
      int  21h;
      loop LP2;
      ret;
PrintfNum endp;输出算术表达式

code ends
end start
