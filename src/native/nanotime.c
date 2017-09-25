/*
 * Copyright 1995-2017 Caplin Systems Ltd
 */
#include <jni.h>
#include <time.h>
#include "nanotime.h"

#ifdef WIN
#include <Windows.h>

typedef int clockid_t;

#define CLOCK_REALTIME 0
#define exp7           10000000i64     //1E+7
#define exp9         1000000000i64     //1E+9
#define w2ux 116444736000000000i64     //1.jan1601 to 1.jan1970

int clock_gettime(clockid_t clk_id, struct timespec *tp);
#endif

JNIEXPORT jlong JNICALL Java_com_caplin_time_NanoClock_clock_1gettime(JNIEnv *env, jobject thisObj) {
    struct timespec tp;
    
    clock_gettime(CLOCK_REALTIME, &tp);

    // Using bitwise here to improve performance so we just need to return a 64 bit long.
    // 32 bits for seconds since epoch, and 32 bits for nanoseconds.
    // This will only work up until 2038!
    return (tp.tv_sec << 32) | (tp.tv_nsec);
}

// Any changes should also be made to //CDev/main/datasrc/compat_win32.c
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
    static  struct timespec startspec;
    static double ticks2nano;
    static __int64 startticks, tps = 0;
    __int64 tmp, curticks;
    QueryPerformanceFrequency((LARGE_INTEGER*)&tmp);
    if (tps != tmp) {
        tps = tmp;
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