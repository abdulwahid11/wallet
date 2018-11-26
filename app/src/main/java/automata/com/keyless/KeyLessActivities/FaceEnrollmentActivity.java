package automata.com.keyless.KeyLessActivities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.isityou.sdk.IsItYouConstants;
import com.isityou.sdk.IsItYouSdk;
import com.isityou.sdk.interfaces.CameraListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import automata.com.keyless.FaceDetectionUtils.CameraPreview;
import automata.com.keyless.R;

public class FaceEnrollmentActivity extends AppCompatActivity implements CameraListener {

    InitOperation iiyInitializationThread;
    TextView tv_res,errorTxt;
    ImageView inst,pinInst;
    PinView pinView;
    Button bt_s,bt_done;// bt_pico, bt_nani, bt_cap, bt_macho, bt_reset;
    ImageView im_pic;
    FrameLayout fl_pic,hole;
    LinearLayout layout;
    Camera camera;
    CameraPreview cameraPreview;
    Switch sw_learning, sw_setas;
    String pin;
    SharedPreferences myPrefs;
    public static final String FILE_NAME="keyLessPrefs";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 123;


    private String[] permissions = {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission
            .ACCESS_NETWORK_STATE, Manifest.permission.INTERNET,Manifest.permission.CAMERA};


    private byte[] image;
    private Bitmap bitmap;
    private Uri imUri;
    private String user;
    private IsItYouSdk iiy;
    boolean pinFlag=true;

    int counter=1;
    private String username;

    SharedPreferences sp;
    SharedPreferences.Editor spEdit;
    private boolean vladimirLernen, vladimirAS;

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap ResizeBitmap(Bitmap source, int width, int height) {
        return Bitmap.createScaledBitmap(source, width, height, true);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_enrollment);








