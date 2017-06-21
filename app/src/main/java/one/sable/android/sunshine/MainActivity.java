package one.sable.android.sunshine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().
			add(R.id.container , new PlaceholderFragment()).
			commit();
		}
    }
	
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment(){
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main,container, false);
			return rootView;
		}
		
	}
}
