package util;

public class Time {
    //when using float fps was infinity but would dip to 7. Using long, my fps is around my monitor refresh rate(240Hz) and more stable
    public static long timeStarted = System.nanoTime();  //static variables initialized at application startup

    public static float getTime(){
        return (float)((System.nanoTime() - timeStarted) * 1E-9); //current time - timeStarted * 1E-9 to convert to seconds
    }

}
