package com.loganfreeman.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.loganfreeman.gallery.utils.PhotoItem;

import java.io.File;


/**
 * Created by scheng on 5/31/2017.
 */

public class VideoViewFragment extends Fragment {

    public static final java.lang.String VIDEO_ITEM = "VIDEO_ITEM";
    private PhotoItem videoItem;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        VideoView videoView = (VideoView) rootView.findViewById(R.id.videoView);

        videoView.setVideoURI(videoItem.getUri());

        MediaController mediaController = new
                MediaController(this.getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();

        return rootView;
    }
}