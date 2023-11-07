package com.example.cameinw.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cameinw.R;
import com.example.cameinw.activities.MainActivity;
import com.example.cameinw.activities.MessageActivity;
import com.example.cameinw.activities.ReservationActivity;
import com.example.cameinw.activities.SearchActivity;
import com.example.cameinw.activities.UserProfileActivity;
import com.example.cameinw.activities.ViewOwnerPlacesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MenuUtils{
    private static BottomNavigationView bottomNav;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private static Toolbar toolbar;

    private static void setBottomMenu(BottomNavigationView bottomNav, Context context) {
        bottomNav.setOnItemSelectedListener(item -> {
            if (((Integer)item.getItemId()).equals(R.id.navB_search)) {
                Intent searchIntent = new Intent(context, SearchActivity.class);
                context.startActivity(searchIntent);
            } else if (((Integer)item.getItemId()).equals(R.id.navB_message)) {
                Intent messageIntent = new Intent(context, MessageActivity.class);
                context.startActivity(messageIntent);
            } else if (((Integer)item.getItemId()).equals(R.id.navB_profile)) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", "");
                editor.putString("id", "");
                Intent profileIntent = new Intent(context, UserProfileActivity.class);
                context.startActivity(profileIntent);
            }
            return true;
        });
    }

    public static void setSideMenu(DrawerLayout drawer, NavigationView navigationView, Toolbar toolbar, Context context) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle((Activity) context, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (((Integer)item.getItemId()).equals(R.id.nav_reservations)) {
                Intent reservaitonIntent = new Intent(context, ReservationActivity.class);
                context.startActivity(reservaitonIntent);
            } else if (((Integer)item.getItemId()).equals(R.id.nav_logOut)) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", "");
                editor.putString("id", "");
                editor.apply();
                Intent mainIntent = new Intent(context, MainActivity.class);
                context.startActivity(mainIntent);
            } else if (((Integer)item.getItemId()).equals(R.id.nav_places)) {
                Intent viewPlacesIntent = new Intent(context, ViewOwnerPlacesActivity.class);
                context.startActivity(viewPlacesIntent);
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public static void setMenus(String title, Context context) {
        bottomNav = ((AppCompatActivity) context).findViewById(R.id.bottom_nav);
        drawer = ((AppCompatActivity) context).findViewById(R.id.drawer_layout);
        navigationView = ((AppCompatActivity) context).findViewById(R.id.nav_view);
        toolbar =  ((AppCompatActivity) context).findViewById(R.id.toolbar);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        TextView tool = ((AppCompatActivity) context).findViewById(R.id.toolbarText);
        tool.setText(title);
        ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) context).getSupportActionBar().setDisplayShowHomeEnabled(true);

        setBottomMenu(bottomNav, context);
        setSideMenu(drawer, navigationView, toolbar, context);
    }

    public static void setTopBar(String title, Context context) {
        toolbar = ((AppCompatActivity) context).findViewById(R.id.toolbar);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);

        ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) context).getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView tool = ((AppCompatActivity) context).findViewById(R.id.toolbarText);
        tool.setText(title);
    }
}
