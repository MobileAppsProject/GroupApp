package team6.uw.edu.amessage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import team6.uw.edu.amessage.contact.ContactDetail;
import team6.uw.edu.amessage.contact.ContactGenerator;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnAcceptedListFragmentInteractionListener}
 * interface.
 */
public class ConnectionsFragment extends Fragment {

    private List<ContactDetail> mAcceptedContacts;
    private OnAcceptedListFragmentInteractionListener mAcceptedListener;
    private RecyclerView acceptedRecyclerView;
    private EditText mMessageInputEditText;
    private RadioButton mUsername;
    private RadioButton mEmail;
    private RadioButton mFirstAndLast;
    private String mMemberID;
    private String mJwToken;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionsFragment() {
    }

    @SuppressWarnings("unused")
    public static ConnectionsFragment newInstance(int columnCount) {
        ConnectionsFragment fragment = new ConnectionsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAcceptedContacts = new ArrayList<ContactDetail>(
                Arrays.asList((ContactDetail[]) getArguments().getSerializable("acceptedcontacts")));

        mJwToken = getArguments().getString("jwtoken");
        mMemberID = getArguments().getString("memberid");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connections_list, container, false);

        Context context = view.getContext();

        Button send = view.findViewById(R.id.frg_connections_button_request);
        send.setOnClickListener(this::onSendButtonClicked);
        mMessageInputEditText = view.findViewById(R.id.frag_connections_editText);
        mUsername = view.findViewById(R.id.radio_button_username);
        mEmail = view.findViewById(R.id.radio_button_email);
        mFirstAndLast = view.findViewById(R.id.radio_button_fullname);
        mUsername.setOnClickListener(this:: onRadioButtonClicked);
        mEmail.setOnClickListener(this:: onRadioButtonClicked);
        mFirstAndLast.setOnClickListener(this:: onRadioButtonClicked);


        acceptedRecyclerView = view.findViewById(R.id.list);
        acceptedRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        acceptedRecyclerView.setAdapter(new MyAcceptedRecyclerViewAdapter(mAcceptedContacts, mAcceptedListener, mMemberID));
        return view;
    }
    public void onRadioButtonClicked(View view) {
        if (mUsername.isChecked())
            mMessageInputEditText.setHint("Username");
        if (mEmail.isChecked())
            mMessageInputEditText.setHint("Email");
        if(mFirstAndLast.isChecked()) {
            mMessageInputEditText.setHint("First and Last Name");
        }

    }
    public void onSendButtonClicked(View view) {
        String input = mMessageInputEditText.getText().toString();

        JSONObject messageJson = new JSONObject();
        String uri = "";
        if (mUsername.isChecked()) {
            uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath("contacts")
                    .appendPath("sendRequest")
                    .build()
                    .toString();
            try {
                messageJson.put("memberid", mMemberID);
                messageJson.put("username", input);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (mEmail.isChecked()) {
            uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath("contacts")
                    .appendPath("sendRequestEmail")
                    .build()
                    .toString();
            try {
                messageJson.put("memberid", mMemberID);
                messageJson.put("email", input);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (mFirstAndLast.isChecked()) {
            uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath("contacts")
                    .appendPath("sendRequestName")
                    .build()
                    .toString();
            try {
                String[] inputArr = input.split(" ");
                messageJson.put("memberid", mMemberID);
                messageJson.put("firstname", inputArr[0]);
                messageJson.put("lastname", inputArr[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        new SendPostAsyncTask.Builder(uri, messageJson)
                .onPostExecute(this::endOfSendRequest)
                .onCancelled(error -> Log.e("ConnectionsFragment", error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }
    private void endOfSendRequest(final String result) {
        try {
            //This is the result from the web service
            JSONObject response = new JSONObject(result);
            Log.w("ConnectionsFragment", "This is the res: " + response);
            if(response.has("success")  && response.getBoolean("success")) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText("");
                String user = response.getString("user");
                Toast.makeText(getContext(),
                        "Sent " + user + " a friend request!", Toast.LENGTH_LONG).show();


            } else {
                mMessageInputEditText.setText("");
                String errorMsg = response.getString("error");
                Toast.makeText(getContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ConnectionsFragment.OnAcceptedListFragmentInteractionListener) {
            mAcceptedListener = (ConnectionsFragment.OnAcceptedListFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAcceptedListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAcceptedListFragmentInteractionListener {
        void onAcceptedListFragmentInteraction(ContactDetail item);
    }
}
