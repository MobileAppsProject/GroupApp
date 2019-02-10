package tcss450.uw.edu.phishapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import tcss450.uw.edu.phishapp.chat.ChatMessage;
import tcss450.uw.edu.phishapp.model.Credentials;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    ChatFragment.OnListFragmentInteractionListener,
                    ChatMessageFragment.OnFragmentInteractionListener,
                    WaitFragment.OnFragmentInteractionListener {

    private Credentials myCredentials;
    private String mJwToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home");
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            //This will get the info passed from the last activity!
            myCredentials = (Credentials)getIntent().getSerializableExtra("Login");

            mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));

            //This will bundle the information currently held in the credentials.
            SuccessFragment successFragment = (SuccessFragment) bundleFragment(
                                                new SuccessFragment(),"Success");

            if (null != findViewById(R.id.fragmentContainer)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, successFragment)
                        .commit();
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) return true;

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            setTitle("Home");
            loadFragmentHelper(bundleFragment(new SuccessFragment(), "Success"));
        } else if (id == R.id.nav_weather) {
            setTitle("Weather");
            loadFragmentHelper(new WeatherFragment());
        } else if (id == R.id.nav_my_chats) {
            setTitle("Chats");
            loadFragmentHelper(new ChatFragment());
        } else if (id == R.id.nav_connections) {
            setTitle("Connections");
            loadFragmentHelper(new ConnectionsFragment());
        } else if (id == R.id.nav_search_connections) {
            setTitle("Search Connections");
            loadFragmentHelper(new SearchConnectionFragment());
        }
//        else if (id == R.id.nav_blog_post) {
////            Uri uri = new Uri.Builder()
////                    .scheme("https")
////                    .appendPath(getString(R.string.ep_base_url))
////                    .appendPath(getString(R.string.ep_phish))
////                    .appendPath(getString(R.string.ep_blog))
////                    .appendPath(getString(R.string.ep_get))
////                    .build();
////
////            new GetAsyncTask.Builder(uri.toString())
////                    .onPreExecute(this::onWaitFragmentInteractionShow)
////                    .onPostExecute(this::handleBlogGetOnPostExecute)
////                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
////                    .build().execute();
//
//        }
        //This will close layout after selecting a item.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Helper method to help bundle the fragment and send information;
    private Fragment bundleFragment(Fragment frag, String theSentToFrag) {
        Bundle args = new Bundle();
        args.putSerializable(theSentToFrag, myCredentials); //Pass the credentials to the new frag.
        frag.setArguments(args); //Will make sure they are set.

        return frag;
    }
    //Helper function for loading a fragment.
    private void loadFragmentHelper(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    //This will open of the url of the blog.
    @Override
    public void onUrlBlogPostFragmentInteraction(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    //This will load the wait fragment.
    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    //This will get ride of the wait fragment that was loaded.
    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();


    }

    //This will get the parse the jason object.
    private void handleBlogGetOnPostExecute(final String result) {
        //parse JSON

        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_blogs_response))) {
                JSONObject response = root.getJSONObject(
                        getString(R.string.keys_json_blogs_response));
                if (response.has(getString(R.string.keys_json_blogs_data))) {
                    JSONArray data = response.getJSONArray(
                            getString(R.string.keys_json_blogs_data));

                    List<ChatMessage> blogs = new ArrayList<>();

                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonBlog = data.getJSONObject(i);

                        blogs.add(new ChatMessage.Builder(
                                jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_pubdate)),
                                jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_title)))
                                .addTeaser(jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_teaser)))
                                .addUrl(jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_url)))
                                .build());
                    }

                    ChatMessage[] blogAsArray = new ChatMessage[blogs.size()];
                    blogAsArray = blogs.toArray(blogAsArray);

                    Bundle args = new Bundle();
                    args.putSerializable(ChatFragment.ARG_BLOG_LIST, blogAsArray);
                    Fragment frag = new ChatFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();
                    loadFragmentHelper(frag);

                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }

    @Override
    public void onListFragmentInteraction(ChatMessage item) {
        Bundle arg = new Bundle();
        arg.putSerializable("ChatMessage", item);
        ChatMessageFragment bp = new ChatMessageFragment();
        bp.setArguments(arg);
        loadFragmentHelper(bp);

    }
}
