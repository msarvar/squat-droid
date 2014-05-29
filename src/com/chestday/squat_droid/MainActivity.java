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
import com.chestday.squat_droid.squat.utils.android.PortraitCameraView;
import com.chestday.squat_droid.squat.utils.android.VideoBridge;
import com.chestday.squat_droid.squat.utils.android.VideoBridge.VideoBridgeReadyCallback;
import com.chestday.squat_droid.squat.utils.android.VideoDisplayAndroid;
import com.chestday.squat_droid.squat.utils.android.VideoInputCamera;
import com.chestday.squat_droid.squat.utils.android.VideoInputDummy;
import com.chestday.squat_droid.squat.utils.android.VideoInputFile;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private PortraitCameraView mOpenCvCameraView;
	private TextView mainTextView;
	private Button startButton;
	private boolean startButtonPressed = false;
	
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
		
		mOpenCvCameraView = (PortraitCameraView) findViewById(R.id.camera_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		//mOpenCvCameraView.setMaxFrameSize(320, 240);
		mOpenCvCameraView.setMaxFrameSize(176, 152);
		
		mainTextView = (TextView)findViewById(R.id.main_text);
		mainTextView.setTextColor(Color.WHITE);
		
		startButton = (Button)findViewById(R.id.start_button);
		startButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startButtonPressed = true;
			}
		});
		
		final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
		
		VideoBridge videoBridge = new VideoBridge();
		
		final SquatMainThread squat = new SquatMainThread(videoBridge, videoBridge, new SquatPipelineListener() {
			public void onTimeToFixCameraSettings() {
				System.out.println("SQUAT: Start Pipeline");
				// Lock the exposure and white balance
				mOpenCvCameraView.fixExposureAndWhiteBalance();
				
				setMainText("Wait for still surroundings");
			}
			
			@Override
			public boolean isStartButtonPressed() {
				
				return startButtonPressed;
			}

			@Override
			public void onBackgroundStationary(boolean isStationary) {
				setMainText(isStationary ? "Press Start" : "Please ensure surroundings are still");
				setStartButtonEnabled(isStationary);
			}
			
			public void onStart() {
				setMainText("Walk into view");
			}
			
			@Override
			public void squatSetupHasFigure() {
				// TODO Auto-generated method stub
				setMainText("Found figure, stand still");
			}

			@Override
			public void squatSetupNotHasFigure() {
				// TODO Auto-generated method stub
				setMainText("Walk into view");
			}
			
			@Override
			public void onMotionDetectorValue(double difference) {
				// TODO Auto-generated method stub
				//setMainText("Motion value: " + difference);
			}
			
			public void onReadyToSquat() {
				System.out.println("SQUAT: Ready to Squat!");
				toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2);
			}

			public void onInitialModelFit() {
				System.out.println("SQUAT: Initial Model Fitted");
				setMainText("Squat!");
			}
			
			@Override
			public void onSquatBelowParallel() {
				// TODO Auto-generated method stub
				toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK);
			}

			public void onSquatsComplete(List<Pair<Double, String>> scores) {
				setMainText("Finished. Reps: " + scores.size());
				System.out.println("SQUAT: Reps: " + scores.size());
				for(int i = 0; i < scores.size(); i++) {
					System.out.println("SQUAT: Rep " + (i+1) + " {Score: " + scores.get(i).l + "%, Problem: " + scores.get(i).r + "}");
				}
			}
		});
		
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
	
	private void setMainText(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mainTextView.setText(text);
			}
		});
	}
	
	private void setStartButtonEnabled(final boolean enabled) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(startButton.isEnabled() != enabled) {
					startButton.setEnabled(enabled);
				}
			}
		});
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
