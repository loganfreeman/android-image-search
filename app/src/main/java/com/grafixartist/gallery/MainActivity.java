package com.grafixartist.gallery;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.grafixartist.gallery.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private static final int TAKE_PICTURE = 0;
    private static final int SELECT_PHOTO = 1;

    private Uri imageUri;

    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Permissions need to be granted at runtime on Marshmallow
        if (Build.VERSION.SDK_INT >= 21) {
            CheckPermissions();
        }




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            if(imageView != null) {
                imageView.setImageBitmap(bitmap);
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

}
