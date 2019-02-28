package team6.uw.edu.amessage;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchConnectionFragment extends Fragment {


    public SearchConnectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_connection, container, false);
        TextView friends = (TextView) v.findViewById(R.id.ChangeText);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = sharedPref.getString("myFriends", null);
        friends.setText("My Friends: \n" + defaultValue);
        Log.d("defaultValue", "defaault: "+ defaultValue);
//        int highScore = sharedPref.getInt(getString(R.string.saved_high_score_key), defaultValue);
        return v;
    }

}
