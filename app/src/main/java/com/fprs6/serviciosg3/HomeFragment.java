package com.fprs6.serviciosg3;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fprs6.serviciosg3.adapters.AdapterService;
import com.fprs6.serviciosg3.objects.ModelServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterService adapterService;
    private List<ModelServices> servicesList;
    private dashboardActivity dActictivity;

    public HomeFragment(){}


    public HomeFragment(dashboardActivity dActictivity) {
        this.dActictivity = dActictivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.services_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        servicesList = new ArrayList<>();

        getAllServices();

        return view;
    }

    private void getAllServices() {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("service_type");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                servicesList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelServices modelServices = ds.getValue(ModelServices.class);

                    servicesList.add(modelServices);

                    adapterService = new AdapterService(dActictivity,getActivity(), servicesList);

                    recyclerView.setAdapter(adapterService);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}