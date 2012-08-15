package com.sloy.jbnotif;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

@SuppressLint("NewApi")
public class TypeFragment extends Fragment {

	private Button mDefault, mBigText, mInbox, mBigPicture, mRandom, mOld;

	private MainActivity mContext;

	private Randomizer mRandomizer;

	private Switch mButtonsEnabled;
	
	private EditText mNumberBox;

	private RadioGroup mButtonsGroup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_types, container, false);

		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Notification notif = null;
				Notification.Builder builder = new Notification.Builder(mContext);

				// If random, add random buttons and take a random type
				if (v.getId() == R.id.type_random) {
					setRandomButtons(builder);
					switch (new Random().nextInt(4)) {
					case 0:
						// default
						notif = getDefaultNotification(builder);
						break;
					case 1:
						// big text
						notif = getBigTextStyle(builder);
						break;
					case 2:
						// big picture
						notif = getBigPictureStyle(builder);
						break;
					case 3:
						// inbox
						notif = getInboxStyle(builder);
						break;
					}
				} else {
					// Set selected buttons
					setButtons(builder, null);
					// And the selected type
					switch (v.getId()) {
					case R.id.type_big_text:
						notif = getBigTextStyle(builder);
						break;
					case R.id.type_inbox:
						notif = getInboxStyle(builder);
						break;
					case R.id.type_big_picture:
						notif = getBigPictureStyle(builder);
						break;
					case R.id.type_old:
						notif = getOldNotification();
						break;
					default:
						notif = getDefaultNotification(builder);
						break;
					}
				}

				notif.number = getCount();
				mContext.sendNotification(notif);
			}

		};

		// Version independent
		mDefault = (Button) v.findViewById(R.id.type_default);
		mOld = (Button) v.findViewById(R.id.type_old);
		mDefault.setOnClickListener(listener);
		mOld.setOnClickListener(listener);
		mNumberBox = (EditText) v.findViewById(R.id.number);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			// Jelly Bean only
			mRandom = (Button) v.findViewById(R.id.type_random);
			mBigText = (Button) v.findViewById(R.id.type_big_text);
			mInbox = (Button) v.findViewById(R.id.type_inbox);
			mBigPicture = (Button) v.findViewById(R.id.type_big_picture);

			mButtonsEnabled = (Switch) v.findViewById(R.id.type_buttons_switch);
			mButtonsGroup = (RadioGroup) v.findViewById(R.id.type_buttons_group);

			mBigText.setOnClickListener(listener);
			mInbox.setOnClickListener(listener);
			mBigPicture.setOnClickListener(listener);
			mRandom.setOnClickListener(listener);

		}

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = (MainActivity) getActivity();

		mRandomizer = new Randomizer(mContext);
	}
	
	public final int getCount() {
		try {
			return Integer.parseInt(mNumberBox.getText().toString(), 10);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	@SuppressWarnings("deprecation")
	private Notification getDefaultNotification(Notification.Builder builder) {
		builder
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setNumber(getCount())
				.setContentTitle("Default notification")
				.setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
				.setContentInfo("Info")
				.setLargeIcon(mRandomizer.getRandomImage());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			// Yummy jelly beans
			return builder.build();
		} else {
			return builder.getNotification();
		}

	}

	private Notification getBigTextStyle(Notification.Builder builder) {
		builder
				.setContentTitle("Reduced BigText title")
				.setContentText("Reduced content")
				.setContentInfo("Info")
				.setNumber(getCount())
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(mRandomizer.getRandomImage());

		return new Notification.BigTextStyle(builder)
				.bigText(getResources().getString(R.string.big_text))
				.setBigContentTitle("Expanded BigText title")
				.setSummaryText("Summary text")
				.build();
	}

	private Notification getBigPictureStyle(Notification.Builder builder) {
		// In this case the icon in reduced mode will be the same as the picture
		// when expanded.
		// And when expanded, the icon will be another one.
		Bitmap large = mRandomizer.getRandomImage();
		Bitmap notSoLarge = mRandomizer.getRandomImage();
		builder
				.setContentTitle("Reduced BigPicture title")
				.setContentText("Reduced content")
				.setContentInfo("Info")
				.setNumber(getCount())
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(large);

		return new Notification.BigPictureStyle(builder)
				.bigPicture(large)
				.bigLargeIcon(notSoLarge)
				.setBigContentTitle("Expanded BigPicture title")
				.setSummaryText("Summary text")
				.build();
	}

	private Notification getInboxStyle(Notification.Builder builder) {
		builder
				.setContentTitle("Reduced Inbox title")
				.setContentText("Reduced content")
				.setContentInfo("Info")
				.setNumber(getCount())
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(mRandomizer.getRandomImage());

		Notification.InboxStyle n = new Notification.InboxStyle(builder)
				.setBigContentTitle("Expanded Inbox title")
				.setSummaryText("Summary text");

		// Add 10 lines
		for (int i = 0; i < 10; i++) {
			n.addLine("This is the line n=" + (i + 1));
		}

		return n.build();
	}

	private Notification getOldNotification() {
		Notification notif = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
		notif.number = getCount();
		notif.setLatestEventInfo(mContext, "Old title", "Old notification content text", PendingIntent.getActivity(mContext, 0, new Intent(), 0));
		return notif;
	}

	private void setRandomButtons(Notification.Builder builder) {
		setButtons(builder, new Random().nextInt(4));
	}

	private void setButtons(Notification.Builder builder, Integer buttons) {
		// Buttons only in Jelly Bean
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			// Get number of buttons
			if (buttons == null) {
				// If not specified, check the input
				buttons = 0;
				if (mButtonsEnabled.isChecked()) {
					switch (mButtonsGroup.getCheckedRadioButtonId()) {
					case R.id.radio0:
						buttons = 1;
						break;
					case R.id.radio1:
						buttons = 2;
						break;
					case R.id.radio2:
						buttons = 3;
						break;
					}
				}
			}
			// Add as many buttons as you have to
			PendingIntent intent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
			for (int i = 0; i < buttons; i++) {
				builder.addAction(mRandomizer.getRandomIconId(), "Action " + (i + 1), intent);
			}
		}
	}

}
