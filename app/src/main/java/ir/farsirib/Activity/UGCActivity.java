package ir.farsirib.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.farsirib.CustomWidgets.MyTextView;
import ir.farsirib.Interfaces.ResultObject;
import ir.farsirib.Interfaces.UGCVideoInterface;
import ir.farsirib.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UGCActivity extends Main2Activity implements EasyPermissions.PermissionCallbacks, AdapterView.OnItemSelectedListener {


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private String barname="";
    List<String> spinnerArray;
    private Context myContext;
    String itemSelected="";
    private EditText name_text;
    private EditText age_text;
    private EditText mobile_text;
    private EditText city_text;
    private EditText description_text;
    private Button submit_btn;
    private TextInputLayout name_layout;
    private TextInputLayout city_layout;
    private TextInputLayout age_layout;
    private TextInputLayout mobile_layout;

    ArrayList<ImageView> images = new ArrayList<ImageView>();
    final int FROM_GALARY = 1;
    final int FROM_CAPTURE = 2;
    final int FROM_CAMERA = 3;
    final int VIDEO_FROM_GALARY = 4;
    private Uri fileUri;
    private final int CAMERA_REQUEST_CODE = 100;
    private static final int READ_REQUEST_CODE = 200;
    private static final int REQUEST_VIDEO_CAPTURE = 300;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final String TAG = MainActivity.class.getSimpleName();
    Uri uri,gallary_uri;
    String image_base64;
    private Button submit_bt;
    TextView account_tv;
    private String address_image="";
    private ImageView display_image_view;
    private JSONObject new_farsisho_UGC;
    private Spinner spinner;
    MyTextView upload_txt;
    ProgressBar uploaProcess;
    private VideoView displayRecordedVideo;
    private String pathToStoredVideo;
    //private static final String SERVER_PATH = "http://www.mob.shahreraz.com/mob/Farsirib/webservice/videos/";
    private static final String SERVER_PATH = "http://www.shahreraz.com/club/app/videos/";
    private String address_video="";
    private int id_category;
    private int category_id;
    ArrayList<JSONObject> data_list;
    private static final int SELECT_VIDEO = 3 ;
    private String selectedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FrameLayout content_frame= findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.ugc,content_frame);

        String title = String.valueOf(getIntent().getExtras().getString("title"));
        final String caller_activity = String.valueOf(getIntent().getExtras().getString("caller_activity"));

        myContext = getApplicationContext();

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);

            if (view instanceof TextView) {

                TextView tv = (TextView) view;
                tv.setTypeface(myfont);
                tv.setTextSize(18);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable arrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        getSupportActionBar().setHomeAsUpIndicator(arrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawer.closeDrawer(Gravity.RIGHT);

                onBackPressed();

            }
        });

        if (settings.getInt("user_id",0)!=0)
        {

            navigationView = findViewById(R.id.nav_view);
            headerView = navigationView.getHeaderView(0);
            account_tv = headerView.findViewById(R.id.account_txt);
            account_tv.setText(settings.getString("mobile_num",""));

            drawer.closeDrawer(Gravity.RIGHT);
        }
        else {
            error_message_dialog("شبکه فارس!" , "شما وارد حساب کاربری نشده اید!");

            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            drawer.closeDrawer(Gravity.RIGHT);
        }

        name_text = findViewById(R.id.name_text);
        name_layout = findViewById(R.id.name_layout);
        name_text.requestFocus();

        age_text = findViewById(R.id.age_text);
        age_layout = findViewById(R.id.age_layout);

        mobile_text = findViewById(R.id.mobile_text);
        mobile_layout = findViewById(R.id.mobile_layout);
        mobile_text.setText(settings.getString("mobile_num",""));

        city_text = findViewById(R.id.city_text);
        city_layout = findViewById(R.id.city_layout);
        city_text.setText(settings.getString("city",""));

        data_list = new ArrayList<JSONObject>();
        spinnerArray =  new ArrayList<String>();

        switch ( caller_activity )
        {
            case "KoodakActivity":
                toolbar.setTitle(title);
                id_category = 72;
                new OptionTask("کودک").execute();
                break;
            case  "FarsiShoActivity":
                toolbar.setTitle(title);
                age_layout.setVisibility(View.GONE);
                age_text.setVisibility(View.GONE);
                age_text.setText("0");
                id_category = 91;
                new OptionTask("فارسی شو").execute();
                break;
            case  "KhoshaActivity":
                toolbar.setTitle(title);
                age_layout.setVisibility(View.GONE);
                age_text.setVisibility(View.GONE);
                age_text.setText("0");
                id_category = 4;
                new OptionTask("خوشاشیراز").execute();
                break;
            case  "KashaneActivity":
                toolbar.setTitle(title);
                age_layout.setVisibility(View.GONE);
                age_text.setVisibility(View.GONE);
                age_text.setText("0");
                id_category = 3;
                new OptionTask("کاشانه مهر").execute();
                break;
            case  "MainListItemsActivity":
                toolbar.setTitle(title);
                age_layout.setVisibility(View.GONE);
                age_text.setVisibility(View.GONE);
                age_text.setText("0");
                id_category = 110;
                new OptionTask("ugc").execute();
                break;
            case  "ChelcheraghActivity":
                toolbar.setTitle(title);
                age_layout.setVisibility(View.GONE);
                age_text.setVisibility(View.GONE);
                age_text.setText("0");
                id_category = 31;
                new OptionTask("چلچراغ").execute();
                break;
        }



