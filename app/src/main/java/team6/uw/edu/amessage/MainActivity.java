package team6.uw.edu.amessage;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import me.pushy.sdk.Pushy;
import team6.uw.edu.amessage.model.Credentials;

/**
 * This is the main class that will be used for login and register.
 */
public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener {

    private boolean mLoadFromChatNotification = false;
    private static final String TAG = MainActivity.class.getSimpleName();


    /**
     * This is the first thing to be called when first loaded and will set up
     * all the necessary information.
     *
     * @param savedInstanceState the incoming saved information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pushy.listen(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("type")) {
                mLoadFromChatNotification = getIntent().getExtras().getString("type").equals("msg");
            }
        }

        //We make activity_main.xml to be a frame layout to put the fragment inside.
        if (savedInstanceState == null) {
            if (findViewById(R.id.frame_main_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_main_container, new LoginFragment())
                        .commit();
            }
        }
    }

    /**
     * This is called when a user clicks to register a new account.
     */
    @Override
    public void onRegisterClick() {
        Bundle args = new Bundle();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_container, new RegisterFragment())
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * This method loads the home activity when the user has
     * successfully logged into the app.
     */
    @Override
    public void onLoginSuccess(Credentials theUser, String jwt) {
        Bundle args = new Bundle();

        int count = getSupportFragmentManager().getBackStackEntryCount();
        //Delete everything off back stack.
        for (int i = 0; i < count; ++i) {
            getSupportFragmentManager().popBackStack();
        }
        //This will open up a new activity.
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        //Pass the credentials to the new activity.
        intent.putExtra("Login", theUser);
        intent.putExtra(getString(R.string.keys_intent_notification_msg), mLoadFromChatNotification);
        intent.putExtra(getString(R.string.keys_intent_jwt), jwt);

        startActivity(intent);
        finish();
    }

    /**
     * This will load the login fragment when the user has succesfully registered a new
     * account.
     *
     * @param theUser the current user information.
     */
    @Override
    public void onRegisterSuccess(Credentials theUser) {

        Bundle args = new Bundle();

        int count = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            getSupportFragmentManager().popBackStack();
        }

        args.putSerializable("Login", theUser);
        RegSuccessInfoFragment theFragment = new RegSuccessInfoFragment();
        theFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, theFragment);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * This will add a fragment loading screen.
     */
    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_main_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    /**
     * This will remove the fragment loading screen.
     */
    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }
}
