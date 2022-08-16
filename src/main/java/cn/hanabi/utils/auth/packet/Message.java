package cn.hanabi.utils.auth.packet;

public class Message {
    public String type;
    public String data;
    public Message(String Type,String Data) {
        this.type = Type;
        this.data = Data;
    }
}
