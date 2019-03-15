package team6.uw.edu.amessage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This class will be used to display a waiting screen when a
 * async task is being done.
 */
public class WaitFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public WaitFragment() {
        // Required empty public constructor
    }

    /**
     * This will the first thing that is created and will set up all the information
     * for the waiting screen.
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
        return inflater.inflate(R.layout.fragment_wait, container, false);
    }

    /**
     * Default on attach method.
     *
     * @param context the current context of the frag.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        void onWaitFragmentInteractionShow();

        void onWaitFragmentInteractionHide();
    }
}
