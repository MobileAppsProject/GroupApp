package team6.uw.edu.amessage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import team6.uw.edu.amessage.model.Credentials;


/**
 * This will be used when a user successfully logs into a account.
 */
public class RegSuccessInfoFragment extends Fragment {

    private Credentials mCredentials; // credentials that were built

    /**
     * Required empty public constructor.
     */
    public RegSuccessInfoFragment() {
        // Required empty public constructor
    }

    /**
     * This will the first thing that is created and will set up all the buttons and information
     * for the user.
     *
     * @param inflater           the layout to inflate.
     * @param container          the container to inflate the layout in.
     * @param savedInstanceState the saved information sent to the fragment.
     * @return the view/layout that has been inflated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reg_success_info, container, false);

        Button b = (Button) v.findViewById(R.id.fragRegSuccessLogin_button);
        //Use a method reference to add the OnClickListener
        b.setOnClickListener(this::onLoginButtonClicked);
        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            mCredentials = (Credentials) savedInstanceState.getSerializable("Login");
            //just passing this along to the next fragment (login)
        }

        return v;

    }

    /**
     * Displays login fragment when button clicked.
     *
     * @param view: the current view
     */
    private void onLoginButtonClicked(View view) {
        Bundle args = new Bundle();
        args.putSerializable("Login", mCredentials);
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_main_container, loginFragment)
                .commit();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}
