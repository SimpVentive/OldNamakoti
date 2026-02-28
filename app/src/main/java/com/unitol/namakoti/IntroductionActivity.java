package com.unitol.namakoti;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroductionActivity extends Activity implements OnClickListener{

	private ImageView logo;
	private View last_clicked_view;
    private LinearLayout app_version_content;
	private LinearLayout what_is_namakoti_content;
	private LinearLayout instructions_content;
	private LinearLayout terms_of_use_content;
	private LinearLayout contact_us_content;
	private LinearLayout registration_help_content;

    private TextView app_version_content_expand;
	private TextView what_is_nama_koti_expand;
	private TextView instructions_expand;
	private TextView terms_of_use_expand;
	private TextView contact_us_expand;
	private TextView registration_expand;
	private TextView lastExpandView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.introduction_main);
		
		logo = (ImageView) findViewById(R.id.logo);
		ImageView back = (ImageView) findViewById(R.id.back);
		logo.setImageDrawable(getResources().getDrawable(R.drawable.namakoti_hdr2));

//        int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            back.setBackgroundDrawable(getResources().getDrawable(R.drawable.back));
//            //layout.setBackgroundDrawable( getResources().getDrawable(R.drawable.ready) );
//        } else {
//            back.setBackground(getResources().getDrawable(R.drawable.back));
//            //layout.setBackground( getResources().getDrawable(R.drawable.ready));
//        }
//

		

		what_is_nama_koti_expand = (TextView) findViewById(R.id.what_is_nama_koti_expand);
		instructions_expand = (TextView) findViewById(R.id.instructions_expand);
		terms_of_use_expand = (TextView) findViewById(R.id.terms_of_use_expand);
		contact_us_expand = (TextView) findViewById(R.id.contact_us_expand);
		registration_expand = (TextView) findViewById(R.id.registration_expand);
        app_version_content_expand = (TextView) findViewById(R.id.app_version_expand);
		
		LinearLayout what_is_namakoti_layout = (LinearLayout) findViewById(R.id.what_is_namakoti_layout);
		LinearLayout instructions_layout = (LinearLayout) findViewById(R.id.instructions_layout);
		LinearLayout terms_of_use_layout = (LinearLayout) findViewById(R.id.terms_of_use_layout);
		LinearLayout contact_us_layout = (LinearLayout) findViewById(R.id.contact_us_layout);
		LinearLayout registration_help = (LinearLayout) findViewById(R.id.registration_help);
		
		what_is_namakoti_layout.setOnClickListener(this);
		instructions_layout.setOnClickListener(this);
		terms_of_use_layout.setOnClickListener(this);
		contact_us_layout.setOnClickListener(this);
		registration_help.setOnClickListener(this);
		
		what_is_namakoti_content = (LinearLayout) findViewById(R.id.what_is_namakoti_content);
		instructions_content = (LinearLayout) findViewById(R.id.instructions_content);
		terms_of_use_content = (LinearLayout) findViewById(R.id.terms_of_use_content);
		contact_us_content = (LinearLayout) findViewById(R.id.contact_us_content);
		registration_help_content = (LinearLayout) findViewById(R.id.registration_help_content);

        LinearLayout app_version_help = (LinearLayout) findViewById(R.id.app_version_help);
        TextView appVersion = (TextView) findViewById(R.id.app_version);
        appVersion.setTextSize(17);
        app_version_help.setOnClickListener(this);

        app_version_content = (LinearLayout) findViewById(R.id.app_version_content);
        TextView app_version = (TextView) app_version_content.findViewById((R.id.app_version_details));
        app_version.setText("v"+BuildConfig.VERSION_NAME);
		
		TextView t2 = (TextView) findViewById(R.id.namakoti_hiperlink);
		t2.setMovementMethod(LinkMovementMethod.getInstance());
		

		TextView t3 = (TextView) findViewById(R.id.namakoti_hiperlink3);
		t3.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView email_hiperlink3 = (TextView) findViewById(R.id.email_hiperlink3);
		email_hiperlink3.setMovementMethod(LinkMovementMethod.getInstance());
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}

	@Override
	public void onClick(View v) {
		boolean sameView = false;
		
		switch (v.getId()) {
            case R.id.app_version_help:
                if (last_clicked_view == app_version_content) {
                    sameView = true;
                    if (app_version_content.getVisibility() == View.GONE) {
                        app_version_content.setVisibility(View.VISIBLE);
                        app_version_content_expand.setText("-");
                    } else {
                        app_version_content.setVisibility(View.GONE);
                        app_version_content_expand.setText("+");
                    }

                } else {
                    sameView = false;
                    app_version_content.setVisibility(View.VISIBLE);
                    app_version_content_expand.setText("-");
                    if (last_clicked_view != null && !sameView) {
                        last_clicked_view.setVisibility(View.GONE);
                        lastExpandView.setText("+");
                    }
                }
                last_clicked_view = app_version_content;
                lastExpandView = app_version_content_expand;
                break;

		case R.id.what_is_namakoti_layout:
			if (last_clicked_view == what_is_namakoti_content) {
				sameView = true;
				if (what_is_namakoti_content.getVisibility() == View.GONE) {
					what_is_namakoti_content.setVisibility(View.VISIBLE);
					what_is_nama_koti_expand.setText("-");
				} else {
					what_is_namakoti_content.setVisibility(View.GONE);
					what_is_nama_koti_expand.setText("+");
				}
				
			} else {
				sameView = false;
				what_is_namakoti_content.setVisibility(View.VISIBLE);
				what_is_nama_koti_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			last_clicked_view = what_is_namakoti_content;
			lastExpandView = what_is_nama_koti_expand;
			break;
		case R.id.instructions_layout:
			if (last_clicked_view == instructions_content) {
				sameView = true;
				if (instructions_content.getVisibility() == View.GONE) {
					instructions_content.setVisibility(View.VISIBLE);
					instructions_expand.setText("-");
				} else {
					instructions_content.setVisibility(View.GONE);
					instructions_expand.setText("+");
				}
				
			} else {
				sameView = false;
				instructions_content.setVisibility(View.VISIBLE);
				instructions_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			last_clicked_view = instructions_content;
			lastExpandView = instructions_expand;
			
			break;
		case R.id.terms_of_use_layout:
			if (last_clicked_view == terms_of_use_content) {
				sameView = true;
				if (terms_of_use_content.getVisibility() == View.GONE) {
					terms_of_use_content.setVisibility(View.VISIBLE);
					terms_of_use_expand.setText("-");
				} else {
					terms_of_use_content.setVisibility(View.GONE);
					terms_of_use_expand.setText("+");
				}
				
			} else {
				sameView = false;
				terms_of_use_content.setVisibility(View.VISIBLE);
				terms_of_use_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			last_clicked_view = terms_of_use_content;
			lastExpandView = terms_of_use_expand;
			
			break;
		case R.id.contact_us_layout:
			if (last_clicked_view == contact_us_content) {
				sameView = true;
				if (contact_us_content.getVisibility() == View.GONE) {
					contact_us_content.setVisibility(View.VISIBLE);
					contact_us_expand.setText("-");
				} else {
					contact_us_content.setVisibility(View.GONE);
					contact_us_expand.setText("+");
				}
				
			} else {
				sameView = false;
				contact_us_content.setVisibility(View.VISIBLE);
				contact_us_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			last_clicked_view = contact_us_content;
			lastExpandView = contact_us_expand;
			
			break;
		case R.id.registration_help:
			if (last_clicked_view == registration_help_content) {
				sameView = true;
				if (registration_help_content.getVisibility() == View.GONE) {
					registration_help_content.setVisibility(View.VISIBLE);
					registration_expand.setText("-");
				} else {
					registration_help_content.setVisibility(View.GONE);
					registration_expand.setText("+");
				}
				
			} else {
				sameView = false;
				registration_help_content.setVisibility(View.VISIBLE);
				registration_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			last_clicked_view = registration_help_content;
			lastExpandView = registration_expand;
			
			break;

		default:
			break;
		}
	}
}
