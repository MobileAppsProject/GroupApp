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

public class MyAcceptedRecyclerViewAdapter extends RecyclerView.Adapter<MyAcceptedRecyclerViewAdapter.ViewHolder>{
    private final List<ContactDetail> mContacts;
    private final ConnectionsFragment.OnAcceptedListFragmentInteractionListener mListener;
    private String mMemberID;

    public MyAcceptedRecyclerViewAdapter(List<ContactDetail> contacts,
                                         ConnectionsFragment.OnAcceptedListFragmentInteractionListener listener,
                                         String memberid) {
        mContacts = contacts;
        mListener = listener;
        mMemberID = memberid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_connections, parent, false);
        return new MyAcceptedRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mContacts.get(position);
        holder.mEmail.setText(mContacts.get(position).getEmail());
        holder.mFname.setText(Html.fromHtml(mContacts.get(position).getAuthor()));

        holder.mDelete.setOnClickListener(v -> {
            Toast.makeText(holder.mView.getContext(),
                    "Deleted " + mContacts.get(position).getAuthor() + " from your connections!", Toast.LENGTH_LONG).show();

            //Post for pending requests
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mEmail;
        public final TextView mFname;
        public Button mDelete;
        public ContactDetail mItem;

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
