package al.jamil.suvo.androidpcfiletransfer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FileToPc extends Activity {
	private Socket client;
	private String FileName;
	ProgressBar bar;
	int Prog;
	private PrintWriter printwriter;
	File F;
	TextView tx;
	ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_to_pc);
		start();
	}

	private void start() {
		tx=(TextView)findViewById(R.id.info);
		tx.setText("Give File Name");
		Button bt=(Button)findViewById(R.id.sent);
		bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				EditText tx=(EditText)findViewById(R.id.ed);
				FileName=tx.getText().toString();
				F=new File(Environment.getExternalStorageDirectory(),FileName);
				Toast.makeText(getBaseContext(), F.getAbsolutePath()+" "+F.getName(), Toast.LENGTH_SHORT).show();
				SendFileInfo finfo=new SendFileInfo();
				finfo.execute();
				SendFile f=new SendFile();
				f.execute();
			}
		});
		
		
	}
	private class SendFileInfo extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				client = new Socket("192.168.43.170", 4445);
				printwriter=new PrintWriter(client.getOutputStream(),true);
				printwriter.println(F.getName());
				printwriter.println(F.length());
				printwriter.close();
				client.close();
			} catch (UnknownHostException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
			return null;
		}
		protected void onPostExecute(Void result)
		{
			tx.setText("File Info Sent.Now File is Sending...");
			super.onPostExecute(result);
		}

	}
	
	private class SendFile extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				client = new Socket("192.168.43.170", 4444);
				FileInputStream in=new FileInputStream(F);
				java.io.OutputStream out=client.getOutputStream();
				BufferedOutputStream bs=new BufferedOutputStream(out);
				if (F.length()>80*Math.pow(1024, 3))
				{
					byte []buffer=new byte[(int) F.length()/8];
					for (int i=0;i<F.length();i+=F.length()/8)
					{
						in.read(buffer, i, buffer.length);
						bs.write(buffer);
					}
				}
				else
				{
				byte []buffer=new byte[(int) F.length()];
				in.read(buffer, 0, buffer.length);
				bs.write(buffer, 0, buffer.length);}
				bs.flush();
				bs.close();
				in.close();
				out.flush();
				out.close();
				client.close();
			} catch (UnknownHostException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
			return null;
		}
		protected void onPostExecute(Void result)
		{
			tx.setText("File Sent.");
			super.onPostExecute(result);
		}

	}
}
