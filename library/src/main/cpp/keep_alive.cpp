
#include <jni.h>
#include <sys/wait.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/file.h>
#include <assert.h>
#include "common.h"

extern "C" {
int lock_file(const char *lock_file_path) {
    LOGD("start try to lock file >> %s <<", lock_file_path);
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    LOGD("lockFileDescriptor: %d", lockFileDescriptor);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, /*O_CREAT*/64, /*S_IRUSR*/256);
        LOGD("lockFileDescriptor: %d", lockFileDescriptor);
    }
    int lockRet = flock(lockFileDescriptor, LOCK_EX);
    LOGD("lockRet: %d", lockRet);
    if (lockRet == -1) {
        LOGE("lock file failed >> %s <<", lock_file_path);
        return 0;
    } else {
        LOGD("lock file success  >> %s <<", lock_file_path);
        return 1;
    }
}
bool wait_file_lock(const char *lock_file_path) {
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1)
        lockFileDescriptor = open(lock_file_path, 64, 256);
    while (flock(lockFileDescriptor, 6) != -1)
        usleep(1000);
    LOGD("retry lock file >> %s << %d", lock_file_path, lockFileDescriptor);
    return flock(lockFileDescriptor, LOCK_EX) != -1;
}

JNIEXPORT void JNICALL
native_nativeSetSid(JNIEnv *env, jclass jobj) {
    setsid();
}

JNIEXPORT void JNICALL
native_waitFileLock(JNIEnv *env, jclass jobj,
                    jstring path) {
    const char *file_path = (char *) env->GetStringUTFChars(path, 0);
    wait_file_lock(file_path);
}

JNIEXPORT void JNICALL
native_lockFile(JNIEnv *env, jclass jobj,
                jstring lockFilePath) {
    const char *lock_file_path = (char *) env->GetStringUTFChars(lockFilePath, 0);
    lock_file(lock_file_path);
}

#define JNIREG_CLASS "com/sogou/daemon/NativeKeepAlive"//指定要注册的类
/**
* Table of methods associated with a single class.
*/
static JNINativeMethod gMethods[] = {
        {"nativeSetSid", "()V",                   (void *) native_nativeSetSid},
        {"waitFileLock", "(Ljava/lang/String;)V", (void *) native_waitFileLock},
        {"lockFile",     "(Ljava/lang/String;)V", (void *) native_lockFile}
};
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[0]))) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    if (!registerNatives(env)) {//注册
        return -1;
    }
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;
    return result;
}
}