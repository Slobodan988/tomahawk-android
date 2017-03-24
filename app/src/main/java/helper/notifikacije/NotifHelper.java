package helper.notifikacije;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Misko on 10/19/16.
 */



public class NotifHelper {


    static final String NotifHelper_TAG = "NotifHelper_TAG";

    static boolean logNotifHelperActivity = false;
    static boolean testNotif = false;

    public static void squeduleNotif(Context con,  NOTIF_TYPE notif_type) {



        LogNotifHelper("pozvana funkcija squeduleNotif za notifikaciju tipa " + notif_type.toString());


        long danUMiliSekundama = 24 * 3600 * 1000;

        Calendar currentTime = Calendar.getInstance();

        Calendar tempTime = Calendar.getInstance();


        if (testNotif)
        {

            LogNotifHelper("----> testNotif je aktivan");
            LogNotifHelper("----> staviti testNotif na false pre builda");

            if (notif_type == NOTIF_TYPE.FRIDAY_NOTIF) {



                tempTime.add(Calendar.MILLISECOND, 20000);
            }
            else if (notif_type == NOTIF_TYPE.SATURDAY_NOTIF)
            {
                tempTime.add(Calendar.MILLISECOND, 40000);
            }





        }
        else
        {

            if (notif_type == NOTIF_TYPE.FRIDAY_NOTIF) {

                tempTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            }
            else if (notif_type == NOTIF_TYPE.SATURDAY_NOTIF)
            {
                tempTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            }


            tempTime.set(Calendar.HOUR_OF_DAY, 19);
            tempTime.set(Calendar.MINUTE, 0);
            tempTime.set(Calendar.SECOND, 0);


        }


        LogNotifHelper("vrsi se provera termina za notifikaciju");


        if ((tempTime.getTimeInMillis() - currentTime.getTimeInMillis()) <= 0)
        {

            tempTime.add(Calendar.DATE, 7);
            LogNotifHelper( "konkretan termin je prosao, zakazi za taj termin u sledecoj sedmici");

        }
//        else if ((tempTime.getTimeInMillis() - currentTime.getTimeInMillis()) < danUMiliSekundama) {
//
//            tempTime.add(Calendar.DATE, 7);
//              LogNotifHelper(
//                    "nije prosao termin ali je vreme manje od 24h zakazi za taj termin u sledecoj sedmici");
//        }
        else
        {

            LogNotifHelper("termin je odgovarajuci, zakazi notifikaciju");
        }


        NotifObject notifObject = new NotifObject(notif_type, tempTime);

        ZakaziNotifikaciju(con, notifObject);

    }



    public enum NOTIF_TYPE {
        FRIDAY_NOTIF(123456),
        SATURDAY_NOTIF(7891011);


        private int intValue;

        NOTIF_TYPE( int value)
        {

            intValue = value;
        }


        public int getIntValue() {
            return intValue;
        }
    }

    public static class NotifObject
    {

        NOTIF_TYPE notif_type;
        Calendar date;

        public NotifObject(NOTIF_TYPE notif_type, Calendar date) {
            this.notif_type = notif_type;
            this.date = date;
        }

        public Calendar getDate() {
            return date;
        }

        public NOTIF_TYPE getNotif_type() {
            return notif_type;
        }
    }

    public static void unsqueduleNotif(Context con, NOTIF_TYPE notifType)
    {

        LogNotifHelper( "Otkazuje prethodnu notifikaciju tipa " + notifType.toString());

        Intent intent = new Intent(con, NotifReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                con, notifType.getIntValue(), intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) con.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);


    }

     static void ZakaziNotifikaciju(Context con, NotifObject notifObject) {

         unsqueduleNotif(con, notifObject.getNotif_type());


         LogNotifHelper(  "zakazuje notifikaciju tipa " + notifObject.getNotif_type().toString());

         DateFormat df = new SimpleDateFormat("EEE yyyy/MM/dd HH:mm:ss");
         LogNotifHelper( "Vreme za zakazivanje: " + df.format(notifObject.getDate().getTime()));



        Intent intent = new Intent(con, NotifReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                con, notifObject.getNotif_type().getIntValue(), intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) con.getSystemService(ALARM_SERVICE);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifObject.getDate().getTimeInMillis(),
                    pendingIntent);

        }
        else
        {


            alarmManager.set(AlarmManager.RTC_WAKEUP, notifObject.getDate().getTimeInMillis(),
                    pendingIntent);
        }



    }

    static void LogNotifHelper(String message)
    {
        if(logNotifHelperActivity) {
            Log.i(NotifHelper_TAG, message);
        }
    }

}
