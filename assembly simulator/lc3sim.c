//
//  lc3sim.c
//  
//
//  author: Eddie Ferguson
//  
// The file provides an assembler for the major components of the
// lc-3 architecture. There are arrays to represent the memory and registers.
// We use variables to represent the IR, PC, and condition code register. 
// The code simulates the fetch-execute cycle by setting the IR equal to
// the PC and then incrementing the PC itself. The decode phase is then entered
// and, finally, the execute phase is handled by individual functions. 

#include <stdio.h>
#include <stdlib.h>

short MEMORY[65536]; //our lc3 memory: each instruction is 16 bits
short generalRegisters[8]; //an array representing the general registers R0 through R7


unsigned short PC  = 0x100;    //the program counter :contains the address of the next instruction; it is initialized to 0x100


unsigned short IR;    //the instruction register: contains the current instruction
unsigned short STATUSFLAG; //the flag register



//////////function declarations//////////
void load(char *filename);
void instructionCycle();
void fetch();
void decode();

void add ( short instruction );
void and ( short instruction );
void br  ( short instruction );
void jmp ( short instruction );
void jsr ( short instruction );
void ld  ( short instruction );
void ldr ( short instruction );
void lea ( short instruction );
void not ( short instruction );
void ret ( short instruction );
void st  ( short instruction );
void str ( short instruction );
void trap( short instruction );

//trap subroutines
void putChars();
void halt();
void getInt();
void decOut();
void hexOut();

void printRegisters();
void printMemory();

/////////////////////////////////////////


int main( int argc, char *argv[] ){
   
    char *filename;
    
    if ( argc != 2 ){
		printf("Argc: %d\n", argc);
        
        fprintf(stderr, "A file has not been supplied\n");
		exit( 1 );
	}	
    
    filename = argv[1];
    
    
    load(filename);     //load the contents of the file into memory
    
    instructionCycle(); //this first function call represents the start of the
                        //fetch-execute cycle
	return 0;

} 

void load( char *filename ){
    
    FILE *infile;
	infile = fopen(filename, "r");
    
    int curNdx = 256;
    int hexValue;
    //short value;
    
    int done = 0;
    while ( !done ) {
        fscanf(infile, "%x", &hexValue);
        MEMORY[curNdx] = hexValue;
        curNdx++;
        
        done = (int) feof( infile );
    }
   
}



void instructionCycle(){
    
    
    //we will continue the fetch-execute cycle until the HALT instruction
    //is encountered, which stops the program and computer.  The handling
    //of this instruction will happen in the decode phase
    while (1) {
        fetch();
        decode();
        //the execute phase is carried out from the decode() function
    }
    
}


/**
 * In the fetch phase of the instruction cycle, we load the current value
 * into the instruction register and increment the PC
 */

void fetch(){
	
	IR = PC;
    PC++;   
}


void decode(){

	//////////the instructions//////////
	int ADD = 1;
	int AND = 5;

	int NOTBR = 15; //since the BR opcode is 0000 we will perform the bitwise
	//and operation with 1111000000000000 to determine if all
	//bits are turned off as opposed to on. 

	int JMP  = 12;
	int JSR  = 4;
	int LD   = 2;
	int LDR  = 6;
	int LEA  = 14;
	int NOT  = 9;
	int RET  = 12;
	int ST   = 3;
	int STR  = 7;
	int TRAP = 15;

    
    short instruction = MEMORY[IR];
   	unsigned int opcode = ( instruction & 65535 )  >> 12; 
    
    if (opcode == ADD ) 
        add(instruction);
    
    
    else if ( opcode == AND ) 
        and(instruction);
    
    
    else if( opcode == 0 )  //BR opcode == 0000
        br(instruction);
    
    
    else if ( opcode == JMP ) {
    	
    	if ( ( instruction & 448 ) == 448)
    		ret(instruction);
    	else
    		jmp(instruction);
        
    }
    
    else if ( opcode == JSR ) 
    	jsr(instruction);
    
    
    else if ( opcode == LD ) 
    	ld(instruction);
    
    else if ( opcode == LDR )
    	ldr(instruction);
    
    else if ( opcode == LEA ) 
    	lea(instruction);
    
    else if ( opcode == NOT )	
        not(instruction);
    
    else if ( opcode == RET )
    	ret(instruction);
    
    
    else if( ( opcode & ST ) == opcode)
    	st(instruction);
    
    else if ( opcode == STR )
    	str(instruction);
    
    
    else if ( opcode == TRAP )
   		trap(instruction);
    
    
    else{ //we have an invalid instruction
        
        fprintf(stderr, "Error!! Invalid Instruction: 0x%x\n", instruction);
        exit(1);
    }
    
    
}


