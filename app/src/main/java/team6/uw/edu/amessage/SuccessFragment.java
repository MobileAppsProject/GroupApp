
package team6.uw.edu.amessage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team6.uw.edu.amessage.model.Credentials;

/**
 * This will be used in the login fragment to load in the text fields that the user
 * used to register with.
 */
public class SuccessFragment extends Fragment {

    private static final String TAG = "LoginFrag";

    public SuccessFragment() {
        // Required empty public constructor
    }

    /**
     * This will the first thing that is created and will set up all the information
     * for the user to be prompted to login page.
     *
     * @param inflater           the layout to inflate.
     * @param container          the container to inflate the layout in.
     * @param savedInstanceState the saved information sent to the fragment.
     * @return the view/layout that has been inflated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_success, container, false);

        savedInstanceState = getArguments();

        if (savedInstanceState != null) {
            Credentials c = (Credentials) savedInstanceState.getSerializable("Success");
            TextView userEmail = (TextView) v.findViewById(R.id.fragSuccess_email_textView);
            TextView weather = (TextView) v.findViewById(R.id.fragSuccess_weather_textView);

            weather.setText("The weather is 34ºF in Tacoma, Wa");
            userEmail.setText("Welcome, " + c.getUsername());
        }
        return v;
    }
}


