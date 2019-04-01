import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;
import com.google.gson.Gson;


public class AppControler {
    Administrator admin;

    private ArrayList <Client> clients = new ArrayList<Client>();
    private ArrayList <Expert> experts = new ArrayList<Expert>();
    private ArrayList <Goal> goals = new ArrayList<Goal>();
    Gson gson = new Gson();
    int variable;


    AppControler (){
        admin = new Administrator();
        //this.admin=admin;
        admin.setLogin("a");
        admin.setPassword("a");

        //Чтение
        String readedGoals;
        String readedClients;
        String readedExperts;
        FileReader readGoals = null;
        FileReader readClients = null;
        FileReader readExperts = null;
        try {
            readGoals = new FileReader("GoalsApp.txt");
            readClients = new FileReader("ClientsApp.txt");
            readExperts = new FileReader("ExpertsApp.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanGoals = new Scanner(readGoals);
        Scanner scanClients = new Scanner(readClients);
        Scanner scanExperts = new Scanner(readExperts);
        readedGoals = scanGoals.nextLine();
        readedClients = scanClients.nextLine();
        readedExperts = scanExperts.nextLine();

        Type itemsArrayListTypeGoals = new TypeToken<ArrayList<Goal>>() {}.getType();
        Type itemsArrayListTypeClients = new TypeToken<ArrayList<Client>>() {}.getType();
        Type itemsArrayListTypeExperts = new TypeToken<ArrayList<Expert>>() {}.getType();
        ArrayList<Goal> goalsFromFile = gson.fromJson(readedGoals, itemsArrayListTypeGoals);
        ArrayList<Client> clientsFromFile = gson.fromJson(readedClients, itemsArrayListTypeClients);
        ArrayList<Expert> expertFromFile = gson.fromJson(readedExperts, itemsArrayListTypeExperts);

        goals = goalsFromFile;
        clients = clientsFromFile;
        experts = expertFromFile;
        try {
            readGoals.close();
            readClients.close();
            readExperts.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int add_goal(Goal goal){
        for (int i = 0; i < goals.size(); i += 1){
            if(goal.getNumber()==goals.get(i).getNumber()){
                return 0;
            }
        }
        goals.add(goal);
        save_to_file();
        return 1;
    }

    public int remove_goal(int number){
        for(int i =0;i<goals.size();i += 1){
            if(goals.get(i).getNumber() == number){
                goals.remove(i);
                save_to_file();
                return 1;
            }
        }
       return 0;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public Goal allow_redact(int number){
        variable = number;
        for(int i =0;i<goals.size();i += 1){
            if(goals.get(i).getNumber() == number){
                return goals.get(i);
            }
        }
        return null;
    }

    public int redact_goal(Goal goal){
        int counter = 0;
        int position = 0;

        if(goal.getNumber() == variable) {
            for (int i = 0; i < goals.size(); i += 1) { if (goals.get(i).getNumber() == variable) counter = i; }
            goals.set(counter, goal);
            save_to_file();
            return 1;
        }
        else{
            for(int i = 0; i < goals.size();i += 1){
                if (goals.get(i).getNumber() == variable) position = i;
                break;
            }

            for(int i = 0; i < goals.size();i += 1){
                if (goals.get(i).getNumber() == goal.getNumber()) counter += 1;
            }

            if(counter>0){
               return 0;
            }
            else{
                goals.set(position, goal);
                save_to_file();
                return 1;
            }
        }
    }

    public void save_to_file(){

        FileWriter writeGoals = null;
        FileWriter writeClients = null;
        FileWriter writeExperts = null;
        try {
            writeGoals = new FileWriter( "GoalsApp.txt" );
            writeClients = new FileWriter( "ClientsApp.txt" );
            writeExperts = new FileWriter( "ExpertsApp.txt" );
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writeGoals.write(this.gson.toJson(goals));
            writeClients.write(this.gson.toJson(clients));
            writeExperts.write(this.gson.toJson(experts));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writeGoals.close();
            writeClients.close();
            writeExperts.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public int add_order(Order order){
        int var = 0;
        for (int i = 0; i < goals.size(); i += 1){
            if(goals.get(i).getNumber() == order.getNumber()){
                var += 1;
            }
        }
        if(var==0) return 0;

        for (int i = 0; i < clients.size(); i += 1){
            if(clients.get(i).getLogin().equals(order.getName())){
                int res = clients.get(i).add_order(order);
                if(res == 0) return 1;
                else{
                    save_to_file();
                    return 2;}// Успешное добавление
            }
        }
        return 1;
        }

    public int remove_order(Order order){

        for(int i = 0; i < clients.size();i += 1){
            if(clients.get(i).getLogin().equals(order.getName())){
                int var = clients.get(i).remove_order(order);
                if(var == 0) return 0;
                else { save_to_file();
                    return 1;}
            }
        }
        return 0;
    }

    public ArrayList<Order> getOrdersData(String login){
        for(int i = 0; i < clients.size();i += 1){
            if(clients.get(i).getLogin().equals(login)){

                ArrayList<Order> orders = clients.get(i).getOrders();
                for(int j = 0; j < orders.size();j+=1){
                    for (int k =0;k<goals.size();k+=1){
                        if(orders.get(j).getNumber()==goals.get(k).getNumber()){
                            orders.get(j).setName(goals.get(k).getName());
                        }
                    }
                }
                return orders;
            }
        }


        return null;
    }






    public int addClient(Client client){
        for (int i = 0; i < clients.size(); i += 1){
            if(client.getLogin().equals(clients.get(i).getLogin())){
                return 0;
            }
        }
        clients.add(client);
        save_to_file();
        return 1;
    }

    public int addExpert(Expert expert){
        for (int i = 0; i < experts.size(); i += 1){
            if(expert.getLogin().equals(experts.get(i).getLogin())){
                return 0;
            }
        }
        experts.add(expert);
        save_to_file();
        return 1;
    }

    public int login(String login,String password){
        if(login.equals(admin.getLogin()) && password.equals(admin.getPassword())){
            return 1;
        }
        else {
            for (int i = 0; i < clients.size(); i += 1){
                if(login.equals(clients.get(i).getLogin()) && password.equals(clients.get(i).getPassword())){
                    return 2;
                }
            }
            for (int i = 0; i < experts.size(); i += 1){
                if(login.equals(experts.get(i).getLogin()) && password.equals(experts.get(i).getPassword())){
                    return 3;
                }
            }
           return 0;
        }
    }


    public ArrayList<Client> getClients() {
        return clients;
    }

    public int addMark(Mark mark) {
        int counter = 0;
        for(int i = 0;i < goals.size();i += 1){
            if(goals.get(i).getNumber()==mark.getNumber()) counter += 1;
        }
        if (counter==0) return 0;


        for (int i = 0; i < experts.size(); i += 1) {
            if (experts.get(i).getLogin().equals(mark.getName())) {
                experts.get(i).addMark(mark);
                save_to_file();
                break;
            }
        }
    return 1;
    }

    GoalInfo[] optTask(){

        GoalInfo[] info = new GoalInfo[goals.size()];

        int value;
        int valueAll = 0;


        for(int i = 0; i < goals.size();i += 1){
            GoalInfo obj = new GoalInfo();
            info[i] = obj;
            info[i].setNumber(goals.get(i).getNumber());
            info[i].setName(goals.get(i).getName());
            value = 0;

            for(int j = 0; j <experts.size();j += 1){


                for(int k = 0; k <experts.get(j).getMarks().size();k+=1){
                    if(goals.get(i).getNumber()==experts.get(j).getMarks().get(k).getNumber()){
                        value += experts.get(j).getMarks().get(k).getScore();
                    }
                }


            }

            info[i].setValue(value);
            valueAll += value;

        }


        if(valueAll>0) {
            for (int i = 0; i < goals.size(); i += 1) {
                info[i].setValue((info[i].getValue()*100) / valueAll);
            }
        }


        return info;
    }

}
