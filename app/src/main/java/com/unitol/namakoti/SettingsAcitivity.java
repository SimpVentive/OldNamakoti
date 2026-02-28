//package com.unitol.namakoti;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//public class SettingsAcitivity extends Activity implements OnClickListener {
//
//	LinearLayout profile, setnama, change_pwd, print_details, accountdetails,
//			logout;
//
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.settings_new_look);
//
//		profile = (LinearLayout) findViewById(R.id.profile_ll);
//		setnama = (LinearLayout) findViewById(R.id.setnama_ll);
//		change_pwd=(LinearLayout)findViewById(R.id.change_pwd_ll);
//		print_details=(LinearLayout)findViewById(R.id.print_details_ll);
//		accountdetails=(LinearLayout)findViewById(R.id.accountdetails_ll);
//		logout=(LinearLayout)findViewById(R.id.logout_ll);
//
//
//		profile.setOnClickListener(this);
//		setnama.setOnClickListener(this);
//		change_pwd.setOnClickListener(this);
//		print_details.setOnClickListener(this);
//		/*enternama.setOnClickListener(this);
//		publishnama.setOnClickListener(this);*/
//		accountdetails.setOnClickListener(this);
//		logout.setOnClickListener(this);
//
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		Intent in = null;
//		switch (v.getId()) {
//	/*	case R.id.back_image:
//			break;
//		case R.id.home_image:
//			break;
//		case R.id.edit_image:
//			in = new Intent(SettingsAcitivity.this, EnternamaActivity.class);
//			startActivity(in);
//			// finish();
//			break;
//		case R.id.settings_image:
//			break;
//		case R.id.exit_image:
//			// finish();
//			break;
//		case R.id.setnama_button:
//			in = new Intent(SettingsAcitivity.this, SelectNamaActivity.class);
//			startActivity(in);
//			// finish();
//			break;
//		case R.id.enternama_button:
//			in = new Intent(SettingsAcitivity.this, EnternamaActivity.class);
//			startActivity(in);
//			// finish();
//			break;
//		case R.id.publishnama_button:
//			in = new Intent(SettingsAcitivity.this, PublishnamaActivity.class);
//			startActivity(in);
//			// finish();
//			break;*/
//
//		case R.id.profile_ll:
//			break;
//		case R.id.setnama_ll:
//			break;
//		case R.id.change_pwd_ll:
//			break;
//		case R.id.print_details_ll:
//			break;
//		case R.id.accountdetails_ll:
//			break;
//		case R.id.logout_ll:
//			break;
//
//		default:
//			break;
//		}
//	}
//}
