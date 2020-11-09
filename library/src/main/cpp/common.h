#ifndef COMMON_H
#define COMMON_H

#include <android/log.h>
#include <stdint.h>
#include <sys/types.h>

#define TAG        "nalive"
//#define LOGE(format, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s : %s : %d ---> " format "%s",__FILE__,__FUNCTION__,__LINE__,##__VA_ARGS__,"\n");
#define LOGE(format, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, "%s : %d ---> " format "%s",__FUNCTION__,__LINE__,##__VA_ARGS__,"\n");

#ifdef LIB_DEBUG
#define LOGV(format, ...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s : %d ---> " format "%s",__FUNCTION__,__LINE__,##__VA_ARGS__,"\n");
#define LOGD(format, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s : %d ---> " format "%s",__FUNCTION__,__LINE__,##__VA_ARGS__,"\n");
#else
#define LOGV(...)
#define LOGD(...)
#endif

typedef unsigned short Char16;
typedef unsigned int Char32;
#endif //KEEPALIVE_COMMON_H