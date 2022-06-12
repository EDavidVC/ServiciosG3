package com.fprs6.serviciosg3;


import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.fprs6.serviciosg3.objects.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class registerActivity extends AppCompatActivity {
    private EditText email;
    private EditText pass;
    private EditText repitePass;


    private ProgressDialog progressDialog;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Instance Elements of the activity
        email           = (EditText) findViewById(R.id.RGEmail);
        pass            = (EditText) findViewById(R.id.RGPass);
        repitePass      = (EditText) findViewById(R.id.RGRepitePass);
        // Action Bar Propierties
        ActionBar actionbar = getSupportActionBar();
        auth = FirebaseAuth.getInstance();

        actionbar.setTitle("Crear Cuenta");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validando Datos...");

    }
    public void register(View view){
        String emailText    = email.getText().toString();
        String passText     = pass.getText().toString();
        String repitePassText = repitePass.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Correo Erroneo!!!");
            email.setFocusable(true);
        }else if( passText.length() < 7 ){
            pass.setError("Su contraseña es muy corta\n(8 caracteres Minimo)");
            pass.setFocusable(true);
        }else if(!passText.equals(repitePassText)){
            repitePass.setError("Las Contraseñas No Coenciden");
            repitePass.setFocusable(true);
        }else {
            registerUser(emailText, passText);
        }
    }
    private void registerUser(String emailText, String passText){
        progressDialog.show();

        auth.createUserWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = auth.getCurrentUser();

                            String emailUser = user.getEmail();
                            String uidUser = user.getUid();

                            HashMap<Object, String> userEstructure = new HashMap<>();
                            userEstructure.put("email", emailUser);
                            userEstructure.put("uid", uidUser);
                            userEstructure.put("userName", "user_"+uidUser.substring(0, 10));
                            userEstructure.put("description", "");
                            userEstructure.put("address", "");
                            userEstructure.put("phone", "");
                            userEstructure.put("ageOld", "");
                            userEstructure.put("profileImage", "");
                            userEstructure.put("profileCoverImage", "");
                            HashMap<String, Object> structure = new HashMap<>();

                            structure.put("actived", false);
                            structure.put("professional_uid", uidUser);
                            structure.put("professional_valoration", 0.0);
                            structure.put("references_image", new ArrayList<String>());
                            structure.put("references_document", new ArrayList<String>());
                            structure.put("service_abstract", "");
                            structure.put("service_description_general", "");
                            structure.put("service_type", "default");
                            structure.put("price_base", "");
                            structure.put("comments", 0);

                            FirebaseDatabase db = FirebaseDatabase.getInstance();


                            DatabaseReference reference = db.getReference("users");
                            reference.child(uidUser).setValue(userEstructure);

                            reference = db.getReference("professionals");
                            reference.child(uidUser).setValue(structure);




                            Toast.makeText(registerActivity.this, "El Registro Fue Exitoso",Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(registerActivity.this, dashboardActivity.class);
                            startActivity(i);

                            finish();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(registerActivity.this, "Error al registrar\nintentelo mas tarde",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(registerActivity.this, "Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }


    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}