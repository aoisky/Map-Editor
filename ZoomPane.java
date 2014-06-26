

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;


/**
 * A generic, zoomable scroll pane.
 */
public class ZoomPane extends JScrollPane {
  /**
	 * 
	 */

private ZoomPanel _panel;

  public ZoomPane(Scene scene) {
    _panel = new ZoomPanel(scene);
    getViewport().add(_panel);

    // Java 1.5 has a terrible scroll increment default
    getVerticalScrollBar().setUnitIncrement(5);
    getHorizontalScrollBar().setUnitIncrement(5);
  }


  public ZoomPanel getZoomPanel() { return _panel; }


  /**
   * Converts a point in pixel coordinates on the screen to view coordinates on
   * the map.
   */
  public Point toViewCoordinates(Point point) {
    double scale = _panel.getScale();
    int x = (int) (point.getX() / scale);
    int y = (int) (point.getY() / scale);
    return new Point(x, y);
  }


  /**
   * Zooms to the given scale, attempting to preserve the center.
   */
  public void zoom(double scale) {
    double oldScale = _panel.getScale();

    // Get the top left coordinates of the viewport
    Point point = getViewport().getViewPosition();

    // Zoom the JPanel containing the graphics
    _panel.zoom(scale);

    double w = getViewport().getWidth();
    double h = getViewport().getHeight();

    // Turn the top left coordinates into the center coordinates of the viewport
    int halfW = (int) (w/2.0);
    int halfH = (int) (h/2.0);
    point.translate(halfW, halfH);

    // Calculate the scaling factor relative to the old one
    double relScale = scale / oldScale;

    // Calculate the new center of the viewport
    point.move((int) (point.getX() * relScale), (int) (point.getY() * relScale));

    // Move back to top left
    point.translate(-halfW, -halfH);

    // Set this to be the new top left coordinates of the viewport
    getViewport().setViewPosition(point);
  }


  /**
   * Creates a JSlider widget that can be used to control the zooming.
   */
  protected JSlider getJSlider() {
    int min = 1, max = 36, inc = 5;
    final JSlider slider = new JSlider(min, max, 16);
    slider.setMajorTickSpacing(5);
    slider.setMinorTickSpacing(1);
    slider.setPaintTicks(true);
    slider.setSnapToTicks(true);
    slider.setLabelTable(getLabelTable(min, max, inc));
    slider.setPaintLabels(true);
    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        double scale = (slider.getValue() + 4) / 20.0;
        zoom(scale);
        repaint();
      }
    });
    return slider;
  }


  /**
   * Returns a table used for labeling the JSlider.
   */
  private Hashtable getLabelTable(int min, int max, int inc) {
    Hashtable<Integer,JLabel> table = new Hashtable<Integer,JLabel>();
    for (int j = min; j <= max; j += inc) {
      String s = String.format("%.2f", (j+4)/20.0);
      table.put(Integer.valueOf(j), new JLabel(s));
    }
    return table;
  }


  /**
   * This is the JPanel that implements the scaling of the map graphics.
   */
  public class ZoomPanel extends JPanel implements ChangeListener {
    private double _scale = 1.0;
    private Scene _scene; // The scene that will be rendered

    public ZoomPanel() {
      setLayout(new BorderLayout());
    }

    public ZoomPanel(Scene scene) {
      this();
      setScene(scene);
    }

    public void setScene(Scene scene) {
      _scene = scene;
      _scene.addChangeListener(this);
    }

    /**
     * This is called when the scene has been updated.
     */
    public void stateChanged(ChangeEvent e) {
      repaint();
    }

    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BICUBIC);

      // Endow this graphics object with a scaling transformation
      // that it will use from this point on.
      g2.scale(_scale, _scale);
      if (_scene != null) _scene.draw(g2);
    }

    public double getScale() { return _scale; }

    public Dimension getPreferredSize() {
      int w = 0, h = 0;
      if (_scene != null) {
        w = (int)(_scale*_scene.getWidth());
        h = (int)(_scale*_scene.getHeight());
      }
      return new Dimension(w, h);
    }

    /**
     * Changes the scaling factor of the scene.
     */
    protected void zoom(double scale) {
      _scale = scale;
      revalidate();
      repaint();
    }
  }
}
