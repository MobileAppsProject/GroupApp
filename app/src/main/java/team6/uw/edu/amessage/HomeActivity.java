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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import me.pushy.sdk.Pushy;
import team6.uw.edu.amessage.add_chat.AddChatFragment;
import team6.uw.edu.amessage.chat_message_list_recycler_view.Messages;
import team6.uw.edu.amessage.chat_message_list_recycler_view.chatMessageListFragment;
import team6.uw.edu.amessage.chat_room.ChatRoom;
import team6.uw.edu.amessage.chat_room.ChatRoomFragment;
import team6.uw.edu.amessage.chat_room.ChatMessageFragment;
import team6.uw.edu.amessage.contact.ContactDetail;
import team6.uw.edu.amessage.model.Credentials;
import team6.uw.edu.amessage.utils.GetAsyncTask;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;
import team6.uw.edu.amessage.weather.WeatherDetail;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    ChatRoomFragment.OnListFragmentInteractionListener,
                    ChatMessageFragment.OnFragmentInteractionListener,
                    WaitFragment.OnFragmentInteractionListener,
                    ConnectionsFragment.OnAcceptedListFragmentInteractionListener,
                    ContactsFragment.OnPendingListFragmentInteractionListener,
                    ContactsFragment.OnSentListFragmentInteractionListener,
                    chatMessageListFragment.OnListFragmentInteractionListener,
                    AddChatFragment.OnListFragmentInteractionListener,
                    WeatherForecastFragment.OnListFragmentInteractionListener
                    {

    private Credentials myCredentials;
    private String mJwToken;
    private String mMemberID;
    private Bundle mContactArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home");
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            //This will get the info passed from the last activity!
            myCredentials = (Credentials)getIntent().getSerializableExtra("Login");

            mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));
            mMemberID = LoginFragment.mUserId;

            if (findViewById(R.id.fragmentContainer) != null) {
                Fragment fragment;
                if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
                    fragment = new ChatMessageFragment();

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
            //This will get the information allowing for chat rooms to be made dynamically.
            String uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath("chats")
                    .appendPath("getChats")
                    .build().toString();
            JSONObject messageJson = new JSONObject();
            //Send in the user ID
            try {
                messageJson.put("memberid", LoginFragment.mUserId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendPostAsyncTask.Builder(uri, messageJson)
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleChatRoomSendOnPostExecute)
                    .onCancelled(error -> Log.e("WRONG", error))
                    .addHeaderField("authorization", mJwToken)
                    .build().execute();
        } else if (id == R.id.nav_connections) {
            setTitle("Connections");
            String uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath("contacts")
                    .appendPath("myContacts")
                    .build().toString();
            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("memberid", mMemberID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendPostAsyncTask.Builder(uri, messageJson )
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleConnectionsGetOnPostExecute)
                    .onCancelled(error -> Log.e("HomeActivity", error))
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();

        } else if (id == R.id.nav_contacts) {
            setTitle("Search Connections");
            setTitle("Requests");

            mContactArgs = new Bundle();

            //Post for pending requests
            String uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath("contacts")
                    .appendPath("pending")
                    .build().toString();

            JSONObject msg = new JSONObject();
            try {
                msg.put("memberid", mMemberID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendPostAsyncTask.Builder(uri, msg)
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleContactPendingOnPostExecute)
                    .onCancelled(error -> Log.e("HomeActivity", error))
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();

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

    // Handles the results of getting all accepted contacts
    private void handleContactSentOnPostExecute(final String result) {
        try {
//This is the result from the web service
            JSONObject root = new JSONObject(result);
            if(root.getBoolean("success") == true) {
                JSONArray arr = root.getJSONArray ("sentlist");
                List<ContactDetail> contacts = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = new JSONObject(arr.get(i).toString());
                    String firstname = obj.getString("firstname");
                    String lastname = obj.getString("lastname");
                    String username = obj.getString("username");
                    String memberid = obj.getString("memberid");
                    String email = obj.getString("email");
                    contacts.add(new ContactDetail.Builder(firstname, lastname)
                            .addAuthor(username)
                            .addEmail(email)
                            .addUserId(memberid)
                            .build());

                }

                ContactDetail[] ContactsAsArray = new ContactDetail[contacts.size()];
                ContactsAsArray = contacts.toArray(ContactsAsArray);

                mContactArgs.putSerializable("sentcontacts", ContactsAsArray);
                mContactArgs.putSerializable("jwtoken", mJwToken);
                mContactArgs.putSerializable("memberid", mMemberID);


                Fragment frag = new ContactsFragment();
                frag.setArguments(mContactArgs);
                loadFragmentHelper(frag);

                onWaitFragmentInteractionHide();

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
    //This will get the parse the jason object.
    private void handleChatRoomSendOnPostExecute(final String result) {
        try {
            //This is the result from the web service
            JSONObject root = new JSONObject(result);
            if(root.has("result")) {
                JSONArray arr = root.getJSONArray ("result");
                List<ChatRoom> theChatRooms = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = new JSONObject(arr.get(i).toString());
                    String chatId = obj.getString("chatid");
                    String chatName = obj.getString("chatname");
                    Log.d("ChatRoomTest", "ChatId: , " + chatId);
                    JSONArray members = obj.getJSONArray("members");
                    for(int j = 0; j < members.length(); j++) {
                        JSONObject mem = new JSONObject(members.get(j).toString());
                        String username = mem.getString("username");
                        Log.d("ChatRoomTest", "USERNAME: , " + username);
                    }

                    theChatRooms.add(new ChatRoom.Builder(Integer.parseInt(chatId), chatName)
                            .build());

                }
                ChatRoom[] chatRoomsAsArray = new ChatRoom[theChatRooms.size()];
                chatRoomsAsArray = theChatRooms.toArray(chatRoomsAsArray);

                Bundle args = new Bundle();
                args.putSerializable(ChatRoomFragment.ARG_BLOG_LIST, chatRoomsAsArray);
                Fragment frag = new ChatRoomFragment();
                frag.setArguments(args);

                onWaitFragmentInteractionHide();
                loadFragmentHelper(frag);

            } else {
                Log.e("ERROR!", "No response");
                loadFragmentHelper(new ChatRoomFragment());
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
    private void handleContactPendingOnPostExecute(final String result) {
        try {
             //This is the result from the web service
            JSONObject root = new JSONObject(result);
            if(root.getBoolean("success") == true) {
                JSONArray arr = root.getJSONArray ("myRequests");
                List<ContactDetail> contacts = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = new JSONObject(arr.get(i).toString());
                    String firstname = obj.getString("firstname");
                    String lastname = obj.getString("lastname");
                    String username = obj.getString("username");
                    String memberid = obj.getString("memberid");
                    String email = obj.getString("email");
                    contacts.add(new ContactDetail.Builder(firstname, lastname)
                            .addAuthor(username)
                            .addEmail(email)
                            .addUserId(memberid)
                            .build());

                }

                ContactDetail[] ContactsAsArray = new ContactDetail[contacts.size()];
                ContactsAsArray = contacts.toArray(ContactsAsArray);

                mContactArgs.putSerializable("pendingcontacts", ContactsAsArray);

                //Post for accepted contact requests
                String uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath("contacts")
                        .appendPath("sent")
                        .build().toString();

                JSONObject messageJson = new JSONObject();
                try {
                    messageJson.put("memberid", mMemberID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendPostAsyncTask.Builder(uri, messageJson)
                        .onPostExecute(this::handleContactSentOnPostExecute)
                        .onCancelled(error -> Log.e("HomeActivity", error))
                        .addHeaderField("authorization", mJwToken) //add the JWT as a header
                        .build().execute();


                onWaitFragmentInteractionHide();

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

    //This will get the parse the jason object.
    private void handleConnectionsGetOnPostExecute(final String result) {
        try {
            //This is the result from the web service
            JSONObject root = new JSONObject(result);
            if(root.getBoolean("success") == true) {
                JSONArray arr = root.getJSONArray ("myContacts");
                List<ContactDetail> contacts = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = new JSONObject(arr.get(i).toString());
                    String firstname = obj.getString("firstname");
                    String lastname = obj.getString("lastname");
                    String username = obj.getString("username");
                    String memberid = obj.getString("memberid");
                    String email = obj.getString("email");
                    contacts.add(new ContactDetail.Builder(firstname, lastname)
                            .addAuthor(username)
                            .addEmail(email)
                            .addUserId(memberid)
                            .build());

                }
                ContactDetail[] ContactsAsArray = new ContactDetail[contacts.size()];
                ContactsAsArray = contacts.toArray(ContactsAsArray);

                Bundle args = new Bundle();
                args.putSerializable("acceptedcontacts", ContactsAsArray);
                args.putSerializable("jwtoken", mJwToken);
                args.putSerializable("memberid", mMemberID);


                Fragment frag = new ConnectionsFragment();
                frag.setArguments(args);
                loadFragmentHelper(frag);

                onWaitFragmentInteractionHide();

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
    public void onListFragmentInteraction(ChatRoom item) {
        Log.w("NotWork", "Chat Item: " + item.getChatId());
        Fragment chat = new ChatMessageFragment();
            Bundle args = new Bundle();
            args.putString(getString(R.string.keys_intent_credentials), myCredentials.getEmail());
            args.putString(getString(R.string.keys_intent_jwt), mJwToken);
            args.putString("chatId", "" + item.getChatId());
            chat.setArguments(args);
            loadFragmentHelper(chat);
    }
                            

    @Override
    public void onListFragmentInteraction(Messages item) {

    }

    @Override
    public void onPendingListFragmentInteraction(ContactDetail item) {

    }

    @Override
    public void onAcceptedListFragmentInteraction(ContactDetail item) {

    }

    @Override
    public void onSentListFragmentInteractionListener(ContactDetail item) {

    }

    @Override
    public void onListFragmentInteraction(ContactDetail item) {

    }

    @Override
    public void onWeatherListFragmentInteraction(WeatherDetail item) {

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
