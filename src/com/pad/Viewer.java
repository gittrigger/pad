package com.pad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Viewer extends Activity {

    @Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
	}





	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
	}

	private static final int ACTIVITY_ADD=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_LIST=2;
    
   
	// Menu Button Menu
	private static final int MENU_ADD = Menu.FIRST;	
	private static final int MENU_DELETE = Menu.FIRST+1;	
	private static final int MENU_CONFIGURE = Menu.FIRST+2;
	private static final int MENU_SHARE = Menu.FIRST+3;
	private static final int MENU_LIST = Menu.FIRST+4;
	
	private static final String TAG = "Pad Viewer";
	

	private EditText mPathText;
	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	private int mSavedFlag;
	private DbAdapter mDbHelper;
	private int mLastKey;
	private Button confirmButton, appendButton, topButton, upButton, downButton;
	private int selectStart, selectEnd; 

//	private Context mContext;
//	public Viewer(Context ctx){
//    	this.mContext = ctx;
//    }
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer);
        
        
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        
		/* Buttons */
		confirmButton = (Button) findViewById(R.id.menu_confirm);
		appendButton = (Button) findViewById(R.id.menu_append);
		topButton = (Button) findViewById(R.id.menu_top);
		upButton = (Button) findViewById(R.id.menu_up);
		downButton = (Button) findViewById(R.id.menu_down);

		selectStart = 0;
		selectEnd   = 0;
		
    	/* Content */
        mTitleText = (EditText) findViewById(R.id.content_title);
		mBodyText = (EditText) findViewById(R.id.content_body);
		
		/* State Persistence */
		mRowId = savedInstanceState != null ? savedInstanceState.getLong(DbAdapter.COL_ROWID) : null;
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();            
			mRowId = extras != null ? extras.getLong(DbAdapter.COL_ROWID) : null;
		}
		
		/* Populate Fields */
		populateFields();
		

		/*
		 * Set entry experience
		 */
		
		if( mTitleText.length() == 0 ){
			mTitleText.requestFocus();
		}else{
			mBodyText.requestFocus();
			mBodyText.setCursorVisible(false);
		}

		try {
			saveState();
		} catch (IOException e) {
			Log.e(TAG,e.getLocalizedMessage() +" failed because " + e.getCause().getLocalizedMessage() );
		}

		/*
		 * Fields: mTitleText, mBodyText
		 */

		mTitleText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER){
					mBodyText.requestFocus();
					mBodyText.setCursorVisible(false);
					return true;
				}
				return false;
			}
		});

		mBodyText.setKeepScreenOn(true);
		mBodyText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event){
				// stop the 2 second timer
				// set the title color to red
				// set the 2 second timer
				// verify 
				
				if( keyCode == KeyEvent.KEYCODE_SPACE ){
					//int color = Color.argb(0, 230, 100, 100);
					//mTitleText.setBackgroundColor( Color.argb(255, 180, 20, 20) );
					//if( mBodyText.getText().toString()[mBodyText.getText().toString().length()] == "\t" ){
					//confirmButton.requestFocus();
					//}
					
						//c.getPosition();
				}
				
				if( (keyCode == KeyEvent.KEYCODE_SPACE && mLastKey == KeyEvent.KEYCODE_PERIOD)
					|| keyCode == KeyEvent.KEYCODE_ENTER
						){
					try {
						saveState();
					} catch (IOException e) {
						Log.e(TAG,e.getLocalizedMessage() +" failed because " + e.getCause().getLocalizedMessage() );
					}
				}
				
				//Context k2 = new Context("background");
				//AttributeSet v2 = "#cccccc";
				//LayoutParams params = new LayoutParams(k2, v2);
				
				//mTitleText.setLayoutParams(params);
				//mTitleText.setBackgroundColor(999999);
				mLastKey = keyCode;
				
				return false;
			}
			
		});
		
		mBodyText.setOnTouchListener(new View.OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == event.ACTION_UP ){
					return false;
				}
				if( event.getAction() == event.ACTION_MOVE ){
					mBodyText.setCursorVisible(false);
					selectEnd = mBodyText.getSelectionStart();	
					//mBodyText.setSelection(selectStart, selectStart);
				}
				if( event.getAction() == event.ACTION_DOWN ){
					mBodyText.setCursorVisible(true);
					selectStart = mBodyText.getSelectionStart();
					/*mBodyText.setCursorVisible(true);
					selectEnd = mBodyText.getSelectionStart();
					
					if( mBodyText.getText().charAt(selectEnd) == (int) ' '
						|| mBodyText.getText().charAt(selectEnd) == (int) 10
						|| mBodyText.getText().charAt(selectEnd) == (int) 13
						){
						
					}else{
						selectStart = selectEnd - 5;
						if(selectStart < 0){selectStart = 0;}
						mBodyText.setSelection(selectStart, selectStart);
					}
					*/
				}
				return false;
			}
			
		});
		
		/*
		 * Buttons: On Touch Listeners, On Click Listeners
		 */
		

		
		
		/*
		 * On Touch Listeners
		 * Not making the cursor visible until content is touched is intentional
		 * Typing to an append without a cursor is peaceful.
		 */
		 
		appendButton.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == event.ACTION_DOWN ){
					//appendButton.clearFocus();
					//appendButton.clearAnimation();
					appendButton.setClickable(false);
					appendButton.setPressed(false);
					mBodyText.clearFocus();
					mBodyText.append("\n");
					mBodyText.requestFocus();
					mBodyText.setCursorVisible(false);
					// show cursor for 1.88 seconds and disappear.
						
					mBodyText.setSelection(mBodyText.length());
					mBodyText.setCursorVisible(true);
					mBodyText.setCursorVisible(false);
				}
				return false;
			}
		});

		topButton.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == event.ACTION_DOWN ){
					mBodyText.requestFocus();
					mBodyText.setCursorVisible(false);
					mBodyText.setSelection(0);
					mBodyText.scrollTo(0, 0);
				}
				return false;

			}

		});
		
		//mBodyText.getTextSize() and mBodyText.computeScroll()
		// use getTextSize to lockstep the up and down
		upButton.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == event.ACTION_DOWN ){
					mBodyText.requestFocus();
					mBodyText.setCursorVisible(false);
					int goUp = mBodyText.getScrollY()-mBodyText.getHeight()-20;
					if(goUp < 0){goUp = 0;}
					mBodyText.scrollTo(mBodyText.getScrollX(), goUp );
					mBodyText.computeScroll();
				}
				
				return false;
			}
		});
		
		downButton.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == event.ACTION_DOWN ){
					performPageDown();
				}
				
				return false;
			}
		});
		
		
		/*
		 * On Click Listeners
		 */
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mBodyText.setCursorVisible(false);
				mBodyText.setEnabled(false);
				Log.i(TAG,"sending setResult OK");
				setResult(RESULT_OK);
			    finish();
		    }  
		});
		
		appendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBodyText.clearFocus();
				mBodyText.append("\n");
				mBodyText.requestFocus();
				mBodyText.setCursorVisible(false);
				// show cursor for 1.88 seconds and disappear.
					
				mBodyText.setSelection(mBodyText.length());
				mBodyText.setCursorVisible(true);
				mBodyText.setCursorVisible(false);
			}

		});

		topButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				mBodyText.requestFocus();
				mBodyText.setCursorVisible(false);
				mBodyText.setSelection(0);
				mBodyText.scrollTo(0, 0);

			}

		});
		
		//mBodyText.getTextSize() and mBodyText.computeScroll()
		// use getTextSize to lockstep the up and down
		upButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				mBodyText.requestFocus();
				mBodyText.setCursorVisible(false);
				int goUp = mBodyText.getScrollY()-mBodyText.getHeight()-20;
				if(goUp < 0){goUp = 0;}
				mBodyText.scrollTo(mBodyText.getScrollX(), goUp );
				mBodyText.computeScroll();
			
			}
		});
		
		/*
		downButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				performPageDown();
			}

		});
		
		/*
		 * 
		 */

		
    }
    
    

    

