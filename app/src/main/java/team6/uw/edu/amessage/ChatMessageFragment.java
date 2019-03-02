package team6.uw.edu.amessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import team6.uw.edu.amessage.chat.ChatMessage;
import team6.uw.edu.amessage.utils.PushReceiver;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatMessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChatMessageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ChatMessage myChatMsgs;

    private static final String TAG = "CHAT_FRAG";

    private static final String CHAT_ID = "1";

    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;

    private String mEmail;
    private String mJwToken;
    private String mSendUrl;
    private String mChatId;
    private PushMessageReceiver mPushMessageReciever;

    public ChatMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootLayout = inflater.inflate(R.layout.fragment_chat_message, container, false);

//        savedInstanceState = getArguments();
//
//        if (savedInstanceState != null) {
            mMessageOutputTextView = rootLayout.findViewById(R.id.fragChat_messageDisplay_textView);
            mMessageInputEditText = rootLayout.findViewById(R.id.fragChat_messageInput_editText);
            rootLayout.findViewById(R.id.fragChat_messageSend_imageView).setOnClickListener(this::handleSendClick);


//        }


        return rootLayout;
    }

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
                .onPostExecute(this::getallChatTask)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();





    }

    private void getallChatTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);
            if(res.getJSONArray("messages") != null) {
                JSONArray arr = res.getJSONArray ("messages");

                for (int i = arr.length() - 1; i >= 0; i--) {
                    JSONObject obj =  new JSONObject(arr.get(i).toString());
                    String messageText = obj.getString("message");
                    String sender = obj.getString("email");
                    mMessageOutputTextView.append(sender + ":" + messageText);
                    mMessageOutputTextView.append(System.lineSeparator());
                    mMessageOutputTextView.append(System.lineSeparator());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new PushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mPushMessageReciever, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null){
            getActivity().unregisterReceiver(mPushMessageReciever);
        }
    }

    private void handleSendClick(final View theButton) {
        String msg = mMessageInputEditText.getText().toString();
        Log.w("NotWorking", "Clicked");
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("email", mEmail);
            messageJson.put("message", msg);
            messageJson.put("chatId", mChatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private void endOfSendMsgTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject response = new JSONObject(result);
            Log.w("NotWorking", "This is the res: " + response);
            if(response.has("success")  && response.getBoolean("success")) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText("");

                //its up to you to decide if you want to send the message to the output here
                //or wait for the message to come back from the web service.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class PushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")
                                && intent.hasExtra("CHATID")) {

                String sender = intent.getStringExtra("SENDER");
//                String chatId = intent.get
                String messageText = intent.getStringExtra("MESSAGE");
                String chatId = intent.getStringExtra("CHATID");
                if (Integer.parseInt(chatId) == Integer.parseInt(mChatId)) {
                    mMessageOutputTextView.append(sender + ":" + messageText + "CHATID: "+ chatId);
                    mMessageOutputTextView.append(System.lineSeparator());
                    mMessageOutputTextView.append(System.lineSeparator());
                }
//                mMessageOutputTextView.append(sender + ":" + messageText + "CHATID: "+ chatId);
//                mMessageOutputTextView.append(System.lineSeparator());
//                mMessageOutputTextView.append(System.lineSeparator());
            }
        }

//    private void viewFullPost(View view) {
//        if (mListener != null) {
//            mListener.onUrlBlogPostFragmentInteraction(myChatMsgs.getUrl());
//        }


    }

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
