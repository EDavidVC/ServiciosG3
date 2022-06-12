package com.fprs6.serviciosg3;

import android.graphics.ColorSpace;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.adapters.AdapterProfessional;
import com.fprs6.serviciosg3.adapters.AdapterService;
import com.fprs6.serviciosg3.objects.ModelProfessionals;
import com.fprs6.serviciosg3.objects.ModelServices;
import com.fprs6.serviciosg3.objects.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class professionalListFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterProfessional adapterProfessional;
    private List<ModelProfessionals> professionalsList;
    private dashboardActivity dsBoard;
    private String typeService;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    public professionalListFragment(){}
    public professionalListFragment(dashboardActivity dsBoard, String typeService) {
        this.dsBoard = dsBoard; this.typeService = typeService;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professional_list, container, false);

        recyclerView = view.findViewById(R.id.professionals_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        professionalsList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        getAllProfessionals();

        return view;
    }
    private void getAllProfessionals() {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("professionals");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                professionalsList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelProfessionals modelProfessionals = ds.getValue(ModelProfessionals.class);
                    if(modelProfessionals.isActived() && !modelProfessionals.getProfessional_uid().equals(firebaseUser.getUid())){
                        if(modelProfessionals.getService_type().equals(typeService)){
                            professionalsList.add(modelProfessionals);
                        }
                    }
                    adapterProfessional = new AdapterProfessional(dsBoard, getActivity(), professionalsList);
                    recyclerView.setAdapter(adapterProfessional);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}