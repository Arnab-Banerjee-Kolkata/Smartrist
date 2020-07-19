package android.arnab.organisationalstudent;

public class TranItem {
    private String mtxt1, mdate, mtime;
    private int mbalance, mchange;

    public TranItem(String txt1, String date, String time, int balance, int change) {
        mtxt1 = txt1;
        mdate = date;
        mtime = time;
        mbalance = balance;
        mchange = change;
    }

    public String getMdate() {
        return mdate;
    }

    public String getMtxt1() {
        return mtxt1;
    }

    public String getMtime() {
        return mtime;
    }

    public int getMbalance() { return mbalance; }

    public int getMchange() { return mchange; }
}
