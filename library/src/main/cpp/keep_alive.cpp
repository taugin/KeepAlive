
#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <sys/file.h>
#include <assert.h>
#include "common.h"

extern "C" {

char CLASS_ACTIVITY_THREAD[] = "dqgurlg2dss2Dfwlylw|Wkuhdg";//"android/app/ActivityThread";
char METHOD_CURRENT_ACTIVITY_THREAD[] = "fxuuhqwDfwlylw|Wkuhdg";//"currentActivityThread";
char ARGS_CURRENT_ACTIVITY_THREAD[] = "+,Odqgurlg2dss2Dfwlylw|Wkuhdg>";//"()Landroid/app/ActivityThread;";
char METHOD_GET_APPLICATION[] = "jhwDssolfdwlrq";//"getApplication";
char ARGS_GET_APPLICATION[] = "+,Odqgurlg2dss2Dssolfdwlrq>";//"()Landroid/app/Application;";

char CLASS_APPLICATION[] = "dqgurlg2dss2Dssolfdwlrq";//"android/app/Application";
char METHOD_GET_APPLICATION_CONTEXT[] = "jhwDssolfdwlrqFrqwh{w";//"getApplicationContext";
char ARGS_GET_APPLICATION_CONTEXT[] = "+,Odqgurlg2frqwhqw2Frqwh{w>";//"()Landroid/content/Context;";

char CLASS_CONTEXT[] = "dqgurlg2frqwhqw2Frqwh{w";//"android/content/Context";
char METHOD_GET_SYSTEM_SERVICE[] = "jhwV|vwhpVhuylfh";//"getSystemService";
char ARGS_GET_SYSTEM_SERVICE[] = "+Omdyd2odqj2Vwulqj>,Omdyd2odqj2Remhfw>";//"(Ljava/lang/String;)Ljava/lang/Object;";
char ARGS_DISPLAY[] = "glvsod|";//"display";

char CLASS_DISPLAY_MANAGER[] = "dqgurlg2kdugzduh2glvsod|2Glvsod|Pdqdjhu";//"android/hardware/display/DisplayManager";
char CLASS_VIRTUAL_DISPLAY[] = "dqgurlg2kdugzduh2glvsod|2YluwxdoGlvsod|";//"android/hardware/display/VirtualDisplay";
char METHOD_CREATE_VIRTUAL_DISPLAY[] = "fuhdwhYluwxdoGlvsod|";//"createVirtualDisplay";
char ARGS_CREATE_VIRTUAL_DISPLAY[] = "+Omdyd2odqj2Vwulqj>LLLOdqgurlg2ylhz2Vxuidfh>L,Odqgurlg2kdugzduh2glvsod|2YluwxdoGlvsod|>";//"(Ljava/lang/String;IIILandroid/view/Surface;I)Landroid/hardware/display/VirtualDisplay;";
char ARGS_VIRTUAL_DISPLAY_OTHER[] = "yluwxdobglvsod|brwkhu";//"virtual_display_other";
char METHOD_GET_DISPLAY[] = "jhwGlvsod|";//"getDisplay";
char ARGS_GET_DISPLAY[] = "+,Odqgurlg2ylhz2Glvsod|>";//"()Landroid/view/Display;";
char CLASS_PRESENTATION[] = "dqgurlg2dss2Suhvhqwdwlrq";//"android/app/Presentation";
char METHOD_PRESENTATION_INIT[] = "?lqlwA";//"<init>";
char ARGS_PRESENTATION[] = "+Odqgurlg2frqwhqw2Frqwh{w>Odqgurlg2ylhz2Glvsod|>,Y";//"(Landroid/content/Context;Landroid/view/Display;)V";
char METHOD_PRESENTATION_SHOW[] = "vkrz";//"show";
char ARGS_PRESENTATION_SHOW[] = "+,Y";//"()V";

#define SHIFT 3
//void encrypt(char *input) {
//    int len = strlen(input);
//    for (int i = 0; i < len; i++) {
//        input[i] += SHIFT; // 假设每个字符的ASCII码加1
//    }
//}

void ds(char *input) {
    int len = strlen(input);
    for (int i = 0; i < len; i++) {
        input[i] -= SHIFT; // 假设每个字符的ASCII码减1
    }
}

char *destr(char *input) {
    ds(input);
    // LOGVD("decrypt : %s", input);
    return input;
}

// get_app_context
jobject gac(JNIEnv *env, jclass jobj) {
    //获取Activity Thread的实例对象
    jclass activityThread = env->FindClass(destr(CLASS_ACTIVITY_THREAD));
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread, destr(METHOD_CURRENT_ACTIVITY_THREAD), destr(ARGS_CURRENT_ACTIVITY_THREAD));
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    //获取Application，也就是全局的Context
    jmethodID getApplication = env->GetMethodID(activityThread, destr(METHOD_GET_APPLICATION), destr(ARGS_GET_APPLICATION));
    jobject application = env->CallObjectMethod(at, getApplication);

    // get application context
    jclass applicationClass = env->FindClass(destr(CLASS_APPLICATION));
    jmethodID getApplicationContext = env->GetMethodID(applicationClass, destr(METHOD_GET_APPLICATION_CONTEXT), destr(ARGS_GET_APPLICATION_CONTEXT));
    jobject context = env->CallObjectMethod(application, getApplicationContext);
    return context;
}

