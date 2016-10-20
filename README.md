#[android开源]简单富文本编辑器MRichEditor，图文混排算个啥
尊重原创，转载请注明出处，原文地址： http://blog.csdn.net/qq137722697
##一、前言

**1、什么是MRichEditor**

MRichEditor是一款Android开源轻量级的富文本编辑器，它可以根据用户最终撰写的文章创建出对应的html文件。

**2、可以撰写哪些内容**

目前支持用户撰写标题、内容、图片（选择相册、拍照都可以），即可以根据用户的需要进行图文混排，支持对内容的添加、删除、修改。

用户撰写图文混排的内容——>生成HTML文件——>开发者拿到HTML想干嘛就干嘛。

**3、关于配置**

开发者只需要简单的配置几行代码就可以实现，它还提供了近40个接口用于开发者自己定制相应的内容，它预留了一个便于开发者扩展功能的按钮，可以定制显示文本、图片以及监听单击事件。

**4、关于兼容性**

兼容Android2.3——>Android6.0系统的机型。

**5、效果展示**

这是使用MRichEditor编写的文章
![这里写图片描述](http://img.blog.csdn.net/20161020165239499) 

##二、使用步骤
**1、gradle添加依赖**
```
compile 'com.huangdali:mricheditor:1.0.1'
```
**2、权限**
```
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" /><!--demo里面需要上传文件,所以需要网络权限-->
```
**3、在布局文件中使用**
```
<com.hdl.mricheditor.view.MRichEditor
    android:id="@+id/mre_editor"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

</com.hdl.mricheditor.view.MRichEditor>
```
**4、重写onActivityResult方法**

在使用MRichEditor的Activity/Fragment中重写onActivityResult方法（直接复制即可）。
```
/**
 * 需要重写这个方法选择图片、拍照才有用哦
 *
 * @param requestCode
 * @param resultCode
 * @param data
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_CANCELED) {
        Toast.makeText(this, "取消操作", Toast.LENGTH_LONG).show();
        return;
    }
    if (requestCode == CamaraRequestCode.CAMARA_GET_IMG) {
        editor.insertImg(data.getData());
    } else if (requestCode == CamaraRequestCode.CAMARA_TAKE_PHOTO) {
        editor.insertImg(data);
    }
}
```
##三、Demo演示
**1、来个最简单的demo**

先看效果图，添加标题、内容、图片，修改内容，预览：

![这里写图片描述](http://img.blog.csdn.net/20161020170806286)

由于模拟器不支持拍照，所以没有演示拍照功能，你自己可以根据上面的步骤简单配置一下就可以看到效果了。


修改（单击）、删除（长按）：

![这里写图片描述](http://img.blog.csdn.net/20161020170932474)

上代码，代码里面有注释就不一一解释了：
```
public class MainActivity extends AppCompatActivity {
    private MRichEditor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editor = (MRichEditor) findViewById(R.id.mre_editor);
        editor.setServerImgDir("http://192.168.0.107:8080/UpLoadDemo/upload");//设置图片存放在服务器的路径
    }
    /**
     * 需要重写这个方法选择图片、拍照才有用哦
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "取消操作", Toast.LENGTH_LONG).show();
            return;
        }
        if (requestCode == CamaraRequestCode.CAMARA_GET_IMG) {
            editor.insertImg(data.getData());
        } else if (requestCode == CamaraRequestCode.CAMARA_TAKE_PHOTO) {
            editor.insertImg(data);
        }
    }
    /**
     * 完成
     *
     * @param view
     */
    public void onFinished(View view) {
        String htmlStr = editor.createHtmlStr();
        Log.e("htmlStr", htmlStr);
    }
}
```
最终logcat中会打印创建的html内容（有点长，就不贴出来了）。

**2、进阶Demo**

同样的先看效果图：

![这里写图片描述](http://img.blog.csdn.net/20161020171818165)

上代码（这里对Android6.0的手机进行了权限的适配，[Android6.0动态权限相关类在这里下载（点我）](http://download.csdn.net/detail/qq137722697/9602707)，[可以去GitHub中下载（点我）](https://github.com/huangdali/mricheditordemo)，[里面使用到的多文件上传框架是可以在我的另外一篇博客查看详细信息（点我）](http://blog.csdn.net/qq137722697/article/details/52843336)）：

```
public class MainActivity extends AppCompatActivity {
    private MRichEditor editor;//编辑器
    private EditText etTitle;//文章标题对象
    private static final String BASE_URL = "http://192.168.2.153:8080/MRichEditorDemoServer/upload.action";//文件上传的接口
    private static final String IMG_URL = "http://192.168.2.153:8080/MRichEditorDemoServer/upload";//文件存放的路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 请求所有必要的权限----android6.0必须要动态申请权限,否则选择照片和拍照功能 用不了哦
         */
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {//权限通过了
            }

            @Override
            public void onDenied(String permission) {//权限拒绝了

            }
        });
        initMRichEditor();//初始化编辑器
    }

    /**
     * 初始化富文本编辑器
     */
    private void initMRichEditor() {
        etTitle = (EditText) findViewById(R.id.et_main_title);
        editor = (MRichEditor) findViewById(R.id.mre_editor);
        editor.setServerImgDir(IMG_URL);//设置图片存放的路径
        editor.setOnPreviewBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priview();//预览
            }
        });
    }

    /**
     * 预览
     */
    private void priview() {
        editor.setHtmlTitle(etTitle.getText().toString().trim());//设置html的标题
        String htmlStr = editor.createHtmlStr();//创建html字符串
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.dialog_preiview_html, null);
        final WebView wvPreiview = (WebView) view.findViewById(R.id.wv_dialog_preiview_html);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_dialog_close);
        ImageView ivRefresh = (ImageView) view.findViewById(R.id.iv_dialog_refresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wvPreiview.reload();
            }
        });
        wvPreiview.loadData(htmlStr, "text/html; charset=UTF-8", null);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        final AlertDialog finalDialog = dialog;
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        uploadImg();//上传图片
    }

    /**
     * 需要重写这个方法,并且加上下面的判断(照写即可)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "取消操作", Toast.LENGTH_LONG).show();
            return;
        }
        if (requestCode == CamaraRequestCode.CAMARA_GET_IMG) {
            editor.insertImg(data.getData());
        } else if (requestCode == CamaraRequestCode.CAMARA_TAKE_PHOTO) {
            editor.insertImg(data);
        }
    }

    /**
     * 完成按钮---将文件和图片提交到服务器
     *
     * @param view
     */
    public void onFinished(View view) {
        editor.setHtmlTitle(etTitle.getText().toString().trim());//设置html的标题,在创建html文件之前,需要将文章的标题(即title标签)设置进去,之后设置无效.
        editor.createHtmlStr();//创建html字符串,会返回一个html字符串.[必须调用,否则内容为空]
        File file = editor.getHtmlFile("sdcard/test.html");//创建html文件,并设置保存的路径
        //添加List<File>的文件列表,用于MyHttpUtils多文件上传的参数.
        List<File> fileList = new ArrayList<>();
        fileList.add(file);
        for (String filePath : editor.getImgPath()) {
            fileList.add(new File(filePath));
        }
        //MyHttpUtils网络请求框架,详细使用介绍:http://blog.csdn.net/qq137722697/article/details/52843336 .
        new MyHttpUtils()
                .url(BASE_URL)//文件上传的接口 (url)
                .addUploadFiles(fileList)//需上传的多个文件
                .setJavaBean(UploadResult.class)//上传完成返回的json格式的数据对应的javabean对象
                .uploadFileMult(new CommCallback<UploadResult>() {//执行多文件上传任务
                    @Override
                    public void onSucess(UploadResult uploadResult) {//成功之后回调
                        Toast.makeText(MainActivity.this, uploadResult.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(String msg) {//失败时候回调
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 上传图片(这里用于实时预览,上传了图片才可以预览哦,否则看不到图片,只能看见文字)
     */
    private void uploadImg() {
        List<File> fileList = new ArrayList<>();
        for (String filePath : editor.getImgPath()) {
            fileList.add(new File(filePath));
        }
        new MyHttpUtils()
                .url(BASE_URL)
                .addUploadFiles(fileList)
                .setJavaBean(UploadResult.class)
                .uploadFileMult(new CommCallback<UploadResult>() {
                    @Override
                    public void onSucess(UploadResult uploadResult) {
                        Toast.makeText(MainActivity.this, uploadResult.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        Log.e("MRichEditorDemo", editor.getHtmlStr());
    }

    /**
     * 打开帮助页面
     *
     * @param view
     */
    public void onHelp(View view) {
        Toast.makeText(this, "              操作手册\n点击-->修改(图片除外),长按-->删除", Toast.LENGTH_LONG).show();
    }
}
```
##四、API介绍

1、创建html字符串（内容），返回String类型的内容
```
 String createHtmlStr()
```

2、插入图片
```
void insertImg() //无参插入图片
void insertImg(Intent data) //带意图的插入图片
void insertImg(Uri bitmapUri) //带图片uri地址的插入图片
```


3、获取输入对话框（对外提供修改dialog颜色的接口），返回InputDialog输入框对象。
```
InputDialog getDialog()
```

4、获取Html字符串（内容）
```
String getHtmlStr()//必须在createHtmlStr()之后调用哦
```

5、获取Html文件，需传入html文件保存路径（你自己设置保存在哪里），返回html文件对象
```
File getHtmlFile(String filePath)
```

6、获取Html文件的输入流（读取流）对象，返回InputStream对象
```
InputStream getHtmlStream()
```

7、获取所有已经添加了的图片，返回图片列表
```
List<String> getImgPath()
```

8、获取所有添加的内容（包括内容标题、内容、图片） 返回添加的单条消息的列表
```
List<EditorBean> getEditorList()
```

9、设置内容字体的大小（默认16）
```
void setContentSize(int size)
```

10、设置内容字体的颜色（默认灰色）
```
void setContentColor(int color)//int值的颜色，比如Color.RED、Color.pares("#FF0000");
void setContentColor(String color)//String值的颜色（不可缩写），比如#FF0000
```

11、设置标题的字体大小（默认18）
```
void setTitleSize(int size)
```

12、设置标题的字体的颜色（默认为黑色）
```
void setTitleColor(int color)//int值的颜色，比如Color.RED、Color.pares("#FF0000");
void setTitleColor(String color)//String值的颜色（不可缩写），比如#FF0000
```

13、设置图片存放在服务器的位置
```
void setServerImgDir(String baseImgUrl)
```

14、设置Html的标题，即设置html文件中title标签的值
```
void setHtmlTitle(String htmlTitle)
```

15、设置预览按钮显示的文本（预览按钮是作为一个扩展按钮来设计的，文本，图片，监听都可以自己来处理，不需要的还可以隐藏）
```
void setSaveBtnText(CharSequence text)；//当时在写代码的时候忘记修改方法名了，将就用，这就是设置预览按钮显示的文本的方法
```

16、设置预览按钮的图片
```
void setPreviewBtnImageResource(Drawable drawable)
```

17、设置监听预览按钮的单击时间
```
void setOnPreviewBtnClickListener(OnClickListener clickListener)
```

18、设置预览按钮是否可见
```
void setPreviewBtnVisibility(int visibility)//传入这三个值View.VISIBLE可见（默认） View.GONE（隐藏、并不占位置） View.INVISIBLE（隐藏，但还在原来的位置）。
```

19、设置图片保存的质量，百分制。默认为20，即图片压缩了80%（这个值很合适，对于目前主流机型拍照压缩下来大概100k-300k大小）。
```
void setImgQuality(int imgQuality)//imgQuality百分制
```

20、设置Html标题的类型
```
void setTitleType(TitleType titleType)//TitleType 对应html中标题标签即h1 h2 h3 ...h6
```

##五、下载地址

源码及demo下载地址：https://github.com/huangdali/mricheditordemo（欢迎star）

>访问我的博客主页了解更多知识：http://blog.csdn.net/qq137722697

----------

>访问我的github主页了解更多开源框架：https://github.com/huangdali
