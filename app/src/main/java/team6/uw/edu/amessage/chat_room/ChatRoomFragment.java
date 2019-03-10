package team6.uw.edu.amessage.chat_room;

import android.content.Context;
import android.net.ParseException;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team6.uw.edu.amessage.LoginFragment;
import team6.uw.edu.amessage.R;
import team6.uw.edu.amessage.add_chat.AddChatFragment;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;

import static android.support.constraint.Constraints.TAG;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatRoomFragment extends Fragment {

    public static final String ARG_BLOG_LIST = "blogs lists";
    private List<ChatRoom> mChat;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mMessageRecycler;
    private String mSendUrl;
    private MyChatRoomRecyclerViewAdapter mMessageAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatRoomFragment() {
    }

    @SuppressWarnings("unused")
    public static ChatRoomFragment newInstance(int columnCount) {
        ChatRoomFragment fragment = new ChatRoomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mChat = new ArrayList<ChatRoom>(
                    Arrays.asList((ChatRoom[]) getArguments().getSerializable(ARG_BLOG_LIST)));
        } else {
            mChat = ChatRoomGenerator.CHAT_MESSAGES;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootLayout = inflater.inflate(R.layout.fragment_chat_room_layout, container, false);

        mMessageRecycler = (RecyclerView) rootLayout.findViewById(R.id.reyclerview_chatRooms_list);

        //2.) Set layout
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(rootLayout.getContext()));

        //Create Default data for now!
        mChat = ChatRoomGenerator.CHAT_MESSAGES;

        //3.) Create adapter
        mMessageAdapter = new MyChatRoomRecyclerViewAdapter(mChat, mListener);

        //4.) set adapter
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.scrollToPosition(0);

        rootLayout.findViewById(R.id.fragTest_addChat_button).setOnClickListener(this::handleSendClick);

        return rootLayout;
    }

    private void handleSendClick(final View theButton) {
        Log.d("CLICKEDSEND", "handleSendClick: CLICKED!");
        loadFragmentHelper(new AddChatFragment());
    }
    //Helper function for loading a fragment.
    private void loadFragmentHelper(Fragment frag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }


    @Override
    public void onStart() {
        super.onStart();

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
                .onPostExecute(this::getAllChatRoomsTask)
                .onCancelled(error -> Log.e("WRONG", error))
                .build().execute();
    }

    private void getAllChatRoomsTask(final String result) {
        Log.d("ChatRoomTest", "In the Start of Method");
        try {
            //This is the result from the web service
            JSONObject root = new JSONObject(result);
            if(root.has("result")) {
//                Log.d("ChatRoomTest", "IN IF, " + root.toString());
                JSONArray arr = root.getJSONArray ("result");
                List<ChatRoom> theChatRooms = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = new JSONObject(arr.get(i).toString());
                    String chatId = obj.getString("chatid");
                    String chatName = obj.getString("chatname");

                    JSONArray members = obj.getJSONArray("members");
                    Log.d("ChatRoomTest", "ChatId: , " + chatId + "members Lenght: " + members.length());
                    Log.d("ChatRoomTest", "messgage: " + members.toString());
                    String users = "";
                    for(int j = 0; j < members.length(); j++) {
                        JSONObject mem = new JSONObject(members.get(j).toString());
                        String username = mem.getString("username");
                        users += username + ", ";
                        Log.d("ChatRoomTest", "USERNAME: , " + users);
                    }
//
                    theChatRooms.add(new ChatRoom.Builder(Integer.parseInt(chatId), users)
                            .build());

                }
                mChat = theChatRooms;
                mMessageAdapter = new MyChatRoomRecyclerViewAdapter(mChat, mListener);

                //4.) set adapter
                mMessageRecycler.setAdapter(mMessageAdapter);
                mMessageRecycler.scrollToPosition(0);


//

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
        void onListFragmentInteraction(ChatRoom item);
    }
}
