#pragma once

extern "C" 
{ 
   short findMax(short * buffer, int size);
   unsigned long long cpuClock();
}

#define DEFAULT_ALIGN_VALUE 8
#define ALIGN_SIMD 16

template<class T> inline T align_pointer(void *pv, size_t lAlignValue = DEFAULT_ALIGN_VALUE)
{
   return (T)((((size_t) (pv)) + (lAlignValue - 1)) & ~(lAlignValue - 1));
}

