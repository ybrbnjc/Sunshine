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
