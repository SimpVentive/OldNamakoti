package com.unitol.namakoti;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpFragment extends BaseFragment implements OnClickListener {

	protected static final String TAG = "HelpFragment";
    private LinearLayout app_version_content;
	private LinearLayout terms_and_conditions_help_content;
	private LinearLayout account_settings_help_content;
	private LinearLayout print_deliver_help_content;
	private LinearLayout upgrade_help_content;
	private LinearLayout nama_chanting_help_content;
	private LinearLayout chant_setiings_help_content;
	private LinearLayout profile_help_content;
	private LinearLayout registration_help_content;
	private View last_clicked_view;
	private TextView registrationExpand;
	private TextView profile_help_expand;
	private TextView chant_setiings_help_expand;
	private TextView nama_chanting_help_expand;
//	private TextView upgrade_help_expand;
	private TextView print_deliver_help_expand;
	private LinearLayout profile_help;
	private TextView lastExpandView;
	private TextView account_settings_expand;
	private TextView terms_and_conditions_expand;
    private TextView app_version_expand;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null) {
			return null;
		}

		View v = inflater.inflate(R.layout.help_new, container, false);
		mActivity.onFragmentTabChange(View.VISIBLE , R.drawable.help_hdr, 0, "", View.GONE);
		
		registrationExpand = (TextView) v.findViewById(R.id.registration_expand);
		profile_help_expand = (TextView) v.findViewById(R.id.profile_help_expand);
		chant_setiings_help_expand = (TextView) v.findViewById(R.id.chant_setiings_help_expand);
		nama_chanting_help_expand = (TextView) v.findViewById(R.id.nama_chanting_help_expand);
//		upgrade_help_expand = (TextView) v.findViewById(R.id.upgrade_help_expand);
		print_deliver_help_expand = (TextView) v.findViewById(R.id.print_deliver_help_expand);
		account_settings_expand = (TextView) v.findViewById(R.id.account_settings_expand);
		terms_and_conditions_expand = (TextView) v.findViewById(R.id.terms_and_conditions_expand);
        app_version_expand = (TextView) v.findViewById(R.id.app_version_expand);
		
		
		
//		LinearLayout registration = (LinearLayout) v.findViewById(R.id.registration_help);
//		registration.setOnClickListener(this);
		registration_help_content = (LinearLayout) v.findViewById(R.id.registration_help_content);

		profile_help = (LinearLayout) v.findViewById(R.id.profile_help);
		profile_help.setOnClickListener(this);
		profile_help_content = (LinearLayout) v.findViewById(R.id.profile_help_content);

		LinearLayout chant_setiings_help = (LinearLayout) v.findViewById(R.id.chant_setiings_help);
		chant_setiings_help.setOnClickListener(this);
		chant_setiings_help_content = (LinearLayout) v.findViewById(R.id.chant_setiings_help_content);

		LinearLayout nama_chanting_help = (LinearLayout) v.findViewById(R.id.nama_chanting_help);
		nama_chanting_help.setOnClickListener(this);
		nama_chanting_help_content = (LinearLayout) v.findViewById(R.id.nama_chanting_help_content);

