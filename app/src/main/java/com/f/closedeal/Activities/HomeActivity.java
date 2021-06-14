package com.f.closedeal.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.f.closedeal.Activities.StartUpActivities.LoginSignUp;
import com.f.closedeal.Fragments.ChatListFragment;
import com.f.closedeal.Fragments.ChatsFragment;
import com.f.closedeal.Fragments.GroupChatsFragment;
import com.f.closedeal.Fragments.HomeFragment;
import com.f.closedeal.Fragments.ProfileFragment;
import com.f.closedeal.Fragments.UsersFragment;
import com.f.closedeal.Notifications.Token;
import com.f.closedeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    MeowBottomNavigation navigationView;

    ActionBar actionBar;
    String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        actionBar = getSupportActionBar();

        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);

        actionBar.setTitle("Close Deal");
        actionBar.setLogo(R.drawable.main_logo);
        actionBar.setDisplayUseLogoEnabled(true);


        checkUserStatus();

        navigationView.add(new MeowBottomNavigation.Model(1, R.drawable.home_icon));
        navigationView.add(new MeowBottomNavigation.Model(2, R.drawable.chat_icon));
        navigationView.add(new MeowBottomNavigation.Model(3, R.drawable.add_icon));
        navigationView.add(new MeowBottomNavigation.Model(4, R.drawable.users_icon));
        navigationView.add(new MeowBottomNavigation.Model(5, R.drawable.profile_icon));

        navigationView.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

                Fragment fragment = null;

                switch (item.getId()) {
                    case 1:
                        actionBar.setTitle("Close Deal");
                        fragment = new HomeFragment();
                        break;

                    case 2:
                        actionBar.setTitle("Chats");
                         fragment = new ChatsFragment();
                       // showMoreOptions();
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), PostActivity.class));
                    case 4:
                        actionBar.setTitle("Users");
                        fragment = new UsersFragment();

                        break;
                    case 5:

                        actionBar.setTitle("Profile");
                        fragment = new ProfileFragment();
                        break;
                }
                loadFragment(fragment);

            }
        });

        navigationView.show(1, true);
        navigationView.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
            }
        });
        navigationView.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });


    }

    private void showMoreOptions() {

        PopupMenu popupMenu = new PopupMenu(this, navigationView, Gravity.END);
        popupMenu.getMenu().add(Menu.NONE, 0,0, "My Private Chats");
        popupMenu.getMenu().add(Menu.NONE, 1,0, "Group Chats");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    ChatListFragment fragment6 = new ChatListFragment();
                    FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
                    ft6.replace(R.id.content, fragment6, "");
                    ft6.commit();
                }
                else if (id ==1){
                    GroupChatsFragment fragment7 = new GroupChatsFragment();
                    FragmentTransaction ft7 = getSupportFragmentManager().beginTransaction();
                    ft7.replace(R.id.content, fragment7, "");
                    ft7.commit();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();

    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);

    }


    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

            updateToken(FirebaseInstanceId.getInstance().getToken());

        } else {
            startActivity(new Intent(getApplicationContext(), LoginSignUp.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
}