/**
 * The function performs the add operation. Given the instruction, it first determines
 * whether the register or immediate addressing mode is being used.  It then performs the operation 
 * and stores the result in the detination register.
 */
void add (short instruction){
    
     
    int immediateFlag = 32;//the immediate flag takes up the sixth bit
    
    unsigned int destRegister;
    unsigned int sourceRegister1;
    unsigned int sourceRegister2;
    
    short immediateValue;
    
    
    destRegister = ( instruction & 3584 ) >> 9; //the destination register starts
                                                //at the 10th bit
    sourceRegister1 = ( instruction & 448 ) >> 6;
     
    if ( instruction & immediateFlag ){ //if the immediate flag is turned on
        
        if (instruction & 16) // if the offset is negative
        	immediateValue = instruction | 65504;
        else
        	immediateValue = instruction & 31;
        	
        generalRegisters[destRegister] = generalRegisters[sourceRegister1] + immediateValue; 
       
    }
    else{ //we are dealing with two source registers
        
        sourceRegister2 = instruction & 7;
        generalRegisters[destRegister] = generalRegisters[sourceRegister1] + generalRegisters[sourceRegister2]; //store the value in the register
        
    }
    
    
    
    //final step: modifying the condition code
    
    if ( generalRegisters[destRegister] < 0 ){
        STATUSFLAG = 1; //2^0
    }
    else if ( generalRegisters[destRegister] == 0 ){
        STATUSFLAG = 2; //2^1
    }
    else if ( generalRegisters[destRegister] > 0 ){
        STATUSFLAG = 4;//2^2
    }
    
}




/**
 * The function performs the bitwise and operation. Given the instruction, it first determines
 * whether the register or immediate addressing mode is being used.  It then performs the operation 
 * and stores the result in the detination register.
 */
void and(short instruction){
    
    
    int immediateFlag = 32;//the immediate flag takes up the sixth bit
    
    unsigned int destRegister;
    unsigned int sourceRegister1;
    unsigned int sourceRegister2;
    
    short immediateValue;

    destRegister = ( instruction & 3584 ) >> 9; //the destination register starts
                                                //at the 10th bit
    
    sourceRegister1 = ( instruction & 448 ) >> 6;
    
    
    if ( instruction & immediateFlag ){ //if the immediate flag is turned on
        
        if (instruction & 16) // if the offset is negative
        	immediateValue = instruction | 65504;
        else
        	immediateValue = instruction & 31;
    }
    else { //we are dealing with two source registers
        
        
        //7 is IS THE MEMORY LOCATION
        
        sourceRegister2 = instruction & 7;
        generalRegisters[destRegister] = generalRegisters[sourceRegister1] & generalRegisters[sourceRegister2];
    }
    
    
    
    //final step: modifying the condition code
    
    if ( generalRegisters[destRegister] < 0 ){
    	
        STATUSFLAG = 1; //2^0
    }
    else if ( generalRegisters[destRegister] == 0 ){
        STATUSFLAG = 2; //2^1
    }
    else if ( generalRegisters[destRegister] > 0 ){
        STATUSFLAG = 4;//2^2
    }
    
    
}//end function