//		LinearLayout upgrade_help = (LinearLayout) v.findViewById(R.id.upgrade_help);
//		upgrade_help.setOnClickListener(this);
		upgrade_help_content = (LinearLayout) v.findViewById(R.id.upgrade_help_content);

		LinearLayout print_deliver_help = (LinearLayout) v.findViewById(R.id.print_deliver_help);
		print_deliver_help.setOnClickListener(this);
		print_deliver_help_content = (LinearLayout) v.findViewById(R.id.print_deliver_help_content);

		LinearLayout account_settings_help = (LinearLayout) v.findViewById(R.id.account_settings_help);
		account_settings_help.setOnClickListener(this);
		account_settings_help_content = (LinearLayout) v.findViewById(R.id.account_settings_help_content);

		LinearLayout terms_and_conditions_help = (LinearLayout) v.findViewById(R.id.terms_and_conditions_help);
		terms_and_conditions_help.setOnClickListener(this);
		terms_and_conditions_help_content = (LinearLayout) v.findViewById(R.id.terms_and_conditions_help_content);

        LinearLayout app_version_help = (LinearLayout) v.findViewById(R.id.app_version_help);
        app_version_help.setOnClickListener(this);
        app_version_content = (LinearLayout) v.findViewById(R.id.app_version_content);
        TextView app_version = (TextView) app_version_content.findViewById((R.id.app_version_details));
        app_version.setText("v"+BuildConfig.VERSION_NAME);

		

		TextView t2 = (TextView) v.findViewById(R.id.namakoti_hiperlink);
		t2.setMovementMethod(LinkMovementMethod.getInstance());
		
		return v;
	}

	
	
	@Override
	public void onClick(View v) {
		boolean sameView = false;

//		View tv = profile_help.getChildAt(0);
//		last_clicked_view = v;
		switch (v.getId()) {
		case R.id.registration_help:
			if (last_clicked_view == registration_help_content) {
				sameView = true;
				if (registration_help_content.getVisibility() == View.GONE) {
//					registrationExpand.setText("-");
					registration_help_content.setVisibility(View.VISIBLE);
					registrationExpand.setText("-");
				} else {
					registration_help_content.setVisibility(View.GONE);
					registrationExpand.setText("+");
				}
				
			} else {
				sameView = false;
				registration_help_content.setVisibility(View.VISIBLE);
				registrationExpand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
				}
			}
			last_clicked_view = registration_help_content;
			break;
		case R.id.profile_help:
//			profile_help.findViewById(R.id.profile_help_expand);
			if (last_clicked_view == profile_help_content) {
				sameView = true;
				if (profile_help_content.getVisibility() == View.GONE) {
					profile_help_content.setVisibility(View.VISIBLE);
					profile_help_expand.setText("-");
				} else {
					profile_help_content.setVisibility(View.GONE);
					profile_help_expand.setText("+");
				}
				
			} else {
				sameView = false;
				profile_help_content.setVisibility(View.VISIBLE);
				profile_help_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			last_clicked_view = profile_help_content;
			lastExpandView = profile_help_expand;
			break;
		case R.id.chant_setiings_help:
			if (last_clicked_view == chant_setiings_help_content) {
				sameView = true;
				if (chant_setiings_help_content.getVisibility() == View.GONE) {
					chant_setiings_help_content.setVisibility(View.VISIBLE);
					chant_setiings_help_expand.setText("-");
				} else {
					chant_setiings_help_content.setVisibility(View.GONE);
					chant_setiings_help_expand.setText("+");
				}
				
			} else {
				sameView = false;
				chant_setiings_help_content.setVisibility(View.VISIBLE);
				chant_setiings_help_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			
			last_clicked_view = chant_setiings_help_content;
			lastExpandView = chant_setiings_help_expand;
			break;
		case R.id.nama_chanting_help:
			
			if (last_clicked_view == nama_chanting_help_content) {
				sameView = true;
				if (nama_chanting_help_content.getVisibility() == View.GONE) {
					nama_chanting_help_content.setVisibility(View.VISIBLE);
					nama_chanting_help_expand.setText("-");
				} else {
					nama_chanting_help_content.setVisibility(View.GONE);
					nama_chanting_help_expand.setText("+");
				}
				
			} else {
				sameView = false;
				nama_chanting_help_content.setVisibility(View.VISIBLE);
				nama_chanting_help_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			
			last_clicked_view = nama_chanting_help_content;
			lastExpandView = nama_chanting_help_expand;
			break;
		/*case R.id.upgrade_help:
			
			if (last_clicked_view == upgrade_help_content) {
				sameView = true;
				if (upgrade_help_content.getVisibility() == View.GONE) {
					upgrade_help_content.setVisibility(View.VISIBLE);
					upgrade_help_expand.setText("-");
				} else {
					upgrade_help_content.setVisibility(View.GONE);
					upgrade_help_expand.setText("+");
				}
				
			} else {
				sameView = false;
				upgrade_help_content.setVisibility(View.VISIBLE);
				upgrade_help_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			
			last_clicked_view = upgrade_help_content;
			lastExpandView = upgrade_help_expand;
			break;*/
		case R.id.print_deliver_help:
			
			if (last_clicked_view == print_deliver_help_content) {
				sameView = true;
				if (print_deliver_help_content.getVisibility() == View.GONE) {
					print_deliver_help_content.setVisibility(View.VISIBLE);
					print_deliver_help_expand.setText("-");
				} else {
					print_deliver_help_content.setVisibility(View.GONE);
					print_deliver_help_expand.setText("+");
				}
				
			} else {
				sameView = false;
				print_deliver_help_content.setVisibility(View.VISIBLE);
				print_deliver_help_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			
			last_clicked_view = print_deliver_help_content;
			lastExpandView = print_deliver_help_expand;
			break;
		case R.id.account_settings_help:
			
			if (last_clicked_view == account_settings_help_content) {
				sameView = true;
				if (account_settings_help_content.getVisibility() == View.GONE) {
					account_settings_help_content.setVisibility(View.VISIBLE);
					account_settings_expand.setText("-");
				} else {
					account_settings_help_content.setVisibility(View.GONE);
					account_settings_expand.setText("+");
				}
				
			} else {
				sameView = false;
				account_settings_help_content.setVisibility(View.VISIBLE);
				account_settings_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			
			last_clicked_view = account_settings_help_content;
			lastExpandView = account_settings_expand;
			break;
		case R.id.terms_and_conditions_help:
			
			if (last_clicked_view == terms_and_conditions_help_content) {
				sameView = true;
				if (terms_and_conditions_help_content.getVisibility() == View.GONE) {
					terms_and_conditions_help_content.setVisibility(View.VISIBLE);
					terms_and_conditions_expand.setText("-");
				} else {
					terms_and_conditions_help_content.setVisibility(View.GONE);
					terms_and_conditions_expand.setText("+");
				}
				
			} else {
				sameView = false;
				terms_and_conditions_help_content.setVisibility(View.VISIBLE);
				terms_and_conditions_expand.setText("-");
				if (last_clicked_view != null && !sameView) {
					last_clicked_view.setVisibility(View.GONE);
					lastExpandView.setText("+");
				}
			}
			
			last_clicked_view = terms_and_conditions_help_content;
			lastExpandView = terms_and_conditions_expand;
			break;

            case R.id.app_version_help:

                if (last_clicked_view == app_version_content) {
                    sameView = true;
                    if (app_version_content.getVisibility() == View.GONE) {
                        app_version_content.setVisibility(View.VISIBLE);
                        app_version_expand.setText("-");
                    } else {
                        app_version_content.setVisibility(View.GONE);
                        app_version_expand.setText("+");
                    }

                } else {
                    sameView = false;
                    app_version_content.setVisibility(View.VISIBLE);
                    app_version_expand.setText("-");
                    if (last_clicked_view != null && !sameView) {
                        last_clicked_view.setVisibility(View.GONE);
                        lastExpandView.setText("+");
                    }
                }

                last_clicked_view = app_version_content;
                lastExpandView = app_version_expand;
                break;

		default:
			break;
			
			
		}
		
//		if (last_clicked_view != null && !sameView) {
//			last_clicked_view.setVisibility(View.GONE);
//		}
		
	}

}