private void performPageDown(){

	//mBodyText.setSelection(mBodyText.length());
	//mBodyText.append("\nAppend Text\n");
	
	mBodyText.requestFocus();
	mBodyText.setCursorVisible(false);
	//Log.w("Scoll Action", "getScrollX:"+getString(mBodyText.getScrollX()).toString());
	int goDown, bottomLines, limit;
	
	/*   */
	goDown = mBodyText.getScrollY()+mBodyText.getHeight()-mBodyText.getLineHeight();
	bottomLines = mBodyText.getLineCount() - 2;
	if(bottomLines < 0){
		bottomLines = 0;
	}
	limit = mBodyText.getLineBounds(bottomLines, null);;
	
	if(goDown > limit){goDown = limit;}
	//if (goDown < mBodyText.sc)
	mBodyText.scrollTo(mBodyText.getScrollX(), goDown );
	downButton.clearFocus();
	//v.setClickable(true);
	mBodyText.computeScroll();
	//TimeDot td = null;
	//td = new TimeDot();
	/*   */
	
	
	
	/*
	for(int seconds = 0; seconds < 4; seconds++){
		if( !downButton.isPressed() ){ break; }
		td.run();
	}
	if( downButton.isPressed() ){
		performPageDown();
	}
	*/
}	

    
    
    
    /*
     * Menu Button Menu
     */
    
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ADD, 0, R.string.menu_add);
        menu.add(0, MENU_DELETE, 0, R.string.menu_delete);
        menu.add(0, MENU_CONFIGURE, 0, R.string.menu_configure);
        menu.add(0, MENU_SHARE, 0, R.string.menu_share);
        menu.add(0, MENU_LIST, 0, R.string.menu_list);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case MENU_ADD:
            setResult(RESULT_OK);
            finish();
        	Intent i = new Intent(this, Viewer.class);
        	startActivityForResult(i, ACTIVITY_ADD);
            return true;
        case MENU_DELETE:
            mDbHelper.deleteEntry(mRowId);
            setResult(RESULT_OK);
            finish();
            return true;
        case MENU_LIST:
            setResult(RESULT_OK);
            finish();
        	Intent i1 = new Intent(this, Pad.class);
        	startActivityForResult(i1, ACTIVITY_LIST);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i(TAG,"onActivityResult requestCode: " + requestCode);
        super.onActivityResult(requestCode, resultCode, intent);
       
        Bundle extras = intent.getExtras();
        switch(requestCode) {
        case ACTIVITY_ADD:
        	break;
        case ACTIVITY_EDIT:
        	break;
        case ACTIVITY_LIST:
			//setResult(RESULT_OK);
		    //finish();
        	Intent i = new Intent(this, Pad.class);
        	startActivityForResult(i, ACTIVITY_LIST);
            break;
        }
    }
	    
    
    
    
    
    
    
    
    
    
    /*   */
	private void populateFields(){
		Log.i(TAG,"populateFields()");
		if (mRowId != null){
			Cursor row = mDbHelper.getEntry(mRowId);
			startManagingCursor(row);
			mTitleText.setText(row.getString(row.getColumnIndexOrThrow(DbAdapter.COL_TITLE)));
			mBodyText.setText(row.getString(row.getColumnIndexOrThrow(DbAdapter.COL_BODY)));
//			mPathText = row.getString(row.getColumnIndexOrThrow(DbAdapter.COL_PATH));
//			mSavedFlag = row.getInt(row.getColumnIndexOrThrow(DbAdapter.COL_SAVED));
			row.close();
		}
	}

	/*   */
	private void saveState() throws IOException {
		Log.i(TAG,"saveState()");
		/*   */
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		if(mRowId == null){
			long id = mDbHelper.addEntry(title, body);
			if( id > 0 ){
				mRowId = id;
			}
		}else{
			//mDbHelper.updateEntryColumn(mRowId,"title",title);
			//mDbHelper.updateEntryColumn(mRowId,"body",body);
			mDbHelper.updateEntry(mRowId, title, body);
		}
		
		String path = "";

		File sDir = new File("/sdcard/","pad");
		Log.w(TAG,"Creating sDir canRead " + sDir.canRead());
		Log.w(TAG,"Creating sDir canWrite " + sDir.canWrite());
		sDir.mkdir();
		Log.w(TAG,"Creating sDir " + sDir.getAbsolutePath());
		Log.w(TAG,"Created sDir exists " + sDir.exists());
		Log.w(TAG,"Created sDir idDirectory " + sDir.isDirectory());
		
		if( sDir.isDirectory() ){
			path = sDir.getAbsolutePath() + "/";
		}
		Log.i(TAG,"path: " + path);
		
		String filename = title + "-" + mRowId + ".txt";
		Log.i(TAG,filename);
		
		
		
		
		//PrintWriter out = new PrintWriter( new FileWriter(filename,0) );
		//out.print(body);
		//out.close();
		
		
		
//		File outfile = new File(path + filename);		
//		if( !outfile.exists() ) {
//			Log.w(TAG,"createNewFile()");
//			outfile.createNewFile();
//		}
//		if( outfile.canWrite() ){}
		
		
		/**/
		FileOutputStream fileOut = null;
		try {
			fileOut = this.openFileOutput( filename, MODE_WORLD_READABLE );
			Log.i(TAG,"Successfully opened");
		} catch (FileNotFoundException e) {
			Log.e(TAG,e.getMessage() + " failed because " + e.getCause() );
		}
		
		FileChannel file = fileOut.getChannel();
		ByteBuffer bodyB = ByteBuffer.allocate(body.length());
		byte[] bodyChars = body.getBytes();
		bodyB.put(bodyChars);
		
		try {
			file.write( bodyB );
			Log.i(TAG,"file.write() successful ");
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
		}
		
		try {
			file.close();
			Log.i(TAG,"file.close() successful");
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
		}
		
		try {
			fileOut.close();
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
		}
		
		if( path != "" ){
			File f2 = new File(filename);
			f2.renameTo(new File(path,filename));
			Log.w(TAG,"Attempted to move file to sdcard");
			File f3 = new File(path,filename);
			if(f3.isFile()){
				Log.i(TAG,"File Moved Successfully");
			}else{
				Log.e(TAG,"File did not move");
			}
		}
		

		String[] tdir = this.fileList();
		String file3 = null;
		for(int i = 0; i < tdir.length; i++){
			file3 = tdir[i];
			Log.i(TAG,"fileList: " + file3 );
		}
		
		
		
	
//		try {
//			File f = new File(path+filename);
//			PrintWriter out = new PrintWriter(new FileWriter(f));
//			out.print(body);
//			out.close();
//		}
//		catch (IOException e){ Log.e(TAG,"Error writing file "+ path + filename + " because " + e.getCause().getMessage() );}

//		FileOutputStream fileOut = null;
//		try {
//			fileOut = this.openFileOutput(path+filename, MODE_WORLD_WRITEABLE);
//		}catch (IOException e){Log.e(TAG,e.getMessage());}

		
		

  	}	

	/*   */
	@Override
	protected void onResume() {
		super.onResume();
		//mgr.registerListener(mSensorListener, mgr.SENSOR_ORIENTATION);
		populateFields();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(DbAdapter.COL_ROWID, mRowId);
	}

	protected void onPause() {
		super.onPause();
		//mgr.unregisterListener(mSensorListener);
		try {
			saveState();
		} catch (IOException e) {
			Log.e(TAG,e.getLocalizedMessage() +" failed because " + e.getCause().getLocalizedMessage() );
		}
	}
    /*   */	
}
