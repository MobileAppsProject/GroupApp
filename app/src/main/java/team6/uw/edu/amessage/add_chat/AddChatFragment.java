package team6.uw.edu.amessage.add_chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import team6.uw.edu.amessage.LoginFragment;
import team6.uw.edu.amessage.R;
import team6.uw.edu.amessage.chat_room.ChatRoomFragment;
import team6.uw.edu.amessage.contact.ContactDetail;
import team6.uw.edu.amessage.contact.ContactGenerator;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;

/**
 * A fragment representing a list of chats allowing the user to add
 * chats to there existing chats.
 */
public class AddChatFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mMessageRecycler;
    private List<ContactDetail> mMessageList;
    private MyAddChatRecyclerViewAdapter mMessageAdapter;
    private String mChatId;
    ArrayList<Integer> chatMembers = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddChatFragment() {
    }

    /**
     * This will be a static method allowing you to create a new instance to
     * create a new instance.
     *
     * @param columnCount the amount of columns.
     * @return the fragment.
     */
    public static AddChatFragment newInstance(int columnCount) {
        AddChatFragment fragment = new AddChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This will be used for the first time when the fragment is
     * created for the column.
     *
     * @param savedInstanceState package being sent in to the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    /**
     * This will be the on create view for the first time the fragment is created.
     * This will set up the recycler view inflate the layout.
     *
     * @param inflater the inflater.
     * @param container the container to be sent in.
     * @param savedInstanceState the bundle of saved info being sent over.
     * @return Will return the inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(R.layout.fragment_add_chat_room_layout, container, false);

        mMessageRecycler = (RecyclerView) rootLayout.findViewById(R.id.reyclerView_addChatRooms_list);

        //2.) Set layout
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(rootLayout.getContext()));

        //Create Default data for now!
        mMessageList = ContactGenerator.CONTACT_DETAIL_LIST;

        //3.) Create adapter
        mMessageAdapter = new MyAddChatRecyclerViewAdapter(mMessageList, mListener);

        //4.) set adapter
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.scrollToPosition(mMessageList.size() - 1);

        rootLayout.findViewById(R.id.fragAddChatRoom_addChat_button).setOnClickListener(this::handleSendClick);

        return rootLayout;
    }

    /**
     * This will handle when the user clicks on the button to make a new chat.
     *
     * @param theButton the incoming button.
     */
    private void handleSendClick(final View theButton) {
        //Holds all the users that the user want to make a chat with.
        for (ContactDetail model : mMessageList) {
            if (model.isSelected()) {
                chatMembers.add(Integer.parseInt(model.getUserId()));
            }
        }

        if (chatMembers.size() == 0) {
            //No Members Added
            Toast.makeText(getActivity(), "No Contacts Selected!",
                    Toast.LENGTH_LONG).show();
        } else if (chatMembers.size() >= 1) {
            //Create a New Chat
            createChatRoom();
        }
    }

    /**
     * This will call a endpoint to make the create chat from the backend.
     *
     */
    private void createChatRoom() {
        String uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("createChat")
                .build().toString();
        JSONObject messageJson = new JSONObject();
        //Send in the user ID
        try {
            messageJson.put("memberid", LoginFragment.mUserId);
            messageJson.put("chatname", "Default Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri, messageJson)
                .onPostExecute(this::handleChatRoomChatIDSendOnPostExecute)
                .onCancelled(error -> Log.e("WRONG", error))
                .build().execute();
    }

    /**
     * This will call a endpoint to add a member to a chatroom.
     */
    private void addMemberToChatRoom() {
        String uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("addtoChat")
                .build().toString();
        for (int i = 0; i < chatMembers.size(); i++) {
            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("memberid", chatMembers.get(i));
                messageJson.put("chatid", mChatId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendPostAsyncTask.Builder(uri, messageJson)
                    .onPostExecute(this::handleLoadFragPostExecute)
                    .onCancelled(error -> Log.e("WRONG", error))
                    .build().execute();
        }
    }

    /**
     * This is a helper function to help load a new fragment.
     *
     * @param s the incoming string.
     */
    private void handleLoadFragPostExecute(String s) {
        loadFragmentHelper(new ChatRoomFragment());
    }

    /**
     * This will package up a user id to package of a json object. With
     * the user id.
     *
     * @param s the incoming string.
     */
    private void handleChatRoomChatIDSendOnPostExecute(String s) {
        try {
            //This is the result from the web service
            JSONObject root = new JSONObject(s);
            if (root.has("chatid")) {
                mChatId = root.getString("chatid");
                addMemberToChatRoom();
            } else {
                Log.e("ERROR!", "No response");
                //notify user
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
        }

    }

    /**
     * Helper function for loading a fragment.
     */
    private void loadFragmentHelper(Fragment frag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * This default method to call a endpoint to load all the chats of that user.
     */
    @Override
    public void onStart() {
        super.onStart();

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("contacts")
                .appendPath("myContacts")
                .build();

        JSONObject messageJson = new JSONObject();
        //Send in the user ID
        try {
            messageJson.put("memberid", LoginFragment.mUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri.toString(), messageJson)
                .onPostExecute(this::handleMyContactsSendOnPostExecute)
                .build().execute();
    }

    /**
     *  This will get the parse the jason object.
     */
    private void handleMyContactsSendOnPostExecute(final String result) {
        try {
            //This is the result from the web service
            JSONObject root = new JSONObject(result);
            if (root.has("myContacts")) {
                JSONArray arr = root.getJSONArray("myContacts");
                List<team6.uw.edu.amessage.contact.ContactDetail> contacts = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = new JSONObject(arr.get(i).toString());
                    String firstname = obj.getString("firstname");
                    String lastname = obj.getString("lastname");
                    String username = obj.getString("username");
                    String email = obj.getString("email");
                    String memberid = obj.getString("memberid");
                    contacts.add(new team6.uw.edu.amessage.contact.ContactDetail.Builder(firstname, lastname)
                            .addEmail(email)
                            .addUserId(memberid)
                            .build());

                }
                mMessageList = contacts;
                mMessageAdapter = new MyAddChatRecyclerViewAdapter(mMessageList, mListener);

                //4.) set adapter
                mMessageRecycler.setAdapter(mMessageAdapter);
                mMessageRecycler.scrollToPosition(mMessageList.size() - 1);

            } else {
                Log.e("ERROR!", "No response");
                //notify user
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
        }
    }

    /**
     * This is the default on attach method.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * This is default onDetach.
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ContactDetail item);
    }
}