//        spinnerArray.add("گزارشگری شهرستان ها");
//        spinnerArray.add("ساخت کلیپ");
//        spinnerArray.add("ساخت قطعات موسیقی");
//        spinnerArray.add("نویسنده");
//        spinnerArray.add("بازیگر");
//        spinnerArray.add("طراح دکور");
//        spinnerArray.add("شاعر");


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this, android.R.layout.simple_spinner_item, spinnerArray);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner = findViewById(R.id.spinner1);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);


        description_text = findViewById(R.id.description_text);
        display_image_view = findViewById(R.id.display_image_view);
        displayRecordedVideo = findViewById(R.id.video_display);
        upload_txt = findViewById(R.id.upload_txt);
        uploaProcess = findViewById(R.id.upload_progress);

        //permission Access
        allowAccessAlert();

        images.add((ImageView) findViewById(R.id.image1));
        images.add((ImageView) findViewById(R.id.image2));
        images.add((ImageView) findViewById(R.id.image3));
        images.add((ImageView) findViewById(R.id.image4));

        images.get(0).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                show_dialog(FROM_GALARY);
            }
        });

        images.get(1).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                show_dialog(FROM_CAPTURE);
            }
        });

        images.get(2).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                show_dialog(FROM_CAMERA);
            }
        });

        images.get(3).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                show_dialog(VIDEO_FROM_GALARY);
            }
        });


        submit_btn = findViewById(R.id.submit_bt);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean is_validate=true;

                if(name_text.getText().toString().trim().length()>=1)
                {
                    name_layout.setErrorEnabled(false);
                }else
                {
                    name_layout.setError("یک اسم وارد کنید");
                    name_layout.setErrorEnabled(true);

                    is_validate=false;
                    name_text.requestFocus();
                }

