import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public final class Obfuscator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: <input-file> <output-img>");
            return;
        }
        byte[] arr;
        try (var os = new ByteArrayOutputStream()) {
            var img = ImageIO.read(new File(args[0]));
            ImageIO.write(img, "png", os);
            arr = os.toByteArray();
        } catch (Exception e) {
            arr = Files.readAllBytes(Path.of(args[0]));
        }
        var b64 = Base64.getEncoder().encodeToString(arr);
        int len = b64.length();
        int height = (int) Math.ceil(Math.sqrt(len));
        var out = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < height; x++) {
                int idx = height * y + x;
                int c = (idx >= len) ? 0 : b64.charAt(idx);
                out.setRGB(x, y, (0xff << 24) | (c << 16) | (c << 8) | (c));
            }
        }
        ImageIO.write(out, "png", new File(args[1]));
    }
}