void br( short instruction ){
    
    //our first task is to see which condition flags are turned on. Our method
    //will be to total the flags that are turned on recognizing that we have
    //7 distinct outcomes
    
    unsigned short flags = instruction >> 9; //since the first four bits of the BR instruction are 
                                           //all zero, we can simply truncate the bits
    
    short PCoffset;
   
    if (instruction & 256){ //if the offset is negative
    	PCoffset = instruction  | 65024 ; 
    	}
    else
    	PCoffset = instruction & 511; 
    
    
    //we now have to reverse the flags since the project specs have a different
    //ordering of the condition codes. We will have to reverse the positive and 
    //negative flag positions. Zero is always in the middle
    
    
    if ( ( (flags & 4) == 4) && ( (flags & 1) != 1) ){ //we are saying if the negative bit is turned on and the positive bit is turned offprintf
    	flags -= 4;
    	flags += 1;
    }
    else if( ( (flags & 1) == 1) && ((flags & 4) != 4)){
    	flags -= 1;
    	flags += 4;
    }
    
    
   
   
    
    if ( (flags != 0) && ( ( flags & STATUSFLAG ) == 0) ) //we are saying if we don't have an unconditional branch and none
    	return;                                           //of our flags correspond to the current flag
    
    
    //if we make it to this point we will perform the branch with the knowledge that the PC has already been incremented in 
    //the FETCH phase
    
    
    PC = PC + PCoffset;
}


/**
 * The function for the JMP command. We must jump to an address that is 
 * stored in a register as indicated by bits [8:6]
 */
void jmp ( short instruction ){
    
    
    if ( (instruction & 3584) != 0){
    	fprintf(stderr, "Incorrect instruction\n");
		exit( 1 );
	}
    
    if  ( ( instruction & 63 ) != 0 ){
    	fprintf(stderr, "Incorrect instruction\n");
		exit( 1 );
	}
    
     
    
    //we need to find the register. 
    
    unsigned int jmpRegister = (instruction & 511) >> 6; //49152 = 1100 0000 0000 0000
    unsigned int address = generalRegisters[jmpRegister];
    
    PC = address; 
}


/**
 * The function for the JSR instruction. The JSR instruction performs two 
 * things: first it saves the contents of the PC (which was just incremented in 
 * fetch) in R7. We need this value in order to return to the particular address that 
 * contains the instruction after the JSR command was called. Then it loads the 
 * contents of the specified address into the PC.
 */
void jsr ( short instruction ){
    
    
    short PCoffset;
    
    //step 1: save the current PC in R7
    generalRegisters[7] = PC;
    
  
    //step 2: set the PC to the value in the PC offset. 
    
    if ( instruction & 1024 ) // if the value is negative
    	PCoffset = instruction | 63488;
    else
    	PCoffset = instruction & 2047; // 2047 = 0000 0111 1111 1111
    	
    PC = PC + PCoffset;
}

/**
 * Implementation of the LD instruction. When performing loads, we are taking values from memory and 
 * storing them in a register. For this particular instruction, we need a destination
 * register and a PC offset. We must also modify the condition code.
 */
void ld ( short instruction ){
    
    //first we need the register. We can simply subtract the value of the opcode 0100 ( which here is equal to
    //2^13 = 8192) and then perform a rightward bit shift of 9
    unsigned int destRegister = ( instruction & 3584 ) >> 9;
    
    short PCoffset;
    //now we need the offset. In order to find this we will simply perform the AND operation with only the first
    //9 bits turned on (this equals 512) if the offset is positive. If it is negative we must pad to the left with 
    //1s
  
  
    if ( instruction & 256 ) //if the offset is negative
    	PCoffset = instruction | 65024;
    else
    	PCoffset = instruction & 511; // 511 = 0000 000 111111111
    
    generalRegisters[destRegister] = MEMORY[PC + PCoffset];
    
    
    //final step: modifying the condition code
    if (generalRegisters[destRegister] < 0){
        STATUSFLAG = 1; //2^0
    }
    else if(generalRegisters[destRegister] == 0){
        STATUSFLAG = 2; //2^1
    }
    else if(generalRegisters[destRegister] > 0){
        STATUSFLAG = 4;//2^2
    }

    
}


/**
 * The LDR instruction is nearly identical to the LD instruction except that we do 
 * not perform an offset based on the PC, but rather a base register. Our final 
 * step is to modify the condition code.
 */