//                if(age_text.getText().toString().trim().length()>=1)
//                {
//                    age_layout.setErrorEnabled(false);
//                }
//                else
//                {
//                    age_layout.setError("سن را وارد کنید");
//                    age_layout.setErrorEnabled(true);
//
//                    is_validate=false;
//                    age_text.requestFocus();
//                }

                if(city_text.getText().toString().trim().length()>=1)
                {
                    city_layout.setErrorEnabled(false);
                }
                else
                {
                    city_layout.setError("نام شهر را وارد کنید");
                    city_layout.setErrorEnabled(true);

                    is_validate=false;
                    city_text.requestFocus();
                }

                if(mobile_text.getText().toString().trim().length()>=1)
                {
                    mobile_layout.setErrorEnabled(false);
                }
                else
                {
                    mobile_layout.setError("شماره تلفن را وارد کنید");
                    mobile_layout.setErrorEnabled(true);

                    is_validate=false;
                    mobile_text.requestFocus();
                }

                if (is_validate) {

                    new_farsisho_UGC =new JSONObject();

                    try {

                        new_farsisho_UGC.put("name",name_text.getText().toString().trim());Log.d("name",name_text.getText().toString().trim());
                        new_farsisho_UGC.put("age",age_text.getText().toString().trim()); Log.d("age",age_text.getText().toString().trim());
                        new_farsisho_UGC.put("city",city_text.getText().toString().trim()); Log.d("city",city_text.getText().toString().trim());
                        new_farsisho_UGC.put("mobile",mobile_text.getText().toString().trim());Log.d("mobile",mobile_text.getText().toString().trim());
                        new_farsisho_UGC.put("description",description_text.getText().toString().trim());Log.d("description",description_text.getText().toString().trim());
                        new_farsisho_UGC.put("category",category_id);Log.d("category",String.valueOf(category_id));
                        new_farsisho_UGC.put("image", address_image);Log.d("image", address_image);
                        new_farsisho_UGC.put("video",address_video);Log.d("video",address_video);
                        new_farsisho_UGC.put("id_category",id_category);Log.d("id_category",String.valueOf(id_category));

                        new_farsisho_UGC.put("command","new_ugc");

                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    new send_farsisho_UGC_info().execute();

                }
                else
                    Toast.makeText(getApplicationContext(),"خطا در ورود اطلاعات",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        itemSelected = parent.getItemAtPosition(position).toString();
        try {
            category_id = data_list.get(position).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toast.makeText(myContext,category_id+ ":" + itemSelected,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void allowAccessAlert() {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeRequest();
                    }
                });

                android.app.AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeRequest();
            }
        }

    }

    private boolean hasPermissions(Context context, String[] permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void show_dialog(int type) {
        switch (type) {
            case FROM_GALARY:

                Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(gallery_intent, "لطفا یک عکس را انتخاب کنید"), 2);

                break;
            case FROM_CAPTURE:

                if (ContextCompat.checkSelfPermission(UGCActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakenPictureIntent();
                } else {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                        Toast.makeText(getApplicationContext(), "Permission Needed.", Toast.LENGTH_LONG).show();
                    }
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                    show_dialog(FROM_CAPTURE);
                }
                break;
            case FROM_CAMERA:

                if (ContextCompat.checkSelfPermission(UGCActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent videoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if(videoCaptureIntent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(videoCaptureIntent, REQUEST_VIDEO_CAPTURE);
                    }
                } else {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                        Toast.makeText(getApplicationContext(), "Permission Needed.", Toast.LENGTH_LONG).show();
                    }
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                    show_dialog(FROM_CAMERA);

                }
                break;

            case VIDEO_FROM_GALARY:

                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "لطفا یک ویدیو انتخاب کنید "), SELECT_VIDEO);

                break;
        }
    }

    private void dispatchTakenPictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){

            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {//camera

            CropImage.activity(fileUri).setAspectRatio(1, 1).setRequestedSize(512, 512).start(this);

        } else if (requestCode == 2 && resultCode == RESULT_OK) {//gallery

            gallary_uri = data.getData();
            CropImage.activity(gallary_uri).setAspectRatio(1, 1).setRequestedSize(512, 512).start(this);


        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {//video

            uri = data.getData();
            if (EasyPermissions.hasPermissions(UGCActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                displayRecordedVideo.setVisibility(View.VISIBLE);
                uploaProcess.setVisibility(View.VISIBLE);
                upload_txt.setVisibility(View.VISIBLE);
                displayRecordedVideo.setVideoURI(uri);
                displayRecordedVideo.start();

                pathToStoredVideo = getRealPathFromURIPath(uri, UGCActivity.this);
                Log.d(TAG, "Recorded Video Path " + pathToStoredVideo);
                //Store the video to your server
                uploadVideoToServer(pathToStoredVideo);

            } else {
                EasyPermissions.requestPermissions(UGCActivity.this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri resutlUri = result.getUri();

            display_image_view.setVisibility(View.VISIBLE);
            display_image_view.setImageURI(resutlUri);

            // fill_images[current_image]=true;


            //BitmapDrawable bd=((BitmapDrawable) images.get(current_image).getDrawable());
            BitmapDrawable bd = ((BitmapDrawable) display_image_view.getDrawable());
            Bitmap bm = bd.getBitmap();

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);

            image_base64 = Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT);
            Log.d("upload","000000");
            new upload_image().execute();
        } else if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {

                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);

                uploaProcess.setVisibility(View.VISIBLE);
                upload_txt.setVisibility(View.VISIBLE);
                displayRecordedVideo.setVisibility(View.VISIBLE);
                displayRecordedVideo.setVideoURI(selectedImageUri);
                displayRecordedVideo.start();

                uploadVideoToServer(selectedPath);
                // textView.setText(selectedPath);
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "User has denied requested permission");
    }

    public class upload_image extends AsyncTask<Void,Void,String> {

        ProgressDialog pd=new ProgressDialog(UGCActivity.this);

        protected void onPreExecute(){
            super.onPreExecute();
            pd.setMessage("در حال آپلود تصویر...");
            pd.show();
            Log.d("upload","1111111");
        }

        @Override
        protected String doInBackground(Void... voids) {

            Log.d("doin","222222");
            ArrayList<NameValuePair> namevaluepairs=new ArrayList<NameValuePair>();
            namevaluepairs.add(new BasicNameValuePair("image",image_base64));

            try{
                Log.d("doin","333333");
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost=new HttpPost("http://www.shahreraz.com/club/app/command.php");
                //HttpPost httppost=new HttpPost("http://77.36.166.137/club/app/command.php");
                httppost.setEntity(new UrlEncodedFormEntity(namevaluepairs));

                HttpResponse httpresponse=httpclient.execute(httppost);

                String response= EntityUtils.toString(httpresponse.getEntity());

                if (response.startsWith("<farsirib_app>")&&response.endsWith("</farsirib_app>")) {//response is valid

                    response = response.replace("<farsirib_app>", "").replace("</farsirib_app>", "");

                    if (!response.trim().equals("0")) {//upload ok

                        Log.d("doin","4444444");
                        //address_images[current_image]=response.trim();
                        address_image =response.trim();
                        Log.d("adrimg",address_image);
                    } else
                    {
                        Log.d("doin","5555555");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getBaseContext(),"خطا در آپلود تصویر",Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }
                else
                {//error

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getBaseContext(),"خطا در آپلود تصویر",Toast.LENGTH_SHORT).show();

//                            images.get(current_image).setImageResource(R.drawable.select_image);
//
//                            fill_images[current_image]=false;

                        }
                    });
                }
            }catch (Exception e)
            {

                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getBaseContext(),"خطا در آپلود تصویر",Toast.LENGTH_SHORT).show();

