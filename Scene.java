

import java.awt.Graphics2D;
import javax.swing.event.ChangeListener;

public interface Scene {
  void draw(Graphics2D g);
  int getWidth();
  int getHeight();
  void addChangeListener(ChangeListener listener);
}
