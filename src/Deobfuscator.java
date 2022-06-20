import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

public final class Deobfuscator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: <input-img> <output-file> [image=true] [pre-compressed=false]");
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
        boolean preGZ = args.length >= 4 && Boolean.parseBoolean(args[3]);
        if (isOutputImg) {
            try (var bis = new ByteArrayInputStream(arr);
                 var gis = preGZ ? new GZIPInputStream(bis) : null
            ) {
                var out = ImageIO.read(preGZ ? gis : bis);
                ImageIO.write(out, "png", new File(args[1]));
            }
        } else {
            try (var bis = preGZ ? new ByteArrayInputStream(arr) : null;
                 var gis = preGZ ? new GZIPInputStream(bis) : null) {
                if (preGZ) {
                    arr = gis.readAllBytes();
                }
                Files.write(Path.of(args[1]), arr, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
    }
}