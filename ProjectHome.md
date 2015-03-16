The goal is to create a program that generates code, sequence of instructions that form a function that lasts a specified amount of time. All the instructions must be valid for the 8085 mP.<br>
<br>
Sample generated code:<br>
Trying to make a function with duration in the intervall [1403773903,1403773903], with the following elements: { 123 memory, 124 available memory, A register, H register, L register, D register, E register } , return  required.<br>
Resulting code:<br>
<br>
ADC A <br>
LXI D,7<br>
loop1:MVI A,17<br>
loop2:MVI H,175<br>
loop3:MVI H,23<br>
loop4:MVI H,208<br>
loop5:DCR H<br>
JNZ loop5<br>
DCR H<br>
JNZ loop4<br>
DCR H<br>
JNZ loop3<br>
DCR A<br>
JNZ loop2<br>
DCX D<br>
JNZ loop1<br>
RET<br>
<br>
Lasting:1403773903 states<br>