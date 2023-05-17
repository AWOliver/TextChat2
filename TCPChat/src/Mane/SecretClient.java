package Mane;

public class SecretClient extends Client{

    public static String secretMessage( String reciever){
        String sMessage = "This is just for " + reciever;
        System.out.println(sMessage);
        return sMessage;
    }
}
