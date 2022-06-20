import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

public final class Obfuscator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: <input-file> <output-img> [pre-compress=false]");
            return;
        }

        byte[] arr;
        boolean preGZ = args.length >= 3 && Boolean.parseBoolean(args[2]);
        try (var bos = new ByteArrayOutputStream();
             var gos = preGZ ? new GZIPOutputStream(bos) : null) {
            var img = ImageIO.read(new File(args[0]));
            ImageIO.write(img, "png", preGZ ? gos : bos);
            if (preGZ) {
                gos.finish();
            }
            arr = bos.toByteArray();
        } catch (Exception e) {
            try (var bos = preGZ ? new ByteArrayOutputStream() : null;
                 var gos = preGZ ? new GZIPOutputStream(bos) : null) {
                arr = Files.readAllBytes(Path.of(args[0]));
                if (preGZ) {
                    gos.write(arr);
                    gos.finish();
                    arr = bos.toByteArray();
                }
            }
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