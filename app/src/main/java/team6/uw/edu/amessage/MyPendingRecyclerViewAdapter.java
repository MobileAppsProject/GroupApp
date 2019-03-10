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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import team6.uw.edu.amessage.contact.ContactDetail;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;

public class MyPendingRecyclerViewAdapter extends RecyclerView.Adapter<MyPendingRecyclerViewAdapter.ViewHolder>{
    private final List<ContactDetail> mContacts;
    private final ContactsFragment.OnPendingListFragmentInteractionListener mListener;
    private String mJwToken;
    private String mMemberID;

    public MyPendingRecyclerViewAdapter(List<ContactDetail> contacts,
                                        ContactsFragment.OnPendingListFragmentInteractionListener listener, String memberid,
                                        String token) {
        mContacts = contacts;
        mListener = listener;
        mMemberID = memberid;
        mJwToken = token;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mEmail;
        public final TextView mFname;
        public ContactDetail mItem;
        public Button mAccept;
        public Button mNo;

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
