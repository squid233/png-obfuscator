import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public final class Deobfuscator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: <input-img> <output-file> [image=true]");
            return;
        }
        var finb64 = new StringBuilder();
        var img = ImageIO.read(new File(args[0]));
        int imgH = img.getHeight();
        for (int y = 0; y < imgH; y++) {
            for (int x = 0; x < imgH; x++) {
                int argb = img.getRGB(x, y);
                if (argb != 0xff000000) {
                    finb64.append((char) (argb << 8 >>> 24));
                }
            }
        }
        byte[] arr = Base64.getDecoder().decode(finb64.toString());
        boolean isOutputImg = args.length < 3 || Boolean.parseBoolean(args[2]);
        if (isOutputImg) {
            try (var is = new ByteArrayInputStream(arr)) {
                var out = ImageIO.read(is);
                ImageIO.write(out, "png", new File(args[1]));
            }
        } else {
            Files.write(Path.of(args[1]), arr, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}