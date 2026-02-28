package com.unitol.namakoti;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class IntroductionActivity2 extends Activity {

	private ImageView logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.introduction);
		
		logo = (ImageView) findViewById(R.id.logo);
		ImageView back = (ImageView) findViewById(R.id.back);
		logo.setImageDrawable(getResources().getDrawable(R.drawable.namakoti_hdr2));
		
		/*TextView swipechant_count = (TextView) findViewById(R.id.swipechant_count);
		String swipechant_countText = getResources().getText(R.string.swipechant_count_text).toString();
		
		String htmlString="<u>"+swipechant_countText+"</u>";
		swipechant_count.setText(htmlString);*/
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}
}
