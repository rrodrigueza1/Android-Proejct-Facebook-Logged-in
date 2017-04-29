package renzellerodrigueza.com.mysharecam30;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

/**
 * Created by renze on 2017-04-19.
 */

public class SingleImageSelect extends Activity implements View.OnClickListener {
    Bitmap image;
    private CallbackManager callbackManager;
    File file;
    Boolean isAuthed;
    byte[] byteArray;
    Button upload;
    EditText message;
    SharePhotoContent content;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_item_layout);
        String filename = getIntent().getExtras().getString("FILENAME");
        message = (EditText)findViewById(R.id.message_text);
        // fetch the file with the retrieved file name
        file = new File(Environment.getExternalStorageDirectory() + "/MyShareCam30/" + filename);
        upload = (Button)findViewById(R.id.upload_photo_button);
        upload.setOnClickListener(this);
        // set image rotation
        Matrix mat = new Matrix();
        mat.postRotate(90);
        Log.d("File Path: ",file.getAbsolutePath());
        image = BitmapFactory.decodeFile(file.getAbsolutePath());
        // rotate the image
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mat, true);
        // convert image into byte[]
        byteArray = getByteArray(image);
        ImageView imageView = (ImageView)findViewById(R.id.single_item_image);
        imageView.setImageBitmap(image);
        // implentShare method
        implementShare();
        // request for publish action permission
        publishLogin();
    }

    private byte[] getByteArray(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();

    }

    private void publishLogin() {

        callbackManager = CallbackManager.Factory.create();
        // Bypasses login button requirement
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        isAuthed = true;
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SingleImageSelect.this, "Login Cancel", Toast.LENGTH_LONG).show();
                        isAuthed = false;
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SingleImageSelect.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        isAuthed = false;
                    }
                });
        // request login with desired publish permission. Take note that facebook will request for an app review before allowing the
        // app to go on live with publish permissions.
        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }

    private void implementShare() {
        // Initialize share button
        ShareButton fbShareButton = (ShareButton) findViewById(R.id.shareButton);
        // Setup photo to share
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        // Setup content to share
        content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        // loads content to share in the shareButton
        fbShareButton.setShareContent(content);
    }


    @Override
    public void onClick(View v) {

        // check if the user in logged in
        if(MainFragment.isAuthed)
        {
            String path = "me/photos";
            AccessToken at = AccessToken.getCurrentAccessToken();
            Bundle parameters = new Bundle();
            parameters.putByteArray("picture",byteArray);
            parameters.putString("message", message.getText().toString());
            // create an instance of POST
            HttpMethod method = HttpMethod.POST;
            // create graphrequest callback
            GraphRequest.Callback cb = new GraphRequest.Callback() {

                @Override
                public void onCompleted(GraphResponse graphResponse) {

                    //check graphResponse for success or failure
                    if(graphResponse.getError()==null){
                        Toast.makeText(SingleImageSelect.this, "Successfully posted to Facebook", Toast.LENGTH_SHORT).show();
                        message.setText("");
                    }
                    else{
                        Toast.makeText(SingleImageSelect.this, "Facebook: There was an error, Please Try Again", Toast.LENGTH_SHORT).show();

                    }
                }
            };
            // Pass in the AccessToken, Graph API Path, parameters bundle, and the callback
            GraphRequest request = new GraphRequest(at,path,parameters,method,cb);
            request.setParameters(parameters);
            request.executeAsync();

        }
        else
        {
            Toast.makeText(SingleImageSelect.this,"Please allow app to publish its item", Toast.LENGTH_SHORT).show();
        }
    }
}
