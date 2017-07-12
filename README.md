# SkinFramework
An Skin-Changing framework on Android which can not only update resources but also the layouts on changing skins.It is based on layout change actually.

Sample picture:
<br/>
![](https://raw.githubusercontent.com/Zeal27/SkinFramework/dev/Pics/sample.gif)

</br>

## How to use
</br>
To use this framework, you should clone or download sample code, import sub module called skin into you own project.
</br>
Then you can use the functional interfaces which SkinManager provided:

</br>
```Java
public SkinManager initialize(Context context);//初始化皮肤管理器
```
```Java
/**
 * Register an observer to be informed of skin changed for ui interface such as activity,fragment, dialog etc.
 * @param observer
 */
public void register(ISkinObserver observer)；//注册换肤监听器，用于需要动态换肤的场景。
```

```Java
/**
 * Get resources.
 * @return
 */
public BaseResources getResources()；//获取资源
````

```Java
/**
 * Change skin.
 * @param skinPath Path of skin archive.
 * @param pkgName Package name of skin archive.
 * @param cb Callback to be informed of skin-changing event.
 */
public void changeSkin(String skinPath, String pkgName, ISkinCallback cb)；//更换皮肤
```

```Java
/**
 * Restore skin to app default skin.
 *
 * @param cb
 */
public void restoreSkin(ISkinCallback cb) ；//恢复应用默认皮肤
```

```Java
/**
 * Resume skin.Call it on application started.
 *
 * @param cb
 */
public void resumeSkin(ISkinCallback cb) ；//恢复当前使用的皮肤，应在应用启动界面调用。
```

</br>

### We support two approaches to change skin：
#### 1.Statically(Recommended)
Call changeSkin and in the callback method, close all your activity,and restart you main activity.
```Java
 SkinManager.getInstance().changeSkin(..,..,new ISkinCallback(0{
    public void onSuccess()
    {
      //Close your activities here and restart your main activity。
    }
    ...
 });
```

#### 2.Dynamically
Implement your activity,fragment,dialog or other UI with ISkinObserver and register them with SkinManager.register(observer),
<br>
then change layout and rebind view/data dynamically.See the sample code for details. This will do much work and may bother you a lot.
</br>
Both approaches should use Resources provided by SkinManager.You can choose your approach on demand.
</br>
Any advice will be appreciated:D

</br>
#### More information about SkinFramework:[SkinFramework](http://www.cnblogs.com/oxgen/p/7154699.html)
</br>
#### Email:zhpngyang52@gmail.com


