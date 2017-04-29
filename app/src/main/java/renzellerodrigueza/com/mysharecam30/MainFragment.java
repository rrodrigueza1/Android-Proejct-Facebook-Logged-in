package renzellerodrigueza.com.mysharecam30;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * tutorial for setting up the project
 * https://developers.facebook.com/docs/android/getting-started
 * tutorial for facebook login button
 * https://developers.facebook.com/docs/facebook-login/android#addbutton
 * https://github.com/slidenerd/FaceBookv4.0HelloWorld
 */

public class MainFragment extends Fragment {

    ArrayList<DataModel> dataModels;
    private static ImageAdapter adapter;
    File directory;
    Button buttonCamera;
    public static Activity activity;
    private CallbackManager callbackManager;
    private AccessTokenTracker mTokenTracker;
    private TextView tv_email, tv_userName;
    // use for checking if the user is logged in
    public static boolean isAuthed;
    ProfilePictureView profilePictureView;
    String[] fileNames;
    private String email = "", name = "", id="";
    Bitmap image;
    Bitmap[] images;
    ListView listView;
    View view;


    private FacebookCallback<LoginResult> callBack = callBack = new FacebookCallback<LoginResult>() {
        @Override
        // if the login is successful
        public void onSuccess(final LoginResult loginResult) {

            // create graph request to pull information and profile picture of the current user/newMe
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        // if successful
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                Bitmap bitmap;
                                Log.d("Access Token: ", loginResult.getAccessToken().getToken());
                                Log.d("UserID: ", object.getString("id"));
                                id = object.getString("id");
                                email = object.getString("email");
                                name = object.getString("name");
                                tv_email.setText(email);
                                tv_userName.setText(name);
                                bitmap = getFacebookProfilePicture(id);
                                isAuthed = true;
                                profilePictureView.setProfileId(id);
                                Log.d("Info: ", email + " " + name + " " + id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    });

            Bundle parameters = new Bundle();
            // fields to retrieve
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
            mTokenTracker.startTracking();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.context, "User cancel", Toast.LENGTH_LONG).show();
            isAuthed = false;
        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(MainActivity.context, "Something went wrong", Toast.LENGTH_LONG).show();
            isAuthed = false;
        }
    };;


    public MainFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        activity = getActivity();
        // initialize AccessTokenTracker
        setupTokenTracker();

    }

    private void setupTokenTracker()
    {
        mTokenTracker = new AccessTokenTracker() {
            @Override

            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                isAuthed = true;
                // Check if the user logged out
                if (currentAccessToken == null) {
                   Toast.makeText(MainActivity.context, "You have successfully logout", Toast.LENGTH_LONG).show();
                    tv_email.setText("User Email");
                    tv_userName.setText("Your Name");
                    profilePictureView.setProfileId("");
                    // stop tracking AccessTokenTracker
                    mTokenTracker.stopTracking();
                    isAuthed = false;
                }


            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        dataModels.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        mTokenTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(email != ""|| name != "")
        {
            tv_email.setText(email);
            tv_userName.setText(name);
        }
        mTokenTracker.startTracking();
        getImages(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_button,container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bitmap bitmap = null;
        this.view = view;
        super.onViewCreated(view, savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        tv_userName = (TextView)view.findViewById(R.id.user_name);
        tv_email = (TextView)view.findViewById(R.id.user_email);
        profilePictureView =(ProfilePictureView)view.findViewById(R.id.profile_picture);
        LoginButton loginButton = (LoginButton)view.findViewById(R.id.login_button);
        // optional if ur using fragment
        loginButton.setFragment(this);
        // set read permission on facebook login
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends" ));

        // register a callback
        loginButton.registerCallback(callbackManager,callBack);
        buttonCamera = (Button)view.findViewById(R.id.button_camera);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTokenTracker.stopTracking();
                if(isAuthed)
                {
                    Intent intent = new Intent(getActivity(), CameraActivity.class);
                    MainFragment.this.startActivity(intent);
                }
                else
                {
                    Toast.makeText(getContext(), "Please login to continue", Toast.LENGTH_LONG).show();
                }


            }
        });


        getImages(view);

    }

    // fetch images from the directory
    public Bitmap[] getImages()
    {
        images = null;
        // fetch images from the directory
        directory = new File(Environment.getExternalStorageDirectory() + "/MyShareCam30");
        if(directory.exists())
        {
        File[] files = directory.listFiles();
        fileNames = directory.list();
        images = new Bitmap[directory.listFiles().length];
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 4;
        bmOptions.inScaled = true;
        Matrix mat = new Matrix();
        // rotate image to 90 degrees. Problem with samsung device.
        mat.postRotate(90);
        int i = 0;
        for (File imagePath : files) {

            image = BitmapFactory.decodeFile(imagePath.getAbsolutePath(), bmOptions);
            image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mat, true);
            images[i] = image;
            i++;
        }


        Log.d("TAG","Image Retrieve");
        }
        return images;

    }

    public static Bitmap getFacebookProfilePicture(String userID){
        URL imageURL = null;
        Bitmap bitmap = null;
        try {
            imageURL = new URL(String.format("http://graph.facebook.com/%s/picture?type=large", userID));
            if(bitmap != null)
            {
                bitmap.recycle();
            }
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return bitmap;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getImages(View v) {

        dataModels = new ArrayList<DataModel>();
        images = this.getImages();
        int counter = 0;
        if (images != null) {


            for (Bitmap image : images) {
                dataModels.add(new DataModel(image, fileNames[counter]));
                counter++;
            }
            adapter = new ImageAdapter(MainActivity.context, dataModels);
            listView = (ListView) view.findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("OnItemClick", "This is click using OnItemClick");
                    if (isAuthed) {
                        DataModel dataModel = dataModels.get(position);
                        Bundle bundle = new Bundle();
                        Log.d("FileName: ", dataModel.getFileName());
                        bundle.putString("FILENAME", dataModel.getFileName());
                        Intent intent = new Intent(MainFragment.activity, SingleImageSelect.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Please login to continue", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }

}
