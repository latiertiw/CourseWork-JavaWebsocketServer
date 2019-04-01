import java.util.ArrayList;

public class Client extends User {
    private ArrayList<Order> orders = new ArrayList<Order>();



    public int add_order(Order order) {
        for (int i = 0; i < orders.size(); i += 1) {
            if (order.getNumber() == orders.get(i).getNumber()) {
                return 0; //Заказ с таким номером уже есть
            }
        }
        orders.add(order);
        return 1;
    }

    public int remove_order(Order order){
        for(int i =0;i<orders.size();i += 1){
            if(orders.get(i).getNumber() == order.getNumber()){
                orders.remove(i);
                return 1;
            }
        }
        return 0;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }
}