package android.arnab.organisationalstudent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.stringcare.library.SC;
import com.tempos21.t21crypt.crypter.Crypter;
import com.tempos21.t21crypt.exception.CrypterException;
import com.tempos21.t21crypt.exception.DecrypterException;
import com.tempos21.t21crypt.exception.EncrypterException;
import com.tempos21.t21crypt.factory.CryptMethod;
import com.tempos21.t21crypt.factory.CrypterFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class StudentCard extends AppCompatActivity
{
    static ImageView qrImg,cardBg,cardFg;
    Context mContext;
    ProgressBar loadCard;
    static RelativeLayout loadLayout;
    private static String KEY_TOKEN;
    static long idCopy;
    static int CARD_VALIDITY_PERIOD=30000;
    TextView stdName,deptName,yrVal,stdId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_card);
        mContext=getApplicationContext();

        Toolbar toolbar=findViewById(R.id.toolbar2);
        toolbar.setTitle("Smart Card");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        qrImg=findViewById(R.id.qrImage);
        loadCard=findViewById(R.id.loadCard);
        loadLayout=findViewById(R.id.loadLayout);
        cardBg=findViewById(R.id.cardBg);
        cardFg=findViewById(R.id.cardFg);
        stdName=findViewById(R.id.stdName);
        deptName=findViewById(R.id.deptName);
        yrVal=findViewById(R.id.yrVal);
        stdId=findViewById(R.id.stdId);

        KEY_TOKEN=this.getResources().getString(R.string.crypter_key);
        loadLayout.setVisibility(View.VISIBLE);
        makeScreenUnresponsive();

        SharedPreferences preferences=getSharedPreferences("Details",0);
        final long id=preferences.getLong("id",-1);
        int yr=preferences.getInt("grade",-1);
        String dept=preferences.getString("department","");
        String name=preferences.getString("personName","");

        stdName.setText(name);
        deptName.setText(dept);
        yrVal.setText("Year: "+yr);
        stdId.setText(id+"");

        idCopy=id;

        getCandidateDetails(getApplicationContext(),id);

        int resid = R.drawable.stubg;
        Glide
                .with(this)
                .load(resid).into(cardBg);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            //Toast.makeText(getApplicationContext(), "Focus changed", Toast.LENGTH_SHORT).show();

                updateOTP(getApplicationContext(), idCopy);
                onBackPressed();

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void getCandidateDetails(final Context mContext, final long id)
    {
        Response.Listener<String> detailsResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                //info.setText(response);
                Map<String,String> params=new HashMap<>();
                JSONObject jsonResponse= null;
                //msg.setText(response);
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        int activeState=jsonResponse.getInt("activeState");
                        if(activeState==1)
                        {
                            //$Id, $personName, $grade, $department, $orgWalletVal, $activeState, $OTP

                            String personName,department;
                            int grade,orgWalletVal,otp;
                            personName=jsonResponse.getString("personName");
                            department=jsonResponse.getString("department");
                            grade=jsonResponse.getInt("grade");
                            orgWalletVal=jsonResponse.getInt("orgWalletVal");
                            otp=jsonResponse.getInt("OTP");
                            params.put("personName",personName);
                            params.put("department",department);
                            params.put("id",id+"");
                            params.put("grade",grade+"");
                            params.put("orgWalletVal",orgWalletVal+"");
                            params.put("activeState",activeState+"");
                            params.put("otp",otp+"");
                            params.put("message","success");

                            SharedPreferences preferences=getSharedPreferences("Details",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putLong("id",jsonResponse.getLong("id"));
                            editor.putInt("grade",jsonResponse.getInt("grade"));
                            editor.putString("department",jsonResponse.getString("department"));
                            editor.putString("personName",jsonResponse.getString("personName"));
                            editor.commit();



                            String information=params.get("id")+"@"+params.get("personName")+"@"+params.get("grade")+"@"+params.get("department").toLowerCase()+
                                    "@"+params.get("orgWalletVal")+"@"+params.get("activeState")+"@"+params.get("otp");

                            QR qr=new QR(mContext,KEY_TOKEN);
                            information=qr.getEncryptedString(information);


                            if(information!=null)
                            {
                                try {
                                    int resid = R.drawable.cardbg;
                                    Glide
                                            .with(mContext)
                                            .load(resid).into(cardFg);


                                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                    BitMatrix bitMatrix = multiFormatWriter.encode(information,
                                            BarcodeFormat.QR_CODE, qrImg.getWidth(), qrImg.getHeight());
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                    qrImg.setImageBitmap(bitmap);
                                    loadLayout.setVisibility(View.GONE);
                                    makeWindowResponsive();

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run()
                                        {

                                                finish();

                                        }
                                    }, CARD_VALIDITY_PERIOD);




                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        else
                        {
                            if(activeState==0)
                            {
                                Toast.makeText(mContext,"Card not active. Contact office",Toast.LENGTH_SHORT).show();
                                params.put("message","Failed");

                            }
                        }
                    }
                    else{
                        Toast.makeText(mContext,"Invalid Id. Contact office",Toast.LENGTH_SHORT).show();
                        params.put("message","Failed");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                    params.put("message","Failed");
                }
                  catch (CrypterException e) {
                    e.printStackTrace();
                } catch (EncrypterException e) {
                    e.printStackTrace();
                }
            }
        };

        String CANDIDATE_DETAILS_URL= String.format("http://arnabbanerjee.dx.am/requestCandidateDetails.php?id=%1$d",id);
        //msg.setText(CANDIDATE_DETAILS_URL);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(CANDIDATE_DETAILS_URL,detailsResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }

    private static void updateOTP(final Context mContext,long id)
    {
        Response.Listener<String> updateResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        //Toast.makeText(mContext,"OTP changed",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    //Toast.makeText(mContext,"OTP not changed",Toast.LENGTH_SHORT).show();
                }


            }
        };
        RequestUpdateOTP requestUpdateOTP=new RequestUpdateOTP(id,updateResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestUpdateOTP);
    }

    public void makeScreenUnresponsive()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void makeWindowResponsive()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
