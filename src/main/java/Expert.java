import java.util.ArrayList;

public class Expert extends User {
    private ArrayList<Mark> marks = new ArrayList<Mark>();

    public void addMark(Mark mark){
        for(int i = 0; i <marks.size();i+=1){
            if(marks.get(i).getNumber()==mark.getNumber()){
                marks.get(i).setNumber(mark.getNumber());
                marks.get(i).setScore(mark.getScore());
                marks.get(i).setName(mark.getName());
                return;
            }
        }
        marks.add(mark);
    }

    public ArrayList<Mark> getMarks() {
        return marks;
    }
}
