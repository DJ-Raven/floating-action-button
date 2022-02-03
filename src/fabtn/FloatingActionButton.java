package fabtn;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

public class FloatingActionButton extends JComponent {

    private final List<EventFloatingActionButton> events;
    private final MigLayout layout;
    private final Animator animator;
    private TimingTarget target;

    public FloatingActionButton() {
        events = new ArrayList<>();
        layout = new MigLayout();
        setLayout(layout);
        Button button = new Button(-1);
        button.setShowButton(true);
        button.setBackground(new Color(40, 154, 242));
        add(button, "pos 1al 1al, w 50, h 50, id ms");
        animator = new Animator(300);
        animator.setResolution(0);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (animator.isRunning()) {
                    float f = animator.getTimingFraction();
                    animator.stop();
                    animator.setStartFraction(1f - f);
                } else {
                    animator.setStartFraction(0);
                }
                animator.removeTarget(target);
                if (button.isSelected()) {
                    target = new PropertySetter(button, "buttonAngle", button.getButtonAngle(), -45, 0);
                } else {
                    target = new PropertySetter(button, "buttonAngle", button.getButtonAngle(), 180, 135);
                }
                animator.addTarget(target);
                button.setSelected(!button.isSelected());
                animator.start();
            }
        });
        animator.addTarget(new TimingTargetAdapter() {
            @Override
            public void begin() {
                if (button.isSelected()) {
                    for (Component com : getComponents()) {
                        if (getComponentZOrder(com) != 0) {
                            com.setVisible(true);
                        }
                    }
                }
            }

            @Override
            public void end() {
                if (!button.isSelected()) {
                    for (Component com : getComponents()) {
                        if (getComponentZOrder(com) != 0) {
                            com.setVisible(false);
                        }
                    }
                }
            }

            @Override
            public void timingEvent(float fraction) {
                for (Component com : getComponents()) {
                    int index = getComponentZOrder(com);
                    if (index != 0) {
                        int location = (int) ((index * 53) * (button.isSelected() ? fraction : 1f - fraction));
                        layout.setComponentConstraints(com, "pos ms.x+3 ms.y-(" + location + "), w 44!, h 44!");
                        revalidate();
                    }
                }
            }
        });
    }

    public void addItem(Icon icon, Color color) {
        Button item = new Button(getComponentCount() - 1);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                runEvent(item.getIndex());
            }
        });
        item.setIcon(icon);
        item.setBackground(color);
        //  ms is id of button
        item.setVisible(false);
        add(item, "pos ms.x+3 ms.y+3, w 44!, h 44!");
    }

    public void addEvent(EventFloatingActionButton event) {
        events.add(event);
    }

    private void runEvent(int index) {
        for (EventFloatingActionButton event : events) {
            event.selected(index);
        }
    }
}
