package com.mbrite.patrol.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.PointGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;


public class ImageUploadActivity extends ParentActivity {

    private static File picDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private ImageView image;
    private Bitmap bitmap;
    private TextView returnBtn;
    private File imageInPicDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        setWindowTitle(R.string.take_photo);

        image = (ImageView) findViewById(R.id.image);

        setupTakePhotoBtn();
        setupSelectPhotoBtn();
        setupReturnBtn();
        setupCompleteBtn();
        setupRemoveBtn();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (bitmap == null && Tracker.INSTANCE.targetPoint.getImage() != null) {
                bitmap = Utils.decodeFile(getImageFileFullPath());
                image.setImageBitmap(bitmap);
            }
        } catch (Exception ex) {
            Utils.showErrorPopupWindow(this, ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.points, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.logout:
                Utils.logout(this);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            bitmap = null;
            if (requestCode == 0) {
                try {
                    addPicToGallery();
                    FileMgr.copy(this,
                            imageInPicDir,
                            new File(getImageFileFullPath()));
                    bitmap = Utils.decodeFile(getImageFileFullPath());
                    image.setImageBitmap(bitmap);
                } catch (Exception ex) {
                    Utils.showErrorPopupWindow(this, ex);
                }
            } else if (requestCode == 1) {
                try {
                    Uri selectedImageUri = data.getData();
                    // IO FILE Manager
                    String fileManagerString = selectedImageUri.getPath();

                    // MEDIA GALLERY
                    String selectedImagePath = getPath(selectedImageUri);

                    String filePath = null;
                    if (selectedImagePath != null) {
                        filePath = selectedImagePath;
                    } else if (fileManagerString != null) {
                        filePath = fileManagerString;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Unknown filePath",
                                Toast.LENGTH_LONG).show();
                        Log.e("Bitmap", "Unknown filePath");
                    }

                    if (filePath != null) {
                        bitmap = Utils.decodeFile(filePath);
                        image.setImageBitmap(bitmap);
                    } else {
                        bitmap = null;
                    }
                } catch (Exception e) {
                    Utils.showErrorPopupWindow(this, e);
                }
            }
        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return null;
    }

    private void setupTakePhotoBtn() {
        TextView button = (TextView) findViewById(R.id.take_photo_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isDeviceSupportCamera(ImageUploadActivity.this)) {
                    new AlertDialog.Builder(ImageUploadActivity.this,
                            R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                            .setMessage(R.string.error_no_camera_support)
                            .setTitle(R.string.error)
                            .setCancelable(false)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).setIcon(R.drawable.error).show();
                    return;
                }

                // http://developer.android.com/training/camera/photobasics.html
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String imageFileName = UUID.randomUUID().toString();
                    imageInPicDir = File.createTempFile(
                            imageFileName,  /* prefix */
                            Constants.IMAGE_FILE_SUFFIX,         /* suffix */
                            picDir      /* directory */
                    );

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageInPicDir));
                    Tracker.INSTANCE.targetPoint.setImage(
                            imageFileName + Constants.IMAGE_FILE_SUFFIX);
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Utils.showErrorPopupWindow(ImageUploadActivity.this, e);
                }
            }
        });
    }

    private void addPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imageInPicDir.getAbsolutePath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setupSelectPhotoBtn() {
        TextView button = (TextView) findViewById(R.id.select_photo_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_photo)),
                        1);
            }
        });
    }

    private void setupRemoveBtn() {
        TextView button = (TextView) findViewById(R.id.remove_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PointGroup point = Tracker.INSTANCE.targetPoint;
                try {
                    if (point.getImage() != null) {
                        FileMgr.delete(ImageUploadActivity.this, point.getImage());
                        RecordProvider.INSTANCE.removePointRecordImage(
                                ImageUploadActivity.this, point.id);
                        point.setImage(null);
                    }
                    bitmap = null;
                    image.setImageBitmap(null);
                    Toast.makeText(
                            ImageUploadActivity.this,
                            R.string.delete_image_success,
                            Toast.LENGTH_LONG)
                            .show();
                } catch (Exception ex) {
                    Utils.showErrorPopupWindow(ImageUploadActivity.this, ex);
                }
            }
        });
    }

    private void setupReturnBtn() {
        returnBtn = (TextView) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageUploadActivity.this, PointsActivity.class);
                startActivity(intent);
                ImageUploadActivity.this.finish();
            }
        });
    }

    private void setupCompleteBtn() {
        TextView button = (TextView) findViewById(R.id.complete_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap == null) {
                    new AlertDialog.Builder(ImageUploadActivity.this,
                            R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                            .setMessage(R.string.no_image_selected)
                            .setTitle(R.string.error)
                            .setCancelable(false)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).setIcon(R.drawable.error).show();
                    return;
                }

                try {
                    String imageFileName = UUID.randomUUID() + Constants.IMAGE_FILE_SUFFIX;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    FileMgr.write(
                            ImageUploadActivity.this,
                            imageFileName,
                            stream.toByteArray());
                    PointGroup point = Tracker.INSTANCE.targetPoint;
                    if (point.getImage() != null) {
                        FileMgr.delete(ImageUploadActivity.this, point.getImage());
                    }
                    point.setImage(imageFileName);

                    Toast.makeText(
                            ImageUploadActivity.this,
                            R.string.save_image_success,
                            Toast.LENGTH_LONG)
                            .show();
                    returnBtn.performClick();
                } catch (Exception ex) {
                    Utils.showErrorPopupWindow(ImageUploadActivity.this, ex);
                }
            }
        });
    }

    private String getImageFileFullPath() {
        return FileMgr.getFullPath(this, Tracker.INSTANCE.targetPoint.getImage());
    }

}
