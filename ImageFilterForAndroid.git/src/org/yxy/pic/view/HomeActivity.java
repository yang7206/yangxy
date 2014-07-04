package org.yxy.pic.view;

import org.yxy.pic.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class HomeActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		findViewById(R.id.pic_get_camera).setOnClickListener(this);
		findViewById(R.id.pic_get_photo).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int type = 0;
		switch (v.getId()) {
		case R.id.pic_get_camera:
			type = ImageFilterMain.REQUEST_CODE_CAPTURE_CAMEIA;
			break;
		case R.id.pic_get_photo:
			type = ImageFilterMain.REQUEST_CODE_PICK_IMAGE;
			break;
		}
		Intent intent =new Intent(getBaseContext(), ImageFilterMain.class);
		intent.putExtra(ImageFilterMain.REQUEST_TYPE, type);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);;
	}

}
