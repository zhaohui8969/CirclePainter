import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class P01 {


    public static void main(String[] args) {
        new P01();
    }

    public P01() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {


        private PaintPane paintPane;

        public TestPane() {
            setLayout(new BorderLayout());
            add((paintPane = new PaintPane()));
            add(new ColorsPane(paintPane), BorderLayout.SOUTH);
        }
    }

    public class ColorsPane extends JPanel {

        public ColorsPane(PaintPane paintPane) {
            add(new JButton(new ColorAction(paintPane, "Red", Color.RED)));
            add(new JButton(new ColorAction(paintPane, "BLACK", Color.BLACK)));
            add(new JButton(new ColorAction(paintPane, "Green", Color.GREEN)));
            add(new JButton(new ColorAction(paintPane, "Blue", Color.BLUE)));
            final JTextField jTextField = new JTextField(Integer.toString(paintPane.diveN), 30);
            jTextField.addActionListener(new DiveNumAction(paintPane));
            add(jTextField);
        }

        public class DiveNumAction extends AbstractAction {
            private PaintPane paintPane;

            public DiveNumAction(PaintPane paintPane) {
                this.paintPane = paintPane;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                final String text = ((JTextField) e.getSource()).getText();
                System.out.println("text = " + text);
                paintPane.diveN = Integer.parseInt(text);
            }
        }

        public class ColorAction extends AbstractAction {

            private PaintPane paintPane;
            private Color color;

            private ColorAction(PaintPane paintPane, String name, Color color) {
                putValue(NAME, name);
                this.paintPane = paintPane;
                this.color = color;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                paintPane.setForeground(color);
            }

        }

    }

    public class PaintPane extends JPanel {

        private int diveN = 3;
        private int pointSize = 5;

        private BufferedImage background;

        public PaintPane() {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            MouseAdapter handler = new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    coreDrawCircle(e.getPoint());
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    coreDrawCircle(e.getPoint());
                }

            };
            addMouseListener(handler);
            addMouseMotionListener(handler);
        }

        protected void coreDrawCircle(Point p) {
            Point center = new Point(this.getWidth() / 2, this.getHeight() / 2);
            int xTransFromCenter = p.x - center.x;
            int yTransFromCenter = p.y - center.y;
            double r = Math.sqrt(Math.pow(xTransFromCenter, 2) + Math.pow(yTransFromCenter, 2));
//            System.out.println("r = " + r);
//            System.out.println("xTransFromCenter = " + xTransFromCenter);
//            System.out.println("yTransFromCenter = " + yTransFromCenter);
            double thetaStart = Math.toDegrees(Math.acos(xTransFromCenter / r));
            if (yTransFromCenter > 0) {
                thetaStart = 360 - thetaStart;
            }
//            System.out.println("thetaStart = " + thetaStart);
            drawMultiDot(getCirclePointList(center, r, diveN, thetaStart));
        }

        private LinkedList<Point> getCirclePointList(Point center, double r, int n, double thetaStart) {
            LinkedList<Point> pointLinkedList = new LinkedList<>();
            double diveTheta = 360f / n;
            for (double theta = thetaStart; theta < 360 + thetaStart; theta += diveTheta) {
//                System.out.println("theta = " + theta);
                final double xTrans = r * Math.cos(Math.toRadians(theta));
                final double yTrans = r * Math.sin(Math.toRadians(theta));
//                System.out.println("xTrans = " + xTrans);
                pointLinkedList.add(new Point((int) (center.x + xTrans), (int) (center.y - yTrans)));
            }
            return pointLinkedList;
        }

        private void drawMultiDot(List<Point> pointList) {
            for (Point point : pointList) {
                drawDot(point);
            }
        }

        protected void drawDot(Point p) {

            if (background == null) {
                updateBuffer();
                ;
            }

            if (background != null) {
                Graphics2D g2d = background.createGraphics();
                g2d.setColor(getForeground());
                g2d.fillOval(p.x - pointSize / 2, p.y - pointSize / 2, pointSize, pointSize);
                g2d.dispose();
            }
            repaint();
        }

        @Override
        public void invalidate() {
            super.invalidate();
            updateBuffer();
        }

        protected void updateBuffer() {

            if (getWidth() > 0 && getHeight() > 0) {
                BufferedImage newBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = newBuffer.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                if (background != null) {
                    g2d.drawImage(background, 0, 0, this);
                }
                g2d.dispose();
                background = newBuffer;
            }

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            if (background == null) {
                updateBuffer();
            }
            g2d.drawImage(background, 0, 0, this);
            g2d.dispose();
        }
    }
}