//                        images.get(current_image).setImageResource(R.drawable.select_image);
//
//                        fill_images[current_image]=false;

                    }
                });
            }
            return null;
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
        }
    }

    private class send_farsisho_UGC_info extends AsyncTask<Void,Void,String>
    {

        ProgressDialog pd=new ProgressDialog(UGCActivity.this);

        protected  void onPreExecute()
        {
            super.onPreExecute();

            pd.setMessage("در حال ارسال ...");
            pd.show();

        }

        @Override
        protected String doInBackground(Void... voids) {

            Log.d("send","1111111");


            ArrayList<NameValuePair> namevaluepairs=new ArrayList<NameValuePair>();

            namevaluepairs.add(new BasicNameValuePair("myjson",new_farsisho_UGC.toString()));

            try
            {
                Log.d("send","2222222");
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost=new HttpPost("http://shahreraz.com/club/app/command.php");
//                HttpPost httppost=new HttpPost("http://77.36.166.137/club/app/command.php");
                httppost.setEntity(new UrlEncodedFormEntity(namevaluepairs, HTTP.UTF_8));
                HttpResponse httpresponse=httpclient.execute(httppost);

                String response= EntityUtils.toString(httpresponse.getEntity());

                if (response.startsWith("<farsirib_app>")&&response.endsWith("</farsirib_app>")) {//response is valid

                    Log.d("send","33333333");

                    response = response.replace("<farsirib_app>", "").replace("</farsirib_app>", "");
                    Log.d("send","44444444");

                    if(response.trim().equals("ok"))
                    {
                        Log.d("send","5555555");
                        //final String finalResponse = response;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                             //   int node_id = Integer.parseInt(finalResponse);
                                Log.d("send","666666666666");
                                //empty_field(node_id);
                                empty_field();
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                error_message_dialog("اوه..." , "خطا در ارسال اطلاعات");
                                //Toast.makeText(getBaseContext(),"خطا در ارسال",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            error_message_dialog("اوه..." , "خطا در ارسال اطلاعات");
                            //Toast.makeText(getBaseContext(),"خطا در ارسال",Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }catch(Exception e)
            {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        error_message_dialog("اوه..." , "خطا در ارسال اطلاعات");
                        //Toast.makeText(getBaseContext(),"خطا در ارسال",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            pd.hide();
            pd.dismiss();

        }
    }

    private void empty_field() {

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("شبکه فارس")
                .setContentText( "اطلاعات با موفقیت ارسال شد" )
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        address_image = "";
                        address_video = "";
                        name_text.setText("");
                        age_text.setText("");
                        description_text.setText("");
                        display_image_view.setVisibility(View.GONE);
                        displayRecordedVideo.setVisibility(View.GONE);
                        name_text.requestFocus();
                        finish();
                    }
                })
                .show();




    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for(int i=0;i<number.length();i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }

    private void uploadVideoToServer(String pathToVideoFile){
        File videoFile = new File(pathToVideoFile);
        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part vFile = MultipartBody.Part.createFormData("video", arabicToDecimal(videoFile.getName()), videoBody);
        address_video = "videos/" + videoFile.getName();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UGCVideoInterface vInterface = retrofit.create(UGCVideoInterface.class);
        Call<ResultObject> serverCom = vInterface.uploadVideoToServer(vFile);
        serverCom.enqueue(new Callback<ResultObject>() {
            @Override
            public void onResponse(Call<ResultObject> call, Response<ResultObject> response) {
                ResultObject result = response.body();
                if(!TextUtils.isEmpty(result.getSuccess())){
                    Toast.makeText(UGCActivity.this, "سپاس، " + result.getSuccess(), Toast.LENGTH_LONG).show();
                    uploaProcess.setVisibility(View.GONE);
                    upload_txt.setVisibility(View.GONE);
                    Log.d(TAG, "Result " + result.getSuccess());
                }
            }
            @Override
            public void onFailure(Call<ResultObject> call, Throwable t) {
                Log.d(TAG, "Error message " + t.getMessage());
            }
        });
    }

    private class OptionTask extends AsyncTask<Void, Void, String> {
        public OptionTask(String my_barname) {
            barname = my_barname;
        }

        @Override
        protected String doInBackground(Void... voids) {

            ArrayList<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>();

            JSONObject get_combo_list = new JSONObject();

            try {
                get_combo_list.put("command", "get_ugc_category");
                get_combo_list.put("barname",  barname);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            namevaluepairs.add(new BasicNameValuePair("myjson", get_combo_list.toString()));

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://www.shahreraz.com/club/app/command.php");
                //HttpPost httppost = new HttpPost("http://77.36.166.137/club/app/command.php");
                httppost.setEntity(new UrlEncodedFormEntity(namevaluepairs, HTTP.UTF_8));

                HttpResponse httpresponse = httpclient.execute(httppost);

                String response = EntityUtils.toString(httpresponse.getEntity());

                if (response.startsWith("<farsirib_app>") && response.endsWith("</farsirib_app>")) {//response is valid

                    response = response.replace("<farsirib_app>", "").replace("</farsirib_app>", "");

                    if(!response.trim().equals(""))
                    {
                        final String finalResponse = response;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    JSONArray combo_list = new JSONArray(finalResponse);
                                    fetch_array(combo_list);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "خطا در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getBaseContext(), "خطا در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                FillData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void FillData() throws JSONException {

        for (int i = 0; i < data_list.size(); i++) {
         //  spinnerArray.add(data_list.get(i).getInt("id"),data_list.get(i).getString("title"));
            spinnerArray.add(data_list.get(i).getString("title"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.spinner1);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void fetch_array(JSONArray combo_list) {
        try {

            for (int i = 0; i < combo_list.length(); i++) {
                data_list.add(combo_list.getJSONObject(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
