package com.pad;

import java.io.IOException;
import java.net.UnknownHostException;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Pad extends ListActivity {


	private static final String TAG = "Pad";
	
    private static final int ACTIVITY_ADD=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_LIST=2;
    

	// Menu Button Menu
	private static final int MENU_ADD = Menu.FIRST;	
	private static final int MENU_DELETE = Menu.FIRST+1;	
	private static final int MENU_CONFIGURE = Menu.FIRST+2;
	private static final int MENU_SHARE = Menu.FIRST+3;
	private static final int MENU_EDIT = Menu.FIRST+4;
	
	// Long touch Menu

	
	private RelativeLayout mListrow;
	private ListActivity mList;
	private ScrollView mListOptions;
	private Button mCreatenew; 
	
	private DbAdapter mDbHelper;
	private Uplink mUplink;
	//private ConnectivityManager mCMX;
	//private InetAddress mHost;	
	private WebView mHttpConsole;
	
	final Handler mHandler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
        setContentView(R.layout.list);
        
        //Toast.makeText(this, "Check out a howdy", 5500);
        
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        loadList();
        registerForContextMenu(getListView());
        //mUplink = new Uplink(this);
        //mList = getListView();
        mListrow = (RelativeLayout) findViewById(R.id.listrow2);
        mListOptions = (ScrollView) findViewById(R.id.optionlist);
        //mHttpConsole = (WebView) findViewById(R.id.httpconsole);
        //mHttpConsole.loadUrl("file:///android_asset/uplink.html");
        //mHttpConsole.loadUrl("file:///android_asset/uplink.html");
        //startLongRunningOperation();
        //setWallpaperDimension();
    }
    
    private void setWallpaperDimension() {
        Display display = getWindowManager().getDefaultDisplay();
        boolean isPortrait = display.getWidth() < display.getHeight();
        final int width = isPortrait ? display.getWidth() : display.getHeight();
        final int height = isPortrait ? display.getHeight() : display.getWidth();

    }

/**//*
	public boolean onTrackballEvent(MotionEvent event) {
		if( event.getAction() == event.ACTION_DOWN ){
			if ( mList.getSelectedItemPosition() < 1 ){
				//mListOptions.setBackgroundColor(Color.rgb(200, 200, 160));
				//mListOptions.setFocusable(true);
				mListOptions.requestFocus();
				mCreatenew.setBackgroundColor(Color.rgb(200, 200, 100));
				mCreatenew.requestFocus();
			}
		}
		return super.onTrackballEvent(event);
	}
	/**/
    
    protected void startLongRunningOperation() {

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
                try {
        			mUplink.SocketTest();
        		} catch (UnknownHostException e) {
        			Log.e(TAG,e.getLocalizedMessage() + " because " + e.getCause().getLocalizedMessage());
        		} catch (IOException e) {
        			Log.e(TAG,e.getLocalizedMessage() + " because " + e.getCause().getLocalizedMessage());
        		}                
                mHandler.post(mUpdateResults);
            }
        };
        t.start();
    }

    private void updateResultsInUi() {
    	
    }


    private void loadList() {
        // Get all of the rows from the database and create the item list
        Cursor lCursor = mDbHelper.getAllEntries();
        startManagingCursor(lCursor);

        String[] from = new String[]{DbAdapter.COL_TITLE, DbAdapter.COL_BODY};
        int[] to = new int[]{R.id.listrowTitle, R.id.listrowDescription};

        SimpleCursorAdapter entries = new SimpleCursorAdapter(this, R.layout.listrowdetail, lCursor, from, to);
        setListAdapter(entries);
        getListView().setTextFilterEnabled(true);
        

        //String[] fromOL = new String[]{"Create New Entry", "Start a new document.  Document is automatically presaved and regularly saved to preserve your information.  Entries are RSA encrypted within the range of 1024 and 2048 bit encryption modulation.  Auto-save every type your press enter."};
        //int[] toOL = new int[]{R.id.listrowTitle, R.id.listrowDescription};
        
        //SimpleAdapter entriesOL = new SimpleAdapter(this, R.layout.listrowdetail, lCursor, fromOL, toOL);
        //setListAdapter(entriesOL);
        
        //setListAdapter(new ArrayAdapter<String>(mListOptions.getContext(), R.layout.listrowdetail, fromOL));
        //getListView().setTextFilterEnabled(true);


    }
    
    
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ADD, 0, R.string.menu_add);
        //menu.add(0, MENU_DELETE, 0, R.string.menu_delete);
        menu.add(0, MENU_CONFIGURE, 0, R.string.menu_configure);
        menu.add(0, MENU_SHARE, 0, R.string.menu_share);
        //menu.add(0, MENU_EDIT, 0, R.string.menu_edit);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case MENU_ADD:
            addEntry();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
        menu.add(0, MENU_ADD, 0, R.string.menu_add);
        menu.add(0, MENU_DELETE, 0, R.string.menu_delete);
        menu.add(0, MENU_CONFIGURE, 0, R.string.menu_configure);
        menu.add(0, MENU_SHARE, 0, R.string.menu_share);
        menu.add(0, MENU_EDIT, 0, R.string.menu_edit);
	}

	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case MENU_ADD:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	        Intent i = new Intent(this, Viewer.class);
	        startActivityForResult(i, ACTIVITY_ADD);
	        return true;
		case MENU_DELETE:
			AdapterContextMenuInfo info2 = (AdapterContextMenuInfo) item.getMenuInfo();
	        mDbHelper.deleteEntry(info2.id);
	        loadList();
	        return true;
		case MENU_EDIT:
			AdapterContextMenuInfo info3 = (AdapterContextMenuInfo) item.getMenuInfo();
	        Intent i2 = new Intent(this, Viewer.class);
	        i2.putExtra(DbAdapter.COL_ROWID, info3.id);
	        startActivityForResult(i2, ACTIVITY_EDIT);
	        return true;
		}

		return super.onContextItemSelected(item);
		
	}



    private void addEntry(){
    	Intent i = new Intent(this, Viewer.class);
    	startActivityForResult(i, ACTIVITY_ADD);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, Viewer.class);
        i.putExtra(DbAdapter.COL_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        loadList();
    }

}
