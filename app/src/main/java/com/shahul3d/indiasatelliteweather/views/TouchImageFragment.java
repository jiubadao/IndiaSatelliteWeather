package com.shahul3d.indiasatelliteweather.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.noveogroup.android.log.Log;
import com.shahul3d.indiasatelliteweather.R;
import com.shahul3d.indiasatelliteweather.data.AppConstants;
import com.shahul3d.indiasatelliteweather.events.DownloadCompletedEvent;
import com.shahul3d.indiasatelliteweather.service.DownloaderService_;
import com.shahul3d.indiasatelliteweather.utils.StorageUtils;
import com.squareup.okhttp.OkHttpClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import de.greenrobot.event.EventBus;

@EFragment(R.layout.touch_image_fragment)
public class TouchImageFragment extends Fragment {
    @Bean
    AppConstants appConstants;

    @FragmentArg
    int pageNumber;

    @Bean
    StorageUtils storageUtils;

    @ViewById
    SubsamplingScaleImageView touchImage;

    EventBus bus = EventBus.getDefault();
    OkHttpClient httpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @AfterViews
    void calledAfterViewInjection() {
//        touchImage.setImageAsset(chooseImage(pageNumber));
        renderImage();
    }

    @UiThread
    void renderImage() {
        //TODO: Check file exits before render.
        touchImage.setImageFile(storageUtils.getExternalStoragePath() + File.separator + appConstants.getMapType(pageNumber) + ".jpg");
        Log.d("Map refreshed");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //TODO: to be refactored.
            Log.d("Refresh clicked:-> with page number:" + pageNumber);
            Intent downloaderIntent = new Intent(getActivity().getApplicationContext(), DownloaderService_.class);
            downloaderIntent.putExtra(appConstants.DOWNLOAD_INTENT_NAME, pageNumber);
            getActivity().getApplicationContext().startService(downloaderIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }*/
    public void onEvent(DownloadCompletedEvent downloadCompleted) {
        int mapID = downloadCompleted.mapID;
        if (pageNumber == mapID) {
            renderImage();
        }
    }

    public void onEvent(Object e) {
    }
}
