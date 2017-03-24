package helper.custom; // 41 Post - Created by DimasTheDriver on May/24/2012 . Part of the 'Android - rendering a Path with a Bitmap fill' post. http://www.41post.com/?p=4766

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.tomahawk.tomahawk_android.R;
import org.tomahawk.tomahawk_android.activities.TomahawkMainActivity;

import static org.tomahawk.tomahawk_android.activities.TomahawkMainActivity.bitmapForSplash;
import static org.tomahawk.tomahawk_android.activities.TomahawkMainActivity.bitmapForSplashHeight;
import static org.tomahawk.tomahawk_android.activities.TomahawkMainActivity.bitmapForSplashWidth;

public class MovingImageViewView1 extends ImageView
{




	Context context;




	public MovingImageViewView1(Context context) {
		super(context);

		this.context = context;

	}

	public MovingImageViewView1(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		this.context = context;

	}

	public MovingImageViewView1(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		this.context = context;

	}







	int viewLeft;
	int viewRight;
	int viewTop;
	int viewBottom;




	int bitmapLeft;
	int bitmapRight;



	boolean variablesInitialized = false;


	@Override
	protected void onDraw(Canvas canvas)
	{


		if(!variablesInitialized )
		{

			variablesInitialized = true;

			viewLeft = MovingImageViewView1.this.getLeft();
			viewTop = MovingImageViewView1.this.getTop();
			viewRight = MovingImageViewView1.this.getRight();
			viewBottom = MovingImageViewView1.this.getBottom();


			float acspectRatioOfView = (viewRight - viewLeft)/((float)(viewBottom - viewTop));

			int bitmapSlicewidth = (int)(bitmapForSplashHeight * acspectRatioOfView);


			bitmapLeft =0;
			bitmapRight = bitmapSlicewidth;

		}





		canvas.drawBitmap(bitmapForSplash,


				new Rect(0 ,
						0,
						bitmapRight,
						bitmapForSplashHeight),

				new Rect(viewLeft
						, viewTop
						, viewRight
						, viewBottom
				),
				null);




		super.onDraw(canvas);


	}
}
