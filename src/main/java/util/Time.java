package util;

public class Time {
    public static float timeStarted = System.nanoTime();  //static variables initialized at application startup

    public static float getTime(){
        return (float)((System.nanoTime() - timeStarted) * 1E-9); //current time - timeStarted * 1E-9 to convert to seconds
    }

}
