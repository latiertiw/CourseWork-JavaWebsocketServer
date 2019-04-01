import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;



import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebServlet(name = "WsServlet", urlPatterns = {"/ws"})
public class WsServlet extends WebSocketServlet {
    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest httpServletRequest) {

        try {
            return new WsConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


