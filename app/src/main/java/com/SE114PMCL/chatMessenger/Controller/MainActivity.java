package com.SE114PMCL.chatMessenger.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.SE114PMCL.chatMessenger.Friends;
import com.SE114PMCL.chatMessenger.Model.FriendData;
import com.SE114PMCL.chatMessenger.Model.UserModel;
import com.SE114PMCL.chatMessenger.R;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import Main.AccountTab.Setting;
import Main.ChatTab.Chat;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{
    Toolbar toolbar;
    BottomNavigationView bottomNav;

    CircleImageView image_chat;
    TextView username_chat;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        toolbar=findViewById(R.id.toolbarUsername);

        image_chat = (CircleImageView) findViewById(R.id.chatImage);
        username_chat = (TextView) findViewById(R.id.chatName);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FriendData friendData = dataSnapshot.getValue(FriendData.class);
                username_chat.setText(friendData.getUsername());
                if(friendData.getImageURL().equals("default")){
                    image_chat.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MainActivity.this).load(friendData.getImageURL()).into(image_chat);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Chat()).commit();
        }
        getSupportActionBar().hide();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    Fragment selectedFragment=null;
                    switch (item.getItemId()) {
                        case R.id.nav_chat:
                            selectedFragment = new Chat();
                            break;
                        case R.id.nav_friends:
                            selectedFragment = new Friends();
                            break;
                        case R.id.nav_setting:
                            selectedFragment = new Setting();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_fade_exit).replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    private void Reset(int name) {

    }
    @Override
    public void onBackPressed() {
        NavController navController;
        int seletedItemId = bottomNav.getSelectedItemId();
        int currentFragment;
        switch (seletedItemId) {
            case R.id.nav_chat:
                navController = Navigation.findNavController(this, R.id.fragmentContainerView2);
                currentFragment=navController.getCurrentDestination().getId();
                if(R.id.messenger==currentFragment) {
                    /*getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                    TextView textView=findViewById(R.id.chatName);
                    textView.setText(R.string.userName);*/
                    getSupportActionBar().hide();
                    NavOptions.Builder navBuilder =  new NavOptions.Builder();
                    navBuilder.setEnterAnim(R.anim.fragment_open_enter).setExitAnim(R.anim.fragment_close_exit).setPopEnterAnim(R.anim.fragment_open_enter).setPopExitAnim(R.anim.fragment_close_exit);
                    navController.navigate(R.id.user,null,navBuilder.build());
                    bottomNav.setVisibility(View.VISIBLE);
                    //CircleImageView circleImageView=findViewById(R.id.chatImage);
                    //circleImageView.setImageResource(R.drawable.avatar3);
                    //navController.navigate(R.id.user);
                }
                break;
            case R.id.nav_friends:
                navController = Navigation.findNavController(this, R.id.fragmentContainerView3);
                currentFragment=navController.getCurrentDestination().getId();
                if(R.id.friendList==currentFragment || R.id.groupList==currentFragment) {
                    bottomNav.setVisibility(View.VISIBLE);
                    navController.popBackStack();
                    setSupportActionBar(toolbar);
                    /*getSupportActionBar().setDisplayShowTitleEnabled(false);
                    getSupportActionBar().show();*/
                }
                break;
            /*case R.id.nav_setting:
                navController = Navigation.findNavController(this, R.id.fragmentContainerView3);
                currentFragment=navController.getCurrentDestination().getId();
                if(R.id.friendList==currentFragment) {
                    Reset(R.string.userName);
                    navController.navigate(R.id.contact);
                }
                break;*/
        }
        //
        //if (R.id.nav_chat != seletedItemId) {
        //    setHomeItem(MainActivity.this);
       // } else {
        //    super.onBackPressed();
        //}
    }

    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
    }
}

