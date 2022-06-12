package com.fprs6.serviciosg3;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int RG_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;
    private EditText emailEntry;
    private EditText passEntry;
    private ProgressDialog progressDialog;
    private SignInButton googleSigninButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEntry = (EditText)findViewById(R.id.emailEntry);
        passEntry = (EditText)findViewById(R.id.passEntry);
        googleSigninButton = findViewById(R.id.googleSignInButton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);


        googleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

    }
    public void checkLogin(View view){
        String emailText = emailEntry.getText().toString();
        String passText = passEntry.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            emailEntry.setError("Inserte correo valido por favor");
            emailEntry.setFocusable(true);
        }else{
            loginUser(emailText, passText);
        }


    }
    private void loginUser(String emailText, String passText){
        progressDialog.setMessage("Verificando...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Datos Validos", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, dashboardActivity.class);
                            startActivity(i);

                            finish();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error al iniciar Sesion", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    public void switchToRegisterActivity(View view){
        Intent i = new Intent(MainActivity.this, registerActivity.class);
        startActivity(i);
        finish();
    }
    public void recoverPass(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Contraseña");
        LinearLayout llayout = new LinearLayout(this);
        EditText emailRecover = new EditText(this);
        emailRecover.setHint("Correo");
        emailRecover.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailRecover.setMinEms(12);

        llayout.addView(emailRecover);
        llayout.setPadding(10,10,10,10);

        builder.setView(llayout);

        builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String emailRecoverText = emailRecover.getText().toString().trim();
                recoveryPassword(emailRecoverText);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
    private void recoveryPassword(String emailRecoverText){
        progressDialog.setMessage("Verificando...");
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(emailRecoverText).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Correcto, revice sus mensajes en su correo para continuar", Toast.LENGTH_LONG).show();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Error con el correo", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }
    public void googleSignIn(){
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RG_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RG_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GOOGLEACCOUNT", "fireBaseAuthWithGoogle: "+account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e){
                Log.d("Error:", "GoogleSingIn Failed",e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
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
                            }
                            Toast.makeText(MainActivity.this, "Verificado", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this, dashboardActivity.class);
                            startActivity(i);

                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Registro Fallido", Toast.LENGTH_LONG).show();

                            //updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error:", "GoogleSingIn Failed",e);
                    }
                });
    }

}