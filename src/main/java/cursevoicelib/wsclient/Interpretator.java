package cursevoicelib.wsclient;

import java.util.ArrayList;
import java.util.List;

import org.java_websocket.handshake.ServerHandshake;

import cursevoicelib.helpers.GsonHelper;
import cursevoicelib.util.log.Log;
import cursevoicelib.wsclient.beans.JoinAnswerBean;
import cursevoicelib.wsclient.beans.Packet;
import cursevoicelib.wsclient.beans.ReceivedMessageBean;
import cursevoicelib.wsclient.events.factories.JoinFactory;
import cursevoicelib.wsclient.events.factories.MessageFactory;


public class Interpretator {
    private final Client mClient;
    private final List<ClientListener> mListeners = new ArrayList<ClientListener>();
    
    public Interpretator(Client client) {
        mClient = client;
    }
    
    public void addListener(ClientListener listener) {
        mListeners.add(listener);
    }
    
    public void removeListener(ClientListener listener) {
        mListeners.remove(listener);
    }
    
    public void interpretate(String fullJson) {
        try {
            Packet packet = GsonHelper.fromJson(fullJson, Packet.class);
            if (packet.TypeID == -635182161) {
                for (ClientListener l : mListeners) l.onMessage(MessageFactory.beanToMessage(mClient.getCurse(),
                                                        GsonHelper.fromJson(fullJson, ReceivedMessageBean.class)));
            } else if (packet.TypeID == -815187584) {
                for (ClientListener l : mListeners) l.onJoinResponse(JoinFactory.beanToEvent(mClient.getCurse(),
                                                        GsonHelper.fromJson(fullJson, JoinAnswerBean.class)));
            } else {
                Log.warn("Packet " + packet.TypeID + " not registered Please send the trace to the doc or my creator. Trace: " + fullJson);
            }
        } catch (Exception e) {
            Log.warn("Interpretate", e);
            for (ClientListener l : mListeners) {
                l.onError(e);
            }
        }
    }
    
    public void onClose(int code, String reason, boolean remote) {
        for (ClientListener l : mListeners) {
            l.onClose(code, reason, remote);
        }
    }
    
    public void onError(Exception ex) {
        for (ClientListener l : mListeners) {
            l.onError(ex);
        }
    }
    
    public void onOpen(ServerHandshake handshakedata) {
        for (ClientListener l : mListeners) {
            l.onOpen(handshakedata);
        }
    }
}

