package com.chestday.squat_droid;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.tracking.SquatPipeline;
import com.chestday.squat_droid.squat.tracking.SquatPipelineListener;
import com.chestday.squat_droid.squat.utils.Pair;
import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoInput;
import com.chestday.squat_droid.squat.utils.android.VideoBridge;
import com.chestday.squat_droid.squat.utils.android.VideoBridge.VideoBridgeReadyCallback;
import com.chestday.squat_droid.squat.utils.android.VideoDisplayAndroid;
import com.chestday.squat_droid.squat.utils.android.VideoInputCamera;
import com.chestday.squat_droid.squat.utils.android.VideoInputDummy;
import com.chestday.squat_droid.squat.utils.android.VideoInputFile;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {

	private CameraBridgeViewBase mOpenCvCameraView;
	
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
		
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view_wow);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setMaxFrameSize(240, 300);
		
		VideoBridge videoBridge = new VideoBridge();
		final SquatMainThread squat = new SquatMainThread(videoBridge, videoBridge);
		
		videoBridge.setReadyCallback(new VideoBridgeReadyCallback() {
			
			@Override
			public void start() {
				// TODO Auto-generated method stub
				squat.start();
			}
		});
		
		mOpenCvCameraView.setCvCameraViewListener(videoBridge);

        mOpenCvCameraView.enableView();

		//VideoInput videoInput = new VideoInputCamera();
		//VideoInput videoInput = new VideoInputFile("/storage/extSdCard/Video/Squat/good_squats.avi");
		//VideoInput videoInput = new VideoInputDummy(imageView.getWidth(),imageView.getHeight());

		
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
