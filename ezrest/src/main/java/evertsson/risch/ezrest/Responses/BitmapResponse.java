package evertsson.risch.ezrest.Responses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by johanrisch on 17/02/15.
 */
public class BitmapResponse implements RestResponse {
    private int mBitmapWidth;
    private int mBitmapHeight;
    private Bitmap mBmp;

    @Override
    public String getAccept() {
        return "image/bmp";
    }

    /**
     * Converts the supplied {@link InputStream} to a {@link android.graphics.Bitmap}
     *
     * @param instream
     * @return
     * @throws IOException
     */
    public void convertStream(InputStream instream) {
        try {
            Bitmap bmp;
            if (mBitmapWidth > 0 || mBitmapHeight > 0) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                instream.mark(instream.available());
                BitmapFactory.decodeStream(instream, null, opts);
                opts.inSampleSize = calculateInSampleSize(opts, mBitmapWidth,
                        mBitmapHeight);
                // opts.inSampleSize = 2;
                opts.inJustDecodeBounds = false;
                instream.reset();
                bmp = BitmapFactory.decodeStream(instream, null, opts);
            } else {
                bmp = BitmapFactory.decodeStream(instream);
            }
            mBmp = bmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBody() {
        return null;
    }

    /**
     * Calculate {@link Bitmap} size
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
