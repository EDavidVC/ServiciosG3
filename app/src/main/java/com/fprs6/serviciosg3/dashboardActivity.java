package com.fprs6.serviciosg3;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.fprs6.serviciosg3.objects.ModelProfessionals;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class dashboardActivity extends AppCompatActivity {
   private FirebaseAuth firebaseAuth;
   private FirebaseUser firebaseUser;
   private static FragmentTransaction ftransaction;
   private Fragment homeFrag, profileFrag;
   private FirebaseDatabase firebaseDatabase;
   private ActionBar actionBar;
   private Fragment viewListProfile;
   private Fragment professionalProfileFragment;
   private Fragment chatListFragment;
   private DatabaseReference databaseReference;
   private BottomNavigationView navigationView;

   private ModelProfessionals modelProfessionals;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        //databaseReference = firebaseDatabase.getReference("users");

        modelProfessionals = new ModelProfessionals();

        homeFrag        = new HomeFragment(dashboardActivity.this);
        profileFrag     = new ProfileFragment(modelProfessionals);
        viewListProfile = new professionalListFragment();
        professionalProfileFragment = new professionalProfileFragment();
        chatListFragment = new chatListFragment();



        actionBar = getSupportActionBar();
        actionBar.setTitle("Inicio");
        navigationView = findViewById(R.id.Gnavigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        navigationView.getMenu().removeItem(R.id.nav_professional);


        getSupportFragmentManager().beginTransaction().add(R.id.FTcontent, homeFrag).commit();
    }

    public void swithProfessionalViewList(String windowSelection, String typeService){

        actionBar.setTitle(windowSelection);

        /*
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        */
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        viewListProfile = new professionalListFragment(dashboardActivity.this, typeService);
        ftransaction = getSupportFragmentManager().beginTransaction();
        ftransaction.replace(R.id.FTcontent, viewListProfile, "");
        ftransaction.commit();
    }

    public void showProfileOfProfessionalSelected(String p_uid){

        Intent professionalProfileServiceIntent = new Intent(dashboardActivity.this, professionalProfileService.class);

        professionalProfileServiceIntent.putExtra("p_uid", p_uid);

        startActivity(professionalProfileServiceIntent);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    ftransaction = getSupportFragmentManager().beginTransaction();
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            actionBar.setTitle("Inicio");
                            ftransaction.replace(R.id.FTcontent, homeFrag, "");
                            ftransaction.commit();
                            return true;
                        case R.id.nav_profile:
                            actionBar.setTitle("Mi Perfil");
                            ftransaction.replace(R.id.FTcontent, profileFrag, "");
                            ftransaction.commit();
                            return true;
                        case  R.id.nav_professional:
                            actionBar.setTitle("Profesional");
                            ftransaction.replace(R.id.FTcontent, professionalProfileFragment, "");
                            ftransaction.commit();
                            return true;
                        case  R.id.nav_chats:
                            actionBar.setTitle("Chats");
                            ftransaction.replace(R.id.FTcontent, chatListFragment , "");
                            ftransaction.commit();
                            return true;
                    }
                    return false;
                }
            };
    public void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            checkIfIsProfessional(user);
        }else{
            Intent i = new Intent(dashboardActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    private void checkIfIsProfessional(FirebaseUser fUser){
        databaseReference = firebaseDatabase.getReference("professionals");
        Query query = databaseReference.orderByChild("professional_uid").equalTo(fUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    modelProfessionals = ds.getValue(ModelProfessionals.class);
                    profileFrag = new ProfileFragment(modelProfessionals);

                    if(modelProfessionals.isActived()){
                        if(navigationView.getMenu().size() == 3){
                            navigationView.getMenu().add(Menu.NONE,R.id.nav_professional,Menu.NONE, "").setIcon(R.drawable.ic_professional_black).setTitle("Profesional");
                        }
                    }
                    else{
                        navigationView.getMenu().removeItem(R.id.nav_professional);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id ==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

}