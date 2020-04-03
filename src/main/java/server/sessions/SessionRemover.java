package server.sessions;

import com.artem.server.api.HttpSession;

import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

public class SessionRemover extends TimerTask {

    private Map<String, HttpSession> sessions;
    private long sessionLifeTime;

    public SessionRemover(Map<String, HttpSession> sessions, long sessionLifeTime){
        this.sessions = sessions;
        this.sessionLifeTime = sessionLifeTime;
    }

    @Override
    public void run() {
        Date nowTime = new Date();
        for (Map.Entry entry: sessions.entrySet()) {
            String key = (String) entry.getKey();
            HttpSession value = (HttpSession) entry.getValue();
            Date lastRequestTime = value.getLastRequestTime();
            if (Math.abs(lastRequestTime.getTime() - nowTime.getTime()) > sessionLifeTime) {
                sessions.remove(key);
            }
        }
    }


}
