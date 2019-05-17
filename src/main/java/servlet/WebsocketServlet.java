import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.CharBuffer;

import com.google.gson.Gson;

@ServerEndpoint("/app")
public class WebsocketServlet {

    @OnMessage
    public void onMessage(Session session, String message)
    {   Gson gson = new Gson();
        AppControler server = new AppControler();

        Packet packet = gson.fromJson(message, Packet.class);
        String key = packet.key;
        String json = packet.data;

        if (key.equals("login")){
            User user = gson.fromJson(json, User.class);
            String login = user.getLogin();
            String password = user.getPassword();
            int res = server.login(login,password);
            if(res == 0){
                packet.key = "wrong_login_data";
                packet.data = "Неправильное имя пользователя или пароль";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "login_admin";
                packet.data = user.getLogin();
                send(session,gson.toJson(packet));
            }
            else if(res == 2){
                packet.key = "login_client";
                packet.data = user.getLogin();
                System.out.println("Авторизирован клиент: "+login);
                send(session,gson.toJson(packet));
            }
            else if(res == 3){
                packet.key = "login_expert";
                packet.data = user.getLogin();
                System.out.println("Авторизирован эксперт: "+login);
                send(session,gson.toJson(packet));
            }

        }

        else if (key.equals("registration_client")){
            Client client = gson.fromJson(json, Client.class);
            int res = server.addClient(client);
            if (res == 0){
                packet.key = "wrong_registration_data";
                packet.data = "Такой пользователь уже существует";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_registration";
                packet.data = client.getLogin();
                send(session,gson.toJson(packet));
            }
        }

        else if (key.equals("registration_expert")){
            Expert expert = gson.fromJson(json, Expert.class);
            int res = server.addExpert(expert);
            if (res == 0){
                packet.key = "wrong_registration_data";
                packet.data = "Такой пользователь уже существует";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_registration";
                packet.data = expert.getLogin();
                send(session,gson.toJson(packet));
            }
        }

        else if (key.equals("add_goal")){
            Goal goal = gson.fromJson(json, Goal.class);
            int res = server.add_goal(goal);
            if(res == 0){
                packet.key = "wrong_add_goal";
                packet.data = "Цель с таким номером уже существует";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_add_goal";
                packet.data = "successful_add_goal";
                send(session,gson.toJson(packet));
            }
        }

        else if (key.equals("goal_request")){
            packet.key = "goals_data";
            packet.data = gson.toJson(server.getGoals());
            send(session,gson.toJson(packet));
        }

        else if (key.equals("delete_goal")){
            int number = gson.fromJson(json,Integer.class);
            int res = server.remove_goal(number);
            if(res == 0){
                packet.key = "wrong_delete_goal";
                packet.data = "wrong_delete_goal";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_delete_goal";
                packet.data = "successful_delete_goal";
                send(session,gson.toJson(packet));
            }
        }

        else if (key.equals("toggle_redact")){
            int number = gson.fromJson(json,Integer.class);
            Goal res = server.allow_redact(number);
            if (res == null){
                packet.key = "wrong_toggle_redact";
                packet.data = "wrong_toggle_redact";
                send(session,gson.toJson(packet));
            }
            else{
                packet.key = "successful_toggle_redact";
                packet.data = gson.toJson(res);
                send(session,gson.toJson(packet));
            }
        }

        else if (key.equals("redact_goal")){

            Goal goal = gson.fromJson(json, Goal.class);
            int res = server.redact_goal(goal);

            if(res == 1){
                packet.key = "successful_redact";
                packet.data = "successful_redact";
                send(session,gson.toJson(packet));
            }
            else if(res == 0){
                packet.key = "wrong_redact";
                packet.data = "wrong_redact";
                send(session,gson.toJson(packet));
            }
        }

        else if (key.equals("add_order")){
            Order order = gson.fromJson(json, Order.class);
            int res = server.add_order(order);
            if(res == 0){
                packet.key = "wrong_add_order";
                packet.data = "Цели с таким номером не существует";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "wrong_add_order_alt";
                packet.data = "Заказ с таким номером уже есть";
                send(session,gson.toJson(packet));
            }
            else if(res == 2){
                packet.key = "successful_add_order";
                packet.data = "Успешное добавление заказа";
                send(session,gson.toJson(packet));
            }
        }

        else if (key.equals("delete_order")){
            Order order = gson.fromJson(json, Order.class);
            int res = server.remove_order(order);
            if(res == 0){
                packet.key = "wrong_delete_order";
                packet.data = "wrong_delete_order";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_delete_order";
                packet.data = "successful_delete_order";
                send(session,gson.toJson(packet));
            }
        }

        else if (key.equals("orders_request")){
            packet.key = "orders_data";
            String data = gson.fromJson(packet.data,String.class);
            packet.data = gson.toJson(server.getOrdersData(data));

            send(session,gson.toJson(packet));
        }

        else if (key.equals("orders_admin_request")){
            packet.key = "orders_admin_data";
            packet.data = gson.toJson(server.getClients());
            send(session,gson.toJson(packet));
        }

        else if (key.equals("orders_expert_request")){
            packet.key = "orders_expert_data";
            packet.data = gson.toJson(server.getClients());
            send(session,gson.toJson(packet));
        }

        else if (key.equals("add_mark")){
            Mark mark = gson.fromJson(json, Mark.class);
            int res = server.addMark(mark);
            if(res == 1){
                packet.key = "successful_add_mark";
                packet.data = "Оценка успешно добавлена";
                send(session,gson.toJson(packet));
            }
            else if(res == 0){
                packet.key = "wrong_add_mark";
                packet.data = "Такой цели не существует";
                send(session,gson.toJson(packet));
            }

        }

        else if (key.equals("delete_real_goal")){

            int number = gson.fromJson(json,Integer.class);
            int res = server.delete_main_goal(number);
            if(res == 0){
                packet.key = "wrong_delete_real_goal";
                packet.data = "Цель с таким номером уже существует";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_delete_real_goal";
                packet.data = "successful_delete_real_goal";
                send(session,gson.toJson(packet));
            }

        }

        else if (key.equals("add_real_goal")){

            MainGoal goal = gson.fromJson(json, MainGoal.class);
            int res = server.add_main_goal(goal);
            if(res == 0){
                packet.key = "wrong_add_real_goal";
                packet.data = "Цель с таким номером уже существует";
                send(session,gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_add_real_goal";
                packet.data = "successful_add_real_goal";
                send(session,gson.toJson(packet));
            }

        }

        else if (key.equals("real_goals_request")){
            packet.key = "real_goals_data";
            packet.data = gson.toJson(server.getRealGoals());
            send(session,gson.toJson(packet));
        }

        else if (key.equals("opt_task_request")){

            packet.key = "opt_task";
            packet.data = gson.toJson(server.optTask());
            send(session,gson.toJson(packet));
        }

        else if (key.equals("test")){
            packet.key = "test";
            packet.data = "test";
            send(session,gson.toJson(packet));
        }
    }

    @OnOpen
    public void onOpen(){
        System.out.println("Открыто соединение");
    }

    @OnClose
    public void onClose(){
        System.out.println("Закрыто соединение");
    }



    private void send(Session session, String message){
        CharBuffer buffer = CharBuffer.wrap(message);
        try {
            // this.outbound.writeTextMessage(buffer);
            session.getBasicRemote().sendText(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
