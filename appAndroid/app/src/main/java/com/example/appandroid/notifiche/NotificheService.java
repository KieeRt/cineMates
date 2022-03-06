package com.example.appandroid.notifiche;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.appandroid.R;
import com.example.appandroid.listViewClass.notifica.Notifica;
import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.repository.Repository;

import com.example.appandroid.ui.main.MainActivity;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class NotificheService {
    // per le notifiche
    public static final String CHANNEL_ID = "NOTIFICHE_CHANNEL";
    public static NotificationCompat.Builder builder;
    public  static NotificheService notifiche;
    Context contex;



    private NotificheService(Context context){
        if(notifiche == null)
        createNotificationChannel(context);
        controllaNotifiche(context);
    }

    public static void initProviderNotifiche(Context context){
        if(notifiche == null){
            Log.i("Notifiche", "creato nuovo Provider Notifiche");
            notifiche = new NotificheService(context);
            notifiche.contex = context;
        }
    }


    private  void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void controllaNotifiche(Context context){
        final Handler handler = new Handler();
        Thread thread = new Thread (new Runnable() {
            public void run() {
                while(true){
                    Repository repository = Repository.getInstance();
                    try {
                        System.out.println("CURRENT THREAD: " + Thread.currentThread().getName());
                        List<Notifica> notificaList = repository.getNotificheNonConsegnate(Utente.getUtente().getEmail());
                        if(notificaList != null){
                            for( Notifica n : notificaList){
                                costruisciNuovaNotifica(n.getMessaggio(), context);
                                triggerNotifica(context, n.getIdNotifica());
                                repository.setConsegnataNotifica(n.getIdNotifica());
                            }
                        }
                        Thread.sleep(5000);
                    } catch (JSONException | TimeoutException | NullPointerException | InterruptedException e ) {
                        e.printStackTrace();
                        if(e instanceof  NullPointerException){
                            Log.i("controllaNotifiche", "Utente e' stato eliminato, chiudo il controllo delle notifiche");
                            notifiche = null;
                            return;
                        }
                    }

                }

               // handler.postDelayed(this, 10000);
            }
        });
        thread.start();
    }

    private void costruisciNuovaNotifica(String testo_notifica, Context context){

        Intent intent = new Intent(context, MainActivity.class);

        // informazioni recuperate successivamente dal MainActivity
        // applicazione ha una memoria del extra che viene assegnato, quindi anche cambiando MainActivity ricevera' i vecchi extra
        // per risolvere bisogna disinstallare l'applicazione
        intent.putExtra("notifica", "new");

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);



        builder = new NotificationCompat.Builder(context, CHANNEL_ID)// icona per notifica
                .setSmallIcon(R.drawable.ic_app)
                .setColor(ContextCompat.getColor(context, R.color.orange))
                .setLargeIcon(generateBitmapFromVectorDrawable(context, R.drawable.ic_app))
                .setContentTitle("Nuova notifica")
                .setContentText(testo_notifica)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }



    private void triggerNotifica(Context context, int idNotifica){
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    // notificationId is a unique int for each notification that you must define
    notificationManager.notify(CHANNEL_ID, idNotifica, builder.build());
    }

    public static void cancellaNotifiche(Context context){
        if(notifiche==null || notifiche.contex==null){
            return;
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(notifiche.contex);
        notificationManager.cancelAll();
    }


    public Bitmap generateBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}
