package team6.uw.edu.amessage;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import team6.uw.edu.amessage.contact.ContactDetail;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;

/**
 * Recycler view adapter for pending connection requests
 */
public class MyPendingRecyclerViewAdapter extends RecyclerView.Adapter<MyPendingRecyclerViewAdapter.ViewHolder>{
    // List of pending contacts
    private final List<ContactDetail> mContacts;
    // Listener for Pending contact list item
    private final ContactsFragment.OnPendingListFragmentInteractionListener mListener;
    // User jwt token
    private String mJwToken;
    // User memberid
    private String mMemberID;

    /**
     * Recycler view adapter for the users pending requests
     *
     * @param contacts list of pending contacts
     * @param listener list view action listener
     * @param memberid user memberid
     * @param token authorization token
     */
    public MyPendingRecyclerViewAdapter(List<ContactDetail> contacts,
                                        ContactsFragment.OnPendingListFragmentInteractionListener listener, String memberid,
                                        String token) {
        mContacts = contacts;
        mListener = listener;
        mMemberID = memberid;
        mJwToken = token;
    }

    /**
     * inflates the layout for the list view item and returns it
     *
     * @param parent contact fragment
     * @param viewType view type
     * @return the adapter
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts_pending, parent, false);
        return new MyPendingRecyclerViewAdapter.ViewHolder(view);
    }

    /**
     * Binds contact object, user email, username, button to accept request
     * and button to decline request
     *
     * @param holder Holds data for the list item
     * @param position position in the contact list
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mContacts.get(position);
        holder.mEmail.setText(mContacts.get(position).getEmail());
        holder.mFname.setText(Html.fromHtml(mContacts.get(position).getFirstName()));

        holder.mAccept.setOnClickListener(v -> {
            Toast.makeText(holder.mView.getContext(),
                    "Accepted " + mContacts.get(position).getEmail() + "'s friend request!", Toast.LENGTH_LONG).show();

            //Post for pending requests
            String uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath("chat450.herokuapp.com")
                    .appendPath("contacts")
                    .appendPath("verify")
                    .build().toString();

            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("memberid_a", mContacts.get(position).getUserId());
                messageJson.put("memberid_b", mMemberID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SendPostAsyncTask.Builder(uri, messageJson)
                    .onPostExecute(this::handleAcceptContactOnPostExecute)
                    .onCancelled(error -> Log.e("PendingRecyclerView", error))
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();

            mContacts.remove(position);
            this.notifyItemRemoved(position);

        });

        holder.mNo.setOnClickListener(v -> {
            Toast.makeText(holder.mView.getContext(),
                    "Declined " + mContacts.get(position).getEmail() + "'s friend request!", Toast.LENGTH_LONG).show();

            //Post for pending requests
            String uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath("chat450.herokuapp.com")
                    .appendPath("contacts")
                    .appendPath("decline")
                    .build().toString();

            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("memberid_a", mContacts.get(position).getUserId());
                messageJson.put("memberid_b", mMemberID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SendPostAsyncTask.Builder(uri, messageJson)
                    .onPostExecute(this::handleDeclineContactOnPostExecute)
                    .onCancelled(error -> Log.e("PendingRecyclerView", error))
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();

            mContacts.remove(position);
            this.notifyItemRemoved(position);
        });

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

    /**
     * If a user declines a request it removes that contact from the
     * contact table
     *
     * @param result of declining a request
     */
    public void handleDeclineContactOnPostExecute(final String result) {
        try {
            //This is the result from the web service
            JSONObject root = new JSONObject(result);
            if(root.getBoolean("success") == true) {


            } else {
                Log.e("ERROR!", "No response");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }
    }

    /**
     * If the user accepts the request, it verifies the contact between
     * the two users
     * @param result
     */
    public void handleAcceptContactOnPostExecute(final String result) {
        try {
            //This is the result from the web service
            JSONObject root = new JSONObject(result);
            if(root.getBoolean("success") == true) {


            } else {
                Log.e("ERROR!", "No response");
                //notify user
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }
    }
    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * Views needed for pending requests and its buttons
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // List itme view
        public final View mView;
        // User email of the request
        public final TextView mEmail;
        // Username of request
        public final TextView mFname;
        public ContactDetail mItem;
        // Button for accepting request
        public Button mAccept;
        // Button for declining requests
        public Button mNo;

        /**
         * Initializes the views needed for user
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mEmail = (TextView) view.findViewById(R.id.fragContact_email_textView);
            mFname = (TextView) view.findViewById(R.id.fragContact_fName_textView);
            mAccept = view.findViewById(R.id.fragContactPending_accept_button);
            mNo = view.findViewById(R.id.fragContactPending_no_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}
