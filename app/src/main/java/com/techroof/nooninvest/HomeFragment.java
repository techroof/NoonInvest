package com.techroof.nooninvest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.nooninvest.Adapters.HomeFragmentRecyclerViewAdapter;
import com.techroof.nooninvest.Authentication.LoginActivity;
import com.techroof.nooninvest.ModelClass.ProductsData;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "MainActivity";
    private ArrayList<ProductsData> productClothingArrayList, productTechArrayList, productAutoMotiveArrayList, productHomeAppliances;
    private HomeFragmentRecyclerViewAdapter recyclerViewAdapter;
    RecyclerView clothingRv, techRv, homeApliancesRv, AutomotivesRv;
    private FirebaseFirestore firestore;
    private HomeFragmentRecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManagerclothing, layoutManagertech, layoutManagerhomeAppliances,
            layoutManagerautoMotive;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog pd;

/*
    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //declerations

        clothingRv = view.findViewById(R.id.clothing_rv);
        homeApliancesRv = view.findViewById(R.id.home_appliancesrv);
        techRv = view.findViewById(R.id.techrv);
        AutomotivesRv = view.findViewById(R.id.automotiverv);
        firestore = FirebaseFirestore.getInstance();

        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        pd=new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        pd.show();

        //arraylist decleration
        productClothingArrayList = new ArrayList<>();
        productTechArrayList = new ArrayList<>();
        productHomeAppliances = new ArrayList<>();
        productAutoMotiveArrayList = new ArrayList<>();

       //methods
        getClothingProduct();
        getTechProduct();
        getHomeAppliances();
        getAutoMotives();
/*
        firebaseAuth=FirebaseAuth.getInstance();
         authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Toast.makeText(getContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                }
            }
        };*/





        return view;

    }

    private void getAutoMotives() {

        firestore.collection("products")
                .whereEqualTo("category", "AutoMotives")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                    ProductsData listData = documentSnapshot.toObject(ProductsData.class);
                    productAutoMotiveArrayList.add(listData);

                }

                layoutManagerautoMotive = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                AutomotivesRv.setLayoutManager(layoutManagerautoMotive);
                adapter = new HomeFragmentRecyclerViewAdapter(productAutoMotiveArrayList,
                        getContext());
                AutomotivesRv.setAdapter(adapter);

                pd.dismiss();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });


    }


    private void getHomeAppliances() {

        firestore.collection("products")
                .whereEqualTo("category", "HomeAppliances")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                    ProductsData listData = documentSnapshot.toObject(ProductsData.class);
                    productHomeAppliances.add(listData);


                }

                layoutManagerhomeAppliances = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                homeApliancesRv.setLayoutManager(layoutManagerhomeAppliances);
                adapter = new HomeFragmentRecyclerViewAdapter(productHomeAppliances, getContext());
                homeApliancesRv.setAdapter(adapter);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });


    }


    private void getTechProduct() {

        firestore.collection("products")
                .whereEqualTo("category", "Tech")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                    ProductsData listData = documentSnapshot.toObject(ProductsData.class);
                    productTechArrayList.add(listData);


                }
                layoutManagertech = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                techRv.setLayoutManager(layoutManagertech);
                adapter = new HomeFragmentRecyclerViewAdapter(productTechArrayList, getContext());
                techRv.setAdapter(adapter);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });


    }


    private void getClothingProduct() {

        firestore.collection("products")
                .whereEqualTo("category", "Clothing")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        ProductsData listData = documentSnapshot.toObject(ProductsData.class);
                        productClothingArrayList.add(listData);


                    }

                    layoutManagerclothing = new LinearLayoutManager(getContext(),
                            LinearLayoutManager.HORIZONTAL, false);
                    clothingRv.setLayoutManager(layoutManagerclothing);
                    adapter = new HomeFragmentRecyclerViewAdapter(productClothingArrayList, getContext());
                    clothingRv.setAdapter(adapter);

                }else{
                    pd.dismiss();
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });


    }


   /* @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser=firebaseAuth.getCurrentUser();

        if(currentUser!=null){
            Intent login=new Intent(getContext(), LoginActivity.class);
            startActivity(login);
        }

    }*/


}