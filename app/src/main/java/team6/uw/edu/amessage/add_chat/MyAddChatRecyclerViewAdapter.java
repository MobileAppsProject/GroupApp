package team6.uw.edu.amessage.add_chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import team6.uw.edu.amessage.R;
import team6.uw.edu.amessage.add_chat.AddChatFragment.OnListFragmentInteractionListener;
import team6.uw.edu.amessage.contact.ContactDetail;

import java.util.List;

/**
 * This class is a recycler view to allow users to add a chat. Allows for the list to be
 * recyclable.
 */
public class MyAddChatRecyclerViewAdapter extends RecyclerView.Adapter<MyAddChatRecyclerViewAdapter.ViewHolder> {

    private final List<ContactDetail> mValues;
    private final OnListFragmentInteractionListener mListener;
    int selected_position = 0;
    private int selectedPos = RecyclerView.NO_POSITION;

    /**
     * This is the constructor to pass in a list of contact details for the user to select.
     *
     * @param items the list of contacts.
     * @param listener the on click listner.
     */
    public MyAddChatRecyclerViewAdapter(List<ContactDetail> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * This is default on create View Holder.
     *
     * @param parent the incoming view layout.
     * @param viewType the type of layout.
     * @return A new viewholder with the new list.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_addchat, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This will set all the values in the recycler view. Set up a onclick listener for
     * each one.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getFirstName());
        holder.mContentView.setText(mValues.get(position).getEmail());
        if (holder.mItem.isSelected()) {
            holder.mLinearLayout.setBackgroundResource(R.drawable.selected);

        } else {
            holder.mLinearLayout.setBackgroundResource(R.drawable.not_selected);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    holder.mItem.setSelected(!holder.mItem.isSelected());
                    if (holder.mItem.isSelected()) {
                        holder.mLinearLayout.setBackgroundResource(R.drawable.selected);

                    } else {
                        holder.mLinearLayout.setBackgroundResource(R.drawable.not_selected);
                    }
                }
            }
        });
    }

    /**
     * Allows for the number of items in the list.
     *
     * @return the size of the recycler view.
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * This will hold the user items to store there information into.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final LinearLayout mLinearLayout;
        public ContactDetail mItem;

        /**
         * Constructor to set up all the values.
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_backGround);
        }

        /**
         * This is the default to string to display the items.
         * @return
         */
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
