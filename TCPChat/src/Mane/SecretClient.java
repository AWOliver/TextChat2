package Mane;

//inherits from client and gives special ability
public class SecretClient extends Client{

    //creates a special message with reciever
    public static String secretMessage( String reciever){
        String sMessage = "This is just for " + reciever;
        System.out.println(sMessage);
        return sMessage;
    }
}
