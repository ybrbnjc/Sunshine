package one.sable.android.sunshine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import android.util.Log;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().
                    add(R.id.container, new PlaceholderFragment()).
                    commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {
        
		private ArrayAdapter<String> mForecastAdapter;
		
		public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			
			String[] fakeDataArray = new String[] {
			"Yesterday - Rainy - 17/8",
			"Today - Cloudy - 16/8",
			"Tomorrow - Rainy - 18/6",
			"Saturday - Cloudy - 19/10",
			"Sunday - Rainy - 20/13",
			"Monday - Rainy - 22/10",
			"Tuesday - Foggy - 30/25"
			};
			List<String> fakeData = new ArrayList<String>(Arrays.asList(fakeDataArray));
			
			
			// These two need to be declared outside the try/catch
			// so that they can be closed in the finally block.
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;

			// Will contain the raw JSON response as a string.
			String forecastJsonStr = null;

			try {
				// Construct the URL for the OpenWeatherMap query
				// Possible parameters are avaiable at OWM's forecast API page, at
				// http://openweathermap.org/API#forecast
				URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=Moscow,ru&appid=2bec85f095f36e589c16cc58de321265&units=metric");

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
				Log.e("PlaceholderFragment", "Error ", e);
				// If the code didn't successfully get the weather data, there's no point in attemping
				// to parse it.
				forecastJsonStr = null;
			} finally{
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						Log.e("PlaceholderFragment", "Error closing stream", e);
					}
				}
			}
			
			mForecastAdapter = new ArrayAdapter<String> (
				getActivity(),
				R.layout.list_item_forecast,
				R.id.list_item_forecast_text_view,
				fakeData);
				
			ListView forecastListview = (ListView) rootView.findViewById(R.id.listview_forecast);
			forecastListview.setAdapter(mForecastAdapter);
            return rootView;
        }

    }
}