void ldr ( short instruction ){
    
    
    //step 1: find the destination register
    unsigned int destRegister = ( instruction & 3584 ) >> 9; // 3584 =  0000 111 000 000000
     
    //step 2: find the base register
    unsigned int baseRegister = ( instruction & 448 ) >> 6; // 448 = 0000 000 111 000000
    
    //step 3: find the 6 bit offset
    int offset; 
    
    if ( instruction & 32 )
    	offset = instruction | 65472;
    else
    	offset = instruction & 63; // 0000 000 000 111111
    
    //now we must load the value in the base register and add it to our offset 
    //to get the memory address of the item we want loaded into our destination
    //register
   
    
    unsigned int address = generalRegisters[baseRegister] + offset; 
    
    generalRegisters[destRegister] = MEMORY[address];
    
    //final step: modifying the condition code
    if (generalRegisters[destRegister] < 0){
        STATUSFLAG = 1; //2^0
    }
    else if(generalRegisters[destRegister] == 0){
        STATUSFLAG = 2; //2^1
    }
    else if(generalRegisters[destRegister] > 0){
        STATUSFLAG = 4;//2^2
    }

        
}



/**
 * The function for the LEA instruction. This particular instruction 
 * does not require memory access as the other load operation do. We
 * will load the destination register with the value derived from adding 
 * offset to the incremented PC. Like the other load operations, we must
 * modify the condition code.
 */
void lea ( short instruction ){
    
    
    //step 1: find the destination register
    unsigned int destRegister = ( instruction & 3584 ) >> 9;
    
    //step2: find the PC offset
    int PCoffset;
    
    if ( instruction & 256 )
    	PCoffset = instruction | 65024;
    else
    	PCoffset = instruction & 511;
    
    generalRegisters[destRegister] = PC + PCoffset;
    
    //final step: modifying the condition code
    if (generalRegisters[destRegister] < 0){
        STATUSFLAG = 1; //2^0
    }
    else if(generalRegisters[destRegister] == 0){
        STATUSFLAG = 2; //2^1
    }
    else if(generalRegisters[destRegister] > 0){
        STATUSFLAG = 4;//2^2
    }

    
    
    //final step: modifying the condition code
    if (generalRegisters[destRegister] < 0){
        STATUSFLAG = 1; //2^0
    }
    else if(generalRegisters[destRegister] == 0){
        STATUSFLAG = 2; //2^1
    }
    else if(generalRegisters[destRegister] > 0){
        STATUSFLAG = 4;//2^2
    }
    
    
}



/**
 * The function performs the bitwise not operation given a destination register and a source register
 */
void not ( short instruction ){
    
    
    //for the NOT instruction we only need a destination register and a source register
    
    int destRegister;
    int sourceRegister;
    
    int first6Bits = 63;
    
    destRegister    = ( instruction & 3584 ) >> 9; // we are isolating the destination register and our source register 
    sourceRegister  = ( instruction & 448 ) >> 6; // by removing the trailing bits from each one
    
    //the first six bits should all be set to 1
    if ( ( instruction & first6Bits ) == first6Bits){
        
        generalRegisters[destRegister] = !(sourceRegister);
    }
    
    //set the condition
    
    
    if (generalRegisters[destRegister] < 0){
        STATUSFLAG = 1; //2^0
    }
    else if(generalRegisters[destRegister] == 0){
        STATUSFLAG = 2; //2^1
    }
    else if(generalRegisters[destRegister] > 0){
        STATUSFLAG = 4;//2^2
    }

    
    
    
}



/**
 * The function for the RET instruction. The RET instruction is a special derivative
 * of the JMP instruction in that we only ever load the contents of R7 into the PC. This 
 * value should be written by the JSR instruction.
 */
void ret ( short instruction ){
    
    
    if ( (instruction & 3584) != 0){
    	fprintf(stderr, "Incorrect instruction\n");
		exit( 1 );
	}
    
    if  ( ( instruction & 63 ) != 0 ){
    	fprintf(stderr, "Incorrect instruction\n");
		exit( 1 );
	}
    
    int address = generalRegisters[7];
    PC = address;
   
}


/**
 * The ST instruction. We are storing a value in memory
 * based upon an offset relative to the PC
 */
void st(short instruction){
  	
    //step 1: finding the source register
    unsigned int sourceRegister = ( instruction & 3584 ) >> 9; //0000 111 000000000
    
    //step 2: find the offset
    int PCoffset;
    
    if ( instruction & 256 )
    	PCoffset = instruction | 65024;
    else
    	PCoffset = instruction & 511; //0000 0001 1111 1111
    
    int value = generalRegisters[sourceRegister]; //the value to be stored
    
    MEMORY[PC + PCoffset] = value;
    
}


