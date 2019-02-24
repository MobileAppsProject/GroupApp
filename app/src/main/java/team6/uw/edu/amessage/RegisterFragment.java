package team6.uw.edu.amessage;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team6.uw.edu.amessage.model.Credentials;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRegisterFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment {

    private OnRegisterFragmentInteractionListener mListener;
    private static final String TAG = "LoginFrag";
    private Credentials mCredentials;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Button b = (Button) v.findViewById(R.id.fragRegister_register_button);
        //Use a method reference to add the OnClickListener
        b.setOnClickListener(this::onRegisterClicked);

        return v;

    }
    //This will call the the register fragment to be called.
    public void onRegisterClicked(View view) {
        if (mListener != null) {

            EditText userName = (EditText) getActivity().findViewById(R.id.fragRegister_userName_editText);
            EditText userFirstName = (EditText) getActivity().findViewById(R.id.fragRegister_firstName_editText);
            EditText userLastName = (EditText) getActivity().findViewById(R.id.fragRegister_lastName_editText);
            EditText userEmail = (EditText) getActivity().findViewById(R.id.fragRegister_email_editText);
            EditText userPassword = (EditText) getActivity().findViewById(R.id.fragRegister_password_editText);
            EditText userRetypePassword = (EditText) getActivity().findViewById(R.id.fragRegister_retypePassword_editText);

            //TODO Clean up the logic better and decompose.
            boolean hasError = false;
            if (userName.getText().length() == 0) {
                hasError = true;
                userName.setError("Field must not be empty.");
            }
            if (userFirstName.getText().length() == 0) {
                hasError = true;
                userFirstName.setError("Field must not be empty.");
            }
            if (userLastName.getText().length() == 0) {
                hasError = true;
                userLastName.setError("Field must not be empty.");
            }
            if (userEmail.getText().length() == 0) {
                hasError = true;
                userEmail.setError("Field must not be empty.");
                //TODO Change this to the better method of checking for email using method down bellow.
                //!isEmailValid(emailEdit.getText().toString())
            }  else if (userEmail.getText().toString().chars() .filter(ch -> ch =='@').count() != 1) {
                hasError = true;
                userEmail.setError("Field must contain a valid email address.");
            }
            if (userPassword.getText().length() == 0) {
                hasError = true;
                userPassword.setError("Field must not be empty.");
            } else if (userPassword.getText().length() < 6) {
                hasError = true;
                userPassword.setError("Password must at least 6 characters.");
            }
            if (userRetypePassword.getText().length() == 0) {
                hasError = true;
                userRetypePassword.setError("Field must not be empty.");
            }
            if (!userPassword.getText().toString().equals(userRetypePassword.getText().toString())) {
                hasError = true;
                userPassword.setError("Passwords do not match!.");
            }
            // require a special character for password
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(userPassword.getText().toString());
            if (!m.find()) {
                userPassword.setError("Password must include at least one special character.");
            }

            if (!hasError) {
                Credentials.Builder credentialsB = new Credentials.Builder(
                        userEmail.getText().toString(),
                        userPassword.getText().toString());

                credentialsB.addUsername(userName.getText().toString());
                credentialsB.addFirstName(userFirstName.getText().toString());
                credentialsB.addLastName(userLastName.getText().toString());

                Credentials credentials = credentialsB.build();

                //build the web service URL
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_register))
                        .build();

                //build the JSONObject
                JSONObject msg = credentials.asJSONObject();

                mCredentials = credentials;

                //instantiate and execute the AsyncTask.
                new SendPostAsyncTask.Builder(uri.toString(), msg)
                        .onPreExecute(this::handleRegisterOnPre)
                        .onPostExecute(this::handleRegisterOnPost)
                        .onCancelled(this::handleErrorsInTask)
                        .build().execute();
            }
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR",  result);
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleRegisterOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_register_success));

            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                mListener.onRegisterSuccess(mCredentials);
                return;
            } else {
                //Login was unsuccessful. Don’t switch fragments and
                // inform the user
                ((TextView) getView().findViewById(R.id.fragRegister_register_button))
                        .setError("Login Unsuccessful");
                Toast.makeText(getActivity(), "Login Unsuccessful!",
                        Toast.LENGTH_LONG).show();
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.fragRegister_register_button))
                    .setError("Login Unsuccessful");
            Toast.makeText(getActivity(), "Login Unsuccessful!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnRegisterFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onRegisterSuccess(Credentials theUser);
        void onWaitFragmentInteractionShow();
        void onWaitFragmentInteractionHide();

    }
}

