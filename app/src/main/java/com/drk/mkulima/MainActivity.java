package com.drk.mkulima;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    private RecyclerView recyclerview;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private ProductsAdapter productsAdapter;
    private List<Products> productsLists = new ArrayList<>();
    private Context mContext;
    public DrawerLayout drawer;
    private NavigationView navigationView;
   // private AVLoadingIndicatorView loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=MainActivity.this;

//        tv_noitem=findViewById(R.id.tv_main_activity_no_item);
       // loader=findViewById(R.id.loader_main_activity);
        //loader.setClickable(false);

        Toolbar toolbar = findViewById(R.id.toolbar); // toolbar initialization
        setSupportActionBar(toolbar);   // setting it on action bar

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // cart activity Implemented on floating button
                Intent orderIntent = new Intent(MainActivity.this, CartListActivity.class);
                startActivity(orderIntent);
                // redirect to cart

            }
        });

         drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawer,toolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        // new code implementation
        mContext=getApplicationContext();
        // Todo ::  home layout implementation

        recyclerview = findViewById(R.id.recyclerview);
        productsAdapter = new ProductsAdapter(productsLists, mContext);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(productsAdapter);
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        if(savedInstanceState==null){
            product_listing("Fruits");
            navigationView.setCheckedItem(R.id.nav_fruits);
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            product_listing("Fruits"); // this will execute when page rotation/ refresh take place
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout_tn) {
            startActivity(new Intent(getApplicationContext(), login.class));
            finishAffinity();
            mAuth.signOut();
            return true;
        } else if (id == R.id.action_notification) {
            Toast.makeText(getApplicationContext(), "No notification", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_my_account) {
            startActivity(new Intent(getApplicationContext(), MyAccount.class));
            return true;
        } else if (id == R.id.action_transaction) {
            startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), OrderActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert!");
            builder.setMessage("Do you want to Exit");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    finishAffinity();

                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();  // Show the Alert Dialog box
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

       switch (item.getItemId()){
           case R.id.nav_fruits:
               product_listing("Fruits");
               break;
           case R.id.nav_vegetables:
               product_listing("Vegetables");
               break;
           case R.id.nav_spices :
               product_listing("Spices");
               break;
           case R.id.nav_cereals :
               product_listing("cereals");
               break;
           case R.id.nav_legumes:
               product_listing("Legumes");
               break;
           case R.id.nav_animal_products :
               product_listing("Animal Products");
               break;
           case R.id.nav_medicinal:
               product_listing("Medicinal");
               break;
           case R.id.nav_flowers:
               product_listing("Flowers");
               break;


           case R.id.nav_mycart:
               startActivity(new Intent(getApplicationContext(),CartListActivity.class));
               break;
           case R.id.nav_myorder:
               startActivity(new Intent(getApplicationContext(),OrderActivity.class));
               break;
           case R.id.nav_transaction_details:
               startActivity(new Intent(getApplicationContext(),TransactionActivity.class));
               break;
           case R.id.nav_my_account:
               startActivity(new Intent(getApplicationContext(),MyAccount.class));
               break;
           case R.id.nav_logout:
               startActivity(new Intent(getApplicationContext(),login.class));
               finishAffinity();
               mAuth.signOut();
               break;


           case R.id.nav_legal:
               break;
           case R.id.nav_about_developer:
               Intent viewIntent =new Intent("android.intent.action.VIEW",Uri.parse("https://android.com/"));
               startActivity(viewIntent);
               break;

           default:
       }


       drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void product_listing(String product_type){
        productsLists.clear();
        productsAdapter.notifyDataSetChanged();

//        tv_noitem.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle(product_type.toUpperCase()); // change the Actionbar title

       // loader.setVisibility(View.VISIBLE);
        if (product_type != null) {
            mDatabase.child("products").child(product_type).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
//                        tv_noitem.setVisibility(INVISIBLE);
                        Products products = dataSnapshot.getValue(Products.class);
                        productsLists.add(products);
                        productsAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }




}