/**
 * The STR instruction. Our approach will be similar to the one
 * found in the ST function, except we are now using addressing relative
 * to some value in a base register plus an offset.
 */
void str ( short instruction ){
    
    
    //step 1: finding the source register
    unsigned int sourceRegister = ( instruction & 3584 ) >> 9; //0000 111 000000000
    
    
    //step 2: find the base register
    unsigned int baseRegister = ( instruction & 448 ) >> 6; //0000 000 111 000000
    
    
    
    //step 3: find the 6 bit offset
    int offset;
    
    if ( instruction & 32 )
    	offset = instruction | 65472;
    else
    	offset = instruction & 63; // 0000 000 000 111111
    
    //we must get the value contained in the base register
    unsigned int value = generalRegisters[baseRegister] + offset;
    
    MEMORY[value] = generalRegisters[sourceRegister];
    
}



/**
 * The trap instruction for service routines. We will be supporting
 * 5 system calls: 
 *                -printing an ascii string to the terminal 
 *                -halting the program
 *                -extracting an integer value and storing it in R0
 *                -printing an integer value in decimal format that is stored in R0
 *                -printing an integer value in hex format that is stored in R0
 */
void trap ( short instruction ){

	if ( ( instruction & 3840) != 0){
		fprintf(stderr, "Incorrect instruction\n");
		exit( 1 );
	}
    
   

	unsigned int trapInstruction = instruction & 255; 
	
	if ( trapInstruction == 0x22 )
		putChars();
	
	else if ( trapInstruction == 0x25 )
		halt();
	
	else if ( trapInstruction == 0x41 )
		getInt();	
	
	else if ( trapInstruction == 0x42 ){
		decOut();
	}
	else if (trapInstruction == 0x43 )
		hexOut();
}


/////////////////////TRAP SERVICE CALLS/////////////////////

/**
 * A function for the puts service routine. We will print a null-terminated 
 * string of ascii characters, with each single character being stored at 
 * a single memory location. The starting address will be contained in R0
 */
void putChars(){

	unsigned int startingAddress = generalRegisters[0]; 
	unsigned int curAddress = startingAddress;
	
	int currentChar = MEMORY[startingAddress];
	
	
	
	int theCounter = 0;
	
	while ( currentChar != 0){
		theCounter++;
		printf("%c", currentChar);
		
		//now find the next character 
		curAddress++;
		currentChar = MEMORY[curAddress];
	}
	printf("\n");
	
}



/**
 * The halt service routine. It should simulate
 * a halt of the program and computer
 */
void halt(){
	
	printRegisters();
	//printMemory();
	fprintf(stderr, "Simulator halted\n");
	exit(1);		
}


/**
 * The service routine for extracting an integer value.
 * We will store the value in R0.
 */
void getInt(){

	short value;
	printf("Please enter a number: \n");
	scanf("%hi", &value);
	
	generalRegisters[0] = value;

}


/**
 * The service routine for printing a decimal integer
 * to standard output.
 */
void decOut(){

	int printValue = generalRegisters[0]; //the value to be printed is in R0
	printf("The decimal value: %d\n", printValue);
	


/**
 * The service routine for printing a hexadecimal integer
 * to standard output.
 */
void hexOut(){

	int printValue = generalRegisters[0]; //the value to be printed is in R0
	printf("The hexadecimal value: %x\n", printValue);	
}


////////////////////////////////////////////////////////////





/**
 * A convenience function that prints the registers. We will
 * print the registers in both hex and decimal notation.
 */
void printRegisters(){

	int i;
	printf("The registers in DECIMAL notation: \n");
	for(i = 0; i < 9; i++){
		printf("\tRegister %d: %d\n", i, generalRegisters[i]);
	}

	printf("\nThe registers in HEX notation: \n");
	for(i = 0; i < 9; i++){
		printf("\tRegister %d: %x\n", i, generalRegisters[i]);
	}
	
    
}


/**
 * Another convenience function that prints all
 * of the memory locations up to the last value
 * of the PC
 */

void printMemory(){
	
	int i = 0x100;
	int pseudo = 0;
	while ( pseudo < 100 ) {
		
		printf("Memory location %x contains: %x\n", i, MEMORY[i]);
		i++;
		pseudo++;
	}
	
}


// end of program