## Introduction ##
Android library project that provide a tool to setting ip and more infos when app is running.
## Dependency ##
Add the following dependency to your build.gradle file.

    compile 'com.bianling:initialize:1.1.0'
## Usage ##

    public class YourApplication extends Application {
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        InitializeUtil.init(this);
	        InitializeUtil.addBooleanClient(new BooleanClient() {
	
	            @Override
	            public String getOptionsName() {
	                return "boolean test";
	            }
	
	            @Override
	            public void onResult(Boolean result) {
	                Toast.makeText(TestApplication.this, result + "", Toast.LENGTH_SHORT).show();
	            }
	
	            @Override
	            public Boolean getDefaultValue() {
	                return false;
	            }
	        });
	    }
	}
 
More clients:FloatClient,IpSettingClient,NumberClient and StringClient.

#### Available methods: ####
    **init(android.app.Application application)** 
    
    **onPermissionResult(android.app.Activity activity)** 
    
    addBooleanClient(BooleanClient booleanClient)
     
    addFloatClient(FloatClient floatClient)
     
    addIpSettingClient(IpSettingClient ipSettingClient) 
    
    addNumberClient(NumberClient numberClient) 
    
    addStringClient(StringClient stringClient) 
    
    setDebug(boolean debug) 
    
    setDefaultIp(java.lang.String clientName, java.lang.String url) 
    
    setDefaultIp(java.lang.String clientName, java.lang.String host, java.lang.String port) 
    
    setEnableValueCallBackWhenAppStart(boolean enableValueCallBackWhenAppStart) 

Call `.init(Application application)` at first.

If targetApi is smaller than 18 or larger than 25,it is necessary to call `onPermissionResult(Activity activity)` at your `category.LAUNCHER` activity.
## Screenshots ##
![](https://i.imgur.com/ON12cWj.png)
![](https://i.imgur.com/og6VkV4.png)
## License ##
Apache License, Version 2.0