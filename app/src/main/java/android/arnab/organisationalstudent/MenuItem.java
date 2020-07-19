package android.arnab.organisationalstudent;

public class MenuItem
{
    private String imageUrl;
    private String name;
    private float price;
    private int availableState;

    public MenuItem(String imageUrl, String name, float price, int availableState)
    {
        this.imageUrl=imageUrl;
        this.name=name;
        this.price=price;
        this.availableState=availableState;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getAvailableState() {
        return availableState;
    }

    public void setAvailableState(int availableState) {
        this.availableState = availableState;
    }
}
