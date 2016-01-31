#ifndef MATHFUNCS_DLL_H
#define MATHFUNCS_DLL_H

// MathFuncsDll.h

namespace MathFuncs
{
    // This class is exported from the MathFuncsDll.dll
    class MyMathFuncs
    {
    public: 
        // Returns a + b
        static SHARED_LIB_API double Add(double a, double b);

        // Returns a - b
        static SHARED_LIB_API double Subtract(double a, double b);

        // Returns a * b
        static SHARED_LIB_API double Multiply(double a, double b);

        // Returns a / b
        // Throws const std::invalid_argument& if b is 0
        static SHARED_LIB_API double Divide(double a, double b);
    };
}

#endif
