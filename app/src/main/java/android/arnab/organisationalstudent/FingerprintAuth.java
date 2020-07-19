package android.arnab.organisationalstudent;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;

public class FingerprintAuth extends AppCompatActivity implements View.OnClickListener,FingerPrintAuthCallback
{
    FingerPrintAuthHelper mFingerPrintAuthHelper;
    ImageView fingerprintBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_auth);
        Toolbar toolbar=findViewById(R.id.toolbar8);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fingerprintBg=findViewById(R.id.fingerprintBg);
        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);

        int resid = R.drawable.stubg;
        Glide
                .with(this)
                .load(resid).into(fingerprintBg);

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

        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {


    }

    @Override
    public void onNoFingerPrintHardwareFound()
    {
        Toast.makeText(getApplicationContext(),"Fingerprint sensor not found",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoFingerPrintRegistered()
    {
        Toast.makeText(getApplicationContext(),"Fingerprint not registered with device",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBelowMarshmallow()
    {
        Toast.makeText(getApplicationContext(),"Fingerprint authentication is not supported in this android version",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject)
    {
        //Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), StudentCard.class);
        startActivity(intent);
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {    //Parse the error code for recoverable/non recoverable error.
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                //Cannot recognize the fingerprint scanned.
                Toast.makeText(getApplicationContext(), "Cannot recognise fingerprint", Toast.LENGTH_SHORT).show();
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                //This is not recoverable error. Try other options for user authentication. like pin, password.
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                //Any recoverable error. Display message to the user.
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
