package helper.notifikacije;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;


import org.tomahawk.tomahawk_android.R;
import org.tomahawk.tomahawk_android.activities.TomahawkMainActivity;

import java.util.Random;


public class NotifReceiver extends BroadcastReceiver {

@Override
public void onReceive(Context context, Intent intent)
{
	

	
	//odredjivanje id-ja od 1 do 5
	int max=6;
	int min=1;
	Random r = new Random();
	int notif_id = r.nextInt(max - min) + min;
	String tekst_notif= context.getResources().getString(R.string.notif_text1);

	switch (notif_id) {
	case 1:
		tekst_notif=context.getResources().getString(R.string.notif_text1);
		break;
	case 2:
		tekst_notif=context.getResources().getString(R.string.notif_text2);
		break;
	case 3:
		tekst_notif=context.getResources().getString(R.string.notif_text3);
		break;

	case 4:
		tekst_notif=context.getResources().getString(R.string.notif_text4);
		break;
	case 5:
		tekst_notif=context.getResources().getString(R.string.notif_text5);
		break;

	default:
		break;
	}

	//change [APP NAME] whit real app_name


	tekst_notif = tekst_notif.replace("{{APP_NAME}}", context.getString(R.string.app_name));


	
	final Intent notifintent = new Intent(context, TomahawkMainActivity.class);
	notifintent.putExtra("notifikacija_id", notif_id);
	
	PendingIntent pendingIntent = PendingIntent.getActivity(context,notif_id, notifintent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);
	
	
	NotificationCompat.Builder mBuilder =
		    new NotificationCompat.Builder(context)
			.setAutoCancel(true)
		    .setSmallIcon(R.drawable.ic_launcher)
		    .setContentTitle(context.getResources().getString(R.string.app_name))
		    .setContentText(tekst_notif)
		    .setContentIntent(pendingIntent); //Required on Gingerbread and below
	
	mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	notificationManager.notify(notif_id, mBuilder.build());

	//opaljena notifikacija
	//zakazi za sledeci
	

	
}
}