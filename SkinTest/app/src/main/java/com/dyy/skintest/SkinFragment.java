package com.dyy.skintest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.irwin.skin.SkinManager;
import com.irwin.skin.SkinnableFragment;
import com.irwin.submodule.TestActivity;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ARES on 2017/7/10.
 */

public class SkinFragment extends SkinnableFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "SkinFragment";
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setForceUpdateSkin(true);
    }

    @Override
    public void bindViewData() {
        List<String> skins = getSkins();
        mListView = (ListView) findViewById(R.id.ListView);
        mListView.setOnItemClickListener(this);
        Log.i("Frag", "Rebind fragment");
        if (skins.size() > 0) {
            initAdapter(skins);
        }
    }

    private List<String> getSkins() {
        Activity activity = getActivity();
        if (activity instanceof FragmentsTestActivity) {
            return ((FragmentsTestActivity) activity).getSkins();
        }
        return Collections.emptyList();
    }

    void initAdapter(List<String> skins) {
        final Context context = mListView.getContext();
        mAdapter = new ArrayAdapter<String>(context, -1, skins) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = SkinManager.getInstance().getResources().getView(context, R.layout.item);
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
    public int getLayoutRes() {
        return R.layout.frag_skin;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter == null) {
            return;
        }
        String skinPath = mAdapter.getItem(position);
        if (skinPath.startsWith(MainActivity.MOCK_SKIN)) {
            showToast("不支持的皮肤，恢复到默认");
            SkinManager.getInstance().restoreSkin(new SkinManager.ISkinCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    try {
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(Throwable error) {

                }
            });
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
                try {
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Throwable error) {
                error.printStackTrace();
                showToast("换肤失败： " + error.getMessage());
            }
        });
    }

    public void showToast(String msg) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
