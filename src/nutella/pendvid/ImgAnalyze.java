package nutella.pendvid;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import nutella.pendvid.imgdiff.DiffData;

public class ImgAnalyze {
	public static final int THRESHOLD = 30;

    public static boolean[][] pxDiff(BufferedImage a, BufferedImage b) {
        boolean[][] d = new boolean[a.getWidth()][a.getHeight()];
        for (int i = 0; i < a.getWidth(); i++) {
            for (int j = 0; j < a.getHeight(); j++) {
                Color o = new Color(a.getRGB(i, j));
                Color n = new Color(b.getRGB(i, j));
                d[i][j] = (o.getRed() - n.getRed()) * (o.getRed() - n.getRed()) +
                        (o.getBlue() - n.getBlue()) * (o.getBlue() - n.getBlue()) +
                      (o.getGreen() - n.getGreen()) * (o.getGreen() - n.getGreen()) >
                	THRESHOLD * THRESHOLD;
            }
        }
        return d;
    }

    public static PointD avg(boolean[][] a) {
        return avg(Util.convert(a));
    }

    public static double stdev(boolean[][] a) {
        return stdev(Util.convert(a));
    }

    public static PointD avg(List<Point> points) {
        double x = 0, y = 0;
        for (Point p : points) {
            x += p.x;
            y += p.y;
        }
        x /= points.size();
        y /= points.size();
        return new PointD(x, y);
    }

    public static double stdev(List<Point> points) {
        PointD avg = avg(points);
        double d = 0;
        for (Point p : points) {
            d += (p.x - avg.x) * (p.x - avg.x) + (p.y - avg.y) * (p.y - avg.y);
        }
        return Math.sqrt(d / points.size());
    }

    public static DiffData analyzeImgDiff(BufferedImage a, BufferedImage b, DiffData prevDiff) {
        boolean[][] diff = pxDiff(a, b);
        List<Point> points = Util.convert(diff);
        if(prevDiff == null) {
        	return new DiffData(points, avg(points), stdev(points));
        }
//        for (int i = 0; i < points.size(); i++) {
//            if ((points.get(i).x - prevDiff.avg.x) * (points.get(i).x - prevDiff.avg.x) +
//                (points.get(i).y - prevDiff.avg.y) * (points.get(i).y - prevDiff.avg.y) > prevDiff.stdev * prevDiff.stdev * 3) {
//                points.remove(i);
//                i--;
//            }
//        }
        PointD avg = avg(points);
        double stdev = stdev(points);
        for (int i = 0; i < points.size(); i++) {
            if ((points.get(i).x - avg.x) * (points.get(i).x - avg.x) +
                (points.get(i).y - avg.y) * (points.get(i).y - avg.y) > 4 * stdev * stdev) {
                points.remove(i);
                i--;
            }
        }
        return new DiffData(points, avg(points), stdev(points));
    }
}
