package team6.uw.edu.amessage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import team6.uw.edu.amessage.WeatherForecastFragment.OnListFragmentInteractionListener;
import team6.uw.edu.amessage.weather.WeatherDetail;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WeatherDetail} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyWeatherForecastRecyclerViewAdapter extends RecyclerView.Adapter<MyWeatherForecastRecyclerViewAdapter.ViewHolder> {

    private final List<WeatherDetail> mValues;
    private final OnListFragmentInteractionListener mListener;


    /**
     * Sets the values and interaction listeners.
     * @param wd set to mValues
     * @param listener set to mListener
     */
    public MyWeatherForecastRecyclerViewAdapter(List<WeatherDetail> wd, OnListFragmentInteractionListener listener) {
        mValues = wd;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_weatherforecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDateView.setText(mValues.get(position).getDate());
        holder.mTempView.setText(mValues.get(position).getTemp());
        String img_url = "https://www.weatherbit.io/static/img/icons/" + mValues.get(position).getDescription() +".png";
        new MyWeatherForecastRecyclerViewAdapter.DownloadImageTask(holder.mDescriptionView)
                .execute(img_url);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Class that defines the view elements.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDateView;
        public final TextView mTempView;
        public final ImageView mDescriptionView;
        public WeatherDetail mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDateView = (TextView) view.findViewById(R.id.fragWeatherForecast_Date_textView);
            mTempView = (TextView) view.findViewById(R.id.fragWeatherForecast_Temp_textView);
            mDescriptionView = (ImageView) view.findViewById(R.id.fragWeatherForecast_icon_imageview);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTempView.getText() + "'";
        }
    }

    /**
     * Class that downloads the weather icons for forecasts.
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        /**
         * Returns bitmap.
         * @param urls
         * @return
         */
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        /**
         * Display icon to user.
         * @param result
         */
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
