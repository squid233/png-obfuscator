import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.*;

public final class Obfuscator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: <input-img> <output-img>");
            return;
        }
        byte[] arr = Files.readAllBytes(Path.of(args[0]));
        var b64 = Base64.getEncoder().encodeToString(arr);
        int len = b64.length();
        int height = (int) Math.ceil(Math.sqrt(len));
        var out = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < height; x++) {
                int idx = height * y + x;
                int c;
                if (idx >= len) {
                    c = 0;
                } else {
                    c = b64.charAt(idx);
                }
                out.setRGB(x, y, (0xff << 24) | (c << 16) | (c << 8) | (c));
            }
        }
        ImageIO.write(out, "png", new File(args[1]));
    }
}