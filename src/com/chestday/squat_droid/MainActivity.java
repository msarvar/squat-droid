package com.chestday.squat_droid;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.tracking.SquatPipeline;
import com.chestday.squat_droid.squat.tracking.SquatPipelineListener;
import com.chestday.squat_droid.squat.utils.Pair;
import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoDisplayAndroid;
import com.chestday.squat_droid.squat.utils.VideoInput;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {

	private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
		@Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS ) {
                // now we can call opencv code !
                start();
            } else {
                super.onManagerConnected(status);
            }
        }
	};
	
	private void start() {
		VideoInput videoInput = new VideoInput(name, true);

		VideoDisplay videoDisplay = new VideoDisplayAndroid((ImageView)findViewById(R.id.squatImageView));
		
		SquatPipeline squatPipeline = new SquatPipeline(videoInput, videoDisplay, new SquatPipelineListener() {
			public void onReadyToSquat() {
				System.out.println("Ready to Squat!");
			}

			public void onInitialModelFit() {
				System.out.println("Initial Model Fitted");
			}

			public void onSquatsComplete(List<Pair<Double, String>> scores) {
				System.out.println("Reps: " + scores.size());
				for(int i = 0; i < scores.size(); i++) {
					System.out.println("Rep " + (i+1) + " {Score: " + scores.get(i).l + "%, Problem: " + scores.get(i).r + "}");
				}
			}
		});
		
		squatPipeline.process();
		
		// Cycle through the last few frames
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			videoDisplay.show(frame);
			videoDisplay.draw();
		}
		
		videoDisplay.close();
		
		System.out.println("done");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, loaderCallback);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
