package renzellerodrigueza.com.mysharecam30;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by renze on 2017-04-08.
 */

// this application is run on start and is used to display key hash values on logcat
// Key Hash value is use when using facebook sdk
// Tutorial on generating key hash value
// http://stackoverflow.com/questions/7506392/how-to-create-android-facebook-key-hash
public class GenerateKeyHashActivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Generate Key Hash Value on your machine
        keyHash();
    }

    private void keyHash() {

            try {
                PackageInfo info = getPackageManager().getPackageInfo("renzellerodrigueza.com.mysharecam30", PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.e("Key:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            } catch (PackageManager.NameNotFoundException e) {

            } catch (NoSuchAlgorithmException e) {

            }
        }
}
