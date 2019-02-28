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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import team6.uw.edu.amessage.chat.ChatMessage;
import team6.uw.edu.amessage.utils.PushReceiver;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class LabChatFragment extends Fragment {

    private static final String TAG = "CHAT_FRAG";

    private static final String CHAT_ID = "1";

    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;

    private String mEmail;
    private String mJwToken;
    private String mSendUrl;
    private PushMessageReceiver mPushMessageReciever;

    public LabChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootLayout = inflater.inflate(R.layout.fragment_lab_chat, container, false);

        mMessageOutputTextView = rootLayout.findViewById(R.id.fragChat_messageDisplay_textView);
        mMessageInputEditText = rootLayout.findViewById(R.id.fragChat_messageInput_editText);
        rootLayout.findViewById(R.id.fragChat_messageSend_imageView).setOnClickListener(this::handleSendClick);

        return rootLayout;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            //get the email and JWT from the Activity. Make sure the Keys match what you used
            mEmail = getArguments().getString(getString(R.string.keys_intent_credentials));
            mJwToken = getArguments().getString(getString(R.string.keys_intent_jwt));
        }

        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();
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

        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("email", mEmail);
            messageJson.put("message", msg);
            messageJson.put("chatId", CHAT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    private void endOfSendMsgTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);

            if(res.has("success")  && res.getBoolean("success")) {
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
            if(intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {

                String sender = intent.getStringExtra("SENDER");
                String messageText = intent.getStringExtra("MESSAGE");

                mMessageOutputTextView.append(sender + ":" + messageText);
                mMessageOutputTextView.append(System.lineSeparator());
                mMessageOutputTextView.append(System.lineSeparator());
            }
        }
    }

//    //This will get the parse the jason object.
//    private void handleChatGetOnPostExecute(final String result) {
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
////                    List<ChatMessage> blogs = new ArrayList<>();
//
//                    for(int i = 0; i < data.length(); i++) {
//                        JSONObject jsonBlog = data.getJSONObject(i);
////                        Log.wtf()
//
////                        blogs.add(new ChatMessage.Builder(
////                                jsonBlog.getString(
////                                        getString(R.string.keys_json_blogs_pubdate)),
////                                jsonBlog.getString(
////                                        getString(R.string.keys_json_blogs_title)))
////                                .addTeaser(jsonBlog.getString(
////                                        getString(R.string.keys_json_blogs_teaser)))
////                                .addEmail(jsonBlog.getString(
////                                        getString(R.string.keys_json_blogs_url)))
////                                .build());
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
////                    onWaitFragmentInteractionHide();
////                    loadFragmentHelper(frag);
//
//                } else {
//                    Log.e("ERROR!", "No data array");
//                    //notify user
////                    onWaitFragmentInteractionHide();
//                }
//            } else {
//                Log.e("ERROR!", "No response");
//                //notify user
////                onWaitFragmentInteractionHide();
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("ERROR!", e.getMessage());
//            //notify user
////            onWaitFragmentInteractionHide();
//        }
//    }

}
