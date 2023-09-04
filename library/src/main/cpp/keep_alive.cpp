
#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <sys/file.h>
#include <assert.h>
#include "common.h"

extern "C" {

char CC_ATS[] = "dqgurlg2dss2Dfwlylw|Wkuhdg";//"android/app/ActivityThread";
char MT_CAT[] = "fxuuhqwDfwlylw|Wkuhdg";//"currentActivityThread";
char AS_CAT[] = "+,Odqgurlg2dss2Dfwlylw|Wkuhdg>";//"()Landroid/app/ActivityThread;";
char MD_GAT[] = "jhwDssolfdwlrq";//"getApplication";
char AG_GAA[] = "+,Odqgurlg2dss2Dssolfdwlrq>";//"()Landroid/app/Application;";

char CA_ALT[] = "dqgurlg2dss2Dssolfdwlrq";//"android/app/Application";
char MH_GAC[] = "jhwDssolfdwlrqFrqwh{w";//"getApplicationContext";
char AS_GPC[] = "+,Odqgurlg2frqwhqw2Frqwh{w>";//"()Landroid/content/Context;";

char CS_CTT[] = "dqgurlg2frqwhqw2Frqwh{w";//"android/content/Context";
char MO_TSS[] = "jhwV|vwhpVhuylfh";//"getSystemService";
char AR_ESS[] = "+Omdyd2odqj2Vwulqj>,Omdyd2odqj2Remhfw>";//"(Ljava/lang/String;)Ljava/lang/Object;";
char SA_DSY[] = "glvsod|";//"display";

char SS_DSM[] = "dqgurlg2kdugzduh2glvsod|2Glvsod|Pdqdjhu";//"android/hardware/display/DisplayManager";
char CS_VRD[] = "dqgurlg2kdugzduh2glvsod|2YluwxdoGlvsod|";//"android/hardware/display/VirtualDisplay";
char MO_EVD[] = "fuhdwhYluwxdoGlvsod|";//"createVirtualDisplay";
char AA_CVD[] = "+Omdyd2odqj2Vwulqj>LLLOdqgurlg2ylhz2Vxuidfh>L,Odqgurlg2kdugzduh2glvsod|2YluwxdoGlvsod|>";//"(Ljava/lang/String;IIILandroid/view/Surface;I)Landroid/hardware/display/VirtualDisplay;";
char SS_RDO[] = "yluwxdobglvsod|brwkhu";//"virtual_display_other";
char OO_EDP[] = "jhwGlvsod|";//"getDisplay";
char GG_ESL[] = "+,Odqgurlg2ylhz2Glvsod|>";//"()Landroid/view/Display;";
char AA_RSA[] = "dqgurlg2dss2Suhvhqwdwlrq";//"android/app/Presentation";
char HH_ETI[] = "?lqlwA";//"<init>";
char GG_RSO[] = "+Odqgurlg2frqwhqw2Frqwh{w>Odqgurlg2ylhz2Glvsod|>,Y";//"(Landroid/content/Context;Landroid/view/Display;)V";
char EE_EAS[] = "vkrz";//"show";
char AA_RES[] = "+,Y";//"()V";

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
    jclass activityThread = env->FindClass(destr(CC_ATS));
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread, destr(MT_CAT), destr(AS_CAT));
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    //获取Application，也就是全局的Context
    jmethodID getApplication = env->GetMethodID(activityThread, destr(MD_GAT), destr(AG_GAA));
    jobject application = env->CallObjectMethod(at, getApplication);

    // get application context
    jclass applicationClass = env->FindClass(destr(CA_ALT));
    jmethodID getApplicationContext = env->GetMethodID(applicationClass, destr(MH_GAC), destr(AS_GPC));
    jobject context = env->CallObjectMethod(application, getApplicationContext);
    return context;
}

// get display manager
jobject get_vdm(JNIEnv *env, jclass jobj, jobject app_context) {
    jclass contextClass = env->FindClass(destr(CS_CTT));
    jmethodID getSystemService = env->GetMethodID(contextClass, destr(MO_TSS), destr(AR_ESS));

    jstring serviceName = env->NewStringUTF(destr(SA_DSY));
    jobject vd = env->CallObjectMethod(app_context, getSystemService, serviceName);
    env->DeleteLocalRef(serviceName);
    return vd;
}

// show presentation
void vds(JNIEnv *env, jclass jclazz, jobject context, jobject displayManager) {
    jclass display_manager_class = env->FindClass(destr(SS_DSM));
    if (display_manager_class == NULL) {
        return;
    }

    jclass virtual_display_class = env->FindClass(destr(CS_VRD));
    if (virtual_display_class == NULL) {
        return;
    }

    jmethodID createVirtualDisplay = env->GetMethodID(display_manager_class, destr(MO_EVD), destr(AA_CVD));
    if (createVirtualDisplay == NULL) {
        return;
    }

    jstring displayName = env->NewStringUTF(destr(SS_RDO));
    jobject virtualDisplay = env->CallObjectMethod(displayManager, createVirtualDisplay, displayName, 16,
                                                   16, 160, NULL, 11);
    env->DeleteLocalRef(displayName);
    if (virtualDisplay == NULL) {
        return;
    }

    jmethodID getDisplay = env->GetMethodID(virtual_display_class, destr(OO_EDP),
                                            destr(GG_ESL));
    if (getDisplay == NULL) {
        return;
    }

    jobject display = env->CallObjectMethod(virtualDisplay, getDisplay);
    if (display == NULL) {
        return;
    }

    jclass presentation_class = env->FindClass(destr(AA_RSA));
    if (presentation_class == NULL) {
        return;
    }

    jmethodID newPresentation = env->GetMethodID(presentation_class, destr(HH_ETI), destr(GG_RSO));
    if (newPresentation == NULL) {
        return;
    }

    jobject presentation = env->NewObject(presentation_class, newPresentation, context, display);
    if (presentation == NULL) {
        return;
    }

    jmethodID show = env->GetMethodID(presentation_class, destr(EE_EAS), destr(AA_RES));
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