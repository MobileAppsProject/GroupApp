package team6.uw.edu.amessage.chat_room;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team6.uw.edu.amessage.R;
import team6.uw.edu.amessage.WaitFragment;
import team6.uw.edu.amessage.chat_message_list_recycler_view.Messages;
import team6.uw.edu.amessage.chat_message_list_recycler_view.MessagesGenerator;
import team6.uw.edu.amessage.chat_message_list_recycler_view.chatMessageListRecyclerViewAdapter;
import team6.uw.edu.amessage.utils.PushReceiver;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;

/**
 * This is the fragment that will set up all the correct view to
 * show the messages of a specific chat room.
 */
public class ChatMessageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "CHAT_FRAG";
    private static final String CHAT_ID = "1";
    private EditText mMessageInputEditText;
    private String mEmail;
    private String mJwToken;
    private String mSendUrl;
    private String mChatId;
    private PushMessageReceiver mPushMessageReciever;
    private List<Messages> mMessageList;
    private RecyclerView mMessageRecycler;
    private chatMessageListRecyclerViewAdapter mMessageAdapter;

    /**
     * Required default constructor.
     */
    public ChatMessageFragment() {
    }


    /**
     * This is the on create view that will set up the chat message recylcer view to be
     * displayed.
     *
     * @param inflater the layout to be show.
     * @param container the container to place the layout in.
     * @param savedInstanceState the current saved data being passed to this fragment.
     * @return the new fragment being inflated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View rootLayout = inflater.inflate(R.layout.fragment_chat_message, container, false);
        //1.) Get a reference to the recycler view.
        mMessageRecycler = (RecyclerView) rootLayout.findViewById(R.id.reyclerview_message_list);

        //2.) Set layout
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(rootLayout.getContext()));

        //Create Default data for now!
        mMessageList = MessagesGenerator.CHAT_MESSAGES;

        //3.) Create adapter
        mMessageAdapter = new chatMessageListRecyclerViewAdapter(mMessageList);

        //4.) set adapter
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.scrollToPosition(mMessageList.size() - 1);

        savedInstanceState = getArguments();

        if (savedInstanceState != null) {
            mMessageInputEditText = rootLayout.findViewById(R.id.fragChatMsg_chatbox_editText);
            rootLayout.findViewById(R.id.fragChatMsg_chatboxSend_button).setOnClickListener(this::handleSendClick);
        }
        return rootLayout;
    }

    /**
     * This is the first thing that happens when the fragment is restarted allowing for the
     * chat messages to be show.
     */
    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            //get the email and JWT from the Activity. Make sure the Keys match what you used
            mEmail = getArguments().getString(getString(R.string.keys_intent_credentials));
            mJwToken = getArguments().getString(getString(R.string.keys_intent_jwt));
            mChatId = getArguments().getString("chatId");

        }

        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_chatmessages_get))
                .build()
                .toString();


        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("chatId", mChatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::getAllChatTask)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();

    }

    /**
     *  This will load the wait fragment.
     */
    public void onWaitFragmentInteractionShow() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    /**
     * This will hide the loading screen.
     */
    public void onWaitFragmentInteractionHide() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(getActivity().getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }

    /**
     * This will get all the chats depending on the user id entered.
     * @param result the messages shown.
     */
    private void getAllChatTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);
            if (res.getJSONArray("messages") != null) {
                JSONArray arr = res.getJSONArray("messages");
                List<Messages> userMessages = new ArrayList<>();
                for (int i = arr.length() - 1; i >= 0; i--) {
                    JSONObject obj = new JSONObject(arr.get(i).toString());
                    String sender = obj.getString("email");
                    String messageText = obj.getString("message");
                    String timeStamp = obj.getString("timestamp");
                    String userId = obj.getString("memberid");

                    String milTime = timeStamp.substring(11, 13) + timeStamp.substring(14, 16);
                    Date date = new SimpleDateFormat("hhmm").parse(milTime);
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

                    userMessages.add(new Messages.Builder(userId, messageText)
                            .addUserEmail(sender)
                            .addTimeStamp("" + sdf.format(date))
                            .build());

                }

                mMessageList = userMessages;
                mMessageAdapter = new chatMessageListRecyclerViewAdapter(mMessageList);

                //4.) set adapter
                mMessageRecycler.setAdapter(mMessageAdapter);
                mMessageRecycler.scrollToPosition(mMessageList.size() - 1);
                onWaitFragmentInteractionHide();

            } else {
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onWaitFragmentInteractionHide();
        } catch (ParseException e) {
            e.printStackTrace();
            onWaitFragmentInteractionHide();
        }
    }

    /**
     * This method will be called when the fragment is resumed allowing for loading in
     * notifications.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new PushMessageReceiver();

        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mPushMessageReciever, iFilter);
    }

    /**
     * This is the on pause allowing to remove a pushy receiver.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null) {
            getActivity().unregisterReceiver(mPushMessageReciever);
        }
    }

    /**
     * This will handle when creating a new message to be sent.
     * @param theButton the current sent button being clicked.
     */
    private void handleSendClick(final View theButton) {
        String msg = mMessageInputEditText.getText().toString();
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("email", mEmail);
            messageJson.put("message", msg);
            messageJson.put("chatId", mChatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //This will send the message into the database.
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    /**
     * This will be filter only the messages between each chat room and only
     * display the correct chat.
     * @param result
     */
    private void endOfSendMsgTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject response = new JSONObject(result);

            //This means that the message has been sent and stored in the database.
            if (response.has("success") && response.getBoolean("success")) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText("");

                //Now we grab the the message that was just sent and grab it from the database.
                mSendUrl = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_messaging_base))
                        .appendPath(getString(R.string.ep_chatmessages_get))
                        .build()
                        .toString();

                JSONObject messageJson = new JSONObject();
                try {
                    messageJson.put("chatId", mChatId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                        .onPostExecute(this::getAllChatTask)
                        .onCancelled(error -> Log.e(TAG, error))
                        .addHeaderField("authorization", mJwToken)
                        .build().execute();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * A BroadcastReceiver that listens for Messages sent from PushReceiver
     */
    private class PushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")
                    && intent.hasExtra("CHATID")) {

                String sender = intent.getStringExtra("SENDER");
                String messageText = intent.getStringExtra("MESSAGE");
                String chatId = intent.getStringExtra("CHATID");
                //This is where we will want to add it to the recycler view.
                //If the user sent is in the same chatID.
                Log.d("OnRECIEVED Out side if", "onReceive: " + chatId + ", " + mChatId);
                if (Integer.parseInt(chatId) == Integer.parseInt(mChatId)) {
                    //Update our chat with the new incoming message.
                    mSendUrl = new Uri.Builder()
                            .scheme("https")
                            .appendPath(getString(R.string.ep_base_url))
                            .appendPath(getString(R.string.ep_messaging_base))
                            .appendPath(getString(R.string.ep_chatmessages_get))
                            .build()
                            .toString();

                    JSONObject messageJson = new JSONObject();
                    try {
                        messageJson.put("chatId", mChatId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                            .onPostExecute(this::getAllChatMessages)
                            .onCancelled(error -> Log.e(TAG, error))
                            .addHeaderField("authorization", mJwToken)
                            .build().execute();

                }
            }
        }

        /**
         * This is a inner method to display all current chats.
         * @param result the chats.
         */
        private void getAllChatMessages(final String result) {
            try {
                //This is the result from the web service
                JSONObject res = new JSONObject(result);
                if (res.getJSONArray("messages") != null) {
                    JSONArray arr = res.getJSONArray("messages");
                    List<Messages> userMessages = new ArrayList<>();
                    for (int i = arr.length() - 1; i >= 0; i--) {
                        JSONObject obj = new JSONObject(arr.get(i).toString());
                        String sender = obj.getString("email");
                        String messageText = obj.getString("message");
                        String timeStamp = obj.getString("timestamp");
                        String userId = obj.getString("memberid");

                        userMessages.add(new Messages.Builder(userId, messageText)
                                .addUserEmail(sender)
                                .addTimeStamp(timeStamp.substring(11, 16))
                                .build());

                    }
                    mMessageList = userMessages;
                    mMessageAdapter = new chatMessageListRecyclerViewAdapter(mMessageList);

                    mMessageRecycler.setAdapter(mMessageAdapter);
                    mMessageRecycler.scrollToPosition(mMessageList.size() - 1);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Default on attach.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Default on detach.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onUrlBlogPostFragmentInteraction(String url);
    }
}
