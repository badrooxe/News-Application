package com.example.newsproject;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<List_item> ListItem;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv = findViewById(R.id.Lv);
        ListItem = new ArrayList<>();
        swipeRefreshLayout = findViewById(R.id.swiper);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 4000);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Uri uri = Uri.parse(ListItem.get(i).link);
                //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //startActivity(intent);
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                intent.putExtra("link",ListItem.get(adapterView.getPositionForView(view)).getLink());
                                                          //may cause problems
                                                          //tried a hundred codes
                                                          //this is the only one that worked
                startActivity(intent);
            }
        });

        new ProcessInBackground().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.Search){

        }else if(id==R.id.Settings){

        }else if(id==R.id.About){

        }else if(id==R.id.Share){

        }else if(id==R.id.Exit){

        }
        return true;
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Exception doInBackground(Integer... integers) {

            try {

                URL xmlurl = new URL("https://www.beinsports.com/ar/rss.xml");//bein sports



                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(xmlurl), "UTF_8");

                boolean GetItem = false;

                ArrayList<String> title = new ArrayList<>();
                ArrayList<String> link = new ArrayList<>();
                ArrayList<String> category = new ArrayList<>();
                ArrayList<String> description = new ArrayList<>();
                ArrayList<String> link_img = new ArrayList<>();

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")) {
                            GetItem = true;
                        }else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (GetItem) {
                                title.add(xpp.nextText());
                            }
                        }else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (GetItem) {
                                link.add(xpp.nextText());
                            }
                        }else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (GetItem) {
                                description.add(xpp.nextText());
                            }
                        }else if (xpp.getName().equalsIgnoreCase("image")) {
                            if (GetItem) {
                                link_img.add(xpp.getAttributeValue(0));
                            }
                        }else if (xpp.getName().equalsIgnoreCase("category")) {
                            if (GetItem) {
                                category.add(xpp.nextText());
                            }
                        }

                    }else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        GetItem = false;
                    }

                    eventType = xpp.next();
                }

                for (int i = 0; i < title.size(); i++) {
                    List_item ls = new List_item();
                    ls.setTitle(title.get(i));
                    ls.setCategory(category.get(i));
                    ls.setDescription(description.get(i));
                    ls.setLink(link.get(i));
                    ls.setImg(link_img.get(i));
                    ListItem.add(ls);
                }


            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            listAdapter list_Adapter = new listAdapter(ListItem);
            lv.setAdapter(list_Adapter);

        }
    }

    class listAdapter extends BaseAdapter {
        ArrayList<List_item> listItem = new ArrayList<>();

        listAdapter(ArrayList<List_item> list) {
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

            title.setText(listItem.get(i).title);
            Text_category.setText(listItem.get(i).category);

            String Des = (listItem.get(i).description);
            Text_description.setText(Html.fromHtml(Des, Html.FROM_HTML_MODE_COMPACT));

            try {
                Picasso.with(MainActivity.this).load(listItem.get(i).Img)
                        .error(R.drawable.ic_action_img)
                        .placeholder(R.drawable.ic_action_img).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(img);
            } catch (Exception e) {
            }
            return view1;
        }
    }

}