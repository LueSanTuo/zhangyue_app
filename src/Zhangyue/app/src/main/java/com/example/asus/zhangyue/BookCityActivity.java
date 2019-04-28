package com.example.asus.zhangyue;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.zhangyue.Data.User;
import com.example.asus.zhangyue.fragments.BookFoundFragment;
import com.example.asus.zhangyue.fragments.BookMallFragment;
import com.example.asus.zhangyue.fragments.BookMineFragment;
import com.example.asus.zhangyue.fragments.BookShelfFragment;
import com.example.asus.zhangyue.myutil.FileOperation;

import java.util.ArrayList;
import java.util.List;

/** 主界面 */
public class BookCityActivity extends AppCompatActivity implements View.OnClickListener {

    public static BookCityActivity curActiveActivity;

    //初始化fragment
    private BookMallFragment mBookMallFragment;
    private BookShelfFragment mBookShelfFragment;
    private BookFoundFragment mBookFoundFragment;
    private BookMineFragment mBookMineFragment;

    //片段类容
    private FrameLayout mFlFragmentContent;
    //底部4个按钮
    private RelativeLayout mRlFirstLayout;
    private RelativeLayout mRlSecondLayout;
    private RelativeLayout mRlThirdLayout;
    private RelativeLayout mRlFourLayout;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

    // 需要设置的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS};
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        curActiveActivity = this;

        setContentView(R.layout.activity_book_city);
        mFragmentManager = getSupportFragmentManager();

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int j = 0; j < permissions.length; j++) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[j]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    showDialogTipUserRequestPermission();
                } else {
                    init();
                }
            }

        } else {
            init ();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        curActiveActivity = this;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        curActiveActivity = this;
    }

    private void getPermission () {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(BookCityActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add((permissions[i]));
            }
        }
        if(!permissionList.isEmpty()){
            // 有未授权权限
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions( BookCityActivity.this, permissions, 1);
        }
    }

    private void init () {
        FileOperation.isFolderExists(FileOperation.DEFAULT_BOOK_SAVE_PATH);
        // 获取用户数据
        User user = User.get();
        user.loadUserData(this);
        user.getBooks();
        //StatusBarUtil.setColor(this, getResources().getColor(R.color.colorStatusBar), 0);
        initView();
    }

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {
        new AlertDialog.Builder(this)
                    .setTitle("存储权限不可用")
                    .setMessage("由于掌阅需要获取存储空间，为你存储个人信息；\n否则，您将无法正常使用掌阅")
                    .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startRequestPermission();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setCancelable(false).show();
    }

        // 开始提交请求权限
        private void startRequestPermission() {
            ActivityCompat.requestPermissions(this, permissions, 321);
        }

        // 用户权限 申请 的回调方法
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == 321) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                        boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                        if (!b) {
                            // 用户还是想用我的 APP 的
                            // 提示用户去应用设置界面手动开启权限
                            showDialogTipUserGoToAppSettting();
                        } else
                            finish();
                    } else {
                        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                        init ();

                    }
                }
            }
        }

        // 提示用户去应用设置界面手动开启权限

        private void showDialogTipUserGoToAppSettting() {

            dialog = new AlertDialog.Builder(this)
                    .setTitle("存储权限不可用")
                    .setMessage("请在-应用设置-权限-中，允许支付宝使用存储权限来保存用户数据")
                    .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 跳转到应用设置界面
                            goToAppSetting();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setCancelable(false).show();
        }

        // 跳转到当前应用的设置界面
        private void goToAppSetting() {
            Intent intent = new Intent();

            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);

            startActivityForResult(intent, 123);
        }

        //
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 123) {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查该权限是否已经获取
                    int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        // 提示用户应该去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }



    @Override
    protected void onPause() {
        User.get().saveUserData(this);
        super.onPause();
    }

    private void initView() {
        mFlFragmentContent = (FrameLayout) findViewById(R.id.fl_fragment_content);

        mRlFirstLayout = (RelativeLayout) findViewById(R.id.rl_first_layout);
        mRlSecondLayout = (RelativeLayout) findViewById(R.id.rl_second_layout);
        mRlThirdLayout = (RelativeLayout) findViewById(R.id.rl_third_layout);
        mRlFourLayout = (RelativeLayout) findViewById(R.id.rl_four_layout);

        //给四个按钮设置监听器
        mRlFirstLayout.setOnClickListener(this);
        mRlSecondLayout.setOnClickListener(this);
        mRlThirdLayout.setOnClickListener(this);
        mRlFourLayout.setOnClickListener(this);
        //默认第二个书城被选中高亮显示
        mRlFirstLayout.setSelected(true);
        mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.fl_fragment_content, new BookShelfFragment());
        mTransaction.commit();
    }

    public void gotoFirstLayout () {
        mRlFirstLayout.callOnClick();
    }

    @Override
    public void onClick(View v) {
        mTransaction = mFragmentManager.beginTransaction(); //开启事务
        hideAllFragment(mTransaction);
        switch (v.getId()){
            // 书架
            case R.id.rl_first_layout:
                seleted();
                mRlFirstLayout.setSelected(true);
                if (mBookShelfFragment == null) {
                    mBookShelfFragment = new BookShelfFragment();
                    mTransaction.add(R.id.fl_fragment_content,mBookShelfFragment);    //通过事务将内容添加到内容页
                }else{
                    mTransaction.show(mBookShelfFragment);
                }
                break;
            // 书城
            case R.id.rl_second_layout:
                seleted();
                mRlSecondLayout.setSelected(true);
                if (mBookMallFragment == null) {
                    mBookMallFragment = new BookMallFragment();
                    mTransaction.add(R.id.fl_fragment_content,mBookMallFragment);    //通过事务将内容添加到内容页
                }else{
                    mTransaction.show(mBookMallFragment);
                }
                break;
            // 发现
            case R.id.rl_third_layout:
                seleted();
                mRlThirdLayout.setSelected(true);
                if (mBookFoundFragment == null) {
                    mBookFoundFragment = new BookFoundFragment();
                    mTransaction.add(R.id.fl_fragment_content,mBookFoundFragment);    //通过事务将内容添加到内容页
                }else{
                    mTransaction.show(mBookFoundFragment);
                }
                break;
            // 我的
            case R.id.rl_four_layout:
                seleted();
                mRlFourLayout.setSelected(true);
                if (mBookMineFragment == null) {
                    mBookMineFragment = new BookMineFragment();
                    mTransaction.add(R.id.fl_fragment_content,mBookMineFragment);    //通过事务将内容添加到内容页
                }else{
                    mTransaction.show(mBookMineFragment);
                }
                break;
        }
        mTransaction.commit();
    }

    /** 设置所有按钮都是默认都不选中 */
    private void seleted() {
        mRlFirstLayout.setSelected(false);
        mRlSecondLayout.setSelected(false);
        mRlThirdLayout.setSelected(false);
        mRlFourLayout.setSelected(false);
    }

    /** 删除所有fragmtne */
    private void hideAllFragment(FragmentTransaction transaction) {
        if (mBookShelfFragment != null) {
            transaction.hide(mBookShelfFragment);
        }
        if (mBookMallFragment != null) {
            transaction.hide(mBookMallFragment);
        }
        if (mBookFoundFragment != null) {
            transaction.hide(mBookFoundFragment);
        }
        if (mBookMineFragment != null) {
            transaction.hide(mBookMineFragment);
        }
    }
}
