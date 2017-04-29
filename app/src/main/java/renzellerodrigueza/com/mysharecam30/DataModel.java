package renzellerodrigueza.com.mysharecam30;

import android.graphics.Bitmap;

/**
 * Created by renze on 2017-04-18.
 */

// DataModel used in ListView
public class DataModel {

    private Bitmap image;
    private String fileName;

    public DataModel(Bitmap image, String fileName ) {
        this.image=image;
        this.fileName = fileName;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


    public String getFileName() {
        return fileName;
    }

}
