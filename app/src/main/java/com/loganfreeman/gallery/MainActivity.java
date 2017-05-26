package com.loganfreeman.gallery;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.loganfreeman.gallery.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.loganfreeman.gallery.R.id.nav_about;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final int TAKE_PICTURE = 0;
    private static final int SELECT_PHOTO = 1;

    private Uri imageUri;



    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.filter_button)
    FloatingActionButton filterButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Permissions need to be granted at runtime on Marshmallow
        if (Build.VERSION.SDK_INT >= 21) {
            CheckPermissions();
        }


        setSupportActionBar(mToolbar);


        initDrawer();

    }

    @OnClick(R.id.filter_button)
    void onFilterButtonClick(View v) {
        startActivity(new Intent(MainActivity.this, UserSettingActivity.class));
    }

    private void initDrawer() {
        if (mNavView != null) {
            mNavView.setNavigationItemSelectedListener(this);
            //navigationView.setItemIconTintList(null);
            mNavView.inflateHeaderView(R.layout.nav_header_main);
            ActionBarDrawerToggle toggle =
                    new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
                            R.string.navigation_drawer_close) {

                        /** Called when a drawer has settled in a completely closed state. */
                        public void onDrawerClosed(View view) {
                            super.onDrawerClosed(view);
                            //getActionBar().setTitle(mTitle);
                            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                        }

                        /** Called when a drawer has settled in a completely open state. */
                        public void onDrawerOpened(View drawerView) {
                            super.onDrawerOpened(drawerView);
                            //getActionBar().setTitle(mDrawerTitle);
                            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                        }
                    };;
            mDrawerLayout.setDrawerListener(toggle);
            toggle.syncState();
        }
    }

    private void search(final String query) {
        ImageSearchClient.searchAsync(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ImageModel>>() {
                    @Override
                    public void onCompleted() {
                        Log.i("Image Search: ", "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("IO: ", e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<ImageModel> imageModels) {
                        ImageListActivity.start(MainActivity.this, imageModels, query);
                    }
                });
    }


    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


                @Override
                public boolean onQueryTextSubmit(String query) {
                    search(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        }

        return true;

    }



    //take photo via camera intent
    public void takePhoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String name = String.valueOf(System.currentTimeMillis() + ".jpg");
        File photo = new File(String.valueOf(Environment.getExternalStorageDirectory()), name);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    public void CheckPermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thanks!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //If user denies Storage Permission, explain why permission is needed and prompt again.
                    Toast.makeText(this, "Storage access is needed to display images.",
                            Toast.LENGTH_SHORT).show();
                    CheckPermissions();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    imageUri = data.getData();
                }
                break;
        }
        if (imageUri != null) {
            Bitmap bitmap = null;
            try {
                Log.d("ImageURI", String.valueOf(imageUri));
                bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static int getOrientation(ContentResolver cr, int id) {

        String photoID = String.valueOf(id);

        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Images.Media.ORIENTATION}, MediaStore.Images.Media._ID + "=?",
                new String[] {"" + photoID}, null);
        int orientation = -1;

        if (cursor.getCount() != 1) {
            return -1;
        }

        if (cursor.moveToFirst())
        {
            orientation = cursor.getInt(0);
        }
        cursor.close();
        return orientation;
    }

    private Drawable getRotateDrawable(final Bitmap b, final float angle) {
        final BitmapDrawable drawable = new BitmapDrawable(getResources(), b) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, b.getWidth() / 2, b.getHeight() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
        return drawable;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch ((item.getItemId())) {
            case R.id.nav_about:
                ToastUtil.showShort("You clicked about");
                break;
            case R.id.downloads:
                startActivity(new Intent(this, ImageSelectActivity.class));
                break;
            case R.id.nav_set:
                ToastUtil.showShort("You clicked setting");
                break;
        }
        return false;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view

        return super.onPrepareOptionsMenu(menu);
    }
}
