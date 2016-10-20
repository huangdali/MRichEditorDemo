package com.hdl.mricheditordemo;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hdl.mricheditor.bean.CamaraRequestCode;
import com.hdl.mricheditor.view.MRichEditor;
import com.hdl.mricheditordemo.runtimepermissions.PermissionsManager;
import com.hdl.mricheditordemo.runtimepermissions.PermissionsResultAction;
import com.hdl.myhttputils.CommCallback;
import com.hdl.myhttputils.MyHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MRichEditor editor;
    private EditText etTitle;
    private static final String BASE_URL = "http://192.168.2.153:8080/MRichEditorDemoServer/upload.action";//文件上传的接口
    private static final String IMG_URL = "http://192.168.2.153:8080/MRichEditorDemoServer/upload";//文件存放的路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 请求所有必要的权限----
         */
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {//权限通过了
            }

            @Override
            public void onDenied(String permission) {//权限拒绝了

            }
        });
        initMyRichEditor();
    }

    /**
     * 初始化富文本编辑器
     */
    private void initMyRichEditor() {
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
     * 需要重写这个方法
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
     * 完成按钮
     *
     * @param view
     */
    public void onFinished(View view) {
        editor.setHtmlTitle(etTitle.getText().toString().trim());//设置html的标题
        editor.createHtmlStr();//创建html字符串
        List<File> fileList = new ArrayList<>();
        File file = editor.getHtmlFile("sdcard/test.html");//创建html文件,并设置保存的路径
        fileList.add(file);
        for (String filePath : editor.getImgPath()) {
            fileList.add(new File(filePath));
        }
        new MyHttpUtils()
                .url(BASE_URL)
                .addUploadFiles(fileList)
                .setJavaBean(UploadResult.class)
                .uploadFileMult(new CommCallback<UploadResult>() {
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
     * 上传图片
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