        sp = getSharedPreferences("iiyDemo", 0);
        spEdit = sp.edit();
        pinView=(PinView)findViewById(R.id.firstPinVi);
        pinView.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_drawable_white));
        hole=(FrameLayout)findViewById(R.id.hole);
        inst=(ImageView)findViewById(R.id.instruction);
        layout=(LinearLayout)findViewById(R.id.pinLayout);

        pinInst=(ImageView)findViewById(R.id.instructionpin);
        iiy = IsItYouSdk.getInstance(getApplicationContext());
        errorTxt=(TextView)findViewById(R.id.errorText);


        tv_res = findViewById(R.id.tv_res);
        bt_s = findViewById(R.id.bt_s);
        bt_done=(Button)findViewById(R.id.bt_d);
        im_pic = findViewById(R.id.im_pic);
        fl_pic = findViewById(R.id.fl_pic);
        sw_learning = findViewById(R.id.sw_learning);
        sw_setas = findViewById(R.id.sw_setas);
        myPrefs=getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        MediaActionSound sound = new MediaActionSound();


        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission
        } else {
            if (checkPermission()) {
                //If you have already permitted the permission
                exectueAdterPermissionsGranted();
            }
        }
    }
    public void exectueAdterPermissionsGranted(){


        sw_learning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iiy.setLearning(isChecked);
            }
        });

        sw_setas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iiy.setAS(isChecked);
            }
        });


        bt_s.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(final View v) {

            //    sound.play(MediaActionSound.SHUTTER_CLICK);

                int res=counter%3;
                counter++;


                takePic();
                enrollPic();
                if(counter==4){

                    hole.setBackground(getDrawable(R.drawable.combined_shape_full));
                    inst.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    //inst.setImageDrawable(getDrawable(R.drawable.resting_pin));
                    //  pinView.setVisibility(View.VISIBLE);
                    //layout.setVisibility(View.VISIBLE);
                    //pinInst.setVisibility(View.VISIBLE);
                    pinView.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(pinView, InputMethodManager.SHOW_IMPLICIT);
                    bt_s.setVisibility(View.GONE);
                }
                else {

                    if (res == 0) {
                        inst.setImageDrawable(getDrawable(R.drawable.resting_ahead));
                    } else if (res == 1) {

                        inst.setImageDrawable(getDrawable(R.drawable.resting_right));
                    } else {

                        inst.setImageDrawable(getDrawable(R.drawable.resting));

                    }
                }

            }
        });


        initIIYSDK();
        iiy.resetAppUser("Guest");
        tv_res.setText("Init user Guest Enrolls: 0");
        bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(FaceEnrollmentActivity.this, "i'm clicked", Toast.LENGTH_SHORT).show();
                Toast.makeText(FaceEnrollmentActivity.this, "done", Toast.LENGTH_LONG).show();

            }

        });
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length()==0){
                    pinView.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_drawable_white));
                    errorTxt.setVisibility(View.GONE);

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pinView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {



                    if(pinView.getText().toString().length()!=6){
                        pinView.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_drawable));
                        errorTxt.setVisibility(View.VISIBLE);
                        errorTxt.setText("Please Enter a 6 Digit Pin");
                        pinView.getText().clear();
                        return true;
                    }


                    if(pinFlag==true)
                    {

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(pinView.getWindowToken(), 0);

                        InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm1.showSoftInput(pinView, InputMethodManager.SHOW_IMPLICIT);

                        pinInst.setImageDrawable(getDrawable(R.drawable.resting_confirm));


                        pin=pinView.getText().toString();
                        pinView.getText().clear();

                        pinView.clearFocus();
                        pinView.requestFocus();


                        pinFlag=false;
                        return true;

                    }
                    else{
                        if(pinView.getText().toString().length()!=6){
                            pinView.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_drawable));
                            errorTxt.setVisibility(View.VISIBLE);
                            errorTxt.setText("Please Enter a 6 Digit Pin");
                            pinView.getText().clear();
                            return true;
                        }
                        if(pin.equals(pinView.getText().toString())){

                            Toast.makeText(FaceEnrollmentActivity.this, "pin matches", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor=myPrefs.edit();
                            editor.putString("pin",pin);
                            editor.apply();

                           startActivity(new Intent(FaceEnrollmentActivity.this,FaceMatchActivity.class));
                        }
                        else{
                            //displayErrorMsg


                            pinView.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_drawable));
                            errorTxt.setVisibility(View.VISIBLE);
                            errorTxt.setText("Pin Not Matching , Please Try Again");
                            pinView.getText().clear();



                        }

                        return true;
                        //Toast.makeText(FaceEnrollmentActivity.this, pinView.getText().toString()+"", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });





    }
    public boolean checkPermission(){
        int perm1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);


        int perm2 = ContextCompat.checkSelfPermission(this,


                Manifest.permission.READ_EXTERNAL_STORAGE);

        int perm3 = ContextCompat.checkSelfPermission(this,


                Manifest.permission.READ_PHONE_STATE);
        int perm4 = ContextCompat.checkSelfPermission(this,


                Manifest.permission.ACCESS_NETWORK_STATE);

        int perm5 = ContextCompat.checkSelfPermission(this,


                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int perm6 = ContextCompat.checkSelfPermission(this,


                Manifest.permission.USE_FINGERPRINT);

        int perm7 = ContextCompat.checkSelfPermission(this,


                Manifest.permission.INTERNET);


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (perm1!= PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (perm2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (perm3 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (perm4 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }

        if (perm5 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (perm6 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.USE_FINGERPRINT);
        }

        if (perm7 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,


                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }

        return true;


    }
    @Override    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED&&
                grantResults[1] == PackageManager.PERMISSION_GRANTED&&
                grantResults[2] == PackageManager.PERMISSION_GRANTED&&
                grantResults[3] == PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
                    exectueAdterPermissionsGranted();
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(this, "Please give permissions to proceed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //do whatever you need for the hardware 'back' button


            if(!pinFlag)
            {


                pinView.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_drawable_white));
                errorTxt.setVisibility(View.GONE);


                pinInst.setImageDrawable(getResources().getDrawable(R.drawable.resting_pin));
                //layout.setVisibility(View.VISIBLE);
                //pinInst.setVisibility(View.VISIBLE);
                pinView.clearFocus();
                pinView.requestFocus();
                pinView.setText("");
                pinFlag=true;

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinView.getWindowToken(), 0);


                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(pinView, InputMethodManager.SHOW_IMPLICIT);
            }
            else{
                finish();
            }

            return true;
        }
        //if you want to pass a result to activity A
        return super.onKeyDown(keyCode, event);
    }

    private void openCameraView() {
        Log.d("ALON", "Opening camera view");
        cameraPreview = new CameraPreview(this, fl_pic,this,true);
        Log.d("ALON", "initialized");
        fl_pic.addView(cameraPreview);
        Log.d("ALON", "Added");
    }

    public void takePic() {
        Log.d("ALON", "Taking frame");

        if(cameraPreview.getCurrentFrame()==null||cameraPreview.getCurrentFrame().length==0){
            return ;
            //Toast.makeText(this, "its null", Toast.LENGTH_SHORT).show();
        }
        image = cameraPreview.getCurrentFrame();
        Log.d("ALON", "Length: "+image.length);

     //   Bitmap mipap = BitmapFactory.decodeByteArray(image, 0, image.length);

       // Toast.makeText(this, ""+mipap.getHeight()+" "+mipap.getWidth(), Toast.LENGTH_SHORT).show();


        Bitmap mipap = automata.com.keyless.FaceDetectionUtils.Utils.getBitmapImageFromYUV(image, 480, 640);


        if (mipap != null) {
            //Bitmap rot = RotateBitmap(mipap, 90);
            im_pic.setImageBitmap(mipap);
        } else {
            Log.d("ALON", "es ist null");
        }
    }

    public void enrollPic() {
        if (image == null) {
            Log.d("ALON", "Image is null");
            return;
        }
        if (user == null || user.equals("")) {
            Log.d("ALON", "User is null");
            return;
        }
                /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                image = stream.toByteArray();*/
        //image = Utils.getNV21(480, 640, bitmap);
                /*try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/sdcard/jaja.yuv"));
                    bos.write(image);
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    Log.e("ALON", e.toString());
                }*/
        int res = IsItYouSdk.getInstance(getApplicationContext()).saveEnrollment(image, 0);
        Log.d("ALON", "Result: "+String.valueOf(res));
        String enrolls = "Enrolls: "+iiy.getNumOfEnrolls();
        tv_res.setText("Result: "+String.valueOf(res)+" "+enrolls);

        Toast.makeText(this, ""+"Result: "+String.valueOf(res)+" "+enrolls, Toast.LENGTH_SHORT).show();

    }

    public void matchPic() {
        if (image == null) {
            Log.d("ALON", "Image is null");
            return;
        }
        if (user == null || user.equals("")) {
            Log.d("ALON", "User is null");
            return;
        }

        int res = iiy.match(image, 0);
        String path = Environment.getExternalStorageDirectory().toString()+"/IIYLOGS/iiylog"+iiy.getNumOfEnrolls()+".txt";
        try {
            PrintWriter p = new PrintWriter(new FileOutputStream(path, true));
            p.println((System.currentTimeMillis()/1000)+":"+res);
            p.close();
        } catch (Exception e) {
            Toast.makeText(this, "Could not write log", Toast.LENGTH_SHORT).show();
            Log.e("Exception", "File write failed: " + e.toString());
        }
        Log.d("ALON", "Result: "+String.valueOf(res));
        Double score = iiy.getScoreDebug();
        String as = iiy.getASReason();
        if (res == IsItYouConstants.SUCCESS) {
            Log.d("ALON", "Finalized with "+res);
            iiy.finalizeMatch(true);
        } else {
            iiy.finalizeMatch(false);
        }
        String enrolls = "Enrolls: "+iiy.getNumOfEnrolls();
        String fnl = "";
        switch(res) {
            case 1:
                fnl = "Success";
                break;
            case 2:
                if (iiy.getAS())
                    fnl = "Second factor";
                else
                    fnl = "Success (SF)";
                break;
            case 3:
                fnl = "Success";
                break;
            case 4:
                fnl = "no enrollments";
                break;
            case 5:
                fnl = "user blocked";
                break;
            case 10:
                fnl = "Not detected";
                break;
            case 11:
                fnl = "Someone else";
                break;
        }
        tv_res.setText(String.valueOf(res)+","+fnl+","+String.valueOf(score)+","+as+" "+enrolls);
    }

    public void elChapo(View v) {
        takePic();
        enrollPic();
    }

    public void elMacho(View v) {
        takePic();
        matchPic();
    }

    public void initIIYSDK()
    {
        iiyInitializationThread = new InitOperation();
        iiyInitializationThread.execute();
    }

    @Override
    public void onImageTaken(byte[] bytes) {
        image = bytes;
    }

    @Override
    public boolean startCapture(byte[] bytes) {
        return false;
    }

    @Override
    public boolean onSurfcaeDestroy() {
        return false;
    }

    public class InitOperation extends AsyncTask<Integer, Void, Integer>
    {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Integer doInBackground(Integer... params) {
            //int initResult = IsItYouSdk.getInstance(getApplicationContext()).init("AlonGoldenberg10", 0);
            int initResult = iiy.init("R3JoeBpA271NzmQ8",90);
            Log.d("ALON", "initResult: "+initResult);
            /*File file = new File("/sdcard/lulu.yuv");
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int res = IsItYouSdk.getInstance(getApplicationContext()).saveEnrollment(bytes, 0);
            Log.d("ALON", "Result: "+String.valueOf(res));*/
            return initResult;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            String initUser = iiy.initUser("Guest");
            //sw_learning.setChecked(iiy.getIsLearning());
            user = iiy.getCurrentUser();
            sw_learning.setChecked(iiy.getIsLearning());
            sw_setas.setChecked(iiy.getAS());
            final String enrolls = "Enrolls: "+iiy.getNumOfEnrolls();
            openCameraView();
            /*File file = new File("/sdcard/lulu.yuv");
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }iiy
            int res = IsItYouSdk.getInstance(getApplicationContext()).saveEnrollment(bytes, 0);
            Log.d("ALON", "Result: "+String.valueOf(res));*/
            //IsItYouSdk.getInstance(getApplicationContext()).setASEnroll(false);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tv_res.setText("Init user "+user + " "+enrolls);
                    bt_s.setEnabled(true);
                    //  bt_macho.setEnabled(true);
                    // bt_reset.setEnabled(true);
                }
            });
        }

    }

    public class ImageOperation extends AsyncTask<Integer, Void, Integer>
    {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            tv_res.setText("Processing...");

        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), imUri);
                /*try {
                    Log.d("ALON", original.getColorSpace().toString());
                } catch (Exception e) {

                }*/
                Bitmap rotated = RotateBitmap(original, 90);
                Bitmap stretched = ResizeBitmap(rotated, 480, 640);
                bitmap = stretched;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        im_pic.setImageBitmap(bitmap);
                    }
                });
                /*Log.d("ALON", "Image size is "+image.length);
                Mat mat = new Mat(480, 640, CvType.CV_8UC3);
                mat.put(0, 0, image);
                Mat yuv = new Mat();
                Imgproc.cvtColor(mat, yuv, Imgproc.COLOR_RGB2YUV);
                Mat nv21 = new Mat();
                Imgproc.cvtColor(yuv, nv21, Imgproc.COLOR_YUV2RGB_NV21);
                */
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tv_res.setText("Processed");
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("ALON", String.format("RC %d RES %d", requestCode, resultCode));

        if (data == null || data.getData() == null) {
            Log.d("ALON", String.format("data: %b getData: %b", data == null, data.getData() == null));
        }

        if (requestCode == 10 && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            imUri = uri;

            ImageOperation imageOperation = new ImageOperation();
            imageOperation.execute();
        }

        if (requestCode == 20 && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            //im_pic.setImageBitmap(bitmap);
            //Log.d("ALON", bitmap.getColorSpace().toString());
            byte[] buffer = bitmapToBytes(bitmap);
            int dodo = iiy.match(buffer, 1);
            Log.d("ALON", "Result: "+dodo);
            if (dodo == 1)
                iiy.finalizeMatch(true);
            else
                iiy.finalizeMatch(false);
        }
    }

    private byte[] bitmapToBytes(Bitmap source) {
        /*Bitmap bitmap = ResizeBitmap(source, 480, 640);
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap, mat);
        byte[] buffer = new byte[(int) (mat.total() * mat.channels())];
        mat.get(0, 0, buffer);
        return buffer;*/

        Log.d("ALON", "Conf: "+source.getConfig().toString());
        //Bitmap b = source;
        source = ResizeBitmap(source, 480, 640);
        int[] pixels = new int[480 * 640];
        source.getPixels(pixels, 0, 480, 0, 0, 480, 640);
        byte[] array = new byte[pixels.length * 3 / 2];
        automata.com.keyless.FaceDetectionUtils.Utils.encodeYUV420SP(array, pixels, 480, 640);
        /*int bytes = b.getByteCount();
        //or we can calculate bytes this way. Use a different value than 4 if you don't use 32bit images.
        //int bytes = b.getWidth()*b.getHeight()*4;

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
        b.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

        byte[] array = buffer.array(); //Get the underlying array containing the data.
        byte[] converted = new byte[array.length * 3 / 2];
        biz.isityou.demo.Utils.encodeYUV420SP(converted, array, 480, 640);*/
        return array;

    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            image = bytes;
            Bitmap mipap = BitmapFactory.decodeByteArray(image, 0, image.length);
            Bitmap rot = RotateBitmap(mipap, 90);
            im_pic.setImageBitmap(rot);
            camera.startPreview();
        }
    };
}
