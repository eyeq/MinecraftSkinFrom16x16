package skin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        File in = new File("blocks");
        if(!in.exists()) {
            in.mkdir();
            return;
        }
        File out = new File("skin");
        if(!out.exists()) {
            if(!out.mkdir()) {
                return;
            }
        }
        for(File file : in.listFiles()) {
            String name = file.getName();
            if(name.endsWith(".png")) {
                System.out.println(name);
                buildSkin(file, new File(out, name), false);
                buildSkin(file, new File(out, name.substring(0, name.length() - 3) + "alex.png"), true);
            }
        }
    }

    public static BufferedImage buildSkin(File in, File out, boolean slim) {
        BufferedImage src16 = null;
        try {
            src16 = ImageIO.read(in);
        } catch(Exception e) {
            e.printStackTrace();
        }

        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, image.getHeight(), image.getHeight());
        graphics.fill(rect);
        graphics.setPaintMode();

        drawHead(graphics, src16);
        drawArmsAndLegs(graphics, src16, slim);
        drawBody(graphics, src16);

        try {
            ImageIO.write(image, "png", out);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private static void drawHead(Graphics2D graphics, Image src) {
        Image src8 = src.getScaledInstance(8, 8, Image.SCALE_SMOOTH);
        int dx = 0;
        int dy = 0;
        // Head HeadWear
        for(int i = 0; i < 2; i++) {
            graphics.drawImage(src8, dx + 8, dy + 0, null);
            graphics.drawImage(src8, dx + 16, dy + 0, null);
            graphics.drawImage(src8, dx + 0, dy + 8, null);
            graphics.drawImage(src8, dx + 8, dy + 8, null);
            graphics.drawImage(src8, dx + 16, dy + 8, null);
            graphics.drawImage(src8, dx + 24, dy + 8, null);
            dx = 32;
        }
    }

    private static void drawArmsAndLegs(Graphics2D graphics, Image src, boolean slim) {
        Image src16x12 = src.getScaledInstance(16, 12, Image.SCALE_SMOOTH);
        Image src14x12 = src.getScaledInstance(14, 12, Image.SCALE_SMOOTH);
        Image src4 = src.getScaledInstance(4, 4, Image.SCALE_SMOOTH);
        Image src3x4 = src.getScaledInstance(3, 4, Image.SCALE_SMOOTH);
        int dx = 0;
        int dy = 0;
        // RightLeg RightArm RightLegPants RightArmSleeve
        // LeftLegPants LeftLeg LeftArm LeftArmSleeve
        for(int i = 0; i < 8; i++) {
            if(slim && (i == 1 || i == 3 || i == 6 || i == 7)) {
                graphics.drawImage(src3x4, dx + 4, dy + 16, null);
                graphics.drawImage(src3x4, dx + 7, dy + 16, null);
                graphics.drawImage(src14x12, dx + 0, dy + 20, null);
            } else {
                graphics.drawImage(src4, dx + 4, dy + 16, null);
                graphics.drawImage(src4, dx + 8, dy + 16, null);
                graphics.drawImage(src16x12, dx + 0, dy + 20, null);
            }
            switch(i) {
            case 0:
            case 2:
                dx = 40;
                break;
            case 3:
                src16x12 = moveX(reverseX(src16x12, 16, 12), 16, 12, -4);
                src14x12 = moveX(reverseX(src14x12, 14, 12), 14, 12, -3);
                src4 = reverseX(src4, 4, 4);
                src3x4 = reverseX(src3x4, 3, 4);
            case 1:
                dx = 0;
                dy += 16;
                break;
            default:
                dx += 16;
                break;
            }
        }
    }

    private static void drawBody(Graphics2D graphics, Image src) {
        Image src12 = src.getScaledInstance(12, 12, Image.SCALE_SMOOTH);
        Image src8x4 = src.getScaledInstance(8, 4, Image.SCALE_SMOOTH);
        int dx = 0;
        int dy = 0;
        // Body Jacket
        BufferedImage[] src8x4s = mirrorY(src8x4, 8, 4);
        src12 = moveX(src12, 12, 12, 2);
        for(int i = 0; i < 2; i++) {
            graphics.drawImage(src8x4s[0], dx + 20, dy + 16, null);
            graphics.drawImage(src8x4s[1], dx + 28, dy + 16, null);
            graphics.drawImage(src12, dx + 16, dy + 20, null);
            graphics.drawImage(src12, dx + 28, dy + 20, null);
            dy = 16;
        }
    }

    public static BufferedImage reverseX(Image image, int width, int height) {
        AffineTransform at = AffineTransform.getScaleInstance(-1d, 1d);
        at.translate(-width, 0);

        BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = ret.createGraphics();
        g.setTransform(at);
        g.drawImage(image, 0, 0, null);
        return ret;
    }

    public static BufferedImage moveX(Image image, int width, int height, int x) {
        AffineTransform at = new AffineTransform();
        if(x < 0) {
            at.setToTranslation(width + x, 0);
        } else {
            at.setToTranslation(-width + x, 0);
        }

        BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = ret.createGraphics();
        g.setTransform(at);
        g.drawImage(image, 0, 0, null);

        at.setToTranslation(x, 0);
        g.setTransform(at);
        g.drawImage(image, 0, 0, null);
        return ret;
    }

    public static BufferedImage[] mirrorY(Image image, int width, int height) {
        AffineTransform at = AffineTransform.getScaleInstance(1d, -1d);
        at.translate(0, -height);

        BufferedImage retUpper = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gu = retUpper.createGraphics();
        gu.drawImage(image, 0, 0, width, height / 2, 0, 0, width, height / 2, null);
        gu.setTransform(at);
        gu.drawImage(image, 0, 0, width, height / 2, 0, 0, width, height / 2, null);

        BufferedImage retLower = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gl = retLower.createGraphics();
        gl.drawImage(image, 0, height / 2, width, height, 0, height / 2, width, height, null);
        gl.setTransform(at);
        gl.drawImage(image, 0, height / 2, width, height, 0, height / 2, width, height, null);

        return new BufferedImage[]{retUpper, retLower};
    }
}
