package com.dyy.skintest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.irwin.skin.SkinManager;
import com.irwin.skin.SkinnableActivity;
import com.irwin.submodule.TestActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends SkinnableActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String SKIN_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "skin" + File.separator;
    public static final String MOCK_SKIN = "Mock Skin";
    private static final String TAG = "Skin";
    private static final String SKIN_SUFFIX = ".skin";
    private static final int REQUEST_WRITE_SD = 1;
    private TextView mBTN_Restore;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mSkins = new ArrayList<>();
    private SkinHelper mSkinHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSkinHelper = new SkinHelper();
        if (!mSkinHelper.hasSkinArchive()) {
            //Unregister temporarily to avoid to call bindViewData on restoring default skin.
            SkinManager.getInstance().unregister(this);
            //Restore to default skin so we can scan skins.
            SkinManager.getInstance().restoreSkin(null);
            SkinManager.getInstance().register(this);
            if (hasWritePermission()) {
                mSkinHelper.initialize(this);
            }
        }
        scanSkins();
        bindViewData();
    }


    /**
     * @return True if permission granted, false otherwise.
     */
    boolean hasWritePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_WRITE_SD);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_SD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mSkinHelper.initialize(this);
                    scanSkins();
                    if (mSkins.size() > 0) {
                        initAdapter();
                    }
                }
                break;
        }
    }


    void scanSkins() {
        if (!hasWritePermission()) {
            return;
        }
        String[] archiveArray = mSkinHelper.getSkinArchive();
        if (archiveArray == null || archiveArray.length == 0) {
            return;
        }
        for (String file : archiveArray) {
            mSkins.add(SKIN_ROOT + File.separator + file);
        }
        int spare = 18 - archiveArray.length;
        for (int i = 0; i < spare; i++) {
            mSkins.add(MOCK_SKIN + "[" + i + "]");
        }
    }


    public void bindViewData() {
        Resources resources = getResources();
        mBTN_Restore = (TextView) findViewById(R.id.BTN_Restore);
        mBTN_Restore.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.ListView);
        mListView.setOnItemClickListener(this);
        boolean hasChangeSkin = hasChangeSkin();
        mBTN_Restore.setVisibility(hasChangeSkin ? View.VISIBLE : View.GONE);
        if (mSkins.size() > 0) {
            initAdapter();
            String text = resources.getString(R.string.BTN_Restore);
            mBTN_Restore.setText(text);
        } else {
            mBTN_Restore.setVisibility(View.VISIBLE);
            mBTN_Restore.setText(resources.getString(R.string.BTN_ScanSkin));
        }
    }

    public boolean hasChangeSkin() {
        return (!SkinManager.getInstance().isSameSkin(null, getPackageName()));
    }

    void initAdapter() {
        mAdapter = new ArrayAdapter<String>(this, -1, mSkins) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = SkinManager.getInstance().getResources().getView(MainActivity.this, R.layout.item);
                }
                TextView tv = (TextView) convertView.findViewById(R.id.id_tv_title);
                String path = getItem(position);
                tv.setText(new File(path).getName());
                return convertView;
            }
        };
        mListView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BTN_Restore:
                if (mSkins.size() > 0 && hasChangeSkin()) {
                    //Restore to default skin.
                    SkinManager.getInstance().restoreSkin(null);
                } else {
                    mSkinHelper.initialize(this);
                    scanSkins();
                    if (mSkins.size() > 0) {
                        initAdapter();
                    }
                }
                break;
            case R.id.BTN_FragmentTest:
                Intent intent = new Intent(this, FragmentsTestActivity.class);
                intent.putExtra("Skins", mSkins);
                startActivity(intent);
                break;
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter == null) {
            return;
        }
        String skinPath = mAdapter.getItem(position);
        if (skinPath.startsWith(MOCK_SKIN)) {
            showToast(SkinManager.getInstance().getResources().getString(R.string.Tips_UnsupportedSkin));
            startActivity(new Intent(this, TestActivity.class));
            return;
        }
        SkinManager.getInstance().changeSkin(skinPath, null, new SkinManager.ISkinCallback() {
            @Override
            public void onStart() {
                Log.i(TAG, "Start changing skin...");
            }

            @Override
            public void onSuccess() {
                showToast("换肤成功");
            }

            @Override
            public void onFail(Throwable error) {
                error.printStackTrace();
                showToast("换肤失败： " + error.getMessage());
            }
        });
    }
}
