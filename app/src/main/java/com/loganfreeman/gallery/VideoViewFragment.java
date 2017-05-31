package com.loganfreeman.gallery;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.loganfreeman.gallery.utils.PhotoItem;


/**
 * Created by scheng on 5/31/2017.
 */

public class VideoViewFragment extends Fragment {

    public static final java.lang.String VIDEO_ITEM = "VIDEO_ITEM";
    private PhotoItem videoItem;

    private VideoView mVideoView;

    MediaController controller;

    // newInstance constructor for creating fragment with arguments
    public static VideoViewFragment newInstance(int page, PhotoItem item) {
        VideoViewFragment fragmentFirst = new VideoViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(VIDEO_ITEM, item);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoItem = getArguments().getParcelable(VIDEO_ITEM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        mVideoView = (VideoView) rootView.findViewById(R.id.videoView);

        initVideoView();

        attachMediaController();

        return rootView;
    }

    private void initVideoView() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
               // mVideoView.requestFocus();
                //mVideoView.start();
            }
        });
        mVideoView.setVideoURI(videoItem.getUri());
    }

    private void attachMediaController() {
        controller = new MediaController(getContext());
        controller.setAnchorView(this.mVideoView);
        controller.setMediaPlayer(this.mVideoView);
        mVideoView.setMediaController(controller);
    }

    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
        {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                mVideoView.pause();
            }

            if (isVisibleToUser) // If we are becoming visible, then...
            {
                mVideoView.resume();
            }
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}