// Shows how to run three Steppers at once with varying speeds
//
// Requires the Adafruit_Motorshield v2 library 
//   https://github.com/adafruit/Adafruit_Motor_Shield_V2_Library
// And AccelStepper with AFMotor support 
//   https://github.com/adafruit/AccelStepper

// This tutorial is for Adafruit Motorshield v2 only!
// Will not work with v1 shields

// ##### RING BUFFER ###### //
struct Instruction {
  byte lsteps, rsteps;
};

Instruction zeroInstruction() {
  return (Instruction) { 0,0 };
}

#define MEM_CAPACITY_BYTES 2000 
const int INSTRUC_SIZE = sizeof(Instruction);
const int RING_SIZE= (2000 / 2) / INSTRUC_SIZE;
int start = 0;
int length = 0;
Instruction instructions[RING_SIZE];

boolean bufferFull() { return length == RING_SIZE - 1; }
boolean dataAvailable() { return length > 0; }
int freeSpace() { return RING_SIZE - length; }

void enqueue(Instruction ins) {
  int index = start + length;
  if (index >= RING_SIZE) {
    index -= RING_SIZE;
  }
  instructions[index] = ins;
  length++;
  if (length == RING_SIZE) {
    length = RING_SIZE - 1;
  }
}
Instruction dequeue() {
  if (length == 0) {
    return zeroInstruction();
  } 
  Instruction result = instructions[start];
  start++;
  if (start == RING_SIZE) start = 0;
  length--;
  return result;
}

int convertFromUByte(int ubyte) {
  if (ubyte > 127) {
    ubyte -= 256; 
  }
  return ubyte;
}
//Instruction building
int field_index = 0;
const int num_fields = 2;
int inProgressInstruction[num_fields];
int test_val;

void addToInstructionInProgress(byte b) {
   inProgressInstruction[field_index++] = b;
  
  if (field_index == num_fields) {
      enqueue((Instruction) { 
           inProgressInstruction[0], inProgressInstruction[1], 
         }); 
     field_index = 0;
  }  
}
// ##### RING BUFFER ###### //

#include <AccelStepper.h>
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"

//const uint8_t STEP_TYPE=DOUBLE;
const uint8_t STEP_TYPE=INTERLEAVE;
//const uint8_t STEP_TYPE=SINGLE;
//const uint8_t STEP_TYPE=MICROSTEP;

const int SHEILD_FREQUENCY=800; // 12500 for micro (whines otherwise)

/* VALUES MUST MATCH JAVA CODE SETTINGS */
const float MAX_SPEED_STEPS_PER_SECOND=120.0;
const float ACCELERATION_STEPS_PER_SECOND=100.0;
const unsigned long TIME_SLICE_US = 2048 * 2; 

int sentinel = 0;

int leftMove = 0;
int rightMove = 0;

float lspeed = 0.0;
float rspeed = 0.0;



const int MAX_SERIAL_BYTES = INSTRUC_SIZE * 6;

byte DATA_REQUEST_SENTINEL='#'; //35

int artificial_wait = 0;

unsigned long micro_count = 0;
unsigned long slice_start_time = 0;

int instructionCount = 0;

Adafruit_MotorShield AFMSbot(0x61); // Rightmost jumper closed
Adafruit_MotorShield AFMStop(0x60); // Default address, no jumpers

// Connect two steppers with 200 steps per revolution (1.8 degree)
// to the top shield
Adafruit_StepperMotor *mystepperLeft = AFMStop.getStepper(200, 2);
Adafruit_StepperMotor *mystepperRight = AFMStop.getStepper(200, 1);

// you can change these to DOUBLE or INTERLEAVE or MICROSTEP!
// wrappers for the first motor!
void forwardstep1() {  
  mystepperLeft->onestep(FORWARD, STEP_TYPE);
}
void backwardstep1() {  
  mystepperLeft->onestep(BACKWARD, STEP_TYPE);
}
// wrappers for the second motor!
void forwardstep2() {  
  mystepperRight->onestep(FORWARD, STEP_TYPE);
}
void backwardstep2() {  
  mystepperRight->onestep(BACKWARD, STEP_TYPE);
}

// Now we'll wrap the 2 steppers in an AccelStepper object
AccelStepper stepperLeft(forwardstep1, backwardstep1);
AccelStepper stepperRight(forwardstep2, backwardstep2);

