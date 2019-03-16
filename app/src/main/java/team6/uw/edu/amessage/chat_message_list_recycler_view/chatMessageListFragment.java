package team6.uw.edu.amessage.chat_message_list_recycler_view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team6.uw.edu.amessage.R;

/**
 * This is the list of chat messages to display all the chats inside that chat room.
 */
public class chatMessageListFragment extends Fragment {

    public static final String ARG_MESSAGES_LIST = "messagesLists";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private List<Messages> mMessagesList;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public chatMessageListFragment() {
    }

    /**
     * This is the static constructor allowing to set up the list.
     *
     * @param columnCount the count.
     * @return the fragment with the sent bundle.
     */
    public static chatMessageListFragment newInstance(int columnCount) {
        chatMessageListFragment fragment = new chatMessageListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This will be called on the first time the frag is created.
     *
     * @param savedInstanceState The incoming saved instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessagesList = new ArrayList<Messages>(
                    Arrays.asList((Messages[]) getArguments().getSerializable(ARG_MESSAGES_LIST)));
        } else {
            mMessagesList = MessagesGenerator.CHAT_MESSAGES;
        }
    }

    /**
     * This will call the recycler view to set up the list.
     *
     * @param inflater           to show the fragment to the user.
     * @param container          the container to put the frag into.
     * @param savedInstanceState the bundle passed to the fragment.
     * @return the layout to send back to the user.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatmessagelist_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new chatMessageListRecyclerViewAdapter(mMessagesList));
        }
        return view;
    }

    /**
     * Default on attach.
     *
     * @param context the context currently.
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
     * This is the default on detach.
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
        void onListFragmentInteraction(Messages item);
    }
}
