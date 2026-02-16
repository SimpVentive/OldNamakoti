package com.namakoti.chanting;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.TextView;

import com.namakoti.R;
import com.namakoti.active.ActiveChantsFragment;
import com.namakoti.base.BaseActivity;
import com.namakoti.karmic.KarmicChantingFragment;
import com.namakoti.settings.SettingFragment;

public class ChantingSelectionActivity extends BaseActivity  implements BottomNavigationView.OnNavigationItemSelectedListener
{

    private TextView mTextMessage;
    private String mChantType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chanting_selection);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        getBundle();
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();

        Bundle bundle = new Bundle();
        if (mChantType.equalsIgnoreCase("self")){
            SelfChantingFragment frag = new SelfChantingFragment();
            bundle.putString(frag.CHANT_TYPE, "self");
            frag.setArguments(bundle);
            transaction.replace(R.id.content,frag).commit();
            navigation.getMenu().findItem(R.id.selfChanting).setChecked(true);
        } else if (mChantType.equalsIgnoreCase("karmic")){
//            startActivity(new Intent(this, NewBeingHelpedActivity.class));

            SelfChantingFragment frag = new SelfChantingFragment();
            bundle.putString(frag.CHANT_TYPE, "karmic");
            frag.setArguments(bundle);
            transaction.replace(R.id.content,new KarmicChantingFragment()).commit();
            navigation.getMenu().findItem(R.id.karmic_chanting).setChecked(true);
        } else if (mChantType.equalsIgnoreCase("active")){
            navigation.getMenu().findItem(R.id.active_chants).setChecked(true);
            transaction.replace(R.id.content,new ActiveChantsFragment()).commit();
        } else if (mChantType.equalsIgnoreCase("settings")){
            navigation.getMenu().findItem(R.id.settings).setChecked(true);
            transaction.replace(R.id.content,new SettingFragment()).commit();
        }

    }

    private void getBundle() {
        Intent intent = getIntent();
        mChantType = intent.getStringExtra("chant type");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.selfChanting:
                SelfChantingFragment frag = new SelfChantingFragment();
                Bundle bundle = new Bundle();
                bundle.putString(frag.CHANT_TYPE, "self");
                frag.setArguments(bundle);
                transaction.replace(R.id.content,frag).commit();
                return true;
            case R.id.karmic_chanting:
//                SelfChantingFragment frag1 = new SelfChantingFragment();
//                Bundle bundle1 = new Bundle();
//                bundle1.putString(frag1.CHANT_TYPE, "karmic");
//                frag1.setArguments(bundle1);
                transaction.replace(R.id.content, new KarmicChantingFragment()).commit();
                return true;
            case R.id.active_chants:
                transaction.replace(R.id.content,new ActiveChantsFragment()).commit();
                return true;
            case R.id.settings:
                transaction.replace(R.id.content,new SettingFragment()).commit();
                return true;
        }
        return false;
    }
}
