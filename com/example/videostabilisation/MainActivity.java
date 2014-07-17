package com.example.videostabilisation;

import java.io.File;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView fps_text;
	private ImageView image_view;
	private ProgressBar progressBar;
	
	private FFmpegFrameGrabber grabber;
	private File sdDir = Environment.getExternalStorageDirectory();
	private File file = new File(sdDir, "/Video/Fußball.mp4");
	private StabilisationThread st;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fps_text = new TextView(this);
		fps_text = (TextView) findViewById(R.id.fps);

		image_view = new ImageView(this);
		image_view = (ImageView) findViewById(R.id.imageView1);
		
		progressBar = new ProgressBar(this);
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void start_Button_Click(View v) {
		grabber = new FFmpegFrameGrabber(file);
		try {
			grabber.start();
			progressBar.setMax(grabber.getLengthInFrames());
			 st = new StabilisationThread(
					grabber.getLengthInTime(), grabber.getFrameRate(), grabber.getLengthInFrames(),file);
			grabber.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				int i = 0;

				while (!st.isFin()) {
					final int x = i;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							st.next();
							fps_text.setText(String.valueOf(x));
							image_view.setImageBitmap(st.getCurrentBitmap());
							progressBar.setProgress(x);
						}
					});
					i++;
				}
				return null;
			}
		};
		task.execute(this);

	}
}
