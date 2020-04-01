package server.sessions;

import com.artem.server.api.HttpSession;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Session implements HttpSession {

    private Map<String, Object> parameters = new HashMap<>();

    private Date lastRequestTime;

    public Session() {
    }

    @Override
    public void addParameters(String string, Object object) {
        parameters.put(string, object);
    }

    @Override
    public Object getParameters(String string) {
        return parameters.get(string);
    }

    @Override
    public Date getLastRequestTime() {
        return lastRequestTime;
    }

    @Override
    public void setLastRequestTime(Date lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }
}
