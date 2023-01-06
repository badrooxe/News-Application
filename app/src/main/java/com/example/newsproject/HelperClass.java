package com.example.newsproject;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelperClass {

    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public void fillStar(ImageView star){

        try {
            Picasso.get().load(R.drawable.saved)
                    .error(R.drawable.ic_action_img)
                    .placeholder(R.drawable.saved).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(star);

        } catch (Exception e) {
        }
    }
    public void unfillStar(ImageView star){

        try {
            Picasso.get().load(R.drawable.star)
                    .error(R.drawable.ic_action_img)
                    .placeholder(R.drawable.star).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(star);

        } catch (Exception e) {
        }
    }

    public void save(ImageView star, ArrayList<List_item> ListItem, int i){
        fillStar(star);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(new HelperClass().getMacAddr());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myRef.child(String.valueOf(hashCode())).setValue(ListItem.get(i));
                } else {
                    myRef.child(String.valueOf(hashCode())).setValue(ListItem.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        ListItem.get(i).setSaved(true);

    }











}