int testl = 0;
int testr = 0;
void setup()
{ 
  //courtesy adafruit faq:  
  TWBR = ((F_CPU /400000l) - 16) / 2; // Change the i2c clock to 400KHz

  if (convertFromUByte(0) != 0) return; //TEST
  Serial.begin(9600);
  while (! Serial);

  while(Serial.available()) Serial.read();

  AFMStop.begin(SHEILD_FREQUENCY); // Start the top shield // default 1.6KHz

  stepperLeft.setMaxSpeed(MAX_SPEED_STEPS_PER_SECOND);
  stepperLeft.setAcceleration(ACCELERATION_STEPS_PER_SECOND);

  stepperRight.setMaxSpeed(MAX_SPEED_STEPS_PER_SECOND);
  stepperRight.setAcceleration(ACCELERATION_STEPS_PER_SECOND);

  establishContact();  // send a byte to establish contact until receiver responds 
  //  fillBuffer(); //TEST ******

  while(!bufferFull()) {
     int ser_available = Serial.available();
     if (ser_available <= MAX_SERIAL_BYTES * .5) {
       if (artificial_wait == 0) {
         Serial.print(Serial.available());
         Serial.write('^');
         Serial.print(field_index);
         Serial.write('^');        

         Serial.println();
         artificial_wait = 390; //2390 TEST // num feels good!
       } 
       else artificial_wait--; 
     } 
     if (ser_available > 0) {
       addToInstructionInProgress(Serial.read()); 
      Serial.print(length);
     //      addToInstructionInProgress(6); 
     }
  } 
}


#define DEBUG
#ifdef DEBUG
float debugSpeedLeft = 0, debugSpeedRight = 0;
#endif
void requestMoreData() {
  //  Serial.write(DATA_REQUEST_SENTINEL);
  //  Serial.write(data_count);
  //  Serial.write(freeSpace());
#ifdef DEBUG
  Serial.print(Serial.available());
  //  Serial.print(debugSpeedLeft);
  Serial.write('#');
  Serial.print(testl);  
  //  Serial.write(':');
  //  Serial.print(debugSpeedRight);
  Serial.write('#');
  Serial.print(testr);
  Serial.write('@');
  Serial.print(length);
#endif
  Serial.println();
  //  delayMicroseconds(20);
}
void fillBuffer() {
  while(!bufferFull()) {
    getOrAskForData(790);
  } 
}

void getOrAskForData(int wait) {
  int ser_available = Serial.available();
  // when to request more data 
  if (ser_available <= MAX_SERIAL_BYTES * .5) {
    if (artificial_wait == 0) {
      requestMoreData(); 
      artificial_wait = wait; // num feels good!
    } 
    else artificial_wait--; 
  } 
  //  else {
  //    artificial_wait = 390;
  //  }
  if (ser_available > 0) {
    addToInstructionInProgress(Serial.read()); 
  }
}

void loop()
{
  if (!bufferFull()) {  
    getOrAskForData(490); ////////******
  }

  micro_count = micros();
  if (micro_count < slice_start_time) { //protect against 70 minute overflow
    slice_start_time = 0;
  }

  if (stepperLeft.distanceToGo() == 0 && stepperRight.distanceToGo() == 0)
  {
    test_val = length; //DEBG
    if (dataAvailable()) { 
      Instruction instruction = dequeue();  
      leftMove =convertFromUByte( instruction.lsteps); 
      rightMove =convertFromUByte( instruction.rsteps);
//      stepperLeft.move(leftMove );
//      stepperRight.move(rightMove ); //this causes a recalc of speed 
testl = leftMove; testr = rightMove;
      stepperLeft.moveTo(stepperLeft.currentPosition() + leftMove );
      stepperRight.moveTo(stepperRight.currentPosition() + rightMove ); //this causes a recalc of speed 
    } 
    else { // no data. should probably sit tight.
      stepperLeft.move(0);
      stepperRight.move(0);
    }
  }
  stepperLeft.run();
  stepperRight.run();
}

void establishContact() {
  while (Serial.available() <= 0) {
    sendHiMessage();
    delay(300);
  }
  delay(100);
}
void sendHiMessage() {
  Serial.println("HI"); 
}














