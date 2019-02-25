package team6.uw.edu.amessage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import me.pushy.sdk.Pushy;
import team6.uw.edu.amessage.chat.ChatMessage;
import team6.uw.edu.amessage.model.Credentials;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    ChatFragment.OnListFragmentInteractionListener,
                    ChatMessageFragment.OnFragmentInteractionListener,
                    WaitFragment.OnFragmentInteractionListener,
                    ContactFragment.OnListFragmentInteractionListener {

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

            if (findViewById(R.id.fragmentContainer) != null) {
                Fragment fragment;
                if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
                    fragment = new LabChatFragment();
                    Log.d("test", "onCreate: HOME ACTIVITY should open up chatFrag");
                } else {
                    fragment = (SuccessFragment) bundleFragment(
                            new SuccessFragment(),"Success");
//                     fragment.setArguments(args);
                }

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, fragment)
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

    private void logout() {
        new DeleteTokenAsyncTask().execute();
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
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

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
            /*  THIS WILL ALLOW US TO DYNAMICALLY GET ALL THE CHATS.
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_phish))
                    .appendPath(getString(R.string.ep_blog))
                    .appendPath(getString(R.string.ep_get))
                    .build();

            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleBlogGetOnPostExecute)
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();
              */
//            Fragment chat = new LabChatFragment();
//            Bundle args = new Bundle();
//            args.putString(getString(R.string.keys_intent_credentials), myCredentials.getEmail());
//            args.putString(getString(R.string.keys_intent_jwt), mJwToken);
//            chat.setArguments(args);
//            loadFragmentHelper(chat);
        } else if (id == R.id.nav_connections) {
            setTitle("Connections");
            loadFragmentHelper(new ContactFragment());
        } else if (id == R.id.nav_search_connections) {
            setTitle("Search Connections");
            loadFragmentHelper(new SearchConnectionFragment());
        }
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

//    //This will get the parse the jason object.
//    private void handleBlogGetOnPostExecute(final String result) {
//        //parse JSON
//
//        try {
//            JSONObject root = new JSONObject(result);
//            if (root.has(getString(R.string.keys_json_blogs_response))) {
//                JSONObject response = root.getJSONObject(
//                        getString(R.string.keys_json_blogs_response));
//                if (response.has(getString(R.string.keys_json_blogs_data))) {
//                    JSONArray data = response.getJSONArray(
//                            getString(R.string.keys_json_blogs_data));
//
//                    List<ChatMessage> blogs = new ArrayList<>();
//
//                    for(int i = 0; i < data.length(); i++) {
//                        JSONObject jsonBlog = data.getJSONObject(i);
//
//                        blogs.add(new ChatMessage.Builder(
//                                jsonBlog.getString(
//                                        getString(R.string.keys_json_blogs_pubdate)),
//                                jsonBlog.getString(
//                                        getString(R.string.keys_json_blogs_title)))
//                                .addTeaser(jsonBlog.getString(
//                                        getString(R.string.keys_json_blogs_teaser)))
//                                .addUrl(jsonBlog.getString(
//                                        getString(R.string.keys_json_blogs_url)))
//                                .build());
//                    }
//
//                    ChatMessage[] blogAsArray = new ChatMessage[blogs.size()];
//                    blogAsArray = blogs.toArray(blogAsArray);
//
//                    Bundle args = new Bundle();
//                    args.putSerializable(ChatFragment.ARG_BLOG_LIST, blogAsArray);
//                    Fragment frag = new ChatFragment();
//                    frag.setArguments(args);
//
//                    onWaitFragmentInteractionHide();
//                    loadFragmentHelper(frag);
//
//                } else {
//                    Log.e("ERROR!", "No data array");
//                    //notify user
//                    onWaitFragmentInteractionHide();
//                }
//            } else {
//                Log.e("ERROR!", "No response");
//                //notify user
//                onWaitFragmentInteractionHide();
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("ERROR!", e.getMessage());
//            //notify user
//            onWaitFragmentInteractionHide();
//        }
//    }

    @Override
    public void onListFragmentInteraction(ChatMessage item) {
        Log.w("NotWork", "Chat Item: " + item.getChatId());
        Fragment chat = new ChatMessageFragment();
            Bundle args = new Bundle();
            args.putString(getString(R.string.keys_intent_credentials), myCredentials.getEmail());
            args.putString(getString(R.string.keys_intent_jwt), mJwToken);
            chat.setArguments(args);
            loadFragmentHelper(chat);
    }
    // Deleting the Pushy device token must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

            //unregister the device from the Pushy servers
            Pushy.unregister(HomeActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();

        }
    }

}
