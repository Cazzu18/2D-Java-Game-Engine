package util;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class JMath {

    //TODO: Understand in Depth
    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float)Math.cos(Math.toRadians(angleDeg));
        float sin = (float)Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;
        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }

    private static int toByte(float channel){
        float clamped = Math.max(0.0f, Math.min(1.0f, channel));
        return Math.round(clamped * 255.0f);
    }

    public int Vector4fToInt32(Vector4f color){
        int alpha = toByte(color.w);
        int red = toByte(color.x);
        int green = toByte(color.y);
        int blue = toByte(color.z);

        return (alpha << 24) | (red << 16) | (green << 8) | blue; //0xAARRGGBB
    }

    //ImGui picker needs values from 0 to 1
    public Vector4f Int32ToVector4f(int argb) {
        float r = ((argb >>> 16) & 0xFF) / 255.0f;
        float g = ((argb >>> 8)  & 0xFF) / 255.0f;
        float b = ( argb         & 0xFF) / 255.0f;
        float a = ((argb >>> 24) & 0xFF) / 255.0f;

        return new Vector4f(r, g, b, a);
    }
}
