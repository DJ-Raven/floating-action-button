package fabtn;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class Button extends JButton {

    public int getButtonAngle() {
        return buttonAngle;
    }

    public void setButtonAngle(int buttonAngle) {
        this.buttonAngle = buttonAngle;
        repaint();
    }

    public boolean isShowButton() {
        return showButton;
    }

    public void setShowButton(boolean showButton) {
        this.showButton = showButton;
    }

    public int getIndex() {
        return index;
    }

    public Color getEffectColor() {
        return effectColor;
    }

    public void setEffectColor(Color effectColor) {
        this.effectColor = effectColor;
    }

    private Animator animator;
    private int targetSize;
    private float animatSize;
    private Point pressedPoint;
    private float alpha;
    private Color effectColor = new Color(220, 220, 220);
    private int buttonAngle;
    private boolean showButton;
    private int index;

    public Button(int index) {
        this.index = index;
        setContentAreaFilled(false);
        setForeground(new Color(242, 242, 242));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                targetSize = Math.max(getWidth(), getHeight()) * 2;
                animatSize = 0;
                pressedPoint = me.getPoint();
                alpha = 0.5f;
                if (animator.isRunning()) {
                    animator.stop();
                }
                animator.start();
            }
        });
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                if (fraction > 0.5f) {
                    alpha = 1 - fraction;
                }
                animatSize = fraction * targetSize;
                repaint();
            }
        };
        animator = new Animator(400, target);
        animator.setResolution(0);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width, height, height, height);
        if (pressedPoint != null) {
            Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, height, height));
            g2.setColor(effectColor);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            area.intersect(new Area(new Ellipse2D.Double((pressedPoint.x - animatSize / 2), (pressedPoint.y - animatSize / 2), animatSize, animatSize)));
            g2.fill(area);
        }
        if (showButton) {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(getForeground());
            int x = width / 2;
            int y = height / 2;
            Line2D line1 = new Line2D.Double(x, y - 6, x, y + 6);
            Line2D line2 = new Line2D.Double(x - 6, y, x + 6, y);
            AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(buttonAngle), line1.getX1(), line2.getY1());
            g2.setStroke(new BasicStroke(3f));
            g2.draw(at.createTransformedShape(line1));
            g2.draw(at.createTransformedShape(line2));
        }
        g2.dispose();
        super.paintComponent(grphcs);
    }
}
