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
    private RecyclerView acceptedRecyclerView;

    private List<ContactDetail> mPendingContacts;
    private List<ContactDetail> mAcceptedContacts;

    private OnPendingListFragmentInteractionListener mPendingListener;
    private OnAcceptedListFragmentInteractionListener mAcceptedListener;


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

            mAcceptedContacts = new ArrayList<ContactDetail>(
                    Arrays.asList((ContactDetail[]) getArguments().getSerializable("acceptedcontacts")));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        Context context = v.getContext();

        pendingRecyclerView = v.findViewById(R.id.list_pending);
        acceptedRecyclerView = v.findViewById(R.id.list_accepted);

        RecyclerView.LayoutManager pendingLayoutManager = new LinearLayoutManager(context);
        RecyclerView.LayoutManager acceptedLayoutManager = new LinearLayoutManager(context);

        pendingRecyclerView.setLayoutManager(pendingLayoutManager);
        acceptedRecyclerView.setLayoutManager(acceptedLayoutManager);

        MyPendingRecyclerViewAdapter pendingAdapter = new MyPendingRecyclerViewAdapter(mPendingContacts, mPendingListener);
        MyAcceptedRecyclerViewAdapter acceptedAdapter = new MyAcceptedRecyclerViewAdapter(mAcceptedContacts, mAcceptedListener);

        pendingRecyclerView.setAdapter(pendingAdapter);
        acceptedRecyclerView.setAdapter(acceptedAdapter);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ContactsFragment.OnPendingListFragmentInteractionListener) {
            mPendingListener = (ContactsFragment.OnPendingListFragmentInteractionListener) context;
        } else if(context instanceof ContactsFragment.OnAcceptedListFragmentInteractionListener) {
            mAcceptedListener = (ContactsFragment.OnAcceptedListFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPendingListener = null;
        mAcceptedListener = null;
    }


    public interface OnPendingListFragmentInteractionListener {
        void onPendingListFragmentInteraction(ContactDetail item);
    }
    public interface OnAcceptedListFragmentInteractionListener {
        void onAcceptedListFragmentInteraction(ContactDetail item);
    }
}
