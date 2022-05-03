import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.graphics.Color;
public class Brick
{
    private Rectangle rect; 
    private Color color; 
    
    public Brick(float x, float y, Color c)
    {
        rect = new Rectangle(x, y, Constants.BRICK_WIDTH, Constants.BRICK_HEIGHT);  
        color = c; 
    }
    
    public Rectangle getRectangle()
    {
        return rect; 
    }
    
    public Color getColor()
    {
       return color;    
    }
}
