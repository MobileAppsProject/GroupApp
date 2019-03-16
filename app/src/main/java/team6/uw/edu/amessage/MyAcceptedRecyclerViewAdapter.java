package team6.uw.edu.amessage;

import android.graphics.Color;
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
 * Adapter for displaying accepted connection requests
 */
public class MyAcceptedRecyclerViewAdapter extends RecyclerView.Adapter<MyAcceptedRecyclerViewAdapter.ViewHolder>{
    private final List<ContactDetail> mContacts;
    private final ConnectionsFragment.OnAcceptedListFragmentInteractionListener mListener;
    private String mMemberID;

    /**
     * Initializes the info need to display the users
     * accepted connections
     *
     * @param contacts List of contacts
     * @param listener Recycler view action listener
     * @param memberid Current user memberid
     */
    public MyAcceptedRecyclerViewAdapter(List<ContactDetail> contacts,
                                         ConnectionsFragment.OnAcceptedListFragmentInteractionListener listener,
                                         String memberid) {
        mContacts = contacts;
        mListener = listener;
        mMemberID = memberid;
    }

    /**
     * Inflates the list item.
     *
     * @param parent connections fragment
     * @param viewType type of view
     * @return the adapter
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_connections, parent, false);
        return new MyAcceptedRecyclerViewAdapter.ViewHolder(view);
    }

    /**
     * Binds the contact email and username to the list view item.
     * Binds onclick listeners to send async tasks to delete a connection
     *
     * @param holder holds contact info
     * @param position Position in the contact list
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mContacts.get(position);
        holder.mEmail.setText(mContacts.get(position).getEmail());
        holder.mFname.setText(Html.fromHtml(mContacts.get(position).getAuthor()));

        // Deletes connection
        holder.mDelete.setOnClickListener(v -> {
            Toast.makeText(holder.mView.getContext(),
                    "Deleted " + mContacts.get(position).getAuthor() + " from your connections!", Toast.LENGTH_LONG).show();
            String uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath("chat450.herokuapp.com")
                    .appendPath("contacts")
                    .appendPath("delete")
                    .build().toString();

            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("memberid_a", mContacts.get(position).getUserId());
                messageJson.put("memberid_b", mMemberID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SendPostAsyncTask.Builder(uri, messageJson)
                    .onPostExecute(this::handleDeleteContactOnPostExecute)
                    .onCancelled(error -> Log.e("PendingRecyclerView", error))
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
                    mListener.onAcceptedListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * Deletes a connection
     * @param result of the async call
     */
    public void handleDeleteContactOnPostExecute(final String result) {
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

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * Holds information needed to display information about contacts
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Current view
        public final View mView;
        //User email
        public final TextView mEmail;
        // User username
        public final TextView mFname;
        // Delete connection
        public Button mDelete;
        public ContactDetail mItem;

        /**
         * View holder constructor
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mEmail = (TextView) view.findViewById(R.id.fragconnect_email_textview);
            mFname = (TextView) view.findViewById(R.id.fragConnect_username_textview);
            mDelete = view.findViewById(R.id.button_delete_connection);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}
