
package team6.uw.edu.phishapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import team6.uw.edu.phishapp.model.Credentials;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class SuccessFragment extends Fragment {

    private static final String TAG = "LoginFrag";

    public SuccessFragment() {
        // Required empty public constructor
    }

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
            userEmail.setText("Welcome, " + c.getEmail());
        }
        return v;
    }
}


