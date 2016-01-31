// The following ifdef block is the standard way of creating macros which make exporting
// from a DLL simpler. All files within this DLL are compiled with the SHARED_LIB_API
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see
// SHARED_LIB_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.

#ifdef SHARED_LIB_EXPORTS
#define SHARED_LIB_API __declspec(dllexport)
#else
#define SHARED_LIB_API __declspec(dllimport)
#endif

// This class is exported from the $(baseName).dll
class SHARED_LIB_API $(baseName) {
public:
	$(baseName)(void);
	// TODO: add your methods here.
};
