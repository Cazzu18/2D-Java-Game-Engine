import jade.Window;

public class Main {

    public static void main(String[] args){
        Window window = Window.get(); //singleton pattern
        window.run();
    }
}
