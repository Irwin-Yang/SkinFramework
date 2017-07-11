package com.dyy.skintest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.irwin.skin.SkinnableActivity;

import java.util.Collections;
import java.util.List;


/**
 * Created by ARES on 2017/7/10.
 */

public class FragmentsTestActivity extends SkinnableActivity {
    private Fragment mFragment = new SkinFragment();
    private List<String> mSkins;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        mSkins = getSkins(getIntent().getStringArrayListExtra("Skins"));
        getSupportFragmentManager().beginTransaction().add(R.id.Container, mFragment).commit();
    }


    public List<String> getSkins() {
        return mSkins;
    }

    private List<String> getSkins(List<String> list) {
        if (list != null && list.size() > 0) {
            return list.subList(0, list.size() > 5 ? 5 : list.size() - 1);
        }
        return Collections.emptyList();
    }

    @Override
    public void bindViewData() {

    }
}
