1. 增加仓库
	maven { url "http://nexus.webeyemobi.com/repository/maven-public/"}
	
2. 添加依赖
    implementation 'com.keep.daemon:daemon:1.0.1.1-SNAPSHOT'
	
3. 在app.gradle的manifestPlaceholders配置PACKAKG_NAME属性值，如下：
	manifestPlaceholders = [PACKAKG_NAME: applicationId]
	
4. 创建一个远程Service，增加process属性，如:resident，并且配置action，name为Service类名全称

5. 在Application的attachBaseContext函数中增加初始化代码，如下：
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        KeepAlive.attachBaseContext(base, NotifyResidentService.class);
    }

6. 启动新创建的远程service：
	ContextCompat.startForegroundService(this, new Intent(this, NotifyResidentService.class));