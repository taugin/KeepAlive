
#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <sys/file.h>
#include <assert.h>
#include "common.h"

extern "C" {

jobject get_app_context(JNIEnv *env, jclass jobj) {
    //获取Activity Thread的实例对象
    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread, "currentActivityThread", "()Landroid/app/ActivityThread;");
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    //获取Application，也就是全局的Context
    jmethodID getApplication = env->GetMethodID(activityThread, "getApplication", "()Landroid/app/Application;");
    jobject application = env->CallObjectMethod(at, getApplication);

    // get application context
    jclass applicationClass = env->FindClass("android/app/Application");
    jmethodID getApplicationContext = env->GetMethodID(applicationClass, "getApplicationContext", "()Landroid/content/Context;");
    jobject context = env->CallObjectMethod(application, getApplicationContext);
    return context;
}

jobject get_vd_manager(JNIEnv *env, jclass jobj, jobject app_context) {
    jclass contextClass = env->FindClass("android/content/Context");
    LOGVD("Context Class : %p", contextClass);
    jmethodID getSystemService = env->GetMethodID(contextClass, "getSystemService", "(Ljava/lang/String;)Ljava/lang/Object;");
    LOGVD("getSystemService methodId : %p", getSystemService);

    jstring serviceName = env->NewStringUTF("display");
    jobject vd = env->CallObjectMethod(app_context, getSystemService, serviceName);
    LOGVD("display_manager_class object : %p", vd);
    env->DeleteLocalRef(serviceName);
    return vd;
}

void vd_show(JNIEnv *env, jclass jclazz, jobject context, jobject displayManager) {
    jclass display_manager_class = env->FindClass("android/hardware/display/DisplayManager");
    if (display_manager_class == NULL) {
        LOGE("display_manager_class is null");
        return;
    }

    jclass VirtualDisplay = env->FindClass("android/hardware/display/VirtualDisplay");
    if (VirtualDisplay == NULL) {
        LOGE("VirtualDisplay is null");
        return;
    }

    jmethodID createVirtualDisplay = env->GetMethodID(display_manager_class, "createVirtualDisplay",
                                                      "(Ljava/lang/String;IIILandroid/view/Surface;I)Landroid/hardware/display/VirtualDisplay;");
    if (createVirtualDisplay == NULL) {
        LOGE("createVirtualDisplay is null");
        return;
    }

    jstring displayName = env->NewStringUTF("virtual_display_other");
    jobject virtualDisplay = env->CallObjectMethod(displayManager, createVirtualDisplay, displayName, 16,
                                                   16, 160, NULL, 11);
    env->DeleteLocalRef(displayName);
    if (virtualDisplay == NULL) {
        LOGE("virtualDisplay is null");
        return;
    }

    jmethodID getDisplay = env->GetMethodID(VirtualDisplay, "getDisplay",
                                            "()Landroid/view/Display;");
    if (getDisplay == NULL) {
        LOGE("getDisplay is null");
        return;
    }

    jobject display = env->CallObjectMethod(virtualDisplay, getDisplay);
    if (display == NULL) {
        LOGE("display is null");
        return;
    }

    jclass Presentation = env->FindClass("android/app/Presentation");
    if (Presentation == NULL) {
        LOGE("Presentation is null");
        return;
    }

    jmethodID newPresentation = env->GetMethodID(Presentation, "<init>",
                                                 "(Landroid/content/Context;Landroid/view/Display;)V");
    if (newPresentation == NULL) {
        LOGE("newPresentation is null");
        return;
    }

    jobject presentation = env->NewObject(Presentation, newPresentation, context, display);
    if (presentation == NULL) {
        LOGE("presentation is null");
        return;
    }

    jmethodID show = env->GetMethodID(Presentation, "show", "()V");
    if (show == NULL) {
        LOGE("show is null");
        return;
    }

    env->CallVoidMethod(presentation, show);
}

// 初始化虚拟屏
void init_vd_locked(JNIEnv *env, jclass jobj) {
    jobject app_context = get_app_context(env, jobj);
    LOGVD("app context : %p", app_context);
    jobject vdmanager = get_vd_manager(env, jobj, app_context);
    vd_show(env, jobj, app_context, vdmanager);
}

int lock_file(const char *lock_file_path) {
    LOGV("lock file >> %s <<", lock_file_path);
    int lockFileDescriptor = open(lock_file_path, O_RDONLY | O_LARGEFILE);
    LOGV("lock file id : %d", lockFileDescriptor);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, O_CREAT/*64*/, S_IRUSR | S_IWUSR/*256*/);
        LOGV("lock file again id : %d", lockFileDescriptor);
    }
    int lockRet = flock(lockFileDescriptor, LOCK_EX | LOCK_NB);
    LOGV("lock result : %d", lockRet);
    if (lockRet == -1) {
        LOGE("lock file failed >> %s <<", lock_file_path);
        return 0;
    } else {
        LOGV("lock file success  >> %s <<", lock_file_path);
        return 1;
    }
}

bool wait_file_lock(const char *lock_file_path) {
    LOGV("enter wait file lock >> %s <<", lock_file_path);
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    LOGV("lock file id : %d", lockFileDescriptor);
    if (lockFileDescriptor == -1)
        lockFileDescriptor = open(lock_file_path, O_CREAT/*64*/, S_IRUSR | S_IWUSR /*256*/);
    LOGV("start loop lock file id : %d", lockFileDescriptor);
    int loop_result = -1;
    for (;;) {
        loop_result = flock(lockFileDescriptor, LOCK_EX | LOCK_NB);
        LOGV("lock_file_path : %s , loop_result : %d", lock_file_path, loop_result);
        if (loop_result != -1) {
            if (loop_result == 0) {
                int unlock_result = flock(lockFileDescriptor, LOCK_UN);
                LOGV("lock_file_path : %s , unlock_result : %d", lock_file_path, unlock_result);
                sleep(1);
            } else {
                usleep(1000);
            }
        } else {
            break;
        }
    }
    LOGV("lock file again >> %s << %d", lock_file_path, lockFileDescriptor);
    int lock_result = flock(lockFileDescriptor, LOCK_EX);
    LOGV("lock file result >> %s << %d << %d", lock_file_path, lockFileDescriptor, lock_result);
    return lock_result != -1;
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

JNIEXPORT void JNICALL
init_vd(JNIEnv *env, jclass jobj) {
    LOGVD("init render");
    init_vd_locked(env, jobj);
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
        {"nativeSetSid", "()V",                   (void *) native_nativeSetSid},
        {"waitFileLock", "(Ljava/lang/String;)V", (void *) native_waitFileLock},
        {"lockFile",     "(Ljava/lang/String;)V", (void *) native_lockFile},
        {"rs",           "()V",                   (void *) init_vd}
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
        LOGD("can not find register class");
        reg_class_name = env->NewStringUTF(JNIREG_CLASS);
    }
    const char *jni_class_name = env->GetStringUTFChars(reg_class_name, 0);
    LOGV("native name : %s", jni_class_name);
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