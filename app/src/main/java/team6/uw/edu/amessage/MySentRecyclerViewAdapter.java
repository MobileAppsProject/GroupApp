package team6.uw.edu.amessage;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

import team6.uw.edu.amessage.contact.ContactDetail;

public class MySentRecyclerViewAdapter extends RecyclerView.Adapter<MySentRecyclerViewAdapter.ViewHolder>{
    private final List<ContactDetail> mContacts;
    private final ContactsFragment.OnSentListFragmentInteractionListener mListener;
    private String mJwToken;
    private String mMemberID;

    public MySentRecyclerViewAdapter(List<ContactDetail> contacts,
                                        ContactsFragment.OnSentListFragmentInteractionListener listener, String memberid,
                                        String token) {
        mContacts = contacts;
        mListener = listener;
        mMemberID = memberid;
        mJwToken = token;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts_sent, parent, false);
        return new MySentRecyclerViewAdapter.ViewHolder(view);
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
                    mListener.onSentListFragmentInteractionListener(holder.mItem);
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
