package team6.uw.edu.amessage;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import team6.uw.edu.amessage.contact.ContactDetail;

public class MyPendingRecyclerViewAdapter extends RecyclerView.Adapter<MyPendingRecyclerViewAdapter.ViewHolder>{
    private final List<ContactDetail> mContacts;
    private final ContactsFragment.OnPendingListFragmentInteractionListener mListener;

    public MyPendingRecyclerViewAdapter(List<ContactDetail> contacts, ContactsFragment.OnPendingListFragmentInteractionListener listener) {
        mContacts = contacts;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts_pending, parent, false);
        return new MyPendingRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mContacts.get(position);
        holder.mEmail.setText(mContacts.get(position).getEmail());
        holder.mFname.setText(Html.fromHtml(mContacts.get(position).getFirstName()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPendingListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mEmail;
        public final TextView mFname;
        public ContactDetail mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mEmail = (TextView) view.findViewById(R.id.fragContact_email_textView);
            mFname = (TextView) view.findViewById(R.id.fragContact_fName_textView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}
