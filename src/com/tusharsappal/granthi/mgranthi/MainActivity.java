package com.tusharsappal.granthi.mgranthi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.granthi.mgranthi.R;


public class MainActivity extends ListActivity {
	String[] pdflist;
	File[] imagelist;


	private AdView adView;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		adView = (AdView) findViewById(R.id.adView);    
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);


		// Copying the pdf from the asset folder to the sd card , from there the app will read and display the contents of the pdf 

		// If already copied to the sd card the app will not copy once again 

		boolean alreadyCopied = getPreferences(MODE_PRIVATE).getBoolean(
				"copied", false);

		if (!alreadyCopied) {
			copyAssets();

			SharedPreferences.Editor edit = getPreferences(MODE_PRIVATE).edit();
			edit.putBoolean("copied", true);
			edit.commit();
		}


		File images = getExternalFilesDir(null);

		imagelist = images.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return ((name.endsWith(".pdf")));
			}
		});

		// Generating the PDF List 
		pdflist = new String[imagelist.length];
		for (int i = 0; i < imagelist.length; i++) {
			pdflist[i] = imagelist[i].getName();
		}

		// Trying to change the font type of the List Names Displayed 
		Typeface.defaultFromStyle(Typeface.BOLD_ITALIC);


		// Create an ad request. Check logcat output for the hashed device ID to
		// get test ads on a physical device and on the emulator.

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, pdflist);
		setListAdapter(adapter);


	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {


		super.onListItemClick(l, v, position, id);
		String path = imagelist[(int) id].getAbsolutePath();
		openPdfIntent(path);


	}



	private void openPdfIntent(String path) {
		try {
			final Intent intent = new Intent(MainActivity.this, Second.class);
			intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, path);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Actual method to copy assets from the assets/pdfs to the sd card
	private void copyAssets() {
		AssetManager assetManager = getAssets();
		String[] files = null;

		try {
			files = assetManager.list("pdfs");

		} catch (IOException e) {
			Log.e("tag ", "Failed to get Asset File list", e);
		}
		for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open("pdfs/" + filename);
				File outFile = new File(getExternalFilesDir(null), filename);
				out = new FileOutputStream(outFile);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}