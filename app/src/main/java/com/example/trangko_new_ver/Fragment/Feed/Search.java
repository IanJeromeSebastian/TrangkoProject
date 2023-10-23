package com.example.trangko_new_ver.Fragment.Feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trangko_new_ver.Adapter.AdapterUsers;
import com.example.trangko_new_ver.Model.ModelUser;
import com.example.trangko_new_ver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    androidx.appcompat.widget.SearchView searchView;
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    FirebaseAuth firebaseAuth;


    public Search() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        searchView = view.findViewById(R.id.searchBuddy);
        recyclerView = view.findViewById(R.id.users_recyclerView);
        //set
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userList = new ArrayList<>();

        getAllUsers();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!TextUtils.isEmpty(query.trim())){

                    searchUser(query);

                }else {

                    getAllUsers();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!TextUtils.isEmpty(newText.trim())){

                    searchUser(newText);

                }else {

                    getAllUsers();
                }

                return false;
            }
        });

        return view;
    }

    private void searchUser(String query) {

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    //get all search users except currently signed in user
                    if (!modelUser.getUID().equals(fUser.getUid())){
                        if (modelUser.getUserName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase()))
                            userList.add(modelUser);
                    }
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllUsers() {
        //current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        System.out.println("Firebase Current user "+ fUser.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    //get all users except currently signed in user
                    System.out.println("Firebase Current username "+ modelUser.getUserName());
                    System.out.println("Firebase Current discription "+ modelUser.getDesciption());
                    System.out.println("Model User  "+ modelUser.getUID());

                    if (!modelUser.getUID().equals(fUser.getUid())){

                        userList.add(modelUser);
                    }
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}