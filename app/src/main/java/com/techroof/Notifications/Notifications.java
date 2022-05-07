package com.techroof.Notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.nooninvest.Adapters.NotificationRecyclerViewAdapter;
import com.techroof.nooninvest.R;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private NotificationRecyclerViewAdapter recyclerViewAdapter;
    RecyclerView notificationRv;
    private LinearLayoutManager layoutManagerdashboard;
    private ImageView imgBack;
    private ArrayList<com.techroof.nooninvest.ModelClass.Notifications> notificationsArrayList;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        notificationsArrayList=new ArrayList<>();
        notificationRv=findViewById(R.id.notification_rv);
        imgBack=findViewById(R.id.img_back_arrow);
        uid=firebaseAuth.getCurrentUser().getUid();


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Notifications.super.onBackPressed();

            }
        });

        Notifications();


    }

    private void Notifications(){


            uid = firebaseAuth.getCurrentUser().getUid();
            //Toast.makeText(getContext(), ""+uId, Toast.LENGTH_SHORT).show();

            firebaseFirestore.collection("Notifications").whereEqualTo("Uid", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {


                        for (QueryDocumentSnapshot document : task.getResult()) {

                           com.techroof.nooninvest.ModelClass.Notifications listData = document.toObject(com.techroof.nooninvest.ModelClass.Notifications.class);
                            notificationsArrayList.add(listData);

                        }

                        layoutManagerdashboard = new LinearLayoutManager(getApplicationContext(),
                                LinearLayoutManager.VERTICAL, false);
                        notificationRv.setLayoutManager(layoutManagerdashboard);

                        recyclerViewAdapter=new NotificationRecyclerViewAdapter(notificationsArrayList,
                                getApplicationContext());
                        notificationRv.setAdapter(recyclerViewAdapter);


                    }

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

