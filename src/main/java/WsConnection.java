import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import java.io.*;
import java.lang.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;


public class WsConnection extends MessageInbound {

    public static ArrayBlockingQueue<WsOutbound> connections = new ArrayBlockingQueue<WsOutbound>(100);
    private WsOutbound outbound;



    Gson gson = new Gson();

    AppControler server = new AppControler();







    WsConnection() throws IOException {



    }


    @Override protected void onBinaryMessage(ByteBuffer byteBuffer) throws IOException {}
    @Override
    protected void onTextMessage(CharBuffer charBuffer) throws IOException {

        Packet packet = gson.fromJson(charBuffer.toString(), Packet.class);
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
                send(gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "login_admin";
                packet.data = user.getLogin();
                send(gson.toJson(packet));
            }
            else if(res == 2){
                packet.key = "login_client";
                packet.data = user.getLogin();
                send(gson.toJson(packet));
            }
            else if(res == 3){
                packet.key = "login_expert";
                packet.data = user.getLogin();
                send(gson.toJson(packet));
            }

        }

        else if (key.equals("registration_client")){
            Client client = gson.fromJson(json, Client.class);
            int res = server.addClient(client);
            if (res == 0){
                packet.key = "wrong_registration_data";
                packet.data = "Такой пользователь уже существует";
                send(gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_registration";
                packet.data = client.getLogin();
                send(gson.toJson(packet));
            }
        }

        else if (key.equals("registration_expert")){
            Expert expert = gson.fromJson(json, Expert.class);
            int res = server.addExpert(expert);
            if (res == 0){
                packet.key = "wrong_registration_data";
                packet.data = "Такой пользователь уже существует";
                send(gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_registration";
                packet.data = expert.getLogin();
                send(gson.toJson(packet));
            }
        }

        else if (key.equals("add_goal")){
            Goal goal = gson.fromJson(json, Goal.class);
            int res = server.add_goal(goal);
            if(res == 0){
                packet.key = "wrong_add_goal";
                packet.data = "Цель с таким номером уже существует";
                send(gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_add_goal";
                packet.data = "successful_add_goal";
                send(gson.toJson(packet));
            }
        }

        else if (key.equals("goal_request")){
            packet.key = "goals_data";
            packet.data = gson.toJson(server.getGoals());
            send(gson.toJson(packet));
        }

        else if (key.equals("delete_goal")){
            int number = gson.fromJson(json,Integer.class);
            int res = server.remove_goal(number);
            if(res == 0){
                packet.key = "wrong_delete_goal";
                packet.data = "wrong_delete_goal";
                send(gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_delete_goal";
                packet.data = "successful_delete_goal";
                send(gson.toJson(packet));
            }
        }

        else if (key.equals("toggle_redact")){
            int number = gson.fromJson(json,Integer.class);
            Goal res = server.allow_redact(number);
            if (res == null){
                packet.key = "wrong_toggle_redact";
                packet.data = "wrong_toggle_redact";
                send(gson.toJson(packet));
            }
            else{
                packet.key = "successful_toggle_redact";
                packet.data = gson.toJson(res);
                send(gson.toJson(packet));
            }
        }

        else if (key.equals("redact_goal")){

            Goal goal = gson.fromJson(json, Goal.class);
            int res = server.redact_goal(goal);

            if(res == 1){
                packet.key = "successful_redact";
                packet.data = "successful_redact";
                send(gson.toJson(packet));
            }
            else if(res == 0){
                packet.key = "wrong_redact";
                packet.data = "wrong_redact";
                send(gson.toJson(packet));
            }
        }

        else if (key.equals("add_order")){
            Order order = gson.fromJson(json, Order.class);
            int res = server.add_order(order);
            if(res == 0){
                packet.key = "wrong_add_order";
                packet.data = "Цели с таким номером не существует";
                send(gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "wrong_add_order_alt";
                packet.data = "Заказ с таким номером уже есть";
                send(gson.toJson(packet));
            }
            else if(res == 2){
                packet.key = "successful_add_order";
                packet.data = "Успешное добавление заказа";
                send(gson.toJson(packet));
            }
        }

        else if (key.equals("delete_order")){
            Order order = gson.fromJson(json, Order.class);
            int res = server.remove_order(order);
            if(res == 0){
                packet.key = "wrong_delete_order";
                packet.data = "wrong_delete_order";
                send(gson.toJson(packet));
            }
            else if(res == 1){
                packet.key = "successful_delete_order";
                packet.data = "successful_delete_order";
                send(gson.toJson(packet));
            }
        }

        else if (key.equals("orders_request")){
            packet.key = "orders_data";
            String data = gson.fromJson(packet.data,String.class);
            packet.data = gson.toJson(server.getOrdersData(data));

            send(gson.toJson(packet));
        }

        else if (key.equals("orders_admin_request")){
            packet.key = "orders_admin_data";
            packet.data = gson.toJson(server.getClients());
            send(gson.toJson(packet));
        }

        else if (key.equals("add_mark")){
            Mark mark = gson.fromJson(json, Mark.class);
            int res = server.addMark(mark);
            if(res == 1){
                packet.key = "successful_add_mark";
                packet.data = "Оценка успешно добавлена";
                send(gson.toJson(packet));
            }
            else if(res == 0){
                packet.key = "wrong_add_mark";
                packet.data = "Такой цели не существует";
                send(gson.toJson(packet));
            }

        }

        else if (key.equals("opt_task_request")){

            packet.key = "opt_task";
            packet.data = gson.toJson(server.optTask());
            send(gson.toJson(packet));
        }

        else if (key.equals("test")){
            packet.key = "test";
            packet.data = "test";
            send(gson.toJson(packet));
        }




        // send(charBuffer.toString());
    }

    @Override
    protected void onOpen(WsOutbound outbound) {
        System.out.println("CONNECTED");
        this.outbound = outbound;
        connections.add(outbound);
    }

    @Override
    protected void onClose(int status) {
        System.out.println("CLOSED");
        connections.remove(this.outbound);
    }

    private void send(String message){
        CharBuffer buffer = CharBuffer.wrap(message);
        try {
            this.outbound.writeTextMessage(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (WsOutbound connection : connections) {
            try {
                CharBuffer buffer = CharBuffer.wrap(message);
                connection.writeTextMessage(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
