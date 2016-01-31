//============================================================================
// Name        : $(baseName).cpp
// Author      : $(author)
// Version     :
// Copyright   : $(copyright)
// Description : Console C++ project with assembler proxy, Ansi-style
//============================================================================

#include "stdafx.h"
#include "$(baseName).h"

void usage(char* str);

using namespace std;

int main(int argc, char* argv[])
{
  int maxValue;
  int simdMaxValue;
  int numberCount;

  double faster;

  short * psRandomBuffer;
  short * psAlignBuffer;

  unsigned long long startTime;
  unsigned long long endTime;
  unsigned long long cppTime;
  unsigned long long simdTime;

  if (argc < 2) {
     usage(argv[0]);
  }

  numberCount = atoi(argv[1]);

  if (!(numberCount > 0)) {
     usage(argv[0]);
  }

  std::cout << "\nEntering Assembler project 101";
//
// SIMD assembler instructions need to access memory on 16 bytes aligned data chunk. Otherwise
// special unaligned assembler instructions need to be used which incur a big processing penalty.
//
  psRandomBuffer = new short [numberCount + (ALIGN_SIMD + sizeof(short) - 1) / sizeof(short)];
  psAlignBuffer  =  align_pointer<short *>(psRandomBuffer, ALIGN_SIMD);
//
// Generate random number. The simd assembler code use a signed maximum instruction and this
// code will generate unsigned short random number only. For this template that's fine.
//
  srand((unsigned int)time(NULL));

  for(int i=0; i < numberCount; i++) {
     psAlignBuffer[i] = std::rand();
  }

  maxValue = 0;
  startTime = cpuClock();   // Get the current time in cpu cycles
//
// Use a C++ standard algorithm to find the maximum value of a set of values. This is needed to
// test the assembler code as well as to compute how faster than C++ is the assembler code.
//
  for(int i=0; i < numberCount; i++) {
     maxValue = std::max(maxValue, (int)psAlignBuffer[i]);
  }
  endTime = cpuClock();
  cppTime = endTime - startTime;   // Compute the processing time in cpu cycles

  startTime = cpuClock();
//
// This is the call to our main assembler routine to find the maximum value. Note that
// the assembler code could be written using C intrinsics macro and would probably yield
// a computing time almost as fast as the pure assembler code.
//
  simdMaxValue = (int) findMax(psAlignBuffer, numberCount);

  endTime = cpuClock();
  simdTime = endTime - startTime;

  if( maxValue != simdMaxValue) {
     std::cout << "\nAssembler debugging is difficult. \nInternal error : " << "maxValue != simdMaxValue" << std::endl;
     exit(1);
  }
  faster =  (double)cppTime / (double)simdTime;

  std::cout << "\nAssembler coding is fun. \nIt is " << faster << " faster than C++ to find a maximum value." << std::endl;

  delete psRandomBuffer;
  psRandomBuffer = NULL;

  return 0;
}

void usage(char* str)
{
   std::cout << "Usage: " << str << " count" << std::endl;
   std::cout << "  With a non-zero, positive count - Suggested count of 10,000" << std::endl;

   exit(1);
}

