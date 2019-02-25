package team6.uw.edu.amessage;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team6.uw.edu.amessage.ContactFragment.OnListFragmentInteractionListener;
import team6.uw.edu.amessage.contact.ContactDetail;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactDetail} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {

    private final List<ContactDetail> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyContactRecyclerViewAdapter(List<ContactDetail> blogs, OnListFragmentInteractionListener listener) {
        mValues = blogs;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());
//        holder.mContentView.setText(mValues.get(position).getPubDate());
        holder.mSamplingView.setText(Html.fromHtml(mValues.get(position).getTeaser()));

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

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mSamplingView;
        public ContactDetail mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.fragContact_Title_textView);
            mContentView = (TextView) view.findViewById(R.id.fragBlog_publishDate_textView);
            mSamplingView = (TextView) view.findViewById(R.id.fragContact_sampling_textView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
