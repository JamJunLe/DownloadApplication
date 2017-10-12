package com.flyman.app.downloadapplication.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.flyman.app.downloadapplication.R;
import com.flyman.app.downloadapplication.adapter.DownloadingAdapter;
import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.service.DownloadService;
import com.flyman.app.downloadapplication.task.FileInfoTask;
import com.flyman.app.downloadapplication.task.OnStartOrPauseListener;
import com.flyman.app.downloadapplication.task.TaskCallback;
import com.flyman.app.downloadapplication.util.IntentUtil;
import com.flyman.app.downloadapplication.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.flyman.app.downloadapplication.adapter.DownloadingAdapter.PAYLOADS_STATUS_DOWNLOADING;
import static com.flyman.app.downloadapplication.adapter.DownloadingAdapter.PAYLOADS_STATUS_PAUSE;
import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_INSERT;
import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_QUERY_ALL;
import static com.flyman.app.downloadapplication.db.dao.IDao.FLAG_QUERY_DOWNLOADING;
import static com.flyman.app.downloadapplication.util.IntentUtil.FILE_INFO;
import static com.flyman.app.downloadapplication.util.IntentUtil.FILE_INFO_POSITION;

public class MainActivity extends AppCompatActivity implements TaskCallback<List<FileInfo>>, OnStartOrPauseListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.rv_downloading)
    RecyclerView rv_downloading;
    private DownloadingAdapter mDownloadingAdapter;
    private List<FileInfo> mDownloadingList = new ArrayList<>();
    private boolean isPause = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        intiTestData();
        getDownloadingFileInfoList();
        registerReceiver(updateBroadcastReceiver, new IntentFilter(IntentUtil.ACTION_UPDATE));
    }

    private void initView() {
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        rv_downloading.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mDownloadingAdapter = new DownloadingAdapter(this, mDownloadingList, this);
        rv_downloading.setAdapter(mDownloadingAdapter);
    }
    private void intiTestData() {
        FileInfo mFileInfo = new FileInfo();
        mFileInfo.setFileInfoId("https://d.ruanmei.com/app/ithome/ithome_android_5.1.apk");
        mFileInfo.setUrl("https://d.ruanmei.com/app/ithome/ithome_android_5.1.apk");
        mFileInfo.setName("ithome_android_5.1.apk");
        mFileInfo.setFinished(false);
        FileInfoTask insertTask = new FileInfoTask(FLAG_INSERT, this);
        insertTask.execute(mFileInfo);
    }

    /**
     * get downloading fileInfo list
     *
     * @param
     * @return nothing
     */
    private void getDownloadingFileInfoList() {
        //from db query
        new FileInfoTask(FLAG_QUERY_DOWNLOADING, this).execute(new FileInfo());
    }


    @Override
    public void onTaskPre(int flag) {
    }

    @Override
    public void onUpdateTask(int flag, List<FileInfo> fileInfoList) {
    }

    @Override
    public void onTaskSuccess(int flag, List<FileInfo> fileInfoList) {
        updateDownloadingList(flag, fileInfoList);//更新列表
    }

    @Override
    public void onTaskFail(int flag) {
        LogUtils.e("onTaskFail int flag = " +flag);
    }

    private void updateDownloadingList(int flags, List<FileInfo> fileInfoList) {
        switch (flags) {
            //下载中
            case FLAG_QUERY_ALL: {
                mDownloadingList.clear();
                mDownloadingList.addAll(fileInfoList);
                mDownloadingAdapter.notifyDataSetChanged();
                break;
            }
            case FLAG_QUERY_DOWNLOADING: {
                mDownloadingList.clear();
                mDownloadingList.addAll(fileInfoList);
                mDownloadingAdapter.notifyDataSetChanged();
                break;
            }
            default: {

            }
        }
    }

    private void updateDownloadingListByPosition(int flags, FileInfo fileInfo, int position) {
        switch (flags) {
            //更新UI
            case IntentUtil.FLAG_UPDATE_UI: {
                FileInfo mFileInfo = mDownloadingList.get(position);
                mFileInfo.setFinished(fileInfo.isFinished());
                mFileInfo.setFinishedPercent(fileInfo.getFinishedPercent());
                mFileInfo.setFinishedLength(fileInfo.getFinishedLength());
                mFileInfo.setInstantaneousLength(fileInfo.getInstantaneousLength());
                mFileInfo.setLength(fileInfo.getLength());
                mFileInfo.setPartial(fileInfo.isPartial());
                mFileInfo.setThreadInfos(mFileInfo.getThreadInfos());
                mFileInfo.setSdPath(mFileInfo.getSdPath());
                mFileInfo.setThreadInfos(mFileInfo.getThreadInfos());
                if(isPause == true)
                {
                    mDownloadingAdapter.notifyItemChanged(position, PAYLOADS_STATUS_PAUSE);
                }
                else {
                    mDownloadingAdapter.notifyItemChanged(position, PAYLOADS_STATUS_DOWNLOADING);
                }
                mDownloadingList.set(position,mFileInfo);
                break;
            }
            default: {

            }
        }
    }
    private BroadcastReceiver updateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IntentUtil.ACTION_UPDATE)) {
                int position = intent.getExtras().getInt(IntentUtil.FILE_INFO_POSITION);
                Bundle mBundle = intent.getExtras();
                FileInfo fileInfo = (FileInfo) mBundle.getSerializable(IntentUtil.FILE_INFO);
                updateDownloadingListByPosition(IntentUtil.FLAG_UPDATE_UI, fileInfo, position);
                LogUtils.e("BroadcastReceiver", "广播接受UI更新 position = "+position+"  FinishedPercent="+fileInfo.toString());
            }

        }
    };

    @Override
    public void start(FileInfo fileInfo, int position) {
        Intent mIntent = new Intent(this, DownloadService.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(FILE_INFO, mDownloadingList.get(position));
        LogUtils.i(mDownloadingList.get(position).toString());
        mBundle.putInt(FILE_INFO_POSITION, position);
        mIntent.putExtras(mBundle);
        mIntent.setFlags(IntentUtil.FLAG_START);//告诉service开始下载任务
        isPause = false;
        startService(mIntent);
    }

    @Override
    public void pause(FileInfo fileInfo,int position) {
        Intent mIntent = new Intent(this, DownloadService.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(FILE_INFO, mDownloadingList.get(position));
        mIntent.putExtras(mBundle);
        mIntent.setFlags(IntentUtil.FLAG_PAUSE);//告诉service这是开始暂停任务
        isPause = true;
        startService(mIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateBroadcastReceiver);
    }


}
