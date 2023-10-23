package com.example.trangko_new_ver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.example.trangko_new_ver.Chat.ChatListFragment;
import com.example.trangko_new_ver.Fragment.Map;
import com.example.trangko_new_ver.Fragment.Newsfeed;
import com.example.trangko_new_ver.Fragment.Safety;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainPage extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        chipNavigationBar = findViewById(R.id.bottom_nav);
        chipNavigationBar.setVisibility(View.VISIBLE);
        chipNavigationBar.setItemSelected(R.id.safety, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new Safety()).commit();

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i){
                    case R.id.home:
                        fragment = new Newsfeed();
                        break;
                    case R.id.community:
                        fragment = new ChatListFragment();
                        break;
                    case R.id.map:
                        fragment = new Map();
                        break;
                    case R.id.safety:
                        fragment = new Safety();
                        break;
                }

                if (fragment!=null){

                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

                }
            }
        });

    }


}