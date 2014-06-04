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
import com.chestday.squat_droid.squat.utils.MatManager;
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

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private static final int RESULT_SETTINGS = 1;
	private static final int RESULT_TTS_CHECK = 2;
	
	private Context context;
	
	private VideoBridge videoBridge;
	private SquatMainThread squat;
	private ToneGenerator toneGenerator;
	private PortraitCameraView mOpenCvCameraView;
	
	private ImageView flipButton;
	private int direction = VideoBridge.LEFT_FACING;
	
	private TextView mainTextView;
	private TableLayout scoresTable;
	private Button startButton;
	private boolean startButtonPressed = false;
	
	private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
		@Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS ) {
                // now we can call opencv code !
            	MatManager.init();
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
		
		scoresTable = (TableLayout)findViewById(R.id.score_table);
		
		flipButton = (ImageView)findViewById(R.id.flip_image_view);
		flipButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(direction == VideoBridge.LEFT_FACING) {
					// Change to right facing
					direction = VideoBridge.RIGHT_FACING;
					flipButton.setImageResource(R.drawable.flipbuttonleft);
				} else {
					// Change to left facing
					direction = VideoBridge.LEFT_FACING;
					flipButton.setImageResource(R.drawable.flipbuttonright);
				}
				
				videoBridge.setDirection(direction);
			}
		});
		
		// Set direction from default in settings
		direction = SquatPreferences.getIntValue("default_direction");
		flipButton.setImageResource(direction == VideoBridge.LEFT_FACING ? R.drawable.flipbuttonright : R.drawable.flipbuttonleft);
		
		startButton = (Button)findViewById(R.id.start_button);
		startButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startButtonPressed = true;
			}
		});
		
		toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
		
		videoBridge = new VideoBridge();
		
		squat = makeSquatMainThread();
		
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
	
	private SquatMainThread makeSquatMainThread() {
		return new SquatMainThread(videoBridge, videoBridge, new SquatPipelineListener() {
			public void onInitialised() {
				System.out.println("SQUAT: Start Pipeline");
				setText(mainTextView, "Ensure surroundings are still");
			}
			
			public void onTimeToFixCameraSettings() {
				// Lock the exposure and white balance
				mOpenCvCameraView.setExposureAndWhiteBalanceLock(true);
			}
			
			public void onTimeToUnFixCameraSettings() {
				mOpenCvCameraView.setExposureAndWhiteBalanceLock(false);
			}
			
			@Override
			public boolean isStartButtonPressed() {
				return startButtonPressed;
			}

			@Override
			public void onBackgroundStationary(boolean isStationary) {
				setText(mainTextView, isStationary ? "Press Start" : "Ensure surroundings are still");
				setStartButtonEnabled(isStationary);
			}
			
			public void onStart() {
				// Reset start button state
				startButtonPressed = false;
				setText(mainTextView, "Walk into view");
				setText(startButton, "Started");
				setStartButtonEnabled(false);
				if(SquatPreferences.getBooleanValue("vocal_instructions")) {
					Speaker.speak("Walk into view and stand still.");
				}
			}
			
			@Override
			public void squatSetupHasFigure() {
				// TODO Auto-generated method stub
				setText(mainTextView, "Found figure, stand still");
			}

			@Override
			public void squatSetupNotHasFigure() {
				// TODO Auto-generated method stub
				setText(mainTextView, "Walk into view");
			}
			
			@Override
			public void onMotionDetectorValue(double difference) {
				// TODO Auto-generated method stub
				//setMainText("Motion value: " + difference);
			}
			
			public void onReadyToSquat() {
				System.out.println("SQUAT: Ready to Squat!");
				if(SquatPreferences.getBooleanValue("sound") && SquatPreferences.getBooleanValue("detect_beep")) {
					toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK);
				}
			}

			public void onInitialModelFit() {
				System.out.println("SQUAT: Initial Model Fitted");
				setText(mainTextView, "Squat!");
				if(SquatPreferences.getBooleanValue("vocal_instructions")) {
					Speaker.speak("Start squatting when ready!");
				}
			}
			
			@Override
			public void onSquatBelowParallel() {
				if(SquatPreferences.getBooleanValue("sound") && SquatPreferences.getBooleanValue("parallel_beep")) {
					toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK);
				}
			}
			
			public void onAscendStart() {
				setText(mainTextView, "Walk away when done");
			}

			public void onSquatsComplete(List<Pair<Double, String>> scores) {
				setText(mainTextView, "Finished");
				
				addScoreRow(new String[]{"Rep", "Score", "Problem"}, Color.WHITE);
				
				for(int i = 0; i < scores.size(); i++) {
					String scorePercentage = String.format("%.1f", scores.get(i).l);
					addScoreRow(new String[]{Integer.toString(i+1), scorePercentage, scores.get(i).r}, percentageToColour(scores.get(i).l));
				}
				
				//setText(scoresTextView, scoreString);
				
				setText(startButton, "Reset");
				
				setStartButtonEnabled(true);
				
				// Now is a good time to garbage collect
				System.gc();
			}
			
			public void onFinish() {
				startButtonPressed = false;
				setStartButtonEnabled(false);
				resetSquatMainThread();
			}
		});
	}
	
	private void resetSquatMainThread() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					squat.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				squat = makeSquatMainThread();
				setText(startButton, "Start");
				
				// Clear the score table
				scoresTable.removeAllViews();
				
				squat.start();
			}
		});
		
	}

	private void setText(final TextView textView, final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				textView.setText(text);			
			}
		});
	}
	
	private void setText(final Button button, final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				button.setText(text);
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
	
	private void addScoreRow(final String[] cols, final int colour) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TableRow row = new TableRow(context);
				
				for(String col : cols) {
					TextView tv = new TextView(context);
					tv.setText(col);
					tv.setTextAppearance(context, R.style.ScoreTable);
					tv.setTextColor(colour);
					row.addView(tv);
				}
				
				scoresTable.addView(row);
			}
		});
	}
	
	private int percentageToColour(double value){
	    return android.graphics.Color.HSVToColor(new float[]{(float)value,1f,1f});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		
		// Initialise preferences object
		SquatPreferences.init(this);
		
		//Debug.startMethodTracing("squat", 80000000);
		
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// Start the text to speech check
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, RESULT_TTS_CHECK);
		
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, loaderCallback);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		//Debug.stopMethodTracing();
		Speaker.shutdown();
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
			Intent i = new Intent(this, UserSettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SETTINGS:
            break;
 
        case RESULT_TTS_CHECK:
        	if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
        		Speaker.init(this);
        		
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
 
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
