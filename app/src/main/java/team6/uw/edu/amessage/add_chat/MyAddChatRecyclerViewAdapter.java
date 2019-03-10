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
 * {@link RecyclerView.Adapter} that can display a {@link ContactDetail} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAddChatRecyclerViewAdapter extends RecyclerView.Adapter<MyAddChatRecyclerViewAdapter.ViewHolder> {

    private final List<ContactDetail> mValues;
    private final OnListFragmentInteractionListener mListener;
    private int selectedPos = RecyclerView.NO_POSITION;
    int selected_position = 0;

    public MyAddChatRecyclerViewAdapter(List<ContactDetail> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_addchat, parent, false);
        return new ViewHolder(view);
    }

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
//        holder.mContentView.setBackgroundColor(holder.mItem.isSelected() ? Color.CYAN : Color.WHITE);
//        holder.

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    holder.mItem.setSelected(!holder.mItem.isSelected());
//                    holder.mContentView.setBackgroundColor(holder.mItem.isSelected() ? Color.CYAN : Color.WHITE);
                    if (holder.mItem.isSelected()) {
                        holder.mLinearLayout.setBackgroundResource(R.drawable.selected);

                    } else {
                        holder.mLinearLayout.setBackgroundResource(R.drawable.not_selected);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final LinearLayout mLinearLayout;
        public ContactDetail mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_backGround);
//            this.setIsRecyclable(false);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
