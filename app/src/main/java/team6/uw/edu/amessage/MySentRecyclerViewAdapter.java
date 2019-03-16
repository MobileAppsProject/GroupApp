package team6.uw.edu.amessage;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import team6.uw.edu.amessage.contact.ContactDetail;

/**
 * Adapter for the sent connection request list item
 */
public class MySentRecyclerViewAdapter extends RecyclerView.Adapter<MySentRecyclerViewAdapter.ViewHolder>{
    // List of sent contacts
    private final List<ContactDetail> mContacts;
    // Listener for the sent contact list view item
    private final ContactsFragment.OnSentListFragmentInteractionListener mListener;
    // Authorization token
    private String mJwToken;
    // User memberid
    private String mMemberID;

    /**
     * Initializes the info needed for the sent connection
     * request list item
     *
     * @param contacts list of sent contacts
     * @param listener Doesnt do anything yet
     * @param memberid User member id
     * @param token Authorization toekn
     */
    public MySentRecyclerViewAdapter(List<ContactDetail> contacts,
                                        ContactsFragment.OnSentListFragmentInteractionListener listener, String memberid,
                                        String token) {
        mContacts = contacts;
        mListener = listener;
        mMemberID = memberid;
        mJwToken = token;
    }

    /**
     *
     * @param parent Recycler list
     * @param viewType type
     * @return layout for the list item
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts_sent, parent, false);
        return new MySentRecyclerViewAdapter.ViewHolder(view);
    }

    /**
     * Binds information needed for the sent contact list item
     *
     * @param holder holds contact
     * @param position in the contact list
     */
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

    /**
     * ViewHolder for Sent Recycler view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // View
        public final View mView;
        // Contact email
        public final TextView mEmail;
        // Contact username
        public final TextView mFname;
        public ContactDetail mItem;


        /**
         * Retrieves the view items needed for list itme
         * @param view list view item
         */
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
