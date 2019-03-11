package team6.uw.edu.amessage;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team6.uw.edu.amessage.contact.ContactDetail;
import team6.uw.edu.amessage.contact.ContactGenerator;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {


    private RecyclerView pendingRecyclerView;
    private RecyclerView sentRecyclierView;

    private List<ContactDetail> mPendingContacts;
    private List<ContactDetail> mSentContacts;

    private OnPendingListFragmentInteractionListener mPendingListener;
    private OnSentListFragmentInteractionListener mSentListener;

    private String mMemberID;
    private String mJwToken;



    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance(int columnCount) {
        ContactsFragment fragment = new ContactsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPendingContacts = new ArrayList<ContactDetail>(
                    Arrays.asList((ContactDetail[]) getArguments().getSerializable("pendingcontacts")));

            mSentContacts = new ArrayList<ContactDetail>(
                    Arrays.asList((ContactDetail[]) getArguments().getSerializable("sentcontacts")));

            mJwToken = getArguments().getString("jwtoken");
            mMemberID = getArguments().getString("memberid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        Context context = v.getContext();

        pendingRecyclerView = v.findViewById(R.id.list_pending);
        sentRecyclierView = v.findViewById(R.id.list_sent);

        RecyclerView.LayoutManager pendingLayoutManager = new LinearLayoutManager(context);
        RecyclerView.LayoutManager sentLayoutManager = new LinearLayoutManager(context);

        pendingRecyclerView.setLayoutManager(pendingLayoutManager);
        sentRecyclierView.setLayoutManager(sentLayoutManager);

        MyPendingRecyclerViewAdapter pendingAdapter = new MyPendingRecyclerViewAdapter(mPendingContacts, mPendingListener, mMemberID, mJwToken);
        MySentRecyclerViewAdapter sentAdapter = new MySentRecyclerViewAdapter(mSentContacts, mSentListener, mMemberID, mJwToken);

        pendingRecyclerView.setAdapter(pendingAdapter);
        sentRecyclierView.setAdapter(sentAdapter);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ContactsFragment.OnPendingListFragmentInteractionListener) {
            mPendingListener = (ContactsFragment.OnPendingListFragmentInteractionListener) context;
        } else if(context instanceof ContactsFragment.OnSentListFragmentInteractionListener) {
            mSentListener = (ContactsFragment.OnSentListFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPendingListener = null;
        mSentListener = null;
    }


    public interface OnPendingListFragmentInteractionListener {
        void onPendingListFragmentInteraction(ContactDetail item);
    }
    public interface OnSentListFragmentInteractionListener {
        void onSentListFragmentInteractionListener(ContactDetail item);
    }
}