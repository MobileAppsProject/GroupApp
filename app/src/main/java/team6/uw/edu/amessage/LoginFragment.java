package team6.uw.edu.amessage;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import me.pushy.sdk.Pushy;
import team6.uw.edu.amessage.model.Credentials;
import team6.uw.edu.amessage.utils.SendPostAsyncTask;

/**
 * This class allow for you to check login credential and call the backend to check
 * for all the specific details.
 */
public class LoginFragment extends Fragment {

    private OnLoginFragmentInteractionListener mListener;
    private static final String TAG = "LoginFrag";
    private Credentials mCredentials;
    private String mJwt;
    public static String mUserId;
    public String mMemberID;
    private String mUserName;

    /**
     * Default constructor.
     */
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * This is the first thing called to set up the layout.
     *
     * @param inflater           the layout to be inflated.
     * @param container          the container to inflate the layout in.
     * @param savedInstanceState the saved info being passed in.
     * @return the inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        // Inflate the layout for this fragment.
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button b = (Button) v.findViewById(R.id.fragLogin_register_button);
        //Use a method reference to add the OnClickListener
        b.setOnClickListener(this::onRegisterButtonClicked);

        b = (Button) v.findViewById(R.id.fragLogin_signIn_button);
        //Use a method reference to add the OnClickListener
        b.setOnClickListener(this::onLoginButtonClicked);

        //Here since not connecting to database we will have to populate some users.
        savedInstanceState = getArguments();

        if (savedInstanceState != null) {
            Credentials c = (Credentials) savedInstanceState.getSerializable("Login");
            EditText userEmail = (EditText) v.findViewById(R.id.fragLogin_email_editText);
            EditText userPassword = (EditText) v.findViewById(R.id.fragLogin_password_editText);
            userEmail.setText(c.getEmail());
            userPassword.setText(c.getPassword());
        }
        return v;

    }

    /**
     * First thing to be called and will login user if haven't logged out.
     */
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {
            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.fragLogin_email_editText);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.fragLogin_password_editText);
            passwordEdit.setText(password);

            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());
        }
    }

    /**
     * Login helper method
     */
    private void doLogin(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();
        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();
        mCredentials = credentials;
        Log.d("JSON Credentials", msg.toString());
        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleLoginOnPre)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * This will call the the register fragment to be called.
     */
    public void onRegisterButtonClicked(View view) {
        if (mListener != null) {
            //check if valid email was sent.
            mListener.onRegisterClick();
            Log.wtf(TAG, "Register!!");
        }
    }

    /**
     * This will call the the Login/Success fragment to be called.
     */
    public void onLoginButtonClicked(View view) {
        if (mListener != null) {
            attemptLogin(view);

        }
    }

    /**
     * Stores credentials in shared preferences
     */
    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }

    /**
     * This will attempt the login and check all the user credentials are correct.
     *
     * @param theButton the login button being clicked.
     */
    private void attemptLogin(final View theButton) {

        EditText emailEdit = getActivity().findViewById(R.id.fragLogin_email_editText);
        EditText passwordEdit = getActivity().findViewById(R.id.fragLogin_password_editText);

        boolean hasError = false;
        if (emailEdit.getText().length() == 0) {
            hasError = true;
            emailEdit.setError("Field must not be empty.");
        } else if (emailEdit.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            emailEdit.setError("Field must contain a valid email address.");
        }

        if (passwordEdit.getText().length() == 0) {
            hasError = true;
            passwordEdit.setError("Field must not be empty.");
        }

        if (!hasError) {
            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());
        }
    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Default on attach.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    /**
     * Default on detach.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));
            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                mJwt = resultsJSON.getString(
                        getString(R.string.keys_json_login_jwt));

                mUserId = resultsJSON.getString(
                        getString(R.string.keys_json_login_userid));

                mUserName = resultsJSON.getString(
                        "username");
                mCredentials = new Credentials.Builder(mCredentials.getEmail(), mCredentials.getPassword())
                        .addUsername(mUserName)
                        .build();

                Log.d("UserID", "My UserId: " + mUserId);
                new RegisterForPushNotificationsAsync().execute();

                return;
            } else {
                //Login was unsuccessful. Don’t switch fragments and
                // inform the user
                ((TextView) getView().findViewById(R.id.fragLogin_email_editText))
                        .setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.fragLogin_email_editText))
                    .setError("Login Unsuccessful");
        }
    }

    /**
     * This will handle push token on post method.
     *
     * @param result the new pushy token.
     */
    private void handlePushyTokenOnPost(String result) {
        try {

            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");


            if (success) {
                saveCredentials(mCredentials);
                mListener.onLoginSuccess(mCredentials, mJwt);
                return;
            } else {
                //Saving the token wrong. Don’t switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.fragLogin_email_editText))
                        .setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.fragLogin_email_editText))
                    .setError("Login Unsuccessful");
        }
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
    public interface OnLoginFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onLoginSuccess(Credentials theUser, String jwt);

        void onRegisterClick();

        void onWaitFragmentInteractionShow();

        void onWaitFragmentInteractionHide();
    }

    /**
     * Async method to handle registering for push notifications.
     */
    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            String deviceToken = "";

            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getActivity().getApplicationContext());

                //subscribe to a topic (this is a Blocking call)
                Pushy.subscribe("all", getActivity().getApplicationContext());
            } catch (Exception exc) {

                cancel(true);
                // Return exc to onCancelled
                return exc.getMessage();
            }

            // Success
            return deviceToken;
        }

        /**
         * Will cancel its current progress.
         *
         * @param errorMsg the error.
         */
        @Override
        protected void onCancelled(String errorMsg) {
            super.onCancelled(errorMsg);
            Log.d("aMessage", "Error getting Pushy Token: " + errorMsg);
        }

        /**
         * This will be called when the async method is finished.
         *
         * @param deviceToken the new token register to the device/user.
         */
        @Override
        protected void onPostExecute(String deviceToken) {
            // Log it for debugging purposes
            Log.d("PhishApp", "Pushy device token: " + deviceToken);

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_pushy))
                    .appendPath(getString(R.string.ep_token))
                    .build();

            //build the JSONObject
            JSONObject msg = mCredentials.asJSONObject();

            try {
                msg.put("token", deviceToken);
                Log.wtf("w", "THIS IS THE DEVICE TOKEN: " + deviceToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(LoginFragment.this::handlePushyTokenOnPost)
                    .onCancelled(LoginFragment.this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwt)
                    .build().execute();
        }
    }

}
