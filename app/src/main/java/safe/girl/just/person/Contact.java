package safe.girl.just.person;

/**
 * Created by 许灡珊 on 2016/7/23.
 */
public class Contact {
    private String name ;   //联系人姓名
    private String number ;       //联系人电话
    public Contact(String name,String number){
        this.name = name ;
        this.number = number ;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
    public String toString(){
        return this.name +"-" +number ;
    }
}
