package com.flyman.app.downloadapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flyman.app.downloadapplication.R;
import com.flyman.app.downloadapplication.bean.FileInfo;
import com.flyman.app.downloadapplication.task.OnStartOrPauseListener;
import com.flyman.app.downloadapplication.util.LogUtils;
import com.flyman.app.downloadapplication.util.UnitConversion;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DownloadingAdapter extends RecyclerView.Adapter implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private List<FileInfo> mFileInfos;
    private HashMap<Integer, Boolean> checkedStatus;
    private OnStartOrPauseListener mOnStartOrPauseListener;
    public static final int PAYLOADS_STATUS_PAUSE = 0;
    public static final int PAYLOADS_STATUS_DOWNLOADING = 1;
    public DownloadingAdapter(Context context, List<FileInfo> fileInfos) {
        this(context, fileInfos, null);
    }

    public DownloadingAdapter(Context context, List<FileInfo> fileInfos, OnStartOrPauseListener onStartOrPauseListener) {
        mContext = context;
        mFileInfos = fileInfos;
        checkedStatus = new HashMap<>();
        this.mOnStartOrPauseListener = onStartOrPauseListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadingViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_downloading_main, parent, false));
    }

    public boolean getCheckBoxStatus(int position) {
        if (checkedStatus.containsKey(position) == false) {
            return false;
        }
        return checkedStatus.get(position);
    }

    private void setCheckBoxStatus(int position, boolean isChecked) {
        checkedStatus.put(position, isChecked);
    }

    private void removeCheckBoxStatus(int position) {
        if (checkedStatus.containsKey(position) == true) {
            checkedStatus.remove(position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()||payloads==null) {
            onBindViewHolder(holder, position);
        }
        else
            {
                FileInfo mFileInfo = mFileInfos.get(position);
                DownloadingViewHolder mViewHolder = (DownloadingViewHolder) holder;
                mViewHolder.tv_downloading_name.setText(mFileInfo.getName());
                mViewHolder.tv_downloading_length.setText(UnitConversion.humanReadableByteCount4File(mFileInfo.getLength()));
                mViewHolder.tv_downloading_finishedLength.setText(UnitConversion.humanReadableByteCount4File(mFileInfo.getFinishedLength()));
                if (mFileInfos.get(position).isFinished() == true) {
                    mViewHolder.tv_downloading_status.setText(mContext.getResources().getText(R.string.download_status_finished));
                } else {
                    for (Object object:payloads)
                    {
                        if(object instanceof Integer && (int)object == PAYLOADS_STATUS_PAUSE)
                        {
                            mViewHolder.tv_downloading_status.setText("暂停");
                        }
                        else {
                            mViewHolder.tv_downloading_status.setText(UnitConversion.humanReadableByteCount4Network(mFileInfo.getInstantaneousLength()));
                        }
                    }

                }
                mViewHolder.pb_downloading.setProgress((int) mFileInfo.getFinishedPercent());
                mViewHolder.cb_start_pause.setTag(position);
                mViewHolder.cb_start_pause.setOnCheckedChangeListener(this);
                mViewHolder.cb_start_pause.setChecked(getCheckBoxStatus(position));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DownloadingViewHolder mViewHolder = (DownloadingViewHolder) holder;
        FileInfo mFileInfo = mFileInfos.get(position);
        mViewHolder.tv_downloading_name.setText(mFileInfo.getName());
        mViewHolder.tv_downloading_length.setText(UnitConversion.humanReadableByteCount4File(mFileInfo.getLength()));
        mViewHolder.tv_downloading_finishedLength.setText(UnitConversion.humanReadableByteCount4File(mFileInfo.getFinishedLength()));
        if (mFileInfos.get(position).isFinished() == true) {
            mViewHolder.tv_downloading_status.setText(mContext.getResources().getText(R.string.download_status_finished));
        } else {
            mViewHolder.tv_downloading_status.setText(UnitConversion.humanReadableByteCount4Network(mFileInfo.getInstantaneousLength()));
        }
        mViewHolder.pb_downloading.setProgress((int) mFileInfo.getFinishedPercent());
        mViewHolder.cb_start_pause.setTag(position);
        mViewHolder.cb_start_pause.setOnCheckedChangeListener(this);
        mViewHolder.cb_start_pause.setChecked(getCheckBoxStatus(position));
    }

    @Override
    public int getItemCount() {
        return mFileInfos == null || mFileInfos.size() == 0 ? 0 : mFileInfos.size();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int position = (int) compoundButton.getTag();
        setCheckBoxStatus(position, isChecked);
        if (mOnStartOrPauseListener != null) {
            if (isChecked == true) {
                //开始下载
                mOnStartOrPauseListener.start(mFileInfos.get(position), position);
            } else {
                //暂停下载
                mOnStartOrPauseListener.pause(mFileInfos.get(position),position);
                LogUtils.a("view holder 返回的数据 "+mFileInfos.get(position));
            }
        }
    }

    class DownloadingViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_downloading)
        ImageView iv_downloading;
        @Bind(R.id.tv_downloading_name)
        TextView tv_downloading_name;
        @Bind(R.id.tv_downloading_length)
        TextView tv_downloading_length;
        @Bind(R.id.pb_downloading)
        ProgressBar pb_downloading;
        @Bind(R.id.tv_downloading_status)
        TextView tv_downloading_status;
        @Bind(R.id.tv_downloading_finishedLength)
        TextView tv_downloading_finishedLength;
        @Bind(R.id.cb_start_pause)
        CheckBox cb_start_pause;

        public DownloadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
