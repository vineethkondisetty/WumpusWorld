
    CSE 5360- Artificial Intellgence-I							
    Name: Venkata Naga Satya Sai Vineeth Kondisetty					      
    Student ID: 1001772021								
    Assignment 3: Wumpus World Problem (Propositional Logic)	
    Programming Language Used : JAVA

Code Structure: 2 Classes
		1) CheckTrueFalse
		2) LogicalExpression
				

******************************* How to run code? *****************************************
	# Compilation : javac *.java
	# Execution : java CheckTrueFalse wumpus_rules.txt [knowledge-base] [statement]
		      For Example : java CheckTrueFalse wumpus_rules.txt knowledgebase.txt statement.txt

****************************Design of our Knowledge Base**********************************
	
		[1,1] : No monster, No pit, No stench, No breeze
		[1,2] : No monster, No pit, No stench, No breeze
		[1,3] : No monster, No pit, No stench, breeze
		[1,4] : No monster, pit, No stench, No breeze
		[2,1] : No monster, No pit, No stench, No breeze
		[2,2] : No monster, No pit, No stench, breeze
		[2,3] : No monster, No pit, No stench, No breeze
		[2,4] : No monster, No pit, No stench, breeze
		[3,1] : No monster, No pit, No stench, breeze
		[3,2] : No monster, pit, No stench, No breeze
		[3,3] : No monster, No pit, No stench, breeze
		[3,4] : No monster, No pit, stench, No breeze
		[4,1] : No monster, No pit, No stench, No breeze
		[4,2] : No monster, No pit, No stench, breeze
		[4,3] : No monster, No pit, stench, No breeze
		[4,4] : Monster, No pit, No stench, No breeze
