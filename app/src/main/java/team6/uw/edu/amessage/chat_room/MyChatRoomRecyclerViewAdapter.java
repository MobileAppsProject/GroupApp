package team6.uw.edu.amessage.chat_room;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team6.uw.edu.amessage.R;
import team6.uw.edu.amessage.chat_room.ChatRoomFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * A recycler view that allows for you to display a list of chat rooms.
 */
public class MyChatRoomRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRoomRecyclerViewAdapter.ViewHolder> {

    private final List<ChatRoom> mValues;
    private final OnListFragmentInteractionListener mListener;

    /**
     * This is the adapter that allows to set up a list of chat rooms and a click listener.
     *
     * @param chatRooms the list of chat rooms.
     * @param listener  the listener for each chat room.
     */
    public MyChatRoomRecyclerViewAdapter(List<ChatRoom> chatRooms, OnListFragmentInteractionListener listener) {
        mValues = chatRooms;
        mListener = listener;
    }

    /**
     * This will inflate the layout.
     *
     * @param parent   the parent layout to inflate.
     * @param viewType the view type.
     * @return the viewfinder to inflate.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat_room, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This will set up all the information in the recycler view.
     *
     * @param holder   the view holder.
     * @param position the position in the list.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("Members: " + mValues.get(position).getChatName());
        holder.mUsersView.setText(Html.fromHtml(mValues.get(position).getTeaser()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * This will get the size of chat rooms.
     *
     * @return the size.
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * This will initialize the information on where to set the information to be seen.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mUsersView;
        public ChatRoom mItem;

        /**
         * Will find all the text views.
         *
         * @param view the current view.
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.fragBlog_blogTitle_textView);
            mContentView = (TextView) view.findViewById(R.id.fragChatMsg_chatbox_editText);
            mUsersView = (TextView) view.findViewById(R.id.fragChatRoom_users_textView);
        }

        /**
         * The string representation of the info.
         *
         * @return the string.
         */
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
