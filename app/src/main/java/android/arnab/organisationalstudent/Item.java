package android.arnab.organisationalstudent;

public class Item {

    private String mtxt1;
    private String mtxt2;
    private String mimageres;
    private int availableState;

    public Item(String imageres, String txt1, String txt2, int availableState){
        mimageres =imageres;
        mtxt1 = txt1;
        mtxt2 = txt2;
        this.availableState=availableState;
    }

    public String getMimageres() {
        return mimageres;
    }

    public String getMtxt1() {
        return mtxt1;
    }

    public String getMtxt2() {
        return mtxt2;
    }

    public int getAvailableState() {
        return availableState;
    }
}