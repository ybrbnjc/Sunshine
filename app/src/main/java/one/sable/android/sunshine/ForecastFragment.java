package one.sable.android.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Created by admin on 26.06.2017.
 */
public class ForecastFragment extends Fragment {
	
	
    //private ArrayAdapter<String> mForecastAdapter;
	private ListView forecastListview;

    public ForecastFragment() {

    }

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}	
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.layout.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
			new FetchWeatherTask().execute("524901");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
      	View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		
        String[] fakeDataArray = new String[]{
                "Yesterday - Rainy - 17/8",
                "Today - Cloudy - 16/8",
                "Tomorrow - Rainy - 18/6",
                "Saturday - Cloudy - 19/10",
                "Sunday - Rainy - 20/13",
                "Monday - Rainy - 22/10",
                "Tuesday - Foggy - 30/25"
        };
        //List<String> fakeData = new ArrayList<String>(Arrays.asList(fakeDataArray));


        new FetchWeatherTask().execute("524901");

/*
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_text_view,
                fakeData);
*/
        forecastListview = (ListView) rootView.findViewById(R.id.listview_forecast);
      //  forecastListview.setAdapter(mForecastAdapter);
        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        protected String[] doInBackground(String... postalCode)
        {
					
			// These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
			HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
			String[] resultStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
				Uri.Builder builder = new Uri.Builder()
					.scheme("http")
					.authority("api.openweathermap.org")
					.appendPath("data")
					.appendPath("2.5")
					.appendPath("forecast")
					.appendQueryParameter("appid", "2bec85f095f36e589c16cc58de321265")
					.appendQueryParameter("id", postalCode[0])
					.appendQueryParameter("units", "metric")
					.appendQueryParameter("mode", "JSON");
                URL url = new URL(builder.build().toString());
				
				Log.v("look_here", builder.build().toString());
				
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("ForecastFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }
            }
			
			Log.v("and_here", forecastJsonStr);
			try {
			resultStr =  getWeatherDataFromJson(forecastJsonStr,28);
			} catch (JSONException e) {
				Log.e("ForecastFragment", "Error parsing JSON string");
				
			}
			return resultStr;
			
			}

		/* The date/time conversion code is going to be moved outside the asynctask later,
		 * so for convenience we're breaking it out into its own method now.
		 */
		private String getReadableDateString(long time){
			// Because the API returns a unix timestamp (measured in seconds),
			// it must be converted to milliseconds in order to be converted to valid date.
			Date date = new Date(time * 1000);
			SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
			return format.format(date).toString();
		}

		/**
		 * Prepare the weather high/lows for presentation.
		 */
		private String formatHighLows(double high, double low) {
			// For presentation, assume the user doesn't care about tenths of a degree.
			long roundedHigh = Math.round(high);
			long roundedLow = Math.round(low);

			String highLowStr = roundedHigh + "/" + roundedLow;
			return highLowStr;
		}

		/**
		 * Take the String representing the complete forecast in JSON Format and
		 * pull out the data we need to construct the Strings needed for the wireframes.
		 *
		 * Fortunately parsing is easy:  constructor takes the JSON string and converts it
		 * into an Object hierarchy for us.
		 */
		private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
        throws JSONException {

			// These are the names of the JSON objects that need to be extracted.
			final String OWM_LIST = "list";
			final String OWM_WEATHER = "weather";
			final String OWM_TEMPERATURE = "temp";
			final String OWM_MAX = "max";
			final String OWM_MIN = "min";
			final String OWM_DATETIME = "dt";
			final String OWM_DESCRIPTION = "main";

			JSONObject forecastJson = new JSONObject(forecastJsonStr);
			JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

			String[] resultStrs = new String[numDays];
			for(int i = 0; i < weatherArray.length(); i++) {
				// For now, using the format "Day, description, hi/low"
				String day;
				String description;
				String highAndLow;

				// Get the JSON object representing the day
				JSONObject dayForecast = weatherArray.getJSONObject(i);

				// The date/time is returned as a long.  We need to convert that
				// into something human-readable, since most people won't read "1400356800" as
				// "this saturday".
				long dateTime = dayForecast.getLong(OWM_DATETIME);
				day = getReadableDateString(dateTime);

				// description is in a child array called "weather", which is 1 element long.
				JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
				description = weatherObject.getString(OWM_DESCRIPTION);

				// Temperatures are in a child object called "temp".  Try not to name variables
				// "temp" when working with temperature.  It confuses everybody.
				JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
				double high = temperatureObject.getDouble(OWM_MAX);
				double low = temperatureObject.getDouble(OWM_MIN);

				highAndLow = formatHighLows(high, low);
				resultStrs[i] = day + " - " + description + " - " + highAndLow;
			}

			return resultStrs;
		
        }

        @Override
        protected void onPostExecute(String[] s) {
			List<String> data = new ArrayList<String>(Arrays.asList(s));
			ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_text_view,
                data);
				
			
        }
    }

}
