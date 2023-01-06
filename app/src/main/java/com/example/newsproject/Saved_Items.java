package com.example.newsproject;

import android.annotation.SuppressLint;
import android.app.LauncherActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Saved_Items  extends AppCompatActivity {
    ListView listView;
    List<List_item> list;
    listAdapter adapter;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);
        listView = (ListView) findViewById(R.id.savelist);
        list = new ArrayList<>();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(new HelperClass().getMacAddr());
        adapter = new listAdapter((ArrayList<List_item>) list);
        listView.setAdapter(adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren() ){
                    List_item a = postSnapshot.getValue(List_item.class);
                    a.setKey(postSnapshot.getKey());
                    list.add(a);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
                progress_bar.setVisibility(View.INVISIBLE);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                intent.putExtra("link",list.get(i).getLink());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        return true;
    }

    class listAdapter extends BaseAdapter {
        ArrayList<List_item> listItem = new ArrayList<>();
        //constructor
        public listAdapter(ArrayList<List_item> list) {
            this.listItem = list;
        }

        @Override
        public int getCount() {
            return listItem.size();
        }

        @Override
        public Object getItem(int i) {
            return listItem.get(i).title;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();

            @SuppressLint("ViewHolder")
            View view1 = layoutInflater.inflate(R.layout.items, null);

            TextView title = view1.findViewById(R.id.Text_title);
            TextView Text_category = view1.findViewById(R.id.Text_category);
            TextView Text_description = view1.findViewById(R.id.Text_description);
            ImageView img = view1.findViewById(R.id.Img);
            ImageView star = (ImageView) view1.findViewById(R.id.star);

            title.setText(listItem.get(i).title);
            Text_category.setText(listItem.get(i).category);

            String Des = (listItem.get(i).description);
            Text_description.setText(Html.fromHtml(Des, Html.FROM_HTML_MODE_COMPACT));

            try {
                Picasso.get().load(listItem.get(i).Img)
                        .error(R.drawable.ic_action_img)
                        .placeholder(R.drawable.ic_action_img).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(img);
            } catch (Exception e) {
            }
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(new HelperClass().getMacAddr());
            if (listItem.get(i).isSaved()) new HelperClass().fillStar(star);


            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listItem.get(i).isSaved()) {
                        try {
                            Picasso.get().load(R.drawable.star)
                                    .error(R.drawable.ic_action_img)
                                    .placeholder(R.drawable.star).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .into(star);

                        } catch (Exception e) {}
                        myRef.child(listItem.get(i).getKey()).removeValue();
                        listItem.get(i).setSaved(false);
                    }else{
                        new HelperClass().save(star,listItem,i);
                    }
                }
            });

            return view1;
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_to_left,R.anim.slide_from_right);
    }
}
