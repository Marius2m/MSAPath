package com.example.marius.path;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOG_IN_ACTIVITY = 100;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(this, LogInActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOG_IN_ACTIVITY);
//            startActivity(intent);
        } else {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            bottomNav.setOnNavigationItemSelectedListener(bottomNavListener);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOG_IN_ACTIVITY) {
            if(resultCode == RESULT_OK) {
                BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
                bottomNav.setOnNavigationItemSelectedListener(bottomNavListener);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
            } else {
                this.getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    android.support.v4.app.Fragment selectedFrag = null;

                    switch(item.getItemId()){
                        case R.id.nav_home:
                            selectedFrag = new HomeFragment();
                            break;
                        case R.id.nav_add:
                            selectedFrag = new AddFragment();
                            break;
                        case R.id.nav_search:
                            selectedFrag = new SearchFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFrag).commit();

                    return true;
                }
            };
}
