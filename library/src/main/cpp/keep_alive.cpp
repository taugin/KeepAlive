
#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <sys/file.h>
#include <assert.h>
#include "common.h"

extern "C" {

/**
lock file
*/
int lf_locked(const char *lock_file_path) {
    // LOGV("lock file >> %s <<", lock_file_path);
    int lockFileDescriptor = open(lock_file_path, O_RDONLY | O_LARGEFILE);
    // LOGV("lock file id : %d", lockFileDescriptor);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, O_CREAT/*64*/, S_IRUSR | S_IWUSR/*256*/);
        // LOGV("lock file again id : %d", lockFileDescriptor);
    }
    int lockRet = flock(lockFileDescriptor, LOCK_EX | LOCK_NB);
    // LOGV("lock result : %d", lockRet);
    if (lockRet == -1) {
        // LOGE("lock file failed >> %s <<", lock_file_path);
        return 0;
    } else {
        // LOGV("lock file success  >> %s <<", lock_file_path);
        return 1;
    }
}

/**
wait_file_lock
*/
bool wfl_locked(const char *lock_file_path) {
    // LOGV("enter wait file lock >> %s <<", lock_file_path);
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    // LOGV("lock file id : %d", lockFileDescriptor);
    if (lockFileDescriptor == -1)
        lockFileDescriptor = open(lock_file_path, O_CREAT/*64*/, S_IRUSR | S_IWUSR /*256*/);
    // LOGV("start loop lock file id : %d", lockFileDescriptor);
    int loop_result = -1;
    for (;;) {
        loop_result = flock(lockFileDescriptor, LOCK_EX | LOCK_NB);
        // LOGV("lock_file_path : %s , loop_result : %d", lock_file_path, loop_result);
        if (loop_result != -1) {
            if (loop_result == 0) {
                int unlock_result = flock(lockFileDescriptor, LOCK_UN);
                // LOGV("lock_file_path : %s , unlock_result : %d", lock_file_path, unlock_result);
                sleep(1);
            } else {
                usleep(1000);
            }
        } else {
            break;
        }
    }
    // LOGV("lock file again >> %s << %d", lock_file_path, lockFileDescriptor);
    int lock_result = flock(lockFileDescriptor, LOCK_EX);
    // LOGV("lock file result >> %s << %d << %d", lock_file_path, lockFileDescriptor, lock_result);
    return lock_result != -1;
}

/**
native_nativeSetSid
*/
JNIEXPORT void JNICALL
native_nss(JNIEnv *env, jclass jobj) {
    setsid();
}

/**
native_waitFileLock
*/
JNIEXPORT void JNICALL
native_wfl(JNIEnv *env, jclass jobj,
                    jstring path) {
    const char *file_path = (char *) env->GetStringUTFChars(path, 0);
    wfl_locked(file_path);
}

/**
native_lockFile
*/
JNIEXPORT void JNICALL
native_lf(JNIEnv *env, jclass jobj,
                jstring lockFilePath) {
    const char *lock_file_path = (char *) env->GetStringUTFChars(lockFilePath, 0);
    lf_locked(lock_file_path);
}

static jstring findJniRegClass(JNIEnv *env) {
    jclass clazz = env->FindClass("java/lang/System");
    if (clazz == NULL) {
        return NULL;
    }
    jmethodID method_get = env->GetStaticMethodID(clazz, "getProperty",
                                                  "(Ljava/lang/String;)Ljava/lang/String;");
    if (method_get == NULL) {
        return NULL;
    }
    jstring reg_class_path = env->NewStringUTF("REGISTER_CLASS_PATH");
    if (reg_class_path == NULL) {
        return NULL;
    }
    jobject jobj = env->CallStaticObjectMethod(clazz, method_get, reg_class_path);
    if (jobj == NULL) {
        return NULL;
    }
    const char *reg_class_name = env->GetStringUTFChars(static_cast<jstring>(jobj), 0);
    if (reg_class_name == NULL) {
        return NULL;
    }
    env->ReleaseStringUTFChars(static_cast<jstring>(jobj), reg_class_name);
    return env->NewStringUTF(reg_class_name);
}

/**
* Table of methods associated with a single class.
*/
static JNINativeMethod gMethods[] = {
        {"nss",     "()V",                   (void *) native_nss},
        {"wfl",     "(Ljava/lang/String;)V", (void *) native_wfl},
        {"lf",      "(Ljava/lang/String;)V", (void *) native_lf}
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
    jstring reg_class_name = findJniRegClass(env);
    if (reg_class_name == NULL) {
        // LOGD("can not find register class");
        reg_class_name = env->NewStringUTF(JNIREG_CLASS);
    }
    const char *jni_class_name = env->GetStringUTFChars(reg_class_name, 0);
    // LOGV("native name : %s", jni_class_name);
    if (!registerNativeMethods(env, jni_class_name, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[0]))) {
        env->ReleaseStringUTFChars(reg_class_name, jni_class_name);
        return JNI_FALSE;
    }
    env->ReleaseStringUTFChars(reg_class_name, jni_class_name);
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