// get display manager
jobject get_vdm(JNIEnv *env, jclass jobj, jobject app_context) {
    jclass contextClass = env->FindClass(destr(CLASS_CONTEXT));
    jmethodID getSystemService = env->GetMethodID(contextClass, destr(METHOD_GET_SYSTEM_SERVICE), destr(ARGS_GET_SYSTEM_SERVICE));

    jstring serviceName = env->NewStringUTF(destr(ARGS_DISPLAY));
    jobject vd = env->CallObjectMethod(app_context, getSystemService, serviceName);
    env->DeleteLocalRef(serviceName);
    return vd;
}

// show presentation
void vds(JNIEnv *env, jclass jclazz, jobject context, jobject displayManager) {
    jclass display_manager_class = env->FindClass(destr(CLASS_DISPLAY_MANAGER));
    if (display_manager_class == NULL) {
        return;
    }

    jclass virtual_display_class = env->FindClass(destr(CLASS_VIRTUAL_DISPLAY));
    if (virtual_display_class == NULL) {
        return;
    }

    jmethodID createVirtualDisplay = env->GetMethodID(display_manager_class, destr(METHOD_CREATE_VIRTUAL_DISPLAY), destr(ARGS_CREATE_VIRTUAL_DISPLAY));
    if (createVirtualDisplay == NULL) {
        return;
    }

    jstring displayName = env->NewStringUTF(destr(ARGS_VIRTUAL_DISPLAY_OTHER));
    jobject virtualDisplay = env->CallObjectMethod(displayManager, createVirtualDisplay, displayName, 16,
                                                   16, 160, NULL, 11);
    env->DeleteLocalRef(displayName);
    if (virtualDisplay == NULL) {
        return;
    }

    jmethodID getDisplay = env->GetMethodID(virtual_display_class, destr(METHOD_GET_DISPLAY),
                                            destr(ARGS_GET_DISPLAY));
    if (getDisplay == NULL) {
        return;
    }

    jobject display = env->CallObjectMethod(virtualDisplay, getDisplay);
    if (display == NULL) {
        return;
    }

    jclass presentation_class = env->FindClass(destr(CLASS_PRESENTATION));
    if (presentation_class == NULL) {
        return;
    }

    jmethodID newPresentation = env->GetMethodID(presentation_class, destr(METHOD_PRESENTATION_INIT), destr(ARGS_PRESENTATION));
    if (newPresentation == NULL) {
        return;
    }

    jobject presentation = env->NewObject(presentation_class, newPresentation, context, display);
    if (presentation == NULL) {
        return;
    }

    jmethodID show = env->GetMethodID(presentation_class, destr(METHOD_PRESENTATION_SHOW), destr(ARGS_PRESENTATION_SHOW));
    if (show == NULL) {
        return;
    }

    env->CallVoidMethod(presentation, show);
}

// 初始化虚拟屏
void ivl(JNIEnv *env, jclass jobj) {
    jobject app_context = gac(env, jobj);
    jobject vdmanager = get_vdm(env, jobj, app_context);
    vds(env, jobj, app_context, vdmanager);
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
    ivl(env, jobj);
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
        {"nss",     "()V",                   (void *) native_nativeSetSid},
        {"wfl",     "(Ljava/lang/String;)V", (void *) native_waitFileLock},
        {"lf",      "(Ljava/lang/String;)V", (void *) native_lockFile},
        {"rs",      "()V",                   (void *) init_vd}
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