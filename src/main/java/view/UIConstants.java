// iew/UIConstants.java
package view;

import java.awt.*;

public class UIConstants {
    // Background
    public static final Color BG_DARK = new Color(17, 24, 39);
    public static final Color BG_PANEL = new Color(30, 41, 59);
    public static final Color BG_LIGHT = new Color(241, 245, 249);
    
    // Room Status
    public static final Color COLOR_AVAILABLE = new Color(134, 239, 172); // green-300
    public static final Color COLOR_PENDING = new Color(253, 224, 71); // yellow-300
    public static final Color COLOR_APPROVED = new Color(252, 165, 165); // red-300
    public static final Color COLOR_REJECTED = new Color(156, 163, 175); // gray-400
    public static final Color COLOR_LOBBY = new Color(0, 0, 0); // black
    public static final Color COLOR_UNAVAILABLE = new Color(156, 163, 175); // gray-400
    
    // Border dan Text
    public static final Color COLOR_BORDER = new Color(148, 163, 184);
    public static final Color COLOR_TEXT = new Color(15, 23, 42);
    public static final Color COLOR_TEXT_WHITE = Color.WHITE;
    
    // Fonts
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font ROOM_FONT = new Font("SansSerif", Font.BOLD, 12);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    
    // Dimensi
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 600;
    public static final int MIN_WIDTH = 1600;
    public static final int MIN_HEIGHT = 600;
}