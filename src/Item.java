public class Item {
    String title;
    String url;
    String phone;
    String username;
    int date;

    public Item(String title, String url, String phone, String username, int date) {
        this.title = title;
        this.url = url;
        this.phone = phone;
        this.username = username;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public int getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
