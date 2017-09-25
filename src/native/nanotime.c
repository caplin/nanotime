#include <jni.h>
#include <time.h>
#include "nanotime.h"

#ifdef WIN
#include <Windows.h>

typedef int clockid_t;

#define CLOCK_REALTIME 0
#define exp7           10000000i64     //1E+7     //C-file part
#define exp9         1000000000i64     //1E+9
#define w2ux 116444736000000000i64     //1.jan1601 to 1.jan1970

int clock_gettime(clockid_t clk_id, struct timespec *tp);
#endif

JNIEXPORT jlong JNICALL Java_com_caplin_time_NanoClock_clock_1gettime(JNIEnv *env, jobject thisObj) {
    struct timespec tp;
    
    clock_gettime(CLOCK_REALTIME, &tp);
   
    return (tp.tv_sec << 32) | (tp.tv_nsec);
}

#ifdef WIN
void unix_time(struct timespec *spec)
{
    __int64 wintime;
    GetSystemTimeAsFileTime((FILETIME*)&wintime);
    wintime -= w2ux;
    spec->tv_sec = wintime / exp7;
    spec->tv_nsec = wintime % exp7 * 100;
}

int clock_gettime(clockid_t clk_id, struct timespec *tp)
{
#define exp9         1000000000i64     //1E+9
    static  struct timespec startspec;
    static double ticks2nano;
    static __int64 startticks, tps = 0;
    __int64 tmp, curticks;
    QueryPerformanceFrequency((LARGE_INTEGER*)&tmp); //some strange system can
    if (tps != tmp) {
        tps = tmp; //init ~~ONCE         //possibly change freq ?
        QueryPerformanceCounter((LARGE_INTEGER*)&startticks);
        unix_time(&startspec); ticks2nano = (double)exp9 / tps;
    }

    QueryPerformanceCounter((LARGE_INTEGER*)&curticks);
    curticks -= startticks;
    tp->tv_sec = startspec.tv_sec + (curticks / tps);
    tp->tv_nsec = startspec.tv_nsec + (double)(curticks % tps) * ticks2nano;
    if (!(tp->tv_nsec < exp9)) { tp->tv_sec++; tp->tv_nsec -= exp9; }
    return 0;
}
#endif