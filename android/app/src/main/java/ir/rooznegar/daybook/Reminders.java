package ir.rooznegar.daybook;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Notification;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;

/** زمان‌بندی یادآورهای روزانه با AlarmManager و نمایش اعلان. */
public class Reminders {
    static final String PREFS = "reminders", KEY_JSON = "json", CHANNEL = "rooznegar-reminders";
    static final String EXTRA_TEXT = "text", EXTRA_INDEX = "index";

    static void save(Context c, String json) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_JSON, json).apply();
        scheduleAll(c);
    }

    private static PendingIntent pending(Context c, int index, String text) {
        Intent i = new Intent(c, AlarmReceiver.class);
        i.setAction("ir.rooznegar.daybook.REMINDER." + index);
        i.putExtra(EXTRA_INDEX, index);
        i.putExtra(EXTRA_TEXT, text == null ? "" : text);
        return PendingIntent.getBroadcast(c, index, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static void scheduleAll(Context c) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences sp = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        // لغو زمان‌بندی‌های قبلی
        int old = sp.getInt("count", 0);
        for (int n = 0; n < old; n++) am.cancel(pending(c, n, ""));
        JSONArray arr;
        try { arr = new JSONArray(sp.getString(KEY_JSON, "[]")); } catch (Exception e) { arr = new JSONArray(); }
        int count = 0;
        for (int n = 0; n < arr.length(); n++) {
            try {
                JSONObject o = arr.getJSONObject(n);
                String[] hm = o.getString("time").split(":");
                int h = Integer.parseInt(hm[0]), m = Integer.parseInt(hm[1]);
                scheduleOne(c, am, n, h, m, o.optString("text", ""));
                count++;
            } catch (Exception ignored) { }
        }
        sp.edit().putInt("count", arr.length()).apply();
    }

    static void scheduleOne(Context c, AlarmManager am, int index, int h, int m, String text) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h); cal.set(Calendar.MINUTE, m);
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTimeInMillis() <= System.currentTimeMillis() + 1000) cal.add(Calendar.DAY_OF_YEAR, 1);
        PendingIntent pi = pending(c, index, text);
        if (Build.VERSION.SDK_INT >= 23) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        else am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context c, Intent in) {
            String text = in.getStringExtra(EXTRA_TEXT);
            if (text == null || text.trim().isEmpty()) text = "وقت کارهای امروزته 🌿";
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder b;
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel ch = new NotificationChannel(CHANNEL, "یادآورهای روزنگار", NotificationManager.IMPORTANCE_DEFAULT);
                nm.createNotificationChannel(ch);
                b = new Notification.Builder(c, CHANNEL);
            } else {
                b = new Notification.Builder(c);
            }
            Intent open = new Intent(c, MainActivity.class);
            open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent tap = PendingIntent.getActivity(c, 0, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            b.setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("روزنگار").setContentText(text)
             .setStyle(new Notification.BigTextStyle().bigText(text)).setContentIntent(tap).setAutoCancel(true);
            try { nm.notify(1000 + in.getIntExtra(EXTRA_INDEX, 0), b.build()); } catch (SecurityException ignored) { }
            // فردا دوباره
            Reminders.scheduleAll(c);
        }
    }

    public static class BootReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context c, Intent in) { Reminders.scheduleAll(c); }
    }
}
