package joaosa.notificareandroidtest;

import android.net.Uri;

import re.notifica.Notificare;
import re.notifica.push.gcm.DefaultIntentReceiver;

public class Receiver extends DefaultIntentReceiver {

    @Override
    public void onReady() {
        Notificare.shared().enableNotifications();
    }

    @Override
    public void onActionReceived(Uri target) {
        super.onActionReceived(target);
    }
}
