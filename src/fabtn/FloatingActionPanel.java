package fabtn;

import javax.swing.JLayeredPane;
import net.miginfocom.swing.MigLayout;

public class FloatingActionPanel extends JLayeredPane {

    public FloatingActionPanel() {
        setLayout(new MigLayout("fill, inset 0", "[fill]", "[fill]"));
    }

    public void addFloatingActionButton(FloatingActionButton fabtn) {
        setLayer(fabtn, JLayeredPane.POPUP_LAYER);
        add(fabtn, "pos 1al 1al, w 60, h 100%", 0);
        repaint();
        revalidate();
    }
}
