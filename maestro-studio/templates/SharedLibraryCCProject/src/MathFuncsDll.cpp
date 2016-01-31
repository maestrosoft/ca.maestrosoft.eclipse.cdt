//============================================================================
// Name        : MathFuncsDll.cpp
// Author      : $(author)
// Version     :
// Copyright   : $(copyright)
// Description : Shared Library in C++, Ansi-style
//============================================================================

#include "stdafx.h"
#include <stdexcept>

using namespace std;

namespace MathFuncs
{
	SHARED_LIB_API double MyMathFuncs::Add(double a, double b)
    {
        return a + b;
    }

	SHARED_LIB_API double MyMathFuncs::Subtract(double a, double b)
    {
        return a - b;
    }

	SHARED_LIB_API double MyMathFuncs::Multiply(double a, double b)
    {
        return a * b;
    }

	SHARED_LIB_API double MyMathFuncs::Divide(double a, double b)
    {
        if (b == 0)
        {
            throw invalid_argument("b cannot be zero!");
        }

        return a / b;